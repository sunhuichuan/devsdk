package com.yao.devsdk.db;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;

/**
 * 数据库更新的Helper
 * 封装了更新所需的一些方法，不再全都挤到SqliteOpenHelper中
 *
 * Created by huichuan on 16/4/18.
 */
public abstract class ORMLiteUpdateHelper {



    protected ORMLiteOpenHelper mDbHelper;

    public ORMLiteUpdateHelper(ORMLiteOpenHelper dbHelper) {
        mDbHelper = dbHelper;
    }


    ORMLiteOpenHelper getDBHelper(){
        return mDbHelper;
    }


    /**
     * onUpdate的默认操作
     * drop掉,并重新创建所有的table
     * @param db
     * @param connectionSource
     * @throws SQLException
     */
    protected void onUpdateDefault(SQLiteDatabase db, ConnectionSource connectionSource) throws SQLException{
        //清除所有数据库表
        mDbHelper.dropAllTables(db,connectionSource);
        // after we drop the old databases, we create the new ones
        mDbHelper.onCreate(db,connectionSource);
    }


    /**
     * 更新方法,该方法中的异常不要捕获,让上层处理
     * @param db
     * @param connectionSource
     * @param oldVersion
     * @param newVersion
     * @throws SQLException
     */
    public abstract void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) throws SQLException;


}
