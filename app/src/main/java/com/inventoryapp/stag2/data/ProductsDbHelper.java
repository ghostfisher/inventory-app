package com.inventoryapp.stag2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductsDbHelper extends SQLiteOpenHelper {

	public ProductsDbHelper(Context context) {
		super(context,ProductsContract.DB_NAME,null,ProductsContract.DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		//we create our table
		db.execSQL(ProductsContract.ProductsTable.CREATION_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		//we check if the database has changed then we change it otherwise we do nothing !
		if(newV>oldV){
			db.execSQL(ProductsContract.ProductsTable.DELETATION_QUERY);
			db.execSQL(ProductsContract.ProductsTable.CREATION_QUERY);
		}
		
	}

}
