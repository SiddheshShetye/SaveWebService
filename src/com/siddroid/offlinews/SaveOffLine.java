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
package com.siddroid.offlinews;

import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.google.gson.Gson;
import com.siddroid.db.DbHelper;

/**
 * The Class SaveOffLine.
 * This class saves data to be sent on webservice in local database with the web service url 
 * and service type.
 * @author Siddhesh S Shetye
 * @version 2013.2801
 * @since 1.0
 */
public class SaveOffLine {
	
	/** The db helper. */
	private DbHelper mDbHelper;
		
	/** Request Type POST_JSON. */
	private static final int POST_JSON=638;
	
	/** Request Type POST. */
	public static final int POST=211;
	
	/**Request Type GET*/
	public static final int GET=564;
	
	private static SQLiteDatabase db;
	
	/**
	 * Instantiates a new save off line.
	 *
	 * @param ctx context
	 */
	public SaveOffLine(Context ctx) {
		/*initialize database*/
		initDb(ctx);
	}

	/**
	 * Initialize database helper.
	 *
	 * @param context the context
	 * @author Siddhesh
	 */
	private void initDb(Context context){

		try {
			mDbHelper=DbHelper.getInstance(context);
			db=mDbHelper.createOrOpenDb();
		} catch (SQLiteException e) {
			// Our app fires an event spawning the db creation task...
			e.printStackTrace();
		}
		catch (Exception e) {
			// Our app fires an event spawning the db creation task...
			e.printStackTrace();
		}
	}

	/**
	 * Save service.
	 * This method is used to save request data using GSON model and 
	 * webservice URL. By default it takes the request type as JSON POST
	 *
	 * @param requestModel the request GSON model
	 * @param serviceUrl the webservice url
	 * @return returns a non zero value if successfully saved in database
	 * @author Siddhesh
	 */
	public long saveService(Object requestModel,String serviceUrl,boolean checkResponse,String keyToLookFor,String valueToLookFor){
		Log.d("CHECK", "SAVING MODEL JSON");
		int responseCheck=checkResponse?0:1;
		/*Convert GSON model in JSON String*/
		String json=new Gson().toJson(requestModel);
		/*Save URL Request and Request type in database*/
		return mDbHelper.insert(serviceUrl, json, POST_JSON,responseCheck,keyToLookFor,valueToLookFor,db);
	}

	/**
	 * Save service.
	 * This method is used to save request data using <code>Map&ltString&gt&ltStirng&gt</code> object,
	 * webservice URL and Type of request.
	 *
	 * @param requestMap the <code>Map&ltString&gt&ltStirng&gt</code> object filled with request name value paires. 
	 * @param serviceUrl the webservice url
	 * @param type the type of request. use <code>SaveOffLine.GET</code> and <code>SaveOffLine.POST</code>
	 * @return returns a non zero value is data saved successfully in database
	 * @author Siddhesh
	 */
	public long saveService(Map<String,String> requestMap,String serviceUrl,int type,boolean checkResponse,String keyToLookFor,String valueToLookFor){
		Log.d("CHECK", "CAME IN GET SAVE");
		int responseCheck=checkResponse?0:1;
		/*initialize a StringBuilder object to extract JSON string from inputstream*/
		StringBuilder req=new StringBuilder("");
		/*extract all the key value pairs from map*/
		for (Object key : requestMap.keySet()) {
			req.append(""+(String)key);
			req.append("=");
			req.append(requestMap.get(key));
			req.append(",");
		}
		/*Remove last , request*/
		int lastAmp=req.lastIndexOf(",");
		req.deleteCharAt(lastAmp);
		/*convert to String Object*/
		String reqString=req.toString();
		Log.d("CHECK", "NOW SAVING REQ");
		/*Save URL Request and Request type in database*/
		return mDbHelper.insert(serviceUrl, reqString, type,responseCheck,keyToLookFor,valueToLookFor,db);
	}

	/**
	 * Save service.
	 * This method is used to save request data using JSON String and 
	 * webservice URL. By default it takes the request type as JSON POST
	 *
	 * @param requestJson the request json string
	 * @param serviceUrl the webservice url
	 * @return returns a non zero value is data saved successfully in database
	 * @author siddhesh
	 */
	public long saveService(String requestJson,String serviceUrl,boolean checkResponse,String keyToLookFor,String valueToLookFor){
		int responseCheck=checkResponse?0:1;
		/*Save URL Request and Request type in database*/
		return mDbHelper.insert(serviceUrl, requestJson, POST_JSON,responseCheck,keyToLookFor,valueToLookFor,db);
	}
}


