/**
 * -----------------------------------------------------------------------
 *     Copyright  2010 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.tecnologiajo.diagnostictestsuniajc;

/**
 * The Class Constants.
 */
/*
 * This class contains all constants variables that require in application.
 */
public class Constants {
	
	/** The Constant App42ApiKey. */
	/*
	 * For getting API_KEY & SECRET_KEY, just login or register to AppHQ Console (http://apphq.shephertz.com/). 
	 */
	 static final String App42ApiKey = "e7cb3839ca4a56b2896c84ae40d0ddf828e5daa751da9ea91473c028986ef10c";  // API_KEY received From AppHQ console, When You create your first app in AppHQ Console.
	 
 	/** The Constant App42ApiSecret. */
 	static final String App42ApiSecret = "f4a534c2448b6a3e86c526e8f40f36e7cd55b75347a4051f68addf2a056a7241"; // SECRET_KEY received From AppHQ console, When You create your first app in AppHQ Console.
	 
	 /** The Constant App42DBName. */
 	/*
	  * For creating Database from AppHQ console, just go to (Technical Service Manager -> Storage Service -> click "Add DB".)
	  */
	 static final String App42DBName = "diagnostictests";  // Change it as your requirement. (Note that, only one DataBase can be created through same API_KEY & SECRET_KEY);
	
	 /** The Constant CollectionName. */
 	static final String CollectionName = "asignaturas"; // Change it as your requirement.
	
	 /** The Constant App42GameName. */
 	/*
	  * For Creating Game, just go to (Business Service Manager -> Game Service -> select Game -> click "Add Game".)
	  */
	 static final String App42GameName = "GameName"; // Change it as your requirement. (You have to create game through AppHQ console.);
	 
	 /** The Constant IntentUserName. */
 	static final String IntentUserName = "intentUserName";
	 
 	/** The Constant App42AndroidPref. */
 	static final String App42AndroidPref="App42AndroidPreferences";
	 
 	/** The Constant UserName. */
 	static final String UserName = "TestUser";
}