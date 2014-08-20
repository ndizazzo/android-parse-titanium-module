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

import com.parse.ParseQuery;
import com.parse.ParseObject;
import com.parse.FindCallback;

import org.appcelerator.kroll.common.Log;

public class QueryFactory {

  private static final String TAG = "ParseSingleton";

  public static ParseQuery New(String className) {
    return new ParseQuery(className);
  }

  public static ParseQuery Build(String className, HashMap[] conditions) {

    ParseQuery<ParseObject> query = new ParseQuery(className);

    // Create a composite query via chaining
    for (int i = 0; i < conditions.length; ++i) {
      HashMap map = conditions[i];

      // These fields must be named exactly the same in the calling application
      String key = (String)map.get("key");
      String condition = (String)map.get("condition");
      Object value = map.get("value");

      if (HasValidConditions(map)) {

        // This sucks, but I don't really want to try to be clever and use
        // reflection to map conditions to method names. It's not by any means
        // a complete implementation, as it only contains condition matching
        // for basic operations.
        if (condition.equals("==")) {
          query = query.whereEqualTo(key, value);
        }
        else if (condition.equals("!=")) {
          query = query.whereNotEqualTo(key, value);
        }
        else if (condition.equals(">")) {
          query = query.whereGreaterThan(key, value);
        }
        else if (condition.equals("<")) {
          query = query.whereLessThan(key, value);
        }
        else if (condition.equals(">=")) {
          query = query.whereGreaterThanOrEqualTo(key, value);
        }
        else if (condition.equals("<=")) {
          query = query.whereLessThanOrEqualTo(key, value);
        }
        else if (condition.equals("exists")) {
          query = query.whereExists(key);
        }
        else if (condition.equals("notexists")) {
          query = query.whereDoesNotExist(key);
        }
        else if (condition.equals("orderby")) {
          if (value.equals("asc")) {
            query = query.orderByAscending(key);
          }
          else if (value.equals("desc")) {
            query = query.orderByDescending(key);
          }
          else {
            Log.e(TAG, "Unrecognized sorting order, use 'asc' or 'desc'.");
          }
        }
        else {
          Log.e(TAG, "The condition '"+ condition + "' was not recognized by " +
          "the module. Create a request for implementation on GitHub, or " +
          "implemented it and create a pull request.");
        }
      }
    }

    return query;
  }

  private static boolean HasValidConditions(HashMap conditions) {
    String key = (String)conditions.get("key");
    String condition = (String)conditions.get("condition");
    Object value = conditions.get("value");
    return (key.length() > 0 && condition.length() > 0 && value != null);
  }
}
