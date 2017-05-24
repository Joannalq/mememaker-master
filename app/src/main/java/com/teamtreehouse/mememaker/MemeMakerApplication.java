package com.teamtreehouse.mememaker;

import android.preference.Preference;
import android.preference.PreferenceManager;
import com.teamtreehouse.mememaker.utils.FileUtilities;

public class MemeMakerApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FileUtilities.saveAssetImage(this, "dogmess.jpg");
        FileUtilities.saveAssetImage(this, "excitedcat.jpg");
        FileUtilities.saveAssetImage(this, "guiltypup.jpg");

        //allow user to setting initial setting ,
        // false : default values are ontly set for shared preferences
        // not allow user to override setting modified
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
    }
}
