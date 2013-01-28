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
package com.siddroid.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * This Class is working as a utility for checking Internet connection availability.
 * @author Siddhesh S Shetye
 * @version 2013.2801
 * @since 1.0
 *
 */

public class ConnectionUtility {

	/**
	 * This method gets active network available in device
	 * @param context
	 * 				Accepts the context as parameter to get connection service
	 * @return
	 * 		true if connected to internet otherwise false
	 */
	public static boolean isConnectedToInternet(Context context)
	{
		try{
			/*initialize NetworkInfo using connectivity manager*/ 
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			/*get information of active network*/
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			Log.d("CHECK", "executed");
			if(netInfo!=null){//if null no network
			if(netInfo.isConnected()){//if true connected to network
				return true;
			}else{//if is 3g or 2g network available
				Boolean is3g = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
				if(is3g)
					return true;
				else{//if wifi connection is available
					Boolean isWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
					if(isWifi)
						return true;
				}
			}
			}else{
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			Log.d("CHECK", "caught in exception");
		}
		
		return false;
	}
	
}
