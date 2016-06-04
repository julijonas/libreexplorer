/*
 * Copyright 2016 Julijonas Kikutis
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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import lt.kikutis.libreexplorer.PathUtils;
import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.connection.ConnectionManager;

public class EditorActivity extends AppCompatActivity {

    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_FILE_NAME = "file_name";

    private Toolbar mToolbar;
    private EditText mEditTextContent;
    private EditText mEditTextFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mEditTextContent = (EditText) findViewById(R.id.edittext_content);
        mEditTextFileName = (EditText) findViewById(R.id.edittext_file_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                String fileName = getIntent().getStringExtra(EXTRA_FILE_NAME);
                String location = getIntent().getStringExtra(EXTRA_LOCATION);
                if (fileName != null) {
                    ConnectionManager.getInstance().getLocalConnection().write(fileName, getContent());
                } else if (location != null) {
                    ConnectionManager.getInstance().getLocalConnection().write(
                            PathUtils.getCombinedPath(getFileName(), location), getContent());
                }
                break;
            case R.id.action_save_as:

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private String getContent() {
        return mEditTextContent.getText().toString();
    }

    private String getFileName() {
        return mEditTextFileName.getText().toString();
    }
}
