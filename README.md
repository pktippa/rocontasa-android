Rocontasa - ROad CONdition Tracking And Safety Alarming [![Build Status](https://travis-ci.org/rocontasa/android.png)](https://travis-ci.org/rocontasa/android)
=======================================================

Overview
--------

The idea is to track the road condition by detecting potholes, speed bumps, road unevenness etc. and providing safety alarm for nearby pothole/speed bump, rash driving & exceeded speed limit. This would be an Android mobile app (service) that people can install and run, and a desktop-browser based report/map that shows the data (for the BBMP personnel)

This mobile app's purpose is to send data about the road conditions while people are travelling, this uses the onboard accelerometer to record the jerk and the GPS of the mobile device to know exact location of jerk event/road condition. This data is sent to a central database over the internet.

The crowd-sourced accelerometer data, coupled with some data analytics to weed out false positives, should provide an idea as to the locations of potholes/speed bumps, which the Municipality can deal with and provide safety to people alarming about road condition.

Technology Stack:
-----------------
#### Android application ####
  * Install and forget – a background service reports the potholes/road condition  and alarms safety.

#### NodeJS ####
  * HTML Dashboard of Road conditions
  * Web services to consume crows sourced data
  * Data algorithms on BigData. 

##### MongoDB ####
  * For data storage and data analytics.
