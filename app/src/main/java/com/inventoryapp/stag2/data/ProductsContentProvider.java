package com.inventoryapp.stag2.data;


import com.inventoryapp.stag2.data.ProductsContract.ProductsTable;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ProductsContentProvider extends ContentProvider {
	private static final String TAG="mContentProvider"; 
	private ProductsDbHelper mDbHelper;
	private static final int PRODUCTS=100;
	private static final int PRODUCT_ID=101;
	private static UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
	static{
		mUriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY,ProductsContract.PATH_PRODUCTS,PRODUCTS);
		mUriMatcher.addURI(ProductsContract.CONTENT_AUTHORITY,ProductsContract.PATH_PRODUCTS+"/#", PRODUCT_ID);
		Log.v(TAG,"static code Executed!");
		
	}
	
	@Override
	public boolean onCreate() {
		mDbHelper=new ProductsDbHelper(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs ) {
		int response=-1;
		
		switch(mUriMatcher.match(uri)){
		case PRODUCT_ID:
			response=mDbHelper.getWritableDatabase().delete(ProductsContract.ProductsTable.TABLE_NAME,ProductsContract.ProductsTable.COL_PRODUCT_ID+"==?",new String[]{uri.getLastPathSegment()});
			;break;
		case PRODUCTS:
			response=mDbHelper.getWritableDatabase().delete(ProductsContract.ProductsTable.TABLE_NAME,selection,selectionArgs);
			;break;
		default :throw new IllegalArgumentException("uri not supported ");
		}
		getContext().getContentResolver().notifyChange(uri,null);
		return response;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case PRODUCT_ID:return ProductsContract.ProductsEntry.CONTENT_ITEM_TYPE;
		case PRODUCTS:return ProductsContract.ProductsEntry.CONTENT_LIST_TYPE;
		default:throw new IllegalStateException("unknown URI:"+uri.toString());
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri resultUri=null;
		if(!(mUriMatcher.match(uri)==PRODUCTS))throw new IllegalArgumentException("invalide Uri: "+uri.toString());
		//if the data is valide then we insert it otherwise we throw an exception
		if(isInsertDataValide(values))resultUri=ContentUris.withAppendedId(uri,mDbHelper.getWritableDatabase().insert(ProductsContract.ProductsTable.TABLE_NAME, null, values));
		getContext().getContentResolver().notifyChange(uri, null);
		return resultUri;
	}

	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
		Cursor cursor=null;
		switch (mUriMatcher.match(uri)) {
		case PRODUCT_ID:
			Log.v(TAG,"matched product id");
			cursor=mDbHelper.getReadableDatabase().query(ProductsContract.ProductsTable.TABLE_NAME,projection, ProductsContract.ProductsTable.COL_PRODUCT_ID+"==?", new String[]{uri.getLastPathSegment()}, null, null, null);
			break;
		case PRODUCTS:
			Log.v(TAG,"matched products");
			cursor=mDbHelper.getReadableDatabase().query(ProductsContract.ProductsTable.TABLE_NAME,projection, selection, selectionArgs, null, null, orderBy);
			break;
		default:throw new IllegalArgumentException("invalide uri: "+uri.toString());
			
		}
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int response=-1;
		//we can only update a single entry
		switch(mUriMatcher.match(uri)){
		case PRODUCT_ID:
			if(isUpdateDataValide(values))
				response=mDbHelper.getWritableDatabase().update(ProductsTable.TABLE_NAME,
						values,
						ProductsTable.COL_PRODUCT_ID+"==?" ,new String[]{uri.getLastPathSegment()});
			;break;
		default:throw new IllegalArgumentException("invalide uri:"+uri.toString());
		}
		getContext().getContentResolver().notifyChange(uri,null);
		return response;
	}
	
	//the method return either true if all the data is oki otherwise it throws an exception which will stop it's execution
	private boolean isUpdateDataValide(ContentValues values){
		if(values.containsKey(ProductsContract.ProductsTable.COL_PRODUCT_NAME)){
		String productName=values.getAsString(ProductsContract.ProductsTable.COL_PRODUCT_NAME);
		if(productName.equals("")||productName==null)throw new IllegalArgumentException("please set a name for your product!");
		}
		//checking product price
		if(values.containsKey(ProductsContract.ProductsTable.COL_PRODUCT_PRICE)){
		double price =values.getAsDouble(ProductsContract.ProductsTable.COL_PRODUCT_PRICE);
		if(price<0.0d)throw new IllegalArgumentException("the product price must never be less than 0 !");
		}
		//cheking the quantity
		if(values.containsKey(ProductsContract.ProductsTable.COL_PRODUCT_QUANTITY)){
			long prodQuantity=values.getAsLong(ProductsContract.ProductsTable.COL_PRODUCT_QUANTITY);
			if(prodQuantity<0)throw new IllegalArgumentException("the product's quantity must never be less than 0");
		}
		//cheking the supplier name
		if(values.containsKey(ProductsContract.ProductsTable.COL_PRODUCT_SUPPLIER_NAME)&&values.getAsString(ProductsContract.ProductsTable.COL_PRODUCT_SUPPLIER_NAME).length()==0)
			throw new IllegalArgumentException("please set the supplier name !");
		//cheking the suplier phone number
		if(values.containsKey(ProductsContract.ProductsTable.COL_PRODUCT_SUPPLIER_PHONE)&&values.getAsString(ProductsContract.ProductsTable.COL_PRODUCT_SUPPLIER_PHONE).length()==0)
			throw new IllegalArgumentException("please set the supplier phone number !");
		
		return true;
	}
	
	//if the data is value to be inserted we return true otherwise we throw an exception 
	private boolean isInsertDataValide(ContentValues values){
		//checking the name of the product
		if(!values.containsKey(ProductsContract.ProductsTable.COL_PRODUCT_NAME)||values.getAsString(ProductsContract.ProductsTable.COL_PRODUCT_NAME)==null)
			throw new IllegalArgumentException("please set a name for your product!");
		String productName=values.getAsString(ProductsContract.ProductsTable.COL_PRODUCT_NAME);
		if(TextUtils.isEmpty(productName))throw new IllegalArgumentException("please set a name for your product!");
		
		//checking product price
		if(!values.containsKey(ProductsContract.ProductsTable.COL_PRODUCT_PRICE)||values.getAsDouble(ProductsContract.ProductsTable.COL_PRODUCT_PRICE)==null)
			throw new IllegalArgumentException("please set a price for you product !");
		double price =values.getAsDouble(ProductsContract.ProductsTable.COL_PRODUCT_PRICE);
		if(price<0.0d)throw new IllegalArgumentException("the product price must never be less than 0 !");
		
		//cheking the quantity
		if(!values.containsKey(ProductsContract.ProductsTable.COL_PRODUCT_QUANTITY)||values.getAsLong(ProductsContract.ProductsTable.COL_PRODUCT_QUANTITY)==null)
			throw new IllegalArgumentException("please set a quantity for you product !");
		long prodQuantity=values.getAsLong(ProductsContract.ProductsTable.COL_PRODUCT_QUANTITY);
			if(prodQuantity<0)throw new IllegalArgumentException("the product's quantity must never be less than 0");
		
			//cheking the supplier name
		if(!values.containsKey(ProductsContract.ProductsTable.COL_PRODUCT_SUPPLIER_NAME)||TextUtils.isEmpty(values.getAsString(ProductsContract.ProductsTable.COL_PRODUCT_SUPPLIER_NAME)))
			throw new IllegalArgumentException("please set the supplier name !");
		//cheking the suplier phone number
		if(!values.containsKey(ProductsContract.ProductsTable.COL_PRODUCT_SUPPLIER_PHONE)||TextUtils.isEmpty(values.getAsString(ProductsContract.ProductsTable.COL_PRODUCT_SUPPLIER_PHONE)))
			throw new IllegalArgumentException("please set the supplier phone number !");
		
		return true;
	}
}
