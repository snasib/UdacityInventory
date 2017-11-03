package com.sadinasib.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.sadinasib.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by sadin on 03-Nov-17.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {
    private static final String TAG = InventoryDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Inventory.db";

    private static final String SQL_CREATE_STOCK_TABLE =
            "CREATE TABLE "
                    + InventoryEntry.TABLE_NAME + " ("
                    + InventoryEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                    + InventoryEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                    + InventoryEntry.COLUMN_PRODUCT_AMOUNT + " INTEGER NOT NULL DEFAULT 0, "
                    + InventoryEntry.COLUMN_PRODUCT_IMAGE_ID + " INTEGER NOT NULL);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + InventoryEntry.TABLE_NAME + ";";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: db");
        db.execSQL(SQL_CREATE_STOCK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: db");
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onDowngrade: db");
        onUpgrade(db, oldVersion, newVersion);
    }
}
