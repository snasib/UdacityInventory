package com.sadinasib.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sadin on 03-Nov-17.
 */

public final class InventoryContract {
    public static final String CONTENT_AUTHORITY = "com.sadinasib.inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_STOCK = "stock";

    private InventoryContract() {

    }

    public static class InventoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "stock";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_AMOUNT = "amount";
        public static final String COLUMN_PRODUCT_IMAGE_ID = "image_id";

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;
    }

}
