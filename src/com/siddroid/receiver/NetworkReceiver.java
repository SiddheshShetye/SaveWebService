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
package com.siddroid.receiver;

import com.siddroid.offlinews.SendOffline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * The Class NetworkReceiver.
 * This class accepts broadcasts for connectivity
 * @author Siddhesh S Shetye
 * @version 2013.2801
 * @since 1.0
 */
public class NetworkReceiver extends BroadcastReceiver {

	/** The Constant TAG. */
	private static final String TAG = "NetworkReceiver";

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Action: " + intent.getAction());
		/*Check the action of broadcast*/
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			/*Initialize NetworkInfo using connectivity manager*/
			NetworkInfo mNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
			if(mNetworkInfo!=null){//if its null no connectivity
				String typeName = mNetworkInfo.getTypeName();//get name of network type
				String subtypeName = mNetworkInfo.getSubtypeName();// get name of network subtype
				boolean available = mNetworkInfo.isAvailable();// check if network is currently available
				Log.d(TAG, "Network Type: " + typeName 
						+ ", subtype: " + subtypeName
						+ ", available: " + available);
				if(available){//if network available start sending webservice records in database
					new SendOffline(context);
				}
			}
		}
	}

}
