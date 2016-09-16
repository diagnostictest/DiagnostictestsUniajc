package com.tecnologiajo.diagnostictestsuniajc.modelos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SignalStrength;

import java.util.HashMap;




/**
 * Created by ADMIN on 12/08/2016.
 */
public class ContentManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsAdd";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // ID Content (make variable public to access from outside)
    public static final String ID_CONTENT = "id_content";

    // ID Content (make variable public to access from outside)
    public static final String CONTENT_TEMP = "content";

    // ID Content (make variable public to access from outside)
    public static final String IS_CONTENT_TEMP = "IsContentTemp";
    // Constructor
    public ContentManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    /**
     * Create login session
     * */
    public void createContentTestCode(String name,String idregister,boolean isadd){
        // Storing content in pref
        editor.putString(KEY_NAME, name);
        // Storing content id in pref
        editor.putString(ID_CONTENT, idregister);
        // Storing content id in pref
        editor.putBoolean(IS_LOGIN, isadd);
        // commit changes
        editor.commit();
    }

    /**
     * Create login session
     * */
    public void createContentTestTemp(String content){
        // Storing content in pref
        editor.putString(CONTENT_TEMP, content);
        editor.putBoolean(IS_CONTENT_TEMP, true);
        // commit changes
        editor.commit();
    }


    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isAdd(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    // Get Login State
    public boolean isContentTemp(){
        return pref.getBoolean(IS_CONTENT_TEMP, false);
    }

    public String getContentTemp(){
        return  pref.getString(CONTENT_TEMP, "");
    }

    public void closeContentTemp(){
        editor.putString(CONTENT_TEMP,"");
        editor.putBoolean(IS_CONTENT_TEMP,false);
        editor.commit();
    }
}
