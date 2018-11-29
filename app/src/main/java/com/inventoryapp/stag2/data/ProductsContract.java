package com.inventoryapp.stag2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductsContract {
	//clicked item id key
	public static final String KEY_SELLECTED_ITEM="sellected_item";
	public static final long NO_ITEM_SELLECTED=-1;

	//data base informations
	public static final String DB_NAME="products.db";
	public static final int DB_VERSION=1;
	//uris of the content provider
	 public static final String CONTENT_AUTHORITY = "com.inventory.products";
	 public static final Uri BASE_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
	 public static final String PATH_PRODUCTS = "products";
	 public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_URI,PATH_PRODUCTS);
	//the inner for our table constants
	public static final class ProductsTable{
		//table name
		public static final String TABLE_NAME="productsTable";
		//table columns
		public static final String COL_PRODUCT_ID="_id";
		public static final String COL_PRODUCT_NAME="prod_name";
		public static final String COL_PRODUCT_PRICE="prod_price";
		public static final String COL_PRODUCT_QUANTITY="prod_quantity";
		public static final String COL_PRODUCT_SUPPLIER_NAME="prod_sup_name";
		public static final String COL_PRODUCT_SUPPLIER_PHONE="prod_sup_phone";
		//some querys to create and drop the table
		public static final String CREATION_QUERY="CREATE TABLE "
												+TABLE_NAME+"("
												+COL_PRODUCT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
												+COL_PRODUCT_NAME+" TEXT NOT NULL,"
												+COL_PRODUCT_PRICE+" REAL NOT NULL,"
												+COL_PRODUCT_QUANTITY+ " INTEGER NOT NULL DEFAULT 0,"
												+COL_PRODUCT_SUPPLIER_NAME+ " TEXT NOT NULL DEFAULT 'UNKNOWN',"
												+COL_PRODUCT_SUPPLIER_PHONE+ " TEXT NOT NULL DEFAULT UNKNOWN"
												+");";
		public static final String DELETATION_QUERY="DROP TABLE "+TABLE_NAME+";";
	}
	//products provider Uri constants
	public static final class ProductsEntry implements BaseColumns {
		/**
         * The MIME type of the {@link #CONTENT_URI} for a list of product.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

	}
}
