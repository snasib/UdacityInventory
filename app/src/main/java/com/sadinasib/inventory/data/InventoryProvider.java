package com.sadinasib.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.sadinasib.inventory.data.InventoryContract.CONTENT_AUTHORITY;
import static com.sadinasib.inventory.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_AMOUNT;
import static com.sadinasib.inventory.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME;
import static com.sadinasib.inventory.data.InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE;
import static com.sadinasib.inventory.data.InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
import static com.sadinasib.inventory.data.InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
import static com.sadinasib.inventory.data.InventoryContract.InventoryEntry.TABLE_NAME;
import static com.sadinasib.inventory.data.InventoryContract.InventoryEntry._ID;
import static com.sadinasib.inventory.data.InventoryContract.PATH_STOCK;

/**
 * Created by sadin on 03-Nov-17.
 */

public class InventoryProvider extends ContentProvider {
    private static final String TAG = InventoryProvider.class.getSimpleName();
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_STOCK, PRODUCTS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_STOCK + "/#", PRODUCT_ID);
    }

    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.i(TAG, "query " + uri);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null);
                break;
            case PRODUCT_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.i(TAG, "getType: " + uri);
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.i(TAG, "insert at: " + uri);
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertPRODUCT(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Nullable
    private Uri insertPRODUCT(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.i(TAG, "insertPRODUCT");

        String name = contentValues.getAsString(COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer price = contentValues.getAsInteger(COLUMN_PRODUCT_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Product requires price");
        }

        Integer amount = contentValues.getAsInteger(COLUMN_PRODUCT_AMOUNT);
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Product requires amount");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(TABLE_NAME, null, null);
        if (id == -1) {
            Log.e(TAG, "insertPRODUCT: Failed to insert row for" + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(TAG, "delete: " + uri);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                int id = database.delete(TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return id;
            case PRODUCT_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                id = database.delete(TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return id;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(TAG, "update: " + uri);
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.i(TAG, "updateProduct");

        if (values.containsKey(COLUMN_PRODUCT_NAME)) {
            if (values.getAsString(COLUMN_PRODUCT_NAME) == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Product requires a non-negative price");
            }
        }

        if (values.containsKey(COLUMN_PRODUCT_AMOUNT)) {
            Integer amount = values.getAsInteger(COLUMN_PRODUCT_AMOUNT);
            if (amount == null || amount < 0) {
                throw new IllegalArgumentException("Product requires a non-negative amount");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int updateId = database.update(TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateId;
    }
}
