package com.sadinasib.inventory.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.product_name_text);
        TextView tvPrice = (TextView) view.findViewById(R.id.product_price_text);
        TextView tvAmount = (TextView) view.findViewById(R.id.product_stock_count_text);
        ImageView ivImage = (ImageView) view.findViewById(R.id.product_image_view);

        String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PRICE));
        String amount = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_AMOUNT));
        int imageId = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_IMAGE_ID));

        tvName.setText(name);
        tvPrice.setText(String.format("Price: %s", price));
        tvAmount.setText(String.format("In stock: %s", amount));
        ivImage.setImageResource(imageId);
    }
}
