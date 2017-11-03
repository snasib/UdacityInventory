package com.sadinasib.inventory;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.sadinasib.inventory.data.InventoryContract;
import com.sadinasib.inventory.data.InventoryContract.InventoryEntry;

import butterknife.BindView;

public class EditorActivity extends AppCompatActivity {
    private static final String TAG = EditorActivity.class.getSimpleName();
    private EditText mNameEdit;
    private EditText mPriceEdit;
    private EditText mCountEdit;
    private EditText mRestockEdit;

    private Uri mCurrentProductUri;
    private boolean mProductHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEdit = (EditText) findViewById(R.id.edit_product_name);
        mPriceEdit = (EditText) findViewById(R.id.edit_product_price);
        mCountEdit = (EditText) findViewById(R.id.edit_product_count);
        mRestockEdit = (EditText) findViewById(R.id.edit_product_restock);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_add_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_edit_product));
            updateUI();
        }
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mProductHasChanged = true;
            }
        };
        mNameEdit.addTextChangedListener(textWatcher);
        mPriceEdit.addTextChangedListener(textWatcher);
        mRestockEdit.addTextChangedListener(textWatcher);
    }

    private void updateUI() {
        Log.i(TAG, "updateUI");
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_AMOUNT};

        Cursor cursor = getContentResolver().query(mCurrentProductUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            mNameEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_NAME)));
            mPriceEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PRICE)));
            mCountEdit.setText(cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_AMOUNT)));
        }
    }

    private void showUnsavedChangesDialog() {
        Log.i(TAG, "showUnsavedChangesDialog");
        new AlertDialog.Builder(this)
                .setMessage(R.string.unsaved_changes_dialog_msg)
                .setPositiveButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                })
                .show();
    }

    private void saveProduct() {
        Log.i(TAG, "savePet: ");
        String nameString = mNameEdit.getText().toString().trim();
        String priceString = mPriceEdit.getText().toString().trim();
        String countString = mCountEdit.getText().toString().trim();
        if (TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(priceString)
                && TextUtils.isEmpty(countString)) {
            Toast.makeText(this, getString(R.string.editor_acitivity_no_data_entered), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.editor_activity_need_name), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_PRODUCT_AMOUNT, countString);



        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (mProductHasChanged) {
                    showUnsavedChangesDialog();
                } else {
                    mProductHasChanged = false;
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.editor_activity_are_you_sure_to_delete)
                .setPositiveButton(getString(R.string.editor_activity_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProduct();
                        finish();
                        return;
                    }
                })
                .setNegativeButton(getString(R.string.editor_activity_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void deleteProduct() {
        int id = getContentResolver().delete(mCurrentProductUri, null, null);
    }
}
