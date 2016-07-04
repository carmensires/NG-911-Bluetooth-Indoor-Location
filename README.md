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

- org.sipdroid.sipua.ui.Caller.java: 
This class is a broadcast Receiver which will be listening the dial pad of the cellphone. If the user dials 767, our 911 number for testing purposes, the application will start and run the Bluetooth low energy sniffing and SIP call.

- org.sipdroid.sipua.ui.Sipdroid.java: 
This class generates the main activity of the Sipdroid SIP client.  It was edited for executing a call to 767 automatically when the main sipdroid activity starts we will execute call_menu(AutoCompleteTextView sip_uri_box) for performing a call to 767 automatically.

- org.zoolu.sip.call.ExetndedCall.java: 
The method callNG911 was created to handle NG9-1-1 calls. The method doesn’t change any call parameters, but redirects the flow of the application calling the method inviteNG911 inside InviteDialog.java.

- org.zoolu.sip.dialog.InviteDialog.java: 
The method inviteNG911 was created in this class to handle NG9-1-1 calls. When it gets a response from the server, embeds the location inside an INVITE message using the method createInviteNG911 defined in NG911MessageFactory.java inside the com package.

- org.sipdroid.sipua.SipdroidEngine.java: 
In this class, the method call has been modified for recognizing the call number and, if it is 767, executing the call method of the UserAgent.java class.

- org.sipdroid.sipua.UserAgent.java: 
The call method was modified for recognizing NG9-1-1 calls and executing the methods which will lead this call to send the indoor location to the PSAP. A new Boolean attribute was added in order to recognize if the call is an emergency call or it is not. If it is an emergency call to the 767 CallNG911 method in the ExtendedCall class will be called.

- org.sipdroid.sipua.ui.Settings.java: 
Additional settings options were added to the Sipdroid Settings (SIP Settings). Those settings are user information such as Name, age, language and medical information. Also, it includes optional Facebook registration for sending automatically the user information to the call dispatcher at the PSAP.

