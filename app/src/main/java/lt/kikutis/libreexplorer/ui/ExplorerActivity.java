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

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lt.kikutis.libreexplorer.PathHistory;
import lt.kikutis.libreexplorer.PathUtils;
import lt.kikutis.libreexplorer.PathVisit;
import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.SettingsManager;
import lt.kikutis.libreexplorer.menu.Clip;
import lt.kikutis.libreexplorer.menu.DrawerMenu;
import lt.kikutis.libreexplorer.menu.Place;
import lt.kikutis.libreexplorer.connection.ConnectionManager;
import lt.kikutis.libreexplorer.connection.OnFinishListener;
import lt.kikutis.libreexplorer.connection.Connection;
import lt.kikutis.libreexplorer.presenter.OnFileCheckedListener;
import lt.kikutis.libreexplorer.presenter.OnFileSelectedListener;

public class ExplorerActivity extends AppCompatActivity implements
        OnFileSelectedListener,
        OnFileCheckedListener,
        OpenAsFragment.OnMimeTypeSelectedListener,
        OpenWithFragment.OnSelectMimeTypeSelectedListener,
        SortByFragment.OnSortFieldSelectedListener,
        DeleteFragment.OnDeleteSelectedListener,
        DrawerFragment.OnCopyMoveSelectedListener,
        NewConnectionFragment.OnConnectionSelectedListener {

    private static final String STATE_HISTORY = "history";

    private static final int ACTIVITY_SETTINGS = 1;

    private PathHistory mHistory;
    private DrawerMenu mDrawerMenu;

    private BreadcrumbsFragment mBreadcrumbsFragment;
    private DirectoryFragment mDirectoryFragment;
    private DrawerFragment mDrawerFragment;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private MenuItem mPasteMenuItem;

    private ActionMode mActionMode;

    /**
     * Tracks when action mode is temporarily turned off when drawer is open.
     */
    private boolean mActionModeOffWhenDrawerOpen;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_file, menu);
            mode.setTitle(mHistory.current().getPath());
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
            ArrayList<String> paths = mDirectoryFragment.getCheckedPaths();
            switch (item.getItemId()) {
                case R.id.action_file_bookmark:
                    for (String path : paths) {
                        mDrawerFragment.add(new Place(PathUtils.getNameFromPath(path), path));
                    }
                    mode.finish();
                    break;
                case R.id.action_file_cut:
                case R.id.action_file_copy:
                    mDrawerFragment.add(new Clip(new ArrayList<>(paths), item.getItemId() == R.id.action_file_cut));
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
                mDirectoryFragment.clearChosenPaths();
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
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
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

        mBreadcrumbsFragment = (BreadcrumbsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_breadcrumbs);
        mDirectoryFragment = (DirectoryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_directory);
        mDrawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        mDrawerMenu = new DrawerMenu(this);
        if (savedInstanceState == null) {
            mHistory = new PathHistory(new PathVisit(((Place) mDrawerMenu.get(0)).getPath()));
        } else {
            mHistory = savedInstanceState.getParcelable(STATE_HISTORY);
            if (!mDirectoryFragment.getCheckedPaths().isEmpty()) {
                mActionMode = startSupportActionMode(mActionModeCallback);
            }
        }
        mDrawerFragment.loadDrawerMenu(mDrawerMenu);
        reloadFragments();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mHistory.current().setPosition(mDirectoryFragment.getPosition());
        outState.putParcelable(STATE_HISTORY, mHistory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_explorer, menu);
        mPasteMenuItem = menu.findItem(R.id.action_paste);
        mPasteMenuItem.setVisible(mDrawerMenu.hasClips());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ensureCleanState();
        switch (item.getItemId()) {
            case R.id.action_paste:
                Clip clip = mDrawerMenu.getLastClip();
                onCopyMoveSelected(clip.getFiles(), clip.isCut());
                break;
            case R.id.action_directory_up:
                navigateUp();
                break;
            case R.id.action_sort_by:
                SortByFragment.newInstance(SettingsManager.getSortField())
                        .show(getSupportFragmentManager(), null);
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
        } else if (mActionMode == null && mHistory.hasBack()) {
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
        if (mDirectoryFragment.getCheckedPaths().isEmpty()) {
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
        mDirectoryFragment.sort();
    }

    @Override
    public void onDeleteSelected(List<String> paths) {
        ConnectionManager.getInstance().getShellConnection().remove(paths, new OnFinishListener() {
            @Override
            public void onFinish() {
                reloadFragments();
            }
        });
        mActionMode.finish();
    }

    @Override
    public void onCopyMoveSelected(List<String> files, boolean move) {
        if (move) {
            ConnectionManager.getInstance().getShellConnection().move(files, mHistory.current().getPath(), new OnFinishListener() {
                @Override
                public void onFinish() {
                    reloadFragments();
                }
            });
        } else {
            ConnectionManager.getInstance().getShellConnection().copy(files, mHistory.current().getPath(), new OnFinishListener() {
                @Override
                public void onFinish() {
                    reloadFragments();
                }
            });
        }
    }

    @Override
    public void onConnectionSelected(Connection connection) {
        mHistory.addPath("foobar://foo/bar");
        reloadFragments();
    }

    private void ensureCleanState() {
        mActionModeOffWhenDrawerOpen = false;
        mDirectoryFragment.getCheckedPaths().clear();
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private String getChosenCountSubtitle() {
        int size = mDirectoryFragment.getCheckedPaths().size();
        return getResources().getQuantityString(R.plurals.files_selected, size, size);
    }

    private void navigateUp() {
        mHistory.current().setPosition(mDirectoryFragment.getPosition());
        mHistory.up();
        reloadFragments();
    }

    private void navigateBack() {
        mHistory.current().setPosition(mDirectoryFragment.getPosition());
        mHistory.back();
        reloadFragments();
    }

    private void navigateToPath(@NonNull String path) {
        mHistory.current().setPosition(mDirectoryFragment.getPosition());
        mHistory.addPath(path);
        reloadFragments();
    }

    private void reloadFragments() {
        PathVisit pathVisit = mHistory.current();
        mDirectoryFragment.loadPath(pathVisit.getPath(), pathVisit.getPosition());
        mBreadcrumbsFragment.loadPath(pathVisit.getPath());
        mDrawerFragment.loadPath(pathVisit.getPath());
    }
}
