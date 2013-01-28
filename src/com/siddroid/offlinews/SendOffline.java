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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import com.siddroid.db.DbHelper;

/**
 * The Class SendOffline.
 * This class sends data to webservice using the saved web service url, request 
 * and service type.
 * @author Siddhesh S Shetye
 * @version 2013.2801
 * @since 1.0
 */
public class SendOffline {
	
	/** The db helper. */
	private DbHelper mDbHelper;
	
	/** The c. */
	private Cursor mSelectAllCursor;
	
	/** The record id. */
	private int mRecId;
	
	/** The Request Type. */
	private int mReqType;
	
	/** Request Type POST_JSON. */
	private static final int POST_JSON=638;
	
	/** The is check made. */
	private boolean isCheckMade;
	
	/** The key. */
	private String key;
	
	/** The value. */
	private String value;
	
	/** The db. */
	private static SQLiteDatabase db;

	/**
	 * Instantiates a new send offline.
	 *
	 * @param ctx the ctx
	 */
	public SendOffline(Context ctx) {
		/*initialize database*/
		initDb(ctx);
		/*get all records in database*/
		getRecords();
	}


	/**
	 * Initialize database helper.
	 *
	 * @param context the context
	 */
	private void initDb(Context ctx){

		try {
			mDbHelper = DbHelper.getInstance(ctx);
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
	 * Gets the response object.
	 * Fetch all records from table
	 * @return the response object
	 */
	private void getRecords(){
		/*fetch all records from database and assign them to a cursor*/
		mSelectAllCursor=mDbHelper.selectAll(db);
		/*check if we have records*/
		if(mSelectAllCursor.getCount()==0){
			Log.d("CHECK", "No RECORDS IN DB");
		}else{
			/*start sending found records*/
			if(mSelectAllCursor.moveToNext())//moves cursor to next position(here on first record)
				/*call sendsavedService function to start actual sending of data on webservice*/
				sendSavedService();
		}
	}

	/**
	 * Send save service.
	 * This function takes record id and request type from cursor of current record and starts an
	 * Async to send data to web service
	 */
	public void sendSavedService(){
		mRecId=mSelectAllCursor.getInt(mSelectAllCursor.getColumnIndex(DbHelper.KEY_ROWID));//record id
		mReqType=mSelectAllCursor.getInt(mSelectAllCursor.getColumnIndex(DbHelper.REQ_TYPE));//request type
		isCheckMade=mSelectAllCursor.getInt(mSelectAllCursor.getColumnIndex(DbHelper.IS_RESPONSE_CHECK_MADE))==0?true:false;
		if(isCheckMade){
			key=mSelectAllCursor.getString(mSelectAllCursor.getColumnIndex(DbHelper.RESPONSE_KEY));
			value=mSelectAllCursor.getString(mSelectAllCursor.getColumnIndex(DbHelper.RESPONSE_KEY_VALUE));
		}
		/*pass webservice URL and request data to async*/
		new HitService().execute(mSelectAllCursor.getString(mSelectAllCursor.getColumnIndex(DbHelper.SERVICE_URL)),mSelectAllCursor.getString(mSelectAllCursor.getColumnIndex(DbHelper.SERVICE_REQ)));
	}

	/**
	 * The Class HitService.
	 * This class hits the webservice in background and returns its response
	 */
	class HitService extends AsyncTask<String, Void, Object> {
		
		/** The result. */
		Object mResult;
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Object doInBackground(String... params) {
			switch(mReqType){
			case POST_JSON:
				/*send JSON POST request*/
				mResult=sendJsonPost(""+params[0], ""+params[1]);
				break;
			case SaveOffLine.POST:
				/*send Normal Post Request*/
				Log.d("CHECK", "sending get");
				mResult=sendNormalPost(""+params[0], ""+params[1]);
				break;
			case SaveOffLine.GET:
				/*send Get Request*/
				Log.d("CHECK", "sending get");
				mResult=sendGet(""+params[0], ""+params[1]);
				break;
			}
			return mResult;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			/*send service result for processing*/
			try {
				onServiceCallComplete(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}


	/**
	 * Send json post.
	 * This function sends a JSON request to webservice
	 * @param url the url of webservice
	 * @param req the req for the webservice
	 * @return the result as object
	 */
	public Object sendJsonPost(String url,String req){
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpPost post = new HttpPost(url);
			/*create entity of request to be sent with service*/
			StringEntity se = new StringEntity(req);
			/*Add headers*/
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));

			Log.d("CHECK", "JSON: "+req);
			/*set Entity*/
			post.setEntity(se);
			HttpResponse response = client.execute(post);
			if (response != null) {//check response
				Log.d("CHECK", "Response: "+response);
				InputStream in = response.getEntity().getContent();
				Log.d("CHECK", "in stream: "+in);
				/*returns result object after processing inputstream*/
				return sendResponse(in);
			} else {
				Log.e("CHECK", "Response found null");
				return null;
			}
		} catch (Exception e) {
			Log.e("CHECK", e.toString());
		}
		return null;
	}

	/**
	 * Send get.
	 * This function sends a Get request to webservice
	 * @param url the url url of webservice
	 * @param req the req for the webservice
	 * @return the result as object
	 */
	public Object sendGet(String url,String req){
		/*encode request data using nameValue pairs*/
		String paramString = URLEncodedUtils.format(getNameValuePaires(req),
				HTTP.UTF_8);
		/*combine url and parameters*/
		url +="?"+paramString;
		HttpGet request = new HttpGet(url);
		DefaultHttpClient client = new DefaultHttpClient();
		InputStream source= fetchResponse(request, client);
		/*returns result object after processing inputstream*/
		return sendResponse(source);
	}

	/**
	 * Send normal post.
	 * This function sends a normal Post request to webservice
	 * @param url the url of webservice
	 * @param req the req for the webservice
	 * @return the result as object
	 */
	public Object sendNormalPost(String url,String req){
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);
		/*encode request data using nameValue pairs*/
		try {
			request.setEntity(new UrlEncodedFormEntity(getNameValuePaires(req),
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		InputStream source=fetchResponse(request, client);
		/*returns result object after processing inputstream*/
		return sendResponse(source);

	}

	/**
	 * Gets the name value pairs.
	 * This function creates <code>NameValuePair</code> list using given String request.
	 * @param req the String request
	 * @return the name value pairs
	 */
	private List<NameValuePair> getNameValuePaires(String req){
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		String pairs[]=req.split(",");
		for( String pair:pairs){
			String nameValue[]=pair.split("=");
			nameValuePairs.add(new BasicNameValuePair(""+nameValue[0], nameValue[1]+""));
		}
		return nameValuePairs;
	}


	/**
	 * Fetch response.
	 * This function executes request.
	 * @param request the request
	 * @param client the client
	 * @return the input stream
	 */
	private InputStream fetchResponse(HttpUriRequest request,
			DefaultHttpClient client) {
		try {

			Log.d("CHECK", ("executing request " + request.getRequestLine()));
			/*execute request and get response*/
			HttpResponse getResponse = client.execute(request);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.d(getClass().getSimpleName(), "Error " + statusCode	+ " for URL " + request.getURI());
				return null;
			}
			HttpEntity getResponseEntity = getResponse.getEntity();
			/*return response inputstream*/
			return getResponseEntity.getContent();
		} catch (IOException e) {
			request.abort();
		}
		return null;
	}

	/**
	 * Send response.
	 * This function converts inputstream to string.
	 * @param is the input stream to be converted to string
	 * @return the converted string as object
	 */
	private Object sendResponse(InputStream is){
		Object responseService=null;
		if (is != null) {
			/*pass inputstream to buffred reader*/
			BufferedReader reader=new BufferedReader(new InputStreamReader(is));
			/*String builder for making string response object*/
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*convert string builder object to string*/
			responseService=sb.toString();
			Log.d("TAG", "Response:  " + responseService);
			return responseService;
		} else {
			Log.e("CHECK", "Response found null !!");
			return null;
		}

	}

	/**
	 * On service call complete.
	 * this function is called after we complete webservice call and get result
	 * @param response the response
	 * @throws JSONException 
	 */
	private void onServiceCallComplete(Object response) throws JSONException{
		Log.d("CHECK", "CALL COMPLETE");
		if(response!=null){//check for null response
			if(isCheckMade){
				if(parseResponse(response)){
					deleteSentService();
				}
			}else{
				deleteSentService();
			}
			
		}
		if(mSelectAllCursor.moveToNext()){//move to next record
			sendSavedService();//start sending next record
		}

	}
	
	/**
	 * Delete sent record from database.
	 */
	private void deleteSentService(){
		if(mDbHelper.deleteById(""+mRecId,db)){
			/*sent successfully delete so delete the sent record*/
			Log.d("CHECK", "DELETED");
		}else{
			Log.d("CHECK", "NOT DELETED");
		}
	}

	/**
	 * Parses the response.
	 *
	 * @param response the response
	 * @return true, if successful
	 * @throws JSONException the jSON exception
	 */
	private boolean parseResponse(Object response) throws JSONException{
		String json=(String)response;
		Object tokenizedJson=new JSONTokener(json).next();
		if(tokenizedJson instanceof JSONObject){
			return parseJsonObject(tokenizedJson);
		}else if(tokenizedJson instanceof JSONArray){
			return parseJsonArray(tokenizedJson);
		}else{
			return false;
		}
	}
	
	/**
	 * Parses the json object.
	 *
	 * @param jsonObject the json object
	 * @return true, if successful
	 * @throws JSONException the jSON exception
	 */
	private boolean parseJsonObject(Object jsonObject) throws JSONException{
		JSONObject json=(JSONObject)jsonObject;
		if(json.has(key)){
			String currentValue=json.getString(key);
			if(currentValue.equals(value)){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Parses the json array.
	 *
	 * @param jsonArray the json array
	 * @return true, if successful
	 * @throws JSONException the jSON exception
	 */
	private boolean parseJsonArray(Object jsonArray) throws JSONException{
		JSONArray json=(JSONArray)jsonArray;
		int length=json.length();
		for(int i=0;i<length;i++){
			JSONObject jsonObject=(JSONObject) json.get(i);
			if(jsonObject.has(key)){
				String currentValue=jsonObject.getString(key);
				if(currentValue.equals(value)){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
	
}
