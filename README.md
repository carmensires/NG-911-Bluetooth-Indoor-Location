#NG-911-Bluetooth
This is the NG911 Bluetooth Android Application project.

It is an Android App created for a Bluetooht Indoor-Location project for NG9-1-1 dispatchers. 
This app is the first module of a Indoor Location project result of the development of a beacon capturing and processing application, as well as the modification of the open-source app Sipdroid, developed by i-p-tel GmbH.

##Set Up Instructions
Username:...
Password:...
Server or Proxy:...
Location Server:...

##Structure
###1. Extra packages

####AltBeacon Library

 Open source ibeacon library made by Radius Networks, an Android library providing APIs to interact with beacons.
 
 git clone https://github.com/AltBeacon/android-beacon-library
 
 Visit the project [website] (http://altbeacon.github.io/android-beacon-library/) for how to use this library.

####Volley

This package lets Sipdroid make HTTP requests and handle HTTP responses. This is used by Sipdroid to make a request to a location server sending its MAC address, and obtain the location information in PIDF-LO/XML format as a response. Volley is an HTTP library developed by Google and accessible to everyone. The easiest way to get it is cloning the repository using the following command:

git clone https://android.googlesource.com/platform/frameworks/volley

Documentation about Volley can be found at the official web site for Android developers.

###2. Sipdroid Modifications
