package com.teamtreehouse.mememaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.teamtreehouse.mememaker.utils.StorageType;

public class MemeMakerApplicationSettings {
    SharedPreferences mSharedPreferences;
    //create sharePreference

    public MemeMakerApplicationSettings(Context context) {
        mSharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getSharedPreferences() {
        return mSharedPreferences.getString("storage",StorageType.INTERNAL);
    }

    public void setSharedPreferences(String storageType) {
        mSharedPreferences.edit()
                              .putString("storage",storageType)
                               .apply();

    }
}
