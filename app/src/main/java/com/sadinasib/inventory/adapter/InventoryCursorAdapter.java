package com.sadinasib.inventory.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sadinasib.inventory.R;
import com.sadinasib.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by sadin on 03-Nov-17.
 */

public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_items, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.product_name_text);
        TextView tvPrice = (TextView) view.findViewById(R.id.product_price_text);
        TextView tvAmount = (TextView) view.findViewById(R.id.product_stock_count_text);

        final String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PRICE));
        String amount = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_AMOUNT));

        tvName.setText(name);
        tvPrice.setText(String.format("Price: %s", price));
        tvAmount.setText(String.format("In stock: %s", amount));
    }
}
