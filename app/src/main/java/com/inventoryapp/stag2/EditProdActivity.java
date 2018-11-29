package com.inventoryapp.stag2;

import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inventoryapp.stag2.data.ProductsContract;
import com.inventoryapp.stag2.data.ProductsContract.ProductsTable;

public class EditProdActivity extends AppCompatActivity implements View.OnClickListener,LoaderCallbacks<Cursor>{
	EditText name,price,quantity,supName,supPhone;
	Button saveBtn,cancelBtn;
	private boolean isFormChanged=false;
	long sellectedItemId=0;
	private static final String TAG=EditProdActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_item_layout);
		setTitle(R.string.edit_activity_l);
		sellectedItemId=getIntent().getLongExtra(ProductsContract.KEY_SELLECTED_ITEM,ProductsContract.NO_ITEM_SELLECTED);
		if(sellectedItemId==ProductsContract.NO_ITEM_SELLECTED){
			Log.v(TAG,"no item sellected !");
			finish();
		}
		Log.v(TAG,"sellected item has id "+sellectedItemId);
		
		///setting up the views
		name=(EditText) findViewById(R.id.edit_prod_name_tv_v);
		price=(EditText) findViewById(R.id.edit_prod_price_tv_v);
		quantity=(EditText) findViewById(R.id.edit_prod_q_tv_v);
		supName=(EditText) findViewById(R.id.edit_prod_s_name_et_v);
		supPhone=(EditText) findViewById(R.id.edit_prod_s_phone_et_v);
		
		saveBtn=(Button) findViewById(R.id.edit_save_btn);
		cancelBtn=(Button) findViewById(R.id.edit_cancel_btn);
		saveBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		//listening to data change
		View.OnTouchListener touchListener=new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					isFormChanged=true;
					return false;
				}
			};
		name.setOnTouchListener(touchListener);
		price.setOnTouchListener(touchListener);
		quantity.setOnTouchListener(touchListener);
		supName.setOnTouchListener(touchListener);
		supPhone.setOnTouchListener(touchListener);
		//initlising ower loader
		getLoaderManager().initLoader(0, null,this);
	}
	//handling clicks on ower buttons
	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.edit_save_btn){//save the changes
			ContentValues values=new ContentValues();
			values.put(ProductsTable.COL_PRODUCT_NAME, name.getText().toString());
			values.put(ProductsTable.COL_PRODUCT_PRICE, price.getText().toString());
			values.put(ProductsTable.COL_PRODUCT_QUANTITY,quantity.getText().toString());
			values.put(ProductsTable.COL_PRODUCT_SUPPLIER_NAME, supName.getText().toString());
			values.put(ProductsTable.COL_PRODUCT_SUPPLIER_PHONE, supPhone.getText().toString());
			try{
				getContentResolver().update(ContentUris.withAppendedId(ProductsContract.CONTENT_URI,sellectedItemId),
						values,
						ProductsTable.COL_PRODUCT_ID+"=?",
						new String[]{String.valueOf(sellectedItemId)});
				Toast.makeText(this,getString(R.string.item_saved) , Toast.LENGTH_SHORT).show();
				this.finish();
			}catch(IllegalArgumentException e){
				//if on of the fields contains invalide info a toast will be displayed explayning how to fill the form
				e.printStackTrace();
				Toast.makeText(this, getString(R.string.invalide_info_msg), Toast.LENGTH_LONG).show();
			}
			
			
		}
		if(id==R.id.edit_cancel_btn&& isFormChanged){//prompt if he realy want to cancel
			if(isFormChanged){
				showUnsavedChangesDialog();
			}
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		return new CursorLoader(this, ContentUris.withAppendedId(ProductsContract.CONTENT_URI, sellectedItemId), null, null, null, null);
	}
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		Log.v(TAG,"cursor has "+c.getCount()+" items");
		//filling the fields with loaded data
		if(c.moveToFirst()){
			String pName=c.getString(c.getColumnIndex(ProductsTable.COL_PRODUCT_NAME));
			name.setText(pName);
			setTitle(getString(R.string.edit_activity_l)+pName);
			price.setText(String.valueOf(c.getDouble(c.getColumnIndex(ProductsTable.COL_PRODUCT_PRICE))));
			quantity.setText(String.valueOf(c.getLong(c.getColumnIndex(ProductsTable.COL_PRODUCT_QUANTITY))));
			supName.setText(c.getString(c.getColumnIndex(ProductsTable.COL_PRODUCT_SUPPLIER_NAME)));
			supPhone.setText(c.getString(c.getColumnIndex(ProductsTable.COL_PRODUCT_SUPPLIER_PHONE)));
		}else Log.v(TAG,"cursor is empty !");

	}
	
	@Override
	public void onBackPressed() {
		if(isFormChanged){
			showUnsavedChangesDialog();
		}else{
			finish();
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch (id) {
		case android.R.id.home:
			if(isFormChanged){
				showUnsavedChangesDialog();
			}
			else{
				finish();
			}
			return true;
		default :
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		//resitting ower fields
		name.setText(null);
		price.setText(null);
		quantity.setText(null);
		supName.setText(null);
		supPhone.setText(null);
	}
	
	//shows a dailog and if the user cliccks on the user clicks on the positive button then the activity will close without saving anything 
	//otherwise the dailog will be canceled 
	private void showUnsavedChangesDialog() {
	    
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.unsaved_changes_dialog_msg);
	    builder.setPositiveButton(R.string.cancel_editing, new DialogInterface.OnClickListener() {
			
			@Override
	        public void onClick(DialogInterface dialog, int id) {
				finish();
				dialog.dismiss();
			}
		});
	    builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	        	//if keep editing is clicked we hide the dialog
	        	if (dialog != null) {
	                dialog.dismiss();
	            }
	        }
	    });

	    // Create and show the AlertDialog
	    AlertDialog alertDialog = builder.create();
	    alertDialog.show();
	}
}
