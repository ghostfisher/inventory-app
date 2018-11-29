package com.inventoryapp.stag2;

import com.inventoryapp.stag2.data.ProductsContract;
import com.inventoryapp.stag2.data.ProductsContract.ProductsTable;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyListAdapter extends CursorAdapter implements OnClickListener{
	Activity activity;
	private static final String TAG=MyListAdapter.class.getSimpleName();
	@SuppressWarnings("deprecation")
	public MyListAdapter(Activity activity, Cursor c) {
		super(activity, c);
		this.activity=activity;
	}

	@Override
	public void bindView(View v, Context context, Cursor cursor) {
		ViewHolder holder=(ViewHolder) v.getTag();
		holder.titleTv.setText(cursor.getString(cursor.getColumnIndex(ProductsContract.ProductsTable.COL_PRODUCT_NAME)));
		holder.priceTv.setText(cursor.getString(cursor.getColumnIndex(ProductsContract.ProductsTable.COL_PRODUCT_PRICE))+" "+activity.getString(R.string.currency_symbole));
		long q=cursor.getLong(cursor.getColumnIndex(ProductsContract.ProductsTable.COL_PRODUCT_QUANTITY));
		holder.quantityTv.setText(q+" "+activity.getString(R.string.items_word));
		//we put the id of the product inside the button so when it's clicked we will know to which product it belongs
		holder.saleBtn.setTag(cursor.getLong(cursor.getColumnIndex(ProductsContract.ProductsTable.COL_PRODUCT_ID)));
		holder.saleBtn.setTag(R.integer.KEY_QUANTITY,q);
		
		}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		//we inflate the layout from the xml and save refrences to a holder object
		View v=activity.getLayoutInflater().inflate(R.layout.list_item,null);
		ViewHolder holder=new ViewHolder();
		holder.titleTv=(TextView) v.findViewById(R.id.item_prod_name);
		holder.quantityTv=(TextView) v.findViewById(R.id.item_prod_q);
		holder.priceTv=(TextView) v.findViewById(R.id.item_prod_price);
		holder.saleBtn=(Button) v.findViewById(R.id.item_sale_btn);
		holder.saleBtn.setOnClickListener(this);
		v.setTag(holder);
		
		return v;
	}
	private class ViewHolder{
		public ViewHolder() {
		}
		public TextView titleTv,priceTv,quantityTv;
		public Button saleBtn;
	}
	@Override
	public void onClick(View v) {
		long id=Long.valueOf(v.getTag().toString());
		Log.v(TAG,"selected item to sale has id "+id);
		ContentValues values=new ContentValues();
		
		long quantity=(Long)v.getTag(R.integer.KEY_QUANTITY);
		//if the quantity is not a zero we sale otherwise we tell the useer that there is no enough products
		String responeMsg=null;
		if(quantity>0){
				quantity--;
				values.put(ProductsTable.COL_PRODUCT_QUANTITY,quantity);
				int rowsAffected=activity.getContentResolver().update(
						ContentUris.withAppendedId(ProductsContract.CONTENT_URI, id),
						values,
						ProductsTable.COL_PRODUCT_ID +"==?",
						new String[]{String.valueOf(id)} 
				);
				if(rowsAffected>0)
					responeMsg=activity.getString(R.string.product_sold);
				else responeMsg=activity.getString(R.string.not_enough_products);
				}
		else{
			responeMsg=activity.getString(R.string.not_enough_products);
	
		}
		Toast.makeText(activity,responeMsg,Toast.LENGTH_SHORT).show();
	}
	

}
