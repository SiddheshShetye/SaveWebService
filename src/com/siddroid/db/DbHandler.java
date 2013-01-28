/*
 * Copyright (C) 2010 The Android Open Source Project 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package com.siddroid.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The Class DbHandler.
 * @author Siddhesh
 */
public final class DbHandler extends SQLiteOpenHelper{

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
	
	public static final String IS_RESPONSE_CHECK_MADE="response_check";
	
	public static final String RESPONSE_KEY="response_key";
	
	public static final String RESPONSE_KEY_VALUE="response_value";
	
	/** The Constant DATABASE_CREATE_OFFLINE. */
	private static final String DATABASE_CREATE_OFFLINE = "create table "+TABLE_NAME+" ("+KEY_ROWID+" integer primary key autoincrement,"+SERVICE_URL+" text, "+SERVICE_REQ+" text, "+REQ_TYPE+" INT, "+IS_RESPONSE_CHECK_MADE+" INT, "+RESPONSE_KEY+" TEXT, "+RESPONSE_KEY_VALUE+" TEXT);";

	/**
	 * Instantiates a new db handler.
	 *
	 * @param context the context
	 */
	private DbHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * New instance.
	 * returns an instance of <code>DbHandler</code>
	 * @param context the context
	 * @return the db handler
	 * @author Siddhesh
	 */
	public static DbHandler newInstance(Context context)
	{
		if(sHandlerdb==null)
			return sHandlerdb=new DbHandler(context);
		else
			return sHandlerdb;
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


	/**
	 * Delete by id.
	 * This function deletes record using its id
	 * @param id the id of record in table
	 * @return true, if successful
	 * @author Siddhesh
	 */
	public boolean deleteById(String id) {
		String[] whereArgs = new String[] { id };
		String whereClause = KEY_ROWID + "=?";
		/*delete record*/
		return getWritableDatabase().delete(TABLE_NAME, whereClause, whereArgs) > 0 ? true: false;
	}

	/**
	 * Insert.
	 * This function inserts record in table
	 * @param url the url of webservice
	 * @param req the request
	 * @param type the type of request
	 * @return a non zero value if inserted successfully
	 * @author Siddhesh
	 */
	public long insert(String url,String req,int type,int isCheckMade,String responseKey,String responseValue){
		ContentValues cv=new ContentValues();
		cv.put(SERVICE_URL, url);
		cv.put(SERVICE_REQ, req);
		cv.put(REQ_TYPE, type);
		cv.put(IS_RESPONSE_CHECK_MADE, isCheckMade);
		cv.put(RESPONSE_KEY, responseKey);
		cv.put(RESPONSE_KEY_VALUE, responseValue);
		/*insert record*/
		return getWritableDatabase().insert(TABLE_NAME, "0.0", cv);
	}

	/**
	 * Select all.
	 * This functions fetches all records from table
	 * @return the cursor containing all records
	 * @author Siddhesh
	 */
	public Cursor selectAll(){
		/*select records*/
		return getWritableDatabase().rawQuery("select * from " + TABLE_NAME,null);
	}


}
