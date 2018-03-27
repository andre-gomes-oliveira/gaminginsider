# Android Nanodegree - Capstone Project - Gaming Insider

A specialized RSS feed desingned for gamers. From e-sport afficionados to one-game-per-month players.

## Description

Most RSS readers have a “gotta catch’em all” approach. They are general purpose apps that do a great job for the vast majority of users, but require tinkering and customization by anyone who wants the best experience. 

This app is designed specifically for those interested in gaming content, which is divided in many categories (such as reviews, independent features, previews, among others), as such, it will come with many neatly organized categories and news sources. You can  customize it further, of course, adding more categories or sources as you like. 

## Intended User

Those with a passion for gaming, be it those who want the latest e-sport news every week or those who check in every month to see what games have been launched and read the latest reviews

## Features

The features that will set this app apart from other RSS readers are:

* It will fetch information from trusted sources and organize them in a sensible manner;
  * Like a gaming outlet website, but fetching  and organizing information from multiple sources instead of being limited to just one source;
* The user will be able to add new categories and news sources as they like;
* The user will be able the customize the notification system, allowing specific sources or categories to send notifications;
* The user will be able to read and share content from the feeds in the app;

## Third-party Libraries used

[Picasso](http://square.github.io/picasso/) - Will be used to load any article images that may be provided in an article;  
[Timber](https://github.com/JakeWharton/timber) - Will be used log any events of interest;  
[Butter Knife](http://jakewharton.github.io/butterknife/) - will be used to load the views instead of the “findViewById” function;  
[Espresso](https://developer.android.com/training/testing/espresso/index.html) - Will be used to write tests for the ui;  
[Firebase Authentication](https://firebase.google.com/docs/auth) - Will be used so that the user can add new sources, logged via Google or Facebook;
[Firebase Realtime Database](https://firebase.google.com/docs/database) - Will store news sources and categories;  
[Firebase JobDispatcher](https://github.com/firebase/firebase-jobdispatcher-android) - Will be used to schedule regular updates to the feeds, and will trigger an update on the widget with the most recent ones as well;




