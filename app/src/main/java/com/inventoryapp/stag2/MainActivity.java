package com.inventoryapp.stag2;
import com.inventoryapp.stag2.data.ProductsContract;
import com.inventoryapp.stag2.data.ProductsContract.ProductsTable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>,OnClickListener{
	private ListView listView;
	private MyListAdapter mAdapter;
	private final String TAG="MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setTitle(R.string.main_activity_label);
		//setting up the listView and its adapter
		listView=(ListView) findViewById(R.id.mlistView);
		mAdapter=new MyListAdapter(this, null);
		listView.setAdapter(mAdapter);
		listView.setEmptyView(findViewById(R.id.no_data_view));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
				Log.v(TAG,"item clicked !");

				//we satrt the details activity 
				Intent i=new Intent(getApplicationContext(),DetailsActivity.class);
				i.putExtra(ProductsContract.KEY_SELLECTED_ITEM,id);
				startActivity(i);
				
			}
		});
		//initilising the loader
		getLoaderManager().initLoader(0, null, this).forceLoad();;
		//setting up the buttons
		findViewById(R.id.add_btn).setOnClickListener(this);
		findViewById(R.id.no_data_add_p_btn).setOnClickListener(this);
		
	
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri=ProductsContract.CONTENT_URI;
		Log.v(TAG,"uri is "+uri);
		String[] projection={
				ProductsTable.COL_PRODUCT_ID,
				ProductsTable.COL_PRODUCT_NAME,
				ProductsTable.COL_PRODUCT_QUANTITY,
				ProductsTable.COL_PRODUCT_PRICE
		};
		return new CursorLoader(this,uri, projection, null,null, ProductsContract.ProductsTable.COL_PRODUCT_NAME+" ASC");
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		switch (id) {
		case R.id.menu_item_delet_all:
			//delete everything
			showDeleteAllDialog();
			return true;
		case R.id.menu_item_inser_dummy_prod:
			insertDummyProduct();
			return true;
			default :
			    return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.v(TAG,"finished loading "+cursor.getCount()+" items found !");
		mAdapter.swapCursor(cursor);
		
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

		mAdapter.swapCursor(null);
		
	}
	
	
	@Override
	public void onClick(View v) {
		int id=v.getId();
		if(id==R.id.add_btn||id==R.id.no_data_add_p_btn){
			Intent i=new Intent(this,Add_prod_Activity.class);
			startActivity(i);
		}
	}	
			
	private void showDeleteAllDialog() {
	    
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.delete_all_dailog_msg);
	    builder.setPositiveButton(R.string.delete_all_confirm, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//let's delete everything
				deleteAllProducts();
				if(dialog!=null)
					dialog.dismiss();
			}
		});
	    builder.setNegativeButton(R.string.delete_all_cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	        	//let's cancel
	        	if (dialog != null) {
	                dialog.dismiss();
	            }
	        }
	    });

	    // Create and show the AlertDialog
	    AlertDialog alertDialog = builder.create();
	    alertDialog.show();
	}
	//delets all the products 
	private void deleteAllProducts(){
		getContentResolver().delete(ProductsContract.CONTENT_URI,null, null);
	}
	//inserts fake product to the database
	private void insertDummyProduct(){
		ContentValues fakeValues=new ContentValues();
		fakeValues.put(ProductsTable.COL_PRODUCT_NAME, getString(R.string.fake_p_name));
		fakeValues.put(ProductsTable.COL_PRODUCT_PRICE,Double.valueOf(getString(R.string.fake_price)));
		fakeValues.put(ProductsTable.COL_PRODUCT_QUANTITY, Long.valueOf(getString(R.string.fake_quantity)));
		fakeValues.put(ProductsTable.COL_PRODUCT_SUPPLIER_NAME, getString(R.string.fake_s_name));
		fakeValues.put(ProductsTable.COL_PRODUCT_SUPPLIER_PHONE, getString(R.string.fake_s_phone));
		getContentResolver().insert(ProductsContract.CONTENT_URI, fakeValues);
	}
}
