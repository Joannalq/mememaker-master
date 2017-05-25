package com.teamtreehouse.mememaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.teamtreehouse.mememaker.models.Meme;
import com.teamtreehouse.mememaker.models.MemeAnnotation;

import java.util.ArrayList;

public class MemeDataSource {

    private Context mContext;
    private MemeSQLiteHelper mMemeSQLiteHelper;

    public MemeDataSource(Context context){
        mContext=context;
        mMemeSQLiteHelper=new MemeSQLiteHelper(context);
        //force our SQLite Helper to set up the databases
       /* SQLiteDatabase database=mMemeSQLiteHelper.getReadableDatabase();
        database.close();*/
    }

    private SQLiteDatabase open(){
        return mMemeSQLiteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase database){
        database.close();
    }

    //read or query option to pull back all the memes stored
    public  ArrayList<Meme> read(){
        return null;
    }

    public ArrayList<Meme> readMemes(){
        SQLiteDatabase database=open();
        //cursor for sifting through larger pieces of data
        //select:query
        Cursor cursor=database.query(
                MemeSQLiteHelper.MEMES_TAB,
                new String[]{MemeSQLiteHelper.COLUMN_MEME_NAME,BaseColumns._ID,MemeSQLiteHelper.COLUMN_MEME_ASSET},
                null,//selection
                null,//selection args
                null,//group by
                null,//having
                null//order
        );
        ArrayList<Meme> memes=new ArrayList<Meme>();
        if(cursor.moveToFirst()){
            do{
                Meme meme=new Meme(getIntColumName(cursor,BaseColumns._ID),
                        getStringColumName(cursor,MemeSQLiteHelper.COLUMN_MEME_NAME),
                        getStringColumName(cursor,MemeSQLiteHelper.COLUMN_MEME_ASSET),
                        null);
                memes.add(meme);
            }while (cursor.moveToNext());
        }
        cursor.close();
        close(database);
        return memes;
    }

    private  int getIntColumName(Cursor cursor,String columName){
        int columnIndex=cursor.getColumnIndex(columName);
        return cursor.getInt(columnIndex);
    }

    private String getStringColumName(Cursor cursor,String columName){
        int columIndex=cursor.getColumnIndex(columName);
        return cursor.getString(columIndex);
    }

    public void create(Meme meme){
        SQLiteDatabase database=open();
        database.beginTransaction();
        //implement details
        //put actual meme model into content values
        ContentValues memeValues=new ContentValues();
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME,meme.getName());
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_ASSET,meme.getAssetLocation());
        long memeID=database.insert(MemeSQLiteHelper.MEMES_TAB,null,memeValues);

        for(MemeAnnotation annotation:meme.getAnnotations()){
            ContentValues annotationVal=new ContentValues();
            annotationVal.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR,annotation.getColor());
            annotationVal.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE,annotation.getTitle());
            annotationVal.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X,annotation.getLocationX());
            annotationVal.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y,annotation.getLocationY());
            annotationVal.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME,memeID);

            database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE,null,annotationVal);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

}













