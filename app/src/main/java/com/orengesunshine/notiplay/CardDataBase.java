package com.orengesunshine.notiplay;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hayatomoritani on 8/1/17.
 */

public class CardDataBase extends SQLiteOpenHelper {
    private static final String TAG = "dbtag";
    private static final String DB_NAME = "main_data_base";
    private static final String FOLDER_DB_TABLE = "folder_data_table";
    static final String CARD_DB_TABLE = "card_data_table";
    private static final String ID = "_id";
    private static final String FOLDER_COLUMN = "folder_object";
    private static final String CARD_COLUMN = "card_object";
    private static final String FOLDER_TITLE = "folder_title";
    private static final String FOLDER_LAST_EDIT = "folder_last_edit";
    private static final String FOLDER_NUMBER_OF_CARDS = "folder_number_of_cards";
    private static int version = 3;

    public CardDataBase(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("CREATE TABLE %s (%s integer primary key autoincrement,%s text,%s integer,%s integer) ",FOLDER_DB_TABLE,ID,FOLDER_TITLE,FOLDER_NUMBER_OF_CARDS,FOLDER_LAST_EDIT);
        db.execSQL(sql);
        String sql2 = String.format("CREATE TABLE %s (%s integer primary key autoincrement,%s text,%s text) ",CARD_DB_TABLE,ID,FOLDER_COLUMN,CARD_COLUMN);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <3){
//            String deleteCommand = String.format("DROP TABLE %s",FOLDER_DB_TABLE);
            String addColumnCommand = String.format("ALTER TABLE %s ADD COLUMN %s",FOLDER_DB_TABLE,FOLDER_COLUMN);
            db.execSQL(addColumnCommand);
        }
    }

    void makeNewFolder(String folderTitle){
        SQLiteDatabase db = getWritableDatabase();
//        Gson gson = new Gson();
//        String gsonFolder = gson.toJson(folder);
        String sql = String.format("INSERT INTO %s (%s)VALUES ('%s');",FOLDER_DB_TABLE,FOLDER_TITLE,folderTitle);
        Log.d(TAG, "makeNewFolder: "+sql);
        db.execSQL(sql);
        db.close();
    }
    void putBackFolder(Folder folder){
        SQLiteDatabase db = getWritableDatabase();
        String sql = String.format("INSERT INTO %s (%s,%s,%s) VALUES('%s',%s,%s)",
                FOLDER_DB_TABLE,FOLDER_TITLE,FOLDER_NUMBER_OF_CARDS,
                FOLDER_NUMBER_OF_CARDS,
                folder.getTitle(),folder.getNumberOfCards(),folder.getLastEdited().getTime());
        Log.d(TAG, "putBackFolder: "+sql);
        db.execSQL(sql);
    }

    void saveFolder(Folder folder){
        SQLiteDatabase db = getWritableDatabase();
        String sql = String.format("UPDATE %s SET %s = %s, %s = %s WHERE %s = '%s'",
                FOLDER_DB_TABLE,FOLDER_NUMBER_OF_CARDS,folder.getNumberOfCards(),
                FOLDER_LAST_EDIT,folder.getLastEdited().getTime(),FOLDER_TITLE,folder.getTitle());
        Log.d(TAG, "saveFolder: "+sql);
        db.execSQL(sql);
    }

    List<Folder> getFolders(){
        SQLiteDatabase db = getReadableDatabase();
        String sql = String.format("SELECT * FROM %s",FOLDER_DB_TABLE);
        Log.d(TAG, "getFolders: "+sql);
        Cursor cursor = db.rawQuery(sql,null);
        List<Folder> folders = new ArrayList<>();
        while (cursor.moveToNext()){
            Folder folder = new Folder(cursor.getString(1),cursor.getInt(2),new Time(cursor.getLong(3)));
            folders.add(folder);
        }
        cursor.close();
        db.close();
        return folders;
    }
    void editFolderName(String oldFolderName, String newFolderName){
        SQLiteDatabase db = getWritableDatabase();
        String sqlF = String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'",FOLDER_DB_TABLE,FOLDER_TITLE,newFolderName,FOLDER_TITLE,oldFolderName);
        String sqlC = String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'",CARD_DB_TABLE,FOLDER_COLUMN,newFolderName,FOLDER_COLUMN,oldFolderName);
        db.execSQL(sqlF);
        db.execSQL(sqlC);
    }

    void deleteFolder(String folderName){
        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",FOLDER_DB_TABLE,FOLDER_TITLE,folderName);
        getWritableDatabase().execSQL(sql);
    }
    void deleteCards(String folderName){
        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",CARD_DB_TABLE,FOLDER_COLUMN,folderName);
        getWritableDatabase().execSQL(sql);
    }


    public List<Card> getCards(String folderName){
        SQLiteDatabase db = getReadableDatabase();
//        String sql = String.format("SELECT * FROM %s",CARD_DB_TABLE);
        String sql = String.format("SELECT %s FROM %s WHERE %s = '%s'",CARD_COLUMN,CARD_DB_TABLE,FOLDER_COLUMN,folderName);
        Log.d(TAG, "DB getCards: "+sql);
        Cursor cursor = db.rawQuery(sql,null);
        Gson gson = new Gson();
        if (cursor.moveToFirst()){
            Log.d(TAG, "DB getCards: cursor is not null "+ cursor.getString(0));
            List<Card> cards = new ArrayList<>();
            do {
                String gsonCard = cursor.getString(0);
                cards.add(gson.fromJson(gsonCard,Card.class));
            }while (cursor.moveToNext());
//            Log.d(TAG, "db getCards: move to first"+cards.get(4).getFront());
            cursor.close();
            return cards;
        }else {
            Log.d(TAG, "DB getCards: cursor is null");
            cursor.close();
            return null;
        }

    }

    void saveCards(String folderName,List<Card> cards){
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, "saveCards: cardsize is "+cards.size());
//        Log.d(TAG, "db getCards: move to first"+cards.get(0).getFront());
        String delSql = String.format("DELETE FROM %s WHERE %s = '%s'",CARD_DB_TABLE,FOLDER_COLUMN,folderName);
        db.execSQL(delSql);
        Gson gson = new Gson();
        for (Card card:cards){
            String gsonCard = gson.toJson(card);
            String sql = String.format("INSERT INTO %s (%s,%s)VALUES('%s','%s')",CARD_DB_TABLE,FOLDER_COLUMN,CARD_COLUMN,folderName,gsonCard);
            db.execSQL(sql);
        }
    }

    void deleteTable(String tableName){
        SQLiteDatabase db = getWritableDatabase();
        String sql = String.format("DROP TABLE %s",tableName);
        db.execSQL(sql);
    }
}
