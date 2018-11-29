package com.inventoryapp.stag2;

import com.inventoryapp.stag2.data.ProductsContract;
import com.inventoryapp.stag2.data.ProductsContract.ProductsTable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends AppCompatActivity implements OnClickListener, LoaderCallbacks<Cursor> {
	TextView name, price, quantity, supName, supPhone;
	Button orderBtn, deleteBtn, editBtn,q_plusBtn,q_minusBtn;
	private long quantityValue=0l;
	private long sellectedItemId = ProductsContract.NO_ITEM_SELLECTED;
	private static final String TAG=DetailsActivity.class.getSimpleName(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_acti_layout);
		setTitle(R.string.details_activity_l);
		sellectedItemId = getIntent().getLongExtra(ProductsContract.KEY_SELLECTED_ITEM,ProductsContract.NO_ITEM_SELLECTED);
		if(sellectedItemId==ProductsContract.NO_ITEM_SELLECTED){
			Log.v(TAG, "no item selected!");
			finish();
		};
		// getting refrence to views
		name = (TextView) findViewById(R.id.details_prod_name_tv_v);
		price = (TextView) findViewById(R.id.details_prod_price_tv_v);
		quantity = (TextView) findViewById(R.id.details_prod_q_tv_v);
		supName = (TextView) findViewById(R.id.details_prod_s_name_tv_v);
		supPhone = (TextView) findViewById(R.id.details_prod_s_phone_tv_v);

		orderBtn = (Button) findViewById(R.id.details_order_btn);
		deleteBtn = (Button) findViewById(R.id.details_delete_btn);
		editBtn = (Button) findViewById(R.id.details_edit_btn);
		q_minusBtn=(Button) findViewById(R.id.details_minusBtn);
		q_plusBtn=(Button) findViewById(R.id.details_plusBtn);
		
		// listening to clicks on buttons
		orderBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		editBtn.setOnClickListener(this);
		q_plusBtn.setOnClickListener(this);
		q_minusBtn.setOnClickListener(this);
		// setting up our loader
		Bundle b = new Bundle();
		b.putLong(ProductsContract.KEY_SELLECTED_ITEM, sellectedItemId);
		getLoaderManager().initLoader(1, b, this).forceLoad();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.details_edit_btn:// start edit activity
			Intent i = new Intent(this, EditProdActivity.class);
			i.putExtra(ProductsContract.KEY_SELLECTED_ITEM, sellectedItemId);
			startActivity(i);
			this.finish();
			break;
		case R.id.details_order_btn:
			// call the supplier
			String phoneNbr = supPhone.getText().toString();
			if (phoneNbr != null && phoneNbr != "") {
				Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNbr, null));
				try {
					startActivity(callIntent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(this, getString(R.string.calling_app_not_f_msg), Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.details_delete_btn:// delete the entry and quit
			promptUserForDeleting(name.getText().toString());
			break;
		case R.id.details_minusBtn://if minus button is clicked we substract 1 from the currunt quantity value
			if(quantityValue>0){
				ContentValues values=new ContentValues();
				values.put(ProductsTable.COL_PRODUCT_QUANTITY,(quantityValue-1));
				getContentResolver().update(ContentUris.withAppendedId(ProductsContract.CONTENT_URI, sellectedItemId),
						values,ProductsTable.COL_PRODUCT_ID+"==?",
						new String[]{String.valueOf(sellectedItemId)}
				);
			}
			break;
		case R.id.details_plusBtn://if plus button is clicked we add 1 to the currunt quantity value
			ContentValues values=new ContentValues();
			values.put(ProductsTable.COL_PRODUCT_QUANTITY,(quantityValue+1));
			getContentResolver().update(ContentUris.withAppendedId(ProductsContract.CONTENT_URI, sellectedItemId),
					values,ProductsTable.COL_PRODUCT_ID+"==?",
					new String[]{String.valueOf(sellectedItemId)}
			);
			break;
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle b) {

		return new CursorLoader(this, ContentUris.withAppendedId(ProductsContract.CONTENT_URI,
				b.getLong(ProductsContract.KEY_SELLECTED_ITEM)), null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		if(c.moveToFirst()){
			String pName=c.getString(c.getColumnIndex(ProductsTable.COL_PRODUCT_NAME));
			name.setText(pName);
			setTitle(pName);
			price.setText(c.getDouble(c.getColumnIndex(ProductsTable.COL_PRODUCT_PRICE)) + " "
					+ getString(R.string.currency_symbole));
			quantityValue=c.getLong(c.getColumnIndex(ProductsTable.COL_PRODUCT_QUANTITY));
			quantity.setText( quantityValue+ " "
					+ getString(R.string.items_word));
			supName.setText(c.getString(c.getColumnIndex(ProductsTable.COL_PRODUCT_SUPPLIER_NAME)));
			supPhone.setText(c.getString(c.getColumnIndex(ProductsTable.COL_PRODUCT_SUPPLIER_PHONE)));
		}else Log.v(TAG, "cursor empty!");
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// clearing the fields
		name.setText(null);
		price.setText(null);
		quantity.setText(null);
		supName.setText(null);
		supPhone.setText(null);
	}
	
	/**
	 * shows a dialog that prompts the user wether he wants to delete the currunt item or not 
	 * @param the selected item name
	 *
	 * */
	private void promptUserForDeleting(String itemName) {
		AlertDialog.Builder dailogBuilder = new AlertDialog.Builder(this);
		dailogBuilder.setMessage(getString(R.string.delete_msg) + " " + itemName + " ?");
		dailogBuilder.setPositiveButton(R.string.delete_confirm, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//deleting the item if possible
				int res = getContentResolver()
						.delete(ContentUris.withAppendedId(ProductsContract.CONTENT_URI, sellectedItemId), null, null);
				if (res != 0) {
					Toast.makeText(getApplicationContext(), getString(R.string.item_removed_success_msg), Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.item_removed_fail_msg), Toast.LENGTH_SHORT).show();
				}}
		});
		dailogBuilder.setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (dialog != null)
					dialog.dismiss();
			}
		});
		dailogBuilder.create().show();
	}
}
