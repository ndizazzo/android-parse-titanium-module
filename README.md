# Parse.com Titanium Module for Android

## About the Module

This module exists to provide Appcelerator developers with an **open source** (licensed under the MIT license) Android module to wrap functionality of the Android Parse SDK.

It attempts to maintain call compatibility with the iOS Titanium Module to avoid applications from having a vastly different code structure.

Right now the number of supported features is limited, but I hope to develop more features over time as well as manage contributions from others.

The long term goal is to provide a more up-to-date module that will avoid the need for two separate projects.

## Quick Start

### Get it [![gitTio](http://gitt.io/badge.png)](http://gitt.io/component/dk.napp.drawer)
Download the latest distribution ZIP-file and consult the [Titanium Documentation](http://docs.appcelerator.com/titanium/latest/#!/guide/Using_a_Module) on how install it, or simply use the [gitTio CLI](http://gitt.io/cli):

`$ gittio install com.ndizazzo.parsemodule`

## SDK-Module Compatibility

**Parse Android SDK version:** 1.7.1

| Supported             | Not Supported      | Not Supported | Not Supported   |
|-----------------------|--------------------|---------------|-----------------|
| Remote Data Objects   | Local Data Objects | ACLs          | Cloud Functions |
| Push Notifications    | Analytics          | Files         | Facebook        |
| Channel Subscriptions | Users              | GeoPoints     | Twitter         |

## Building

The Appcelerator module creation documentation provides instructions on how to build for your platform / environment. You can find the [module build documentation here](http://docs.appcelerator.com/titanium/3.0/#!/guide/Android_Module_Development_Guide-section-29004945_AndroidModuleDevelopmentGuide-Building).

## Installing

Once the module is built, you can install it manually by unpacking the newly created zip file in your application's `modules` directory, and adding:

    <module platform="android">com.ndizazzo.parsemodule</module>

to your `tiapp.xml` file under the `<modules>` section.

Alternatively, you can install the one of the module zip files (from the `/dist` folder) through the Studio via the properties page for the `tiapp.xml` file.

Once this is complete, you **must** add the following property keys to your `tiapp.xml` file:

    <property name="Parse_AppId">your id goes here</property>
    <property name="Parse_ClientKey">your key goes here</property>

If you don't do this, you'll get runtime errors in your application stating that you must call initializeParse(appId, clientKey).

## Usage

Please refer to the [module usage documentation](documentation/index.md).

## Credits

I want to shout out [ewindso](http://github.com/ewindso) for [his iOS Parse module](https://github.com/ewindso/ios-parse-titanium-module). This module's interface is modelled after his in order to keep your application straightforward (no platform-specific code aside from initialization), and his module defines a clear interface to the Parse SDK.
