package com.sadinasib.inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sadinasib.inventory.adapter.InventoryCursorAdapter;

import static com.sadinasib.inventory.data.InventoryContract.*;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = CatalogActivity.class.getSimpleName();
    private static final int INVENTORY_LOADER_ID =35;

    private InventoryCursorAdapter mCursorAdapter;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mListView = (ListView) findViewById(R.id.listView);
        View emptyView = findViewById(R.id.empty_view);
        mListView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        mListView.setAdapter(mCursorAdapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editIntent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                editIntent.setData(currentPetUri);
                startActivity(editIntent);
                return true;
            }
        });

        getLoaderManager().initLoader(INVENTORY_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_data:
                startEditIntent();
                return true;
            case R.id.action_delete_all_data:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startEditIntent() {
        Log.i(TAG, "startEditIntent");
        Intent editIntent = new Intent(CatalogActivity.this, EditorActivity.class);
        startActivity(editIntent);
    }

    private void deleteAllProducts() {
        Log.i(TAG, "deleteAllProducts");
        @SuppressLint("Recycle")
        Cursor cursor = getContentResolver().query(InventoryEntry.CONTENT_URI, new String[]{InventoryEntry._ID}, null, null, null);
        if (cursor != null && cursor.getCount() == 0) {
            Toast.makeText(this, "List is already empty", Toast.LENGTH_SHORT).show();
            return;
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Warning!")
                    .setMessage("Are you sure to delete all entries")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.i(TAG, "onCreateLoader");
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_AMOUNT,
                InventoryEntry.COLUMN_PRODUCT_IMAGE_ID};

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "onLoadFinished");
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(TAG, "onLoaderReset");
        mCursorAdapter.swapCursor(null);
    }
}
