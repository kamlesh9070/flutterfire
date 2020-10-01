package org.dadabhagwan.AKonnect.dbo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.lang.Exception;

import org.dadabhagwan.AKonnect.dbo.model.NotificationLogTO;


public class DBHelper extends SQLiteOpenHelper {

  protected static final String TAG = "AKonnect[DBHelper]";

  // Database Version
  private static final int DATABASE_VERSION = 1;

  // Database Name
  private static final String DATABASE_NAME = "notification_db";

  private static DBHelper mInstance = null;

  public static synchronized DBHelper getInstance(Context ctx) {

    // Use the application context, which will ensure that you
    // don't accidentally leak an Activity's context.
    // See this article for more information: http://bit.ly/6LRzfx

    if (mInstance == null) {
      mInstance = new DBHelper(ctx.getApplicationContext());
    }
    return mInstance;
  }

  /**
   * Constructor should be private to prevent direct instantiation.
   * make call to static factory method "getInstance()" instead.
   */
  private DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Creating Tables
  @Override
  public void onCreate(SQLiteDatabase db) {
    // create notes table
    db.execSQL(NotificationLogTO.CREATE_TABLE);
  }

  // Upgrading database
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older table if existed
    db.execSQL("DROP TABLE IF EXISTS " + NotificationLogTO.TABLE_NAME);

    // Create tables again
    onCreate(db);
  }

  public int insertNotificationLog(int msgId) {
    int isMsgInserted = 1;
    Log.d(TAG, " inside insertNotificationLog : ");
    SQLiteDatabase db = null;
    try {
      // get writable database as we want to write data
      db = this.getWritableDatabase();

      ContentValues values = new ContentValues();
      // `timestamp` will be inserted automatically. no need to add timestamp
      values.put(NotificationLogTO.COLUMN_ID, msgId);

      // insert row
      long id = db.insert(NotificationLogTO.TABLE_NAME, null, values);
    } catch (Exception e) {
      Log.e(TAG, " Exception insertNotificationLog : " + e.getMessage());
      isMsgInserted = 0;
    } finally {
      if (db != null) {
        db.close();
      }
    }
    Log.d(TAG, " insertNotificationLog : isMsgInserted --> " + isMsgInserted);
    // return newly inserted row id
    return isMsgInserted;
  }

  public boolean getNotificationLogByMsgId(long id) {
    boolean isMsgIdExist = false;
    Cursor cursor = null;
    try {
      // get readable database as we are not inserting anything
      SQLiteDatabase db = this.getReadableDatabase();
      String getMsgIdQuery = "SELECT 1 FROM " + NotificationLogTO.TABLE_NAME + " WHERE " + NotificationLogTO.COLUMN_ID + " = " + id;
      cursor = db.rawQuery(getMsgIdQuery, null);

      if (cursor != null && cursor.getCount() > 0) {
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0)
          isMsgIdExist = true;
      }
    } catch (Exception e) {
      Log.e(TAG, " Exception getNotificationLogByMsgId : " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }
    Log.d(TAG, " getNotificationLogByMsgId : isMsgIdExist --> " + isMsgIdExist);
    return isMsgIdExist;
  }

  public int getMaxMsgIdFromNotificationMaster() {
    int maxMsgId = 0;
    Cursor cursor = null;
    try {
      String maxMsgIdQuery = "SELECT  max(" + NotificationLogTO.COLUMN_ID + ") FROM " + NotificationLogTO.TABLE_NAME;
      SQLiteDatabase db = this.getReadableDatabase();
      cursor = db.rawQuery(maxMsgIdQuery, null);

      if (cursor != null && cursor.getCount() > 0) {
        cursor.moveToFirst();
        maxMsgId = cursor.getInt(0);
      }
      cursor.close();
    } catch (Exception e) {
      Log.e(TAG, " Exception getMaxMsgIdFromNotificationMaster : " + e.getMessage());
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }

    Log.d(TAG, " getMaxMsgIdFromNotificationMaster : maxMsgId --> " + maxMsgId);
    // return maxMsgId
    return maxMsgId;
  }


}
