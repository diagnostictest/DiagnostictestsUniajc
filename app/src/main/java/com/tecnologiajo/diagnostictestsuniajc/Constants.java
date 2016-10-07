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
	 static final String App42ApiKey = "f58efc1a890cd9cf2ca89dfe23da14cb2272bd177b9cefe16b7092c4dab0ac9a";  // API_KEY received From AppHQ console, When You create your first app in AppHQ Console.
	 
 	/** The Constant App42ApiSecret. */
 	static final String App42ApiSecret = "e4354e725c450acae04087d86c6487efb6d5cd050a569f6bd1e8a459e91c7591"; // SECRET_KEY received From AppHQ console, When You create your first app in AppHQ Console.
	 
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

	/** The Constant UserName. */
	static final String TESTBYCODE = "TESTBYCODE";

	/** The Constant Host **/
	static final String HOSTSERVER ="http://diagnostictest-solutionsao.rhcloud.com/";//"http://147.120.0.123:3000/";//"http://192.168.0.20:3000/";//; //
}