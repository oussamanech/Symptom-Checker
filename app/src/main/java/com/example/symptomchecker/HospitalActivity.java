package com.example.symptomchecker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.symptomchecker.data.HospitalContract.HospitalEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HospitalActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the hospital data loader */
    private static final int HOSPITAL_LOADER = 0;

    /** Adapter for the ListView */
    HospitalCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HospitalActivity.this, HospitalEditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the hospital data
        ListView hospitalListView = (ListView) findViewById(R.id.list_hospital);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view_hospital);
        hospitalListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of hospital data in the Cursor.
        // There is no hospital data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new HospitalCursorAdapter(this, null);
        hospitalListView.setAdapter(mCursorAdapter);

        hospitalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(HospitalActivity.this, HospitalEditorActivity.class);

                Uri currentHospitalUri = ContentUris.withAppendedId(HospitalEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentHospitalUri);

                // Launch the {@link EditorActivity} to display the data for the current doctor.
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(HOSPITAL_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_doctor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertHospital();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllHospitals();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                HospitalEntry._ID,
                HospitalEntry.COLUMN_HOSPITAL_NAME,
                HospitalEntry.COLUMN_HOSPITAL_WORKTIME,
                HospitalEntry.COLUMN_HOSPITAL_PHONE,
                HospitalEntry.COLUMN_HOSPITAL_ADDRESS};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                HospitalEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    private void insertHospital() {
        ContentValues values = new ContentValues();
        values.put(HospitalEntry.COLUMN_HOSPITAL_NAME, "HOSPITAL KRASNODAR N07");
        values.put(HospitalEntry.COLUMN_HOSPITAL_WORKTIME, "08:00 - 22:00");
        values.put(HospitalEntry.COLUMN_HOSPITAL_PHONE, "+79875554132");
        values.put(HospitalEntry.COLUMN_HOSPITAL_ADDRESS, "ул Красная, 29");

        Uri newUri = getContentResolver().insert(HospitalEntry.CONTENT_URI, values);
    }

    private void deleteAllHospitals() {
        int rowsDeleted = getContentResolver().delete(HospitalEntry.CONTENT_URI, null, null);
        Log.v("HospitalActivity1", rowsDeleted + " rows deleted from table database");
    }

}