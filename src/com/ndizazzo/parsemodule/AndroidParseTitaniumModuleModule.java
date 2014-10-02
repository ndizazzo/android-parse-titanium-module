/**
* This file was auto-generated by the Titanium Module SDK helper for Android
* Appcelerator Titanium Mobile
* Copyright (c) 2009-2013 by Appcelerator, Inc. All Rights Reserved.
* Licensed under the terms of the Apache Public License
* Please see the LICENSE included with this distribution for details.
*
*/
package com.ndizazzo.parsemodule;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.FindCallback;
import com.parse.SaveCallback;
import com.parse.DeleteCallback;
import com.parse.ParseQuery;
import com.parse.ParseException;

import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollFunction;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiConvert;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;

@Kroll.module(name="AndroidParseTitaniumModule", id="com.ndizazzo.parsemodule")
public class AndroidParseTitaniumModuleModule extends KrollModule {
	static ParseSingleton parseSingleton = ParseSingleton.Instance();
   
	static Context mContext = null;
	
	// Standard Debugging variables
	private static final String TAG = "AndroidParseTitaniumModule";

	public AndroidParseTitaniumModuleModule() {
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		if (mContext==null) {mContext = app.getApplicationContext();}
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(mContext);

		String appId = p.getString("parseAppId", "");
		String clientKey = p.getString("parseClientKey", "");
		
		if (!appId.equals("") && !clientKey.equals("")) {
			Parse.initialize(app, appId, clientKey);
		}
	}

	@Kroll.method
	public void initParse(HashMap initOpts) {

		// Store a reference to the parse singleton now that the app is ready
		//parseSingleton = ParseSingleton.Instance();

		if (initOpts != null) {
			Log.d(TAG, "initParse called with parameters " + initOpts.toString());

			String appId = null;
			String clientKey = null;

			if (initOpts.containsKey("appId")) {
				appId = (String)initOpts.get("appId");
			}

			if (initOpts.containsKey("clientKey")) {
				clientKey = (String)initOpts.get("clientKey");
			}

		    // Save App ID and Client Key to shared preference
			SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(mContext);
            
			String savedAppId = p.getString("parseAppId", "");
			String savedClientKey = p.getString("parseClientKey", "");
			
			if (savedAppId.equals(appId) && savedClientKey.equals(clientKey)) {
				// Invoke the Parse SDK Initialize method
				parseSingleton.InitializeParse(appId, clientKey, true);
			} else {
				if (!savedClientKey.equals(appId)) {
					p.edit().putString("parseAppId", appId).commit();
				}
				
				if (!savedClientKey.equals(clientKey)) {
					p.edit().putString("parseClientKey", clientKey).commit();
				}
				// Invoke the Parse SDK Initialize method
				parseSingleton.InitializeParse(appId, clientKey, false);
			}
		}
	}

	@Kroll.method
	public void findObjects(String className, HashMap[] conditions, final KrollFunction applicationCallback) {
		FindCallback<ParseObject> parseCallback = new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {

				// General hash map to invoke the Titanium method with
				HashMap returnMap = new HashMap();
				if (objects != null && objects.size() > 0) {
					HashMap[] resultObjects = new HashMap[objects.size()];

					if (e == null) {
						int count = 0;
						for (ParseObject po : objects) {
							// Convert each object to a hash map to retain it's key/value properties
							HashMap objectMap = ParseDataConversions.ObjectToHashMap(po);
							resultObjects[count] = objectMap;
							++count;
						}

						returnMap.put("results", resultObjects);
					}
					else {
						// no objects were returned,
						returnMap.put("results", null);
					}

				}
				else {
					returnMap.put("error", e.toString());
				}

				applicationCallback.callAsync(getKrollObject(), returnMap);
			}
		};

		parseSingleton.FindDataObjects(className, conditions, parseCallback);
	}

	@Kroll.method
	public void createObject(String className, final HashMap data, final KrollFunction applicationCallback) {

		SaveCallback parseCallback = new SaveCallback() {
			public void done(ParseException e) {
				HashMap result = new HashMap();

				if (e == null) {
					result.put("object", data);
				} else {
					// There was an error
					result.put("error", e.toString());
				}

				if (applicationCallback != null) {
					applicationCallback.callAsync(getKrollObject(), result);
				}
			}
		};

		parseSingleton.CreateDataObject(className, data, parseCallback);
	}

	@Kroll.method
	public void updateObject(final HashMap data, final KrollFunction applicationCallback) {
		final ParseObject convertedObject = (ParseObject)ParseDataConversions.ConvertToParseObjectIfNecessary(data);
		SaveCallback parseCallback = new SaveCallback() {
			public void done(ParseException e) {
				HashMap result = new HashMap();
				HashMap returnObject = ParseDataConversions.ObjectToHashMap(convertedObject);
				if (e == null) {
					result.put("object", returnObject);
				} else {
					// There was an error
					result.put("error", e.toString());
				}

				if (applicationCallback != null) {
					applicationCallback.callAsync(getKrollObject(), result);
				}
			}
		};

		parseSingleton.UpdateDataObject(convertedObject, parseCallback);
	}

	@Kroll.method
	public void deleteObject(String className, String objectId, final KrollFunction applicationCallback) {
		DeleteCallback parseCallback = new DeleteCallback() {
			public void done(ParseException e) {
				HashMap result = new HashMap();
				if (e == null) {
					result.put("success", true);
				} else {
					result.put("error", e.toString());
				}

				applicationCallback.callAsync(getKrollObject(), result);
			}
		};

		parseSingleton.DeleteDataObject(className, objectId, parseCallback);
	}

	@Kroll.method
	public void registerForPush(String deviceToken, String channelName, KrollFunction applicationCallback) {

		// NOTE: deviceToken is not used, but in order to maintain call
		// compatibility with the iOS module, I'm leaving it as a parameter
		HashMap results = new HashMap();

		// The channel name can only match letters, numbers, dashes and underscores, and must start with a letter
		if (parseSingleton.ValidChannelName(channelName)) {
			parseSingleton.SubscribeToPushChannel(channelName);
			results.put("success", true);
		}
		else {
			// Invalid channel name
			results.put("success", false);
		}

		applicationCallback.callAsync(getKrollObject(), results);
	}

	@Kroll.method
	public void unsubscribeFromPush(String channelName, KrollFunction applicationCallback) {

		HashMap results = new HashMap();

		if (parseSingleton.ValidChannelName(channelName)) {
			parseSingleton.UnsubscribeFromPushChannel(channelName);
			results.put("success", true);
		}
		else {
			results.put("success", false);
		}

		applicationCallback.callAsync(getKrollObject(), results);
	}

	@Kroll.method
	public void pushChannelList(KrollFunction applicationCallback) {
		HashMap results = new HashMap();

		Set<String> channelList = parseSingleton.ChannelSubscriptionList();
		String[] channels = new String[channelList.size()];

		int count = 0;
		for (String channelName : channelList) {
			channels[count] = channelName;
			++count;
		}

		// Store the resulting string list under the 'channels' field for the Titanium App
		results.put("channels", channels);

		applicationCallback.callAsync(getKrollObject(), results);
	}
}
