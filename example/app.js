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

var parse = require('com.ndizazzo.parsemodule');

parse.initParse({
  appId: 'YOUR PARSE APP ID',
  clientKey: 'YOUR PARSE CLIENT KEY'
});

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

parse.findObjects('Song', [
    { key: 'playTimeSeconds', condition: '>=', value: 600 },
    { key: 'artist', condition: '==', value: 'Dream Theater' },
    { key: 'artist', condition: 'orderby', value: 'desc' }
  ],
  function(data) {

  if(data.error) {
    Ti.API.error(data.error);
    return;
  }

  if(data.results.length > 0) {
    // found some results!
    Ti.API.info("Results: " + data.results.length);
  }
});

parse.findObjects('Song', [{key: 'artist', condition: '==', value: 'Dream Theater'}], function(data) {
  if (data.error) {
    // Handle problems here
    Ti.API.error(data.error);
  }
  else {
    if (data.results.length > 0) {
      var firstObject = data.results[0];

      // Here we modify it
      firstObject.title = "Illumination Theory";
      firstObject.album = "Dream Theater";

      // Now we can update it
      parse.updateObject(firstObject, function(data) {
        if(data.error) {
          // It didn't work :(
          Ti.API.error(data.error)
        } else {
          // It worked! :D
          Ti.API.info("Update Success!");
        }
      });

			parse.deleteObject("Song", firstObject._objectId, function(result) {
				if (result.success) {
					Ti.API.info("Delete worked!");
				}
				else {
					Ti.API.error("Delete didn't work.");
				}
			});
    }
    else {
      Ti.API.error("No results!");
    }
  }
});
