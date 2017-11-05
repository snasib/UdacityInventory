package com.sadinasib.inventory.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sadinasib.inventory.CatalogActivity;
import com.sadinasib.inventory.R;
import com.sadinasib.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by sadin on 03-Nov-17.
 */

public class InventoryCursorAdapter extends CursorAdapter {
    private static final String TAG = InventoryCursorAdapter.class.getSimpleName();
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_items, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final long row_id = cursor.getLong(cursor.getColumnIndex(InventoryEntry._ID));

        TextView tvName = (TextView) view.findViewById(R.id.product_name_text);
        TextView tvPrice = (TextView) view.findViewById(R.id.product_price_text);
        TextView tvAmount = (TextView) view.findViewById(R.id.product_stock_count_text);

        final String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PRICE));
        String amount = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_AMOUNT));

        tvName.setText(name);
        tvPrice.setText(String.format("Price: %s", price));
        tvAmount.setText(String.format("In stock: %s", amount));


        Button button = (Button) view.findViewById(R.id.product_sale_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellOneItem(context, row_id);
            }
        });
    }

    private void sellOneItem(Context context, long id) {
        String[] projection = {InventoryEntry.COLUMN_PRODUCT_AMOUNT};
        Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
        Integer val = null;

        Cursor cursor = context.getContentResolver().query(currentProductUri,
                projection,
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            val = Integer.parseInt(cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_AMOUNT)));
        }
        if (val != null && val >= 1) {
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_AMOUNT, --val);
            context.getContentResolver().update(currentProductUri, values, null, null);
        } else {
            Toast.makeText(context, "No more such product", Toast.LENGTH_SHORT).show();
        }
    }
}
