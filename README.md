Rocontasa - ROad CONdition Tracking And Safety Alarming [![Build Status](https://travis-ci.org/rocontasa/android.png)](https://travis-ci.org/rocontasa/android)
=======================================================

[P K](https://github.com/pkhub/pk)
====================================

# Problem Identifer(s)/Owner(s) - [Ajith Vasudevan](https://github.com/ajithvasudevan), [P K Tippa](https://github.com/pktippa).

# Problem - Potholes, speed bumps on road and no safety alarming about them

# Solution - Let's remove them and give safety to drivers and passengers.

# Dream(s) - Steering less & Driver less vehicles.

# Investment Sponsored - Last Quarter - $ 0.

# Sponsorship Distribution - X - Full X/2 based on contribution - 1 Year.

# Revenue Generated - Last Quarter - $ 0.

# Revenew Distribution - X - Full X/2 based on contribution - 1 Year.

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


How to contribute:
-----------------

#### Setup Dev Env
* Join [google group](https://groups.google.com/forum/#!forum/rocontasa) for general discussion
* Fork on [GitHub](https://github.com/rocontasa)
* ...


#### Making Changes
* Raise an issue on GitHub, describe the feature/enhancement/bug
* Discuss with others in google group or issue comments, make sure the proposed changes fit in with what others are doing and have planned for the project
* Make changes in your fork
* Write unit test if no existing cover your change
* Push to GitHub under your fork


#### Contribute The Work
* Raise a pull request on GitHub, include both **code** and **test**, link with **related issue**
* Committer will review in terms of correctness, performance, design, coding style, test coverage
* Discuss and revise if necessary
* Finally committer merge code into main branch
