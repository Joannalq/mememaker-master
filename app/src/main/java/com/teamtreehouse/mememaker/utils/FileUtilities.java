package com.teamtreehouse.mememaker.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.teamtreehouse.mememaker.MemeMakerApplicationSettings;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtilities {

    public static void saveAssetImage(Context context, String assetName) {
        File fileDir=getFileDirection(context);
        File fileToWrite=new File(fileDir,assetName);

        AssetManager assetManager=context.getAssets();
        try {
            InputStream input=assetManager.open(assetName);
            FileOutputStream output=new FileOutputStream(fileToWrite);
            copyFile(input,output);
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream in,OutputStream out) throws IOException {
        byte[] buffer=new byte[1024];
        int read;
        while ((read=in.read(buffer))!=-1){
            out.write(buffer,0,read);
        }
    }

    public static File[] listFiles(Context context){
        File fileDir=getFileDirection(context);
        File[] filterFiles=fileDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.getAbsolutePath().contains(".jpg")){
                    return true;
                }else {
                    return false;
                }
            }
        });
        return filterFiles;
    }

    public static File getFileDirection(Context context){
        MemeMakerApplicationSettings settings=new MemeMakerApplicationSettings(context);
        String storType=settings.getSharedPreferences();
        if(storType.equals(StorageType.INTERNAL)){
            return context.getFilesDir();
        }else {
            if(isExternalAvail()){
                if(storType.equals(StorageType.PRIVATE_EXTERNAL)){
                    return context.getExternalFilesDir(null);
                }else{
                    //for the public external
                    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                }
            }else {
                return context.getFilesDir();
            }
        }
    }

    public static boolean isExternalAvail(){
        String state=Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public static Uri saveImageForSharing(Context context, Bitmap bitmap,  String assetName) {
        File fileToWrite = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), assetName);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return Uri.fromFile(fileToWrite);
        }
    }


    public static void saveImage(Context context, Bitmap bitmap, String name) {
        File fileDirectory =getFileDirection(context);
        File fileToWrite = new File(fileDirectory, name);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
