package com.inventoryapp.stag2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inventoryapp.stag2.data.ProductsContract;
import com.inventoryapp.stag2.data.ProductsContract.ProductsTable;

public class Add_prod_Activity extends AppCompatActivity implements OnClickListener{
	EditText name,price,quantity,supName,supPhone;
	Button saveBtn,cancelBtn;
	private boolean isFormChanged=false;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.add_item_layout);
			setTitle(R.string.add_activity_l);
			//getting refrence to views
			name=(EditText) findViewById(R.id.add_prod_name_et_v);
			price=(EditText) findViewById(R.id.add_prod_price_et_v);
			quantity=(EditText) findViewById(R.id.add_prod_q_et_v);
			supName=(EditText) findViewById(R.id.add_prod_s_name_et_v);
			supPhone=(EditText) findViewById(R.id.add_prod_s_phone_et_v);
			
			saveBtn=(Button) findViewById(R.id.add_save_btn);
			cancelBtn=(Button) findViewById(R.id.add_cancel_btn);
			saveBtn.setOnClickListener(this);
			cancelBtn.setOnClickListener(this);
			//listening to the form change
			View.OnTouchListener changeListener=new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					isFormChanged=true;
					return false;
				}
			};
			
			name.setOnTouchListener(changeListener);
			price.setOnTouchListener(changeListener);
			quantity.setOnTouchListener(changeListener);
			supName.setOnTouchListener(changeListener);
			supPhone.setOnTouchListener(changeListener);
		}
		@Override
		public void onClick(View v) {
			int id=v.getId();
			if(id==R.id.add_cancel_btn){
				showUnsavedChangesDialog();			}
			if(id==R.id.add_save_btn){
				//save the data
				ContentValues values=new ContentValues();
				values.put(ProductsTable.COL_PRODUCT_NAME, name.getText().toString());
				values.put(ProductsTable.COL_PRODUCT_PRICE, price.getText().toString());
				values.put(ProductsTable.COL_PRODUCT_QUANTITY,quantity.getText().toString());
				values.put(ProductsTable.COL_PRODUCT_SUPPLIER_NAME, supName.getText().toString());
				values.put(ProductsTable.COL_PRODUCT_SUPPLIER_PHONE, supPhone.getText().toString());
				try{
					getContentResolver().insert(ProductsContract.CONTENT_URI, values);
					Toast.makeText(this,getString(R.string.item_saved) , Toast.LENGTH_SHORT).show();
					this.finish();
				}catch(IllegalArgumentException e){
					//if on of the fields contains invalide info a toast will be displayed explayning how to fill the form
					e.printStackTrace();
					Toast.makeText(this, getString(R.string.invalide_info_msg), Toast.LENGTH_LONG).show();
				}
			}
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
		/*if the up back button is clicked and the form data ish changed then we have to ask the user if he realy 	wants to ignore the changes*/
			if(isFormChanged){
			showUnsavedChangesDialog();
			return true;}
			else {
				finish();
				return true;}
			
			default :
			    return super.onOptionsItemSelected(item);
		}
	}

		
		private void showUnsavedChangesDialog() {
		    
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage(R.string.unsaved_changes_dialog_msg);
		    builder.setPositiveButton(R.string.cancel_editing,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//if positive button is clicked we close the activity
					finish();
					
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
