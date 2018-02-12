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

## Key Considerations

### Data persistence

The data that this app will store in a database will be:

* A list of news sources, identified by a unique id;
* A list of information categories, identified by a unique id;

The data will be stored using Firebase Realtime Database, authenticated users will be able write new sources that their app will be able to read from. An user will have to be authenticated to add a new source, and only that specific user will be able to read from sources that they added;

### Corner cases in the UX

* No internet connection - without an internet connection it will not be possible to load the feeds.
  * So a simple error message, with an optional error image, will be displayed, explaining what happened to the user;
* If an article fails to load, an error message will be displayed;
* If the user tries to add a source that does not work, that source will not be added to the database;
* If the user is not authenticated, they can still read the news, but won’t be able to add new categories or sources;

### Third-party Libraries used

[Picasso](http://square.github.io/picasso/) - Will be used to load any article images that may be provided in an article;  
[Timber](https://github.com/JakeWharton/timber) - Will be used log any events of interest;  
[Butter Knife](http://jakewharton.github.io/butterknife/) - will be used to load the views instead of the “findViewById” function;  
[Espresso](https://developer.android.com/training/testing/espresso/index.html) - Will be used to write tests for the ui;  
[Firebase Authentication](https://firebase.google.com/docs/auth) - Will be used so that the user can add new sources, logged via Google or Facebook;
[Firebase Realtime Database](https://firebase.google.com/docs/database) - Will store news sources and categories;  
[Firebase JobDispatcher](https://github.com/firebase/firebase-jobdispatcher-android) - Will be used to schedule regular updates to the feeds, and will trigger an update on the widget with the most recent ones as well;

## Development plan

### Step 1: Project setup

The first thing to do is to set up the project to begin development, that involves:

* Create a basic skeleton project with a blank activity to start with;
* Configure libraries, searching for information on their latest versions and adding gradle dependencies;
* Configure gradle script using the latest build tools and signing the apk for a release build of the project;

### Step 2: Implement basic RSS reading functionality

The core function of the app is to read RSS feeds, in this step:

* Develop POJO classes that represent a feed (with a link, a title, a description, etc.)
* Develop classes that will request and parse the data from a feed;
* Develop tests a few of these classes and functions;
* Optional improvement: make these a part of a separated java library;

### Step 3: Create the database

* Create the Firebase database with a categories and sources tables;
* Populate the tables with the RSS feeds and categories that all users will have access to;

### Step 4: Create the user Interface

* Create the layout of the Main activity
  * Where the news will be displayed as a list of card views;
  * Use the SwipeRefreshLayout so that the user can reload the feeds;
* Create the layout for the individual news items;
  * Design the layout of the Card Views;
* Create a navigation drawer,
  * So that the user can navigate among the news categories;
* Create the layout of the activity that will display the article:
  * Using Material design principles to allow for a comfortable reading experience;
* Create the layout of the activity that will allow the user to add new categories or news ources;
* Add notifications;
* Customize notifications channels;

### Step 5: Firebase & adMobs integration 

* Hook up Firebase Auth, with Google and Facebook options;
* Handle cases such as login declined to login, user logged out, etc;
* Develop the functions that allow logged users to add new categories and sources/
* Customize the Firebase Database rules;
  * Add the banner ad on the Article Activity;
* A new ad should be loaded only when a  new article is read;
* Create a JobDispatcher, which will be used later to keep the feeds updated;
* Basic tests of the database functions;

### Step 6: Tying it all together

* Use the categories from the database to populate the navigation drawer;
* Develop the functions that populate the main activity with news from the selected category from all the sources associated with that category;
  * Ordering them by most recent first;
  * Using a JobDispatcher to update the feeds at timely intervals;
* Develop the functions that will allow the user to read a selected article;
  * Optional: Using transitions from one activity to the other;
* Develop tests for the basic workflow of the app;
  * Selecting a category, selecting a specific feed to read, etc;
* Develop a basic widget that displays the latest news;
  * It shall display only titles, and the number will depend on the widget size;
  * Will be updated regularly by the JobDispatcher;




