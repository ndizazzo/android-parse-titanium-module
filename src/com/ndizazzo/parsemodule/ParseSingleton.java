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

  public void InitializeParse(String appId, String clientKey) {
    // Grab the application context
    TiApplication appContext = TiApplication.getInstance();
    if (appContext == null) {
      Log.e(TAG, "Application context is null, cannot continue...");
      return;
    }

    if (!initialized) {
      Parse.initialize(appContext, appId, clientKey);

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
      dataObject.put(key, ConvertToParseObjectIfNecessary(value));
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

  private static boolean IsParseObject(Object parseObject) {
    if (parseObject instanceof HashMap) {
      HashMap hashMap = (HashMap)parseObject;
      if (hashMap != null && hashMap.containsKey("_objectId") && hashMap.containsKey("_className")) {
        return true;
      }
    }

    return false;
  }

  public static Object ConvertToParseObjectIfNecessary(Object object) {
    if (IsParseObject(object)) {
      HashMap map = (HashMap)object;
      String objectID = (String)map.get("_objectId");
      String className = (String)map.get("_className");

      if (className != null && className.length() > 0 && objectID != null && objectID.length() > 0) {
        ParseObject parseObject = ParseObject.createWithoutData(className, objectID);
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
          // Ignore the keys we don't need to set (ACL is currently unsupported)
          if (key.equals("_createdAt") || key.equals("_updatedAt") || key.equals("_ACL") ||
              key.equals("_objectId") || key.equals("_className")) {
            continue;
          }

          // Check if its a nested hash map. This only converts the original
          // object to a parse object if it requires conversion, otherwise it just
          // returns the original value
          Object mapValue = map.get(key);
          parseObject.put(key, ConvertToParseObjectIfNecessary(mapValue));
        }

        return parseObject;
      }
    }

    return object;
  }

  public static HashMap ConvertPOToHashMap(ParseObject parseObject) {
    HashMap map = new HashMap();

    if (parseObject != null) {
      Set<String> keySet = parseObject.keySet();

      for (String key : keySet) {
        Object value = parseObject.get(key);
        map.put(key, value);
      }

      // Remember class name, object IDs, created and updated dates
      map.put("_className", parseObject.getClassName());
      map.put("_objectId", parseObject.getObjectId());
      map.put("_createdAt", parseObject.getCreatedAt());
      map.put("_updatedAt", parseObject.getUpdatedAt());
    }

    return map;
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
