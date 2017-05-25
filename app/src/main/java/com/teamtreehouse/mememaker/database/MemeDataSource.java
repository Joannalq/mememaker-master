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

    //delete data: delete annotation first due to foreign key
    public void delete(int memeId){
        SQLiteDatabase database=open();
        database.beginTransaction();

        //1st param: talbe; 2nd Param:where clause; 3rd Param:not needed
        database.delete(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                String.format("%s=%s",MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME,String.valueOf(memeId)),
                null);
        //delete record on the actural meme table
        database.delete(MemeSQLiteHelper.MEMES_TAB,
                String.format("&s=%s", BaseColumns._ID,String.valueOf(memeId)),
                null);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    //read or query option to pull back all the memes stored
    public  ArrayList<Meme> read(){
        ArrayList<Meme> memes=readMemes();
        addMemeAnnotations(memes);
        return memes;
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

    public void addMemeAnnotations(ArrayList<Meme> memes){
        SQLiteDatabase database=open();
        for(Meme meme:memes){
            ArrayList<MemeAnnotation> annotations=new ArrayList<MemeAnnotation>();
            Cursor cursor=database.rawQuery(
                    "SELECT * FROM"+MemeSQLiteHelper.ANNOTATIONS_TABLE+
                            " WHERE MEME_ID="+meme.getId(),null
            );

            if(cursor.moveToFirst()){
                do{
                   MemeAnnotation annotation=new MemeAnnotation(getIntColumName(cursor,BaseColumns._ID),
                            getStringColumName(cursor,MemeSQLiteHelper.COLUMN_MEME_NAME),
                            getStringColumName(cursor,MemeSQLiteHelper.COLUMN_MEME_ASSET),
                            getIntColumName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_X),
                           getIntColumName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_Y));
                   annotations.add(annotation);
                }while (cursor.moveToNext());
            }
            meme.setAnnotations(annotations);
            cursor.close();
        }
        database.close();
    }

    public void update(Meme meme) {
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues updateMemeValues = new ContentValues();
        updateMemeValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME, meme.getName());
        database.update(MemeSQLiteHelper.MEMES_TAB,
                updateMemeValues,
                String.format("%s=%d", BaseColumns._ID, meme.getId()), null);
        for(MemeAnnotation annotation : meme.getAnnotations()) {
            ContentValues updateAnnotations = new ContentValues();
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, annotation.getTitle());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X, annotation.getLocationX());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y, annotation.getLocationY());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, meme.getId());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, annotation.getColor());

            if(annotation.hasBeenSaved()) {
                database.update(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                        updateAnnotations,
                        String.format("%s=%d", BaseColumns._ID, annotation.getId())
                        ,null);
            } else {
                database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                        null,
                        updateAnnotations);
            }
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
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













