/*

The MIT License (MIT)

Copyright (c) 2014 Nick DiZazzo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package com.ndizazzo.parsemodule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.FindCallback;
import com.parse.SaveCallback;
import com.parse.DeleteCallback;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.PushService;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseRelation;
import com.parse.ParseGeoPoint;
import com.parse.ParseFile;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.common.Log;

import android.app.Activity;
import android.content.Context;

public class ParseSingleton {
  private static final String TAG = "ParseSingleton";
  private static ParseSingleton instance = null;
  private boolean initialized = false;

  protected ParseSingleton() {
  }

  public static ParseSingleton Instance() {
    if (instance == null) {
       instance = new ParseSingleton();
    }
    return instance;
  }

  public void InitializeParse(String appId, String clientKey, boolean alreadyInitialized) {
    // Grab the application context
    TiApplication appContext = TiApplication.getInstance();
    if (appContext == null) {
      Log.e(TAG, "Application context is null, cannot continue...");
      return;
    }

    if (!initialized) {
    	if (!alreadyInitialized) {
    		Parse.initialize(appContext, appId, clientKey);
    	}

    	EnablePush();

    	initialized = true;
    }
    else
    {
      Log.e(TAG, "Parse has already been initialized!");
    }
  }

  public void FindDataObjects(String objectName, HashMap[] conditions, FindCallback<ParseObject> callback) {
    ParseQuery<ParseObject> query = QueryFactory.Build(objectName, conditions);
    query.findInBackground(callback);
  }

  public void CreateDataObject(String objectName, HashMap fields, SaveCallback callback) {
    ParseObject dataObject = new ParseObject(objectName);
    Set<Map.Entry<Object, Object>> keyValuePairs = fields.entrySet();

    for (Map.Entry<Object, Object> item : keyValuePairs) {
      String key = (String)item.getKey();
      Object value = item.getValue();
      dataObject.put(key, ParseDataConversions.ConvertToParseObjectIfNecessary(value));
    }

    dataObject.saveInBackground(callback);
  }

  public void UpdateDataObject(ParseObject updateObject, SaveCallback callback) {
    if (updateObject != null) {
      updateObject.saveInBackground(callback);
    }
  }

  public void DeleteDataObject(String className, String objectId, DeleteCallback callback) {
    ParseObject object = ParseObject.createWithoutData(className, objectId);
    object.deleteInBackground(callback);
  }

  public void EnablePush() {
		// Get application context / activity to tell Parse what to dispatch to
    Context appContext = TiApplication.getInstance().getApplicationContext();
    Activity appActivity = TiApplication.getAppRootOrCurrentActivity();

    // Set the default callback / handler for push notifications
    PushService.setDefaultPushCallback(appContext, appActivity.getClass());

    // Parse requires you to save the installation which contains the deviceToken for GCM
		ParseInstallation.getCurrentInstallation().saveInBackground();

		// Track Push opens
		ParseAnalytics.trackAppOpened(TiApplication.getAppRootOrCurrentActivity().getIntent());
  }

  public boolean ValidChannelName(String channelName) {
    if (channelName.matches("^[a-zA-Z].[a-zA-Z0-9_-]*$")) {
      return true;
    }
    else {
      return false;
    }
  }

  public void SubscribeToPushChannel(String channelName) {
    // No need for custom activities - we want to dispatch directly to the Titanium application.
    Context appContext = TiApplication.getInstance().getApplicationContext();
    Activity activity = TiApplication.getAppRootOrCurrentActivity();
    PushService.subscribe(appContext, channelName, activity.getClass());
  }

  public void UnsubscribeFromPushChannel(String channelName) {
    Context appContext = TiApplication.getInstance().getApplicationContext();
    PushService.unsubscribe(appContext, channelName);
  }

  public Set<String> ChannelSubscriptionList() {
    Context appContext = TiApplication.getInstance().getApplicationContext();
    return PushService.getSubscriptions(appContext);
  }
}
