package com.siddroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The Class DbHelper.
 * @author Siddhesh S Shetye
 * @version 2013.2801
 * @since 1.0
 */
public class DbHelper {
	
	/** The s context. */
	private static Context sContext;
	
	/** The s db helper. */
	private static DbHelper sDbHelper;
	
	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;
	
	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "offline.db";
	
	/** The Constant TABLE_NAME. */
	private static final String TABLE_NAME = "service_table";
	
	/** The s handlerdb. */
	private static DbHandler sHandlerdb;
	
	/** The Constant KEY_ROWID. */
	public static final String KEY_ROWID = "_id";
	
	/** The Constant SERVICE_URL. */
	public static final String SERVICE_URL = "service_url";
	
	/** The Constant SERVICE_REQ. */
	public static final String SERVICE_REQ = "request";
	
	/** The Constant REQ_TYPE. */
	public static final String REQ_TYPE = "request_type";
	
	/** The Constant IS_RESPONSE_CHECK_MADE. */
	public static final String IS_RESPONSE_CHECK_MADE="response_check";
	
	/** The Constant RESPONSE_KEY. */
	public static final String RESPONSE_KEY="response_key";
	
	/** The Constant RESPONSE_KEY_VALUE. */
	public static final String RESPONSE_KEY_VALUE="response_value";
	
	/** The Constant DATABASE_CREATE_OFFLINE. */
	private static final String DATABASE_CREATE_OFFLINE = "create table "+TABLE_NAME+" ("+KEY_ROWID+" integer primary key autoincrement,"+SERVICE_URL+" text, "+SERVICE_REQ+" text, "+REQ_TYPE+" INT, "+IS_RESPONSE_CHECK_MADE+" INT, "+RESPONSE_KEY+" TEXT, "+RESPONSE_KEY_VALUE+" TEXT);";

	/** The offline db. */
	private SQLiteDatabase offlineDb;
	
	
	/**
	 * New instance.
	 * returns an instance of <code>DbHelper</code>
	 * @param context the context
	 * @return the db helper
	 */
	public static DbHelper getInstance(Context context)
	{
		sContext=context;
		if(sDbHelper==null)
			return sDbHelper=new DbHelper();
		else
			return sDbHelper;
	}
	
	/**
	 * Instantiates a new db helper.
	 */
	private DbHelper(){}
	
	/**
	 * Creates the or open db.
	 *
	 * @return the sqlite database
	 */
	public SQLiteDatabase createOrOpenDb(){
		if(sHandlerdb==null)
		sHandlerdb=new DbHandler(sContext);
		if(offlineDb!=null && offlineDb.isOpen())
			return offlineDb;
		else
			offlineDb=sHandlerdb.getWritableDatabase();
		return offlineDb;
		
	}
	
	
	/**
	 * Close database object.
	 */
	public void close(){
		if(offlineDb!=null)
			if(offlineDb.isOpen())
				offlineDb.close();
	}
	
	
	/**
	 * Delete by id.
	 * This function deletes record using its id
	 * @param id the id of record in table
	 * @return true, if successful
	 */
	public boolean deleteById(String id,SQLiteDatabase db) {
		String[] whereArgs = new String[] { id };
		String whereClause = KEY_ROWID + "=?";
		/*delete record*/
		return db.delete(TABLE_NAME, whereClause, whereArgs) > 0 ? true: false;
	}

	/**
	 * Insert.
	 * This function inserts record in table
	 * @param url the url of webservice
	 * @param req the request
	 * @param type the type of request
	 * @return a non zero value if inserted successfully
	 */
	public long insert(String url,String req,int type,int isCheckMade,String responseKey,String responseValue,SQLiteDatabase db){
		ContentValues cv=new ContentValues();
		cv.put(SERVICE_URL, url);
		cv.put(SERVICE_REQ, req);
		cv.put(REQ_TYPE, type);
		cv.put(IS_RESPONSE_CHECK_MADE, isCheckMade);
		cv.put(RESPONSE_KEY, responseKey);
		cv.put(RESPONSE_KEY_VALUE, responseValue);
		/*insert record*/
		return db.insert(TABLE_NAME, "0.0", cv);
	}

	/**
	 * Select all.
	 * This functions fetches all records from table
	 * @return the cursor containing all records
	 */
	public Cursor selectAll(SQLiteDatabase db){
		/*select records*/
		return db.rawQuery("select * from " + TABLE_NAME,null);
	}



	
	
	
	
	/**
	 * The Class DbHandler.
	 * @author Siddhesh
	 */
	private final class DbHandler extends SQLiteOpenHelper{

		
		/**
		 * Instantiates a new db handler.
		 *
		 * @param context the context
		 */
		private DbHandler(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			/*create table*/
			db.execSQL(DATABASE_CREATE_OFFLINE);
			Log.e("CHECK", "------ created table followup");
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d("upgrading database", DATABASE_NAME);
			onCreate(db);
		}


	}


}
