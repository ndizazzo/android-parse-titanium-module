# Parse.com Titanium Module for Android

## About the Module

This module exists to provide Appcelerator developers with an **open source** (licensed under the MIT license) Android module to wrap functionality of the Android Parse SDK.

It attempts to maintain call compatibility with the iOS Titanium Module to avoid applications from having a vastly different code structure.

Right now the number of supported features is limited, but I hope to develop more features over time as well as manage contributions from others.

The long term goal is to combine both modules once functionality is sufficient, avoiding the need for two separate modules.

## Quick Start

### Get it [![gitTio](http://gitt.io/badge.png)](http://gitt.io/component/dk.napp.drawer)
Download the latest distribution ZIP-file and consult the [Titanium Documentation](http://docs.appcelerator.com/titanium/latest/#!/guide/Using_a_Module) on how install it, or simply use the [gitTio CLI](http://gitt.io/cli):

`$ gittio install com.ndizazzo.parsemodule`

## SDK Compatibility

**Current Android SDK version:** 1.5.1

### Supported

* Remote Data Objects (CRUD)
* Push Subscriptions

### Currently Unsupported

* Local Data Objects
* Analytics
* Users
* Access Control Lists
* Files
* Cloud Functions
* Facebook / Twitter integration
* GeoPoints

## Building

The Appcelerator module creation documentation provides instructions on how to build for your platform / environment. You can find the [module build documentation here](http://docs.appcelerator.com/titanium/3.0/#!/guide/Android_Module_Development_Guide-section-29004945_AndroidModuleDevelopmentGuide-Building).

Once the module is built, you can install it manually by unpacking the newly created zipfile in your application's `modules` directory, and adding:

    <module platform="android">com.ndizazzo.parsemodule</module>

to your `tiapp.xml` file under the `<modules>` section.

Alternatively, you can install the module through the Studio via the properties page for the `tiapp.xml` file.

## Usage

The sections below outline each of the major (and currently supported) methods in the Android Parse module.

#### Initialization

    var parse = require('com.ndizazzo.parsemodule');
    parse.initParse({
      appId: 'YOUR PARSE APP ID',
      clientKey: 'YOUR PARSE CLIENT KEY'
    });

### Data Objects

#### Parse Data Objects

Create a new data object in the class of 'Song':

    parse.createObject('Song', {
      title: 'a song name',
      artist: 'an artist name',
      album: 'an album name',
      playTimeSeconds: 335
    }, function(data) {
       if(data.error) {
          // error happened
       } else {
          // data.object contains all the fields you provided to the module
       }
    });

**NOTE:** You might need to enable 'client class creation' in your application settings in order for the SDK to create the object if the class doesn't already exist. Otherwise, add it yourself using the Data Browser.

#### Find an object

You can provide an array of conditions to find objects. For example:

    // Specifying _Classname targets the in-built classes of Parse.  If you want to specify your own class, no need for the _.
    parse.createObject('Song', {
      title: 'Octavarium',
      artist: 'Dream Theater',
      album: 'Octavarium',
      playTimeSeconds: 1440
    }, function(data) {
       if(data.error) {
          // error happened
          Ti.API.error(data.error);
       } else {
          // data.object contains all the fields you provided to the module
       }
    });

You can also use multiple conditions and control result ordering:

    parse.findObjects('Song', [
      { key: 'playTimeSeconds', condition: '>=', value: 600 },
      { key: 'artist', condition: '==', value: 'Dream Theater' },
      { key: 'artist', condition: 'orderby', value: 'desc' }
    ], function(data) {  ... });

#### Update an object

**NOTE:** Below, the parse object must first be retrieved from the module (and later modified).

    var firstObject = null;
    parse.findObjects('Song', [{key: 'artist', condition: '==', value: 'Dream Theater'}], function(data) {
      if (data.error) {
        // Handle problems here
      }
      else {
        if (data.results.length > 0) {
          firstObject = data.results[0];
        }
      }

      // Here we modify it
      firstObject.title = "Illumination Theory";
      firstObject.album = "Dream Theater";

      // Now we can update it
      parse.updateObject(firstObject, function(data) {
        if(data.error) {
          // It didn't work :(
        } else {
          // It worked! :D
        }
      });
    });

The object must first be retrieved from the Parse module because fields like `_className`, `_objectId`, `_createdAt`, and `_updatedAt` *must* be set in order for the module to properly work with it.

#### Delete an Object

Deleting an object is fairly straightforward, pass in the class name and the object id:

    parse.deleteObject("Song", "<the object id>", function(result) {
      if (result.success) {
        console.log("Delete worked!");
      }
      else {
        // Nope
      }
    });

### Push Service

The Parse Push Service allows your app to receive push notifications from GCM or PPNS.

#### Subscribing to a Channel

**NOTE:** The first parameter in the call below is for a device token, but is unused on Android. This is to maintain call compatibility with the iOS module.

    Parse.registerForPush("dummyToken", "PushChannel", function(data) {
      // data.success is true / false, if the channel name is valid or not
      console.log(data);
    });

#### Unsubscribing from a Channel

    Parse.unsubscribeFromPush("PushChannel", function(data) {
      // data.success is true / false, if the channel name is valid or not
      console.log(data);
    });

#### Channel List

    Parse.pushChannelList(function(data) {
      // Channel names are stored in data.channels
      console.log(data);
    });

## Credits

I want to shout out [ewindso](http://github.com/ewindso) for [his iOS Parse module](https://github.com/ewindso/ios-parse-titanium-module). This module's interface is modelled after his in order to keep your application straightforward (no platform-specific code aside from initialization), and his module defines a clear interface to the Parse SDK.
