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
import java.util.Date;
import java.util.Set;

import com.parse.Parse;
import com.parse.ParseRelation;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseFile;

import org.appcelerator.kroll.common.Log;

public class ParseDataConversions {

  private static final String TAG = "ParseDataConverter";

  public static HashMap ObjectToHashMap(ParseObject parseObject) {
    HashMap map = new HashMap();

    if (parseObject != null) {
      Set<String> keySet = parseObject.keySet();

      for (String key : keySet) {
        Object value = parseObject.get(key);

        if (value instanceof Boolean || value instanceof String || value instanceof Integer ||
            value instanceof Float || value instanceof Double ||
            value instanceof HashMap || value instanceof Date)
        {
          map.put(key, value);
        }
        else if (value instanceof ArrayList) {
          map.put(key, ParseDataConversions.ArrayListToHashMapArray((ArrayList)value));
        }
        else if (value instanceof ParseObject) {
          HashMap convertedObject = ParseDataConversions.ObjectToHashMap((ParseObject)value);
          map.put(key, convertedObject);
        }
        else if (value instanceof ParseGeoPoint) {
          map.put(key, ParseDataConversions.GeoPointToHashMap((ParseGeoPoint)value));
        }
        else if (value instanceof ParseRelation) {
          map.put(key, ParseDataConversions.RelationToHashMap((ParseRelation)value));
        }
        else if (value instanceof ParseFile) {
          map.put(key, ParseDataConversions.FileToHashMap((ParseFile)value));
          Log.e(TAG, "Returned data contains an unsupported column type: ParseFile");
        }
        else {
          Log.e(TAG, "Unrecognized object in ArrayList, skipping:" + key + " " + value.getClass().getName());
        }
      }

      // Remember class name, object IDs, created and updated dates
      map.put("_className", parseObject.getClassName());
      map.put("_objectId", parseObject.getObjectId());
      map.put("_createdAt", parseObject.getCreatedAt());
      map.put("_updatedAt", parseObject.getUpdatedAt());
    }

    return map;
  }

  public static HashMap GeoPointToHashMap(ParseGeoPoint parseGeoPoint) {
    HashMap geo = new HashMap();
    geo.put("longitude", parseGeoPoint.getLongitude());
    geo.put("latitude", parseGeoPoint.getLatitude());
    return geo;
  }

  public static HashMap[] ArrayListToHashMapArray(ArrayList arrayList) {
    int count = arrayList.size();
    HashMap[] items = null;

    if (count > 0) {
      items = new HashMap[count];
      for (int i = 0; i < count; ++i) {
        HashMap convertedObject = (HashMap)arrayList.get(i);
        items[i] = convertedObject;
      }
    }

    return items;
  }

  public static HashMap RelationToHashMap(ParseRelation parseRelation) {
    Log.e(TAG, "Unsupported conversion: ParseRelation");
    return new HashMap();
  }

  public static HashMap FileToHashMap(ParseFile parseFile) {
    Log.e(TAG, "Unsupported conversion: ParseFile");
    return new HashMap();
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
}
