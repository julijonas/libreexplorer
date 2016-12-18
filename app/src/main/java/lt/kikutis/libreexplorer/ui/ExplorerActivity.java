/*
 * Copyright 2015 Julijonas Kikutis
 *
 * This file is part of Libre Explorer.
 *
 * Libre Explorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Libre Explorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Libre Explorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package lt.kikutis.libreexplorer.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lt.kikutis.libreexplorer.DeviceUtils;
import lt.kikutis.libreexplorer.PathUtils;
import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.SettingsManager;
import lt.kikutis.libreexplorer.connection.Connection;
import lt.kikutis.libreexplorer.connection.OnFinishListener;
import lt.kikutis.libreexplorer.menu.Clip;
import lt.kikutis.libreexplorer.menu.DrawerManager;
import lt.kikutis.libreexplorer.menu.Place;
import lt.kikutis.libreexplorer.presenter.OnFileCheckedListener;
import lt.kikutis.libreexplorer.presenter.OnFileSelectedListener;
import lt.kikutis.libreexplorer.tab.History;
import lt.kikutis.libreexplorer.tab.Tab;
import lt.kikutis.libreexplorer.tab.TabManager;
import lt.kikutis.libreexplorer.tab.Visit;
import lt.kikutis.libreexplorer.ui.adapter.TabPagerAdapter;

public class ExplorerActivity extends AppCompatActivity implements
        OnFileSelectedListener,
        OnFileCheckedListener,
        OpenAsFragment.OnMimeTypeSelectedListener,
        OpenWithFragment.OnSelectMimeTypeSelectedListener,
        SortByFragment.OnSortFieldSelectedListener,
        DeleteFragment.OnDeleteSelectedListener,
        OnCopyMoveSelectedListener,
        NewConnectionFragment.OnConnectionSelectedListener {

    private static final String STATE_TAB_MANAGER = "tab_manager";

    private static final int ACTIVITY_SETTINGS = 1;
    private static final int ACTIVITY_EDITOR = 2;

    private DrawerManager mDrawerManager;

    private BreadcrumbsFragment mBreadcrumbsFragment;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private MenuItem mPasteMenuItem;

    private ActionMode mActionMode;

    private CoordinatorLayout mCoordinatorLayout;
    private TabManager mTabManager;
    private TabPagerAdapter mTabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    /**
     * Tracks when action mode is temporarily turned off when drawer is open.
     */
    private boolean mActionModeOffWhenDrawerOpen;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_file, menu);
            mode.setTitle(getCurrentTab().getHistory().current().getPath());
            mode.setSubtitle(getChosenCountSubtitle());

            /* Because I am unable to set clickable property on the contextual action bar this is needed to prevent
             * clicking through to toolbar buttons. See also corresponding line in onDestroyActionMode. */
            mToolbar.setVisibility(View.INVISIBLE);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            ArrayList<String> paths = getCurrentFragment().getCheckedPaths();
            switch (item.getItemId()) {
                case R.id.action_file_bookmark:
                    for (String path : paths) {
                        mDrawerManager.add(new Place(PathUtils.getNameFromPath(path), path));
                    }
                    mode.finish();
                    break;
                case R.id.action_file_cut:
                case R.id.action_file_copy:
                    mDrawerManager.add(new Clip(new ArrayList<>(paths), item.getItemId() == R.id.action_file_cut));
                    mPasteMenuItem.setVisible(true);
                    mode.finish();
                    break;
                case R.id.action_file_delete:
                    if (PreferenceManager.getDefaultSharedPreferences(ExplorerActivity.this).getBoolean(
                            getString(R.string.key_confirm_deletion),
                            getResources().getBoolean(R.bool.default_confirm_deletion))) {
                        DialogFragment fragment = DeleteFragment.newInstance(paths);
                        fragment.show(getSupportFragmentManager(), null);
                    } else {
                        onDeleteSelected(paths);
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mToolbar.setVisibility(View.VISIBLE);
            if (!mActionModeOffWhenDrawerOpen) {
                getCurrentFragment().clearChosenPaths();
            }
            mActionMode = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view_drawer);
        assert navigationView != null;
        TextView deviceName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_device_name);
        assert deviceName != null;
        deviceName.setText(String.format("%s %s", Build.BRAND.toUpperCase(), Build.MODEL.toUpperCase()));
        deviceName.setCompoundDrawablesWithIntrinsicBounds(DeviceUtils.getDeviceImage(this), 0, 0, 0);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (mActionModeOffWhenDrawerOpen) {
                    mActionModeOffWhenDrawerOpen = false;
                    mActionMode = startSupportActionMode(mActionModeCallback);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_DRAGGING && mActionMode != null) {
                    mActionModeOffWhenDrawerOpen = true;
                    mActionMode.finish();
                }
            }
        });

        mDrawerManager = new DrawerManager(this, navigationView, this, this);

        if (savedInstanceState == null) {
            String path = mDrawerManager.getFirstPlace().getPath();

            mTabManager = new TabManager(path, mDrawerManager);

            mBreadcrumbsFragment = BreadcrumbsFragment.newInstance(path);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_breadcrumbs, mBreadcrumbsFragment).commit();
        } else {
            mTabManager = savedInstanceState.getParcelable(STATE_TAB_MANAGER);

            mBreadcrumbsFragment = (BreadcrumbsFragment) getSupportFragmentManager().findFragmentById(R.id.container_breadcrumbs);
        }

        mTabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), mTabManager, this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        assert mViewPager != null;
        mViewPager.setAdapter(mTabPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        assert mTabLayout != null;
        mTabLayout.setupWithViewPager(mViewPager);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getCurrentTab().getHistory().current().setPosition(getCurrentFragment().getPosition());
        outState.putParcelable(STATE_TAB_MANAGER, mTabManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_explorer, menu);
        mPasteMenuItem = menu.findItem(R.id.action_paste);
        mPasteMenuItem.setVisible(mDrawerManager.hasClips());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ensureCleanState();
        switch (item.getItemId()) {
            case R.id.action_paste:
                Clip clip = mDrawerManager.getLastClip();
                onCopyMoveSelected(clip.getFiles(), clip.isCut());
                break;
            case R.id.action_directory_up:
                navigateUp();
                break;
            case R.id.action_sort_by:
                SortByFragment.newInstance(SettingsManager.getSortField())
                        .show(getSupportFragmentManager(), null);
                break;
            case R.id.action_new_folder:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText editText = new EditText(this);
                editText.setHint(R.string.folder_name);
                builder.setTitle(R.string.new_folder)
                        .setView(editText)
                        .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(mCoordinatorLayout, R.string.folder_created, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        })
                                        .show();
                            }
                        })
                        .show();
                break;
            case R.id.action_new_file:
                Intent intent = new Intent(this, EditorActivity.class);
                intent.putExtra(EditorActivity.EXTRA_LOCATION, getCurrentTab().getHistory().current());
                startActivityForResult(intent, ACTIVITY_EDITOR);
                break;
            case R.id.action_new_connection:
                new NewConnectionFragment().show(getSupportFragmentManager(), null);
                break;
            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), ACTIVITY_SETTINGS);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        closeOptionsMenu();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_SETTINGS) {
            if (resultCode == SettingsActivity.RESULT_RELOAD_DIRECTORY) {
                reloadFragments();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mActionMode == null && getCurrentTab().getHistory().hasBack()) {
            navigateBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFileSelected(String path, boolean directory) {
        ensureCleanState();
        if (directory) {
            navigateToPath(path);
        } else {
            String mimeType = PathUtils.getMimeTypeFromPath(path);
            if (mimeType == null) {
                onSelectMimeTypeSelected(path);
            } else {
                onMimeTypeSelected(path, mimeType);
            }
        }
    }

    @Override
    public void onFileChecked() {
        if (getCurrentFragment().getCheckedPaths().isEmpty()) {
            mActionMode.finish();
        } else if (mActionMode == null) {
            mActionMode = startSupportActionMode(mActionModeCallback);
        } else {
            mActionMode.setSubtitle(getChosenCountSubtitle());
        }
    }

    @Override
    public void onMimeTypeSelected(String path, String mimeType) {
        DialogFragment fragment = OpenWithFragment.newInstance(path, mimeType);
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onSelectMimeTypeSelected(String path) {
        DialogFragment fragment = OpenAsFragment.newInstance(path);
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onSortFieldSelected(int field, boolean reverse) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(getString(R.string.key_sort_field), field)
                .putBoolean(getString(R.string.key_sort_reverse), reverse)
                .apply();
        getCurrentFragment().sort();
    }

    @Override
    public void onDeleteSelected(List<String> paths) {
        getCurrentTab().getConnection().remove(paths, new OnFinishListener() {
            @Override
            public void onFinish() {
                reloadFragments();
            }
        });
        mActionMode.finish();
    }

    @Override
    public void onCopyMoveSelected(List<String> files, boolean move) {
        History history = getCurrentTab().getHistory();
        if (move) {
            getCurrentTab().getConnection().move(files, history.current().getPath(), new OnFinishListener() {
                @Override
                public void onFinish() {
                    reloadFragments();
                }
            });
        } else {
            getCurrentTab().getConnection().copy(files, history.current().getPath(), new OnFinishListener() {
                @Override
                public void onFinish() {
                    reloadFragments();
                }
            });
        }
    }

    @Override
    public void onConnectionSelected(Connection connection) {
        getCurrentTab().getHistory().addPath("foobar://foo/bar");
        reloadFragments();
    }

    private void ensureCleanState() {
        mActionModeOffWhenDrawerOpen = false;
        //getCurrentFragment().getCheckedPaths().clear();
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private String getChosenCountSubtitle() {
        int size = getCurrentFragment().getCheckedPaths().size();
        return getResources().getQuantityString(R.plurals.files_selected, size, size);
    }

    private void navigateUp() {
        History history = getCurrentTab().getHistory();
        history.current().setPosition(getCurrentFragment().getPosition());
        history.up();
        reloadFragments();
    }

    private void navigateBack() {
        History history = getCurrentTab().getHistory();
        history.current().setPosition(getCurrentFragment().getPosition());
        history.back();
        reloadFragments();
    }

    private void navigateToPath(@NonNull String path) {
        History history = getCurrentTab().getHistory();
        history.current().setPosition(getCurrentFragment().getPosition());
        history.addPath(path);
        reloadFragments();
    }

    private void reloadFragments() {
        Visit visit = getCurrentTab().getHistory().current();
        getCurrentFragment().loadPath(visit.getPath(), visit.getPosition());
        mTabPagerAdapter.notifyDataSetChanged();
        mBreadcrumbsFragment.loadPath(visit.getPath());
        mDrawerManager.setCurrentPath(visit.getPath());
    }

    private Tab getCurrentTab() {
        return mTabManager.getTab(0);
    }

    private DirectoryFragment getCurrentFragment() {
        return (DirectoryFragment) mTabPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
    }
}
