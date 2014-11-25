## Parse Android Module Usage

The sections below outline each of the major (and currently supported) methods in the Android Parse module.

#### Initialization

You need to call the `initParse()` method at some point in your application before you intend to use it:

```javascript
var parse = require('com.ndizazzo.parsemodule');
parse.initParse({
  appId: 'YOUR PARSE APP ID',
  clientKey: 'YOUR PARSE CLIENT KEY'
});
```

### Data Objects

#### Parse Data Objects

Create a new data object in the class of 'Song':

```javascript
parse.createObject('Song', {
  title: 'a song name',
  artist: 'an artist name',
  album: 'an album name',
  playTimeSeconds: 335
}, function(data) {
   if(data.error) {
      // error happened
   } else {
      // data.object contains all the fields provided to the module
   }
});
```

**NOTE:** You might need to enable 'client class creation' in your application settings in order for the SDK to create the object if the class doesn't already exist. Otherwise, add it yourself using the Data Browser.

#### Find an object

You can provide an array of conditions to find objects. For example:

```javascript
// Specifying _Classname targets the in-built classes of Parse.  
// If you want to specify your own class, no need for the _.
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
      // data.object contains all the fields provided to the module
   }
});
```

You can also use multiple conditions and control result ordering:

```javascript
parse.findObjects('Song', [
  { key: 'playTimeSeconds', condition: '>=', value: 600 },
  { key: 'artist', condition: '==', value: 'Dream Theater' },
  { key: 'artist', condition: 'orderby', value: 'desc' }
], function(data) {  ... });
```

#### Update an object

**NOTE:** Below, the parse object must first be retrieved from the module (and later modified).

```javascript
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
```

The object must first be retrieved from the Parse module because fields like `_className`, `_objectId`, `_createdAt`, and `_updatedAt` *must* be set in order for the module to properly work with it.

#### Delete an Object

Deleting an object is fairly straightforward, pass in the class name and the object id:

```javascript
parse.deleteObject("Song", "<the object id>", function(result) {
  if (result.success) {
    console.log("Delete worked!");
  }
  else {
    // Nope
  }
});
```

### Push Service

The Parse Push Service allows your app to receive push notifications from GCM or PPNS.

#### Subscribing to a Channel

**NOTE:** The first parameter in the call below is for a device token, but is unused on Android. This is to maintain call compatibility with the iOS module.

```javascript
Parse.registerForPush("dummyToken", "PushChannel", function(data) {
  // data.success is true / false if the channel name is valid
  console.log(data);
});
```

#### Unsubscribing from a Channel

```javascript
Parse.unsubscribeFromPush("PushChannel", function(data) {
  // data.success is true / false if the channel name is valid
  console.log(data);
});
```

#### Channel List

```javascript
Parse.pushChannelList(function(data) {
  // Channel names are stored in data.channels
  console.log(data);
});
```
