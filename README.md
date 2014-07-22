# Titanium module for Parse.com Android SDK

## Purpose

This module exists to provide Appcelerator users with a *free* Android module to wrap functionality of the Android Parse SDK.

It attempts to maintain call compatibility with the iOS Titanium Module to avoid applications from having a vastly different code structure.

The long term goal is to combine both modules once functionality is sufficient, avoiding the need for two separate modules.

## Usage

### Initialization

    var parse = require('com.ndizazzo.androidparsemodule');
    parse.initParse({
      appId: 'YOUR PARSE APP ID',
      clientKey: 'YOUR PARSE CLIENT KEY'
    });

### Parse Data Objects

Create a new data object in the class of 'Game':

    parse.createObject('Game', {
      name: 'My first game',
      level: 1
    }, function(data) {
       if(data.error) {
          // error happened
       } else {
          // use data.object -- it is just plain JSON
       }
    });

### Update an object


    // NOTE: obj must have been retrieved from parse module (and later modified).
    parse.updateObject(obj, function(data) {
      if(data.error) {

      } else {
        // worked!
      }
    });

### Find an object

I think this is pretty cool.  You can pass in an array of conditions in which to find them.  For example:

    // specifying _User targets the 'User' class in Parse.  If you want to specify your own class, no need for the _.
    parse.findObjects('_User', [
      {key: 'email', condition: '==', value: 'someemail@someemail.com'}
    ], function(data) {
      if(data.error) {
        // error, probably with connection
        return;
      }

      if(data.results.length > 0) { // found some results!

      }
    });

You can also do multiple conditions:

    parse.findObjects('Game', [
      {key: 'level', condition: '>=', value: 1},
      {key: 'level', condition: '<=', value: 5},
      {key: 'status', condition: '==', value: 'live'},
      {key: 'position', condition: 'orderby', value: 'asc'}
    ], function(data) {  ... });

### Save All Objects

    // for example, this one starts with findObjects
    parse.findObjects('Test', [], function(data) {
      var objectArray = data.results;

      // assuming there are at least 2 objects in the array
      objectArray[0].key = 'Another value';
      objectArray[1].key = 'Yet another value';

      // now you can save them all at the same time here
      parse.saveAllObjects(objectArray, function(data) {
        if(data.success) { // yay!

        }
      });
    });

## Push Notifications

To register for push notifications unique token should first be retrieved from the device.

    Ti.Network.registerForPushNotifications({
      callback: function pushCallback(e)
      {
        // Handle receiving of push notifications
      },
      success: function pushSuccess(e)
      {
        deviceToken = e.deviceToken;
        // Store device token for use later
      },
      error: function pushError(e)
      {
        // If unable to get deviceToken check for errors here
        // alert('Error!: '+JSON.stringify(e));
      },
      types: [
        Ti.Network.NOTIFICATION_TYPE_BADGE,
        Ti.Network.NOTIFICATION_TYPE_ALERT,
        Ti.Network.NOTIFICATION_TYPE_SOUND
      ]
    });

### Subscribing to a channel

To register for a push channel, you must provide the device token, the name of the channel, and a callback that handles the result of the call.

    parse.registerForPush(deviceToken, 'sampleChannel', function(data) {
      // output some data to check for success / errors
      // alert(data);
    });

### Unsubscribing from a channel

  parse.unsubscribeFromPush('sampleChannel', function(data) {
    // alert(data);
  });
