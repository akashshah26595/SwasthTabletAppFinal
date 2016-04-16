package com.example.swasth.swasthtabletapplication;

import android.content.Context;
import android.content.SharedPreferences;


public class UserLocalStore {
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    //redundant
    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(String cardNo) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("cardno", cardNo);
        spEditor.commit();
    }

    public void getLoggedInUser() {
        String cardNo = userLocalDatabase.getString("cardno", "");

        // User storedUser = new User(name,username,password,age);

        //return  storedUser;
    }

    public void setLoggedInUser(Boolean loggedInUser) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedInUser);
        spEditor.commit();
    }

    public boolean getLoginStatus() {
        if (userLocalDatabase.getBoolean("loggedIn", false)) {
            return true;
        } else {
            return false;
        }
    }

    public void clearUserData() {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }


}


