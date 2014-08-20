# Parse.com Titanium Module for Android

## About the Module

This module exists to provide Appcelerator developers with an **open source** (licensed under the MIT license) Android module to wrap functionality of the Android Parse SDK.

It attempts to maintain call compatibility with the iOS Titanium Module to avoid applications from having a vastly different code structure.

Right now the number of supported features is limited, but I hope to develop more features over time as well as manage contributions from others.

The long term goal is to combine both modules once functionality is sufficient, avoiding the need for two separate modules.

## SDK Compatibility

**Current Android SDK version:** 1.5.1

### Supported

* Remote Data Objects
 * Create
 * Find
 * Update
 * Delete

### Currently Unsupported

* Local Data Objects
* Analytics
* Users
* Access Control Lists
* Files
* Push Subscriptions
* Cloud Functions
* Facebook / Twitter integration
* GeoPoints

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

Create a new data object in the class of 'Game':

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

## Credits

I want to shout out [ewindso](http://github.com/ewindso) for [his iOS Parse module](https://github.com/ewindso/ios-parse-titanium-module). If you need support for both iOS and Android, you should be able to combine the two with minimal impact. This module's interface is modelled after it in order to keep your application fairly straightforward and simple.
