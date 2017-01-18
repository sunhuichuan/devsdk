package com.yao.devsdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.yao.devsdk.log.CustomCrashHandler;
import com.yao.devsdk.log.LogUtil;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 数据库openHelper
 *
 * <p>
 * 增加一个数据库表,需要三步操作
 * 一：在{@link #addTableClassToList}方法中给需要创建数据库表集合添加对应类;
 * 二：增加数据库版本号，一般 +1
 * 三：在{@link ORMLiteUpdateHelper#onUpgrade(SQLiteDatabase, ConnectionSource, int, int)}方法中根据版本号单独增加表的操作;
 * </p>
 * <p>
 * 删除一个数据库表，同增加一样三步操作，创建表改为删除即可
 * </p>
 */
public abstract class ORMLiteOpenHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "ORMLiteOpenHelper";

    private Context context;

    private ORMLiteUpdateHelper mUpdateHelper;

    /**
     * 需要数据存储的类集合
     */
    protected List<Class> mTableClassList = new ArrayList<>();
    /**
     * 当清除缓存时可以清空的数据库表
     */
    protected List<Class> mClearCacheTableClassList = new ArrayList<>();

    /**
     * 需要有数据存储对象的dao集合
     * the DAO object we use to access the tables
     */
    protected Map<String,RuntimeExceptionDao> mDaoMap = new HashMap<>();



    public ORMLiteOpenHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
        this.context = context;
        try {
            mUpdateHelper = createUpgradeHelper(this);

            //填充所有准备创建，和可以清空的数据库表集合
            mTableClassList.clear();
            mClearCacheTableClassList.clear();
            addTableClassToList(mTableClassList, mClearCacheTableClassList);

        } catch (SQLException e) {
            LogUtil.e(TAG, "创建UpgradeHelper异常", e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            LogUtil.d(TAG, "onCreate");

            //创建所有的数据库表
            for (Class clazz : mTableClassList){
                TableUtils.createTable(connectionSource, clazz);
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "Can't create database", e);
//            throw new RuntimeException(e);
            if (context!=null){
                CustomCrashHandler.saveExceptionLog(context,"ORMLiteOpenHelper 在onCreate的时候崩溃",e);
            }
        }

    }

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        LogUtil.d(TAG, "onUpgrade");
        try {

            if (mUpdateHelper !=null){
                mUpdateHelper.onUpgrade(db,connectionSource,oldVersion,newVersion);
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "Can't drop databases", e);
            try {
                //更新异常时，重建所有的表
                mUpdateHelper.onUpdateDefault(db,connectionSource);
            }catch (Exception err){
                if (context!=null){
                    CustomCrashHandler.saveExceptionLog(context,"ORMLiteOpenHelper 在onUpgrade的重建时候崩溃",e);
                }
            }
//            throw new RuntimeException(e);
            if (context!=null){
                CustomCrashHandler.saveExceptionLog(context,"ORMLiteOpenHelper 在onUpgrade的时候崩溃",e);
            }
        }

    }


    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao
     * for our classes. It will create it or just give the cached value.
     * RuntimeExceptionDao only through RuntimeExceptions.
     */
    public <T,ID> RuntimeExceptionDao<T, ID> getRuntimeDao(Class<T> clazz) {

        String className = clazz.getSimpleName();

        @SuppressWarnings({ "unchecked", "rawtypes" })
        RuntimeExceptionDao<T,ID>  dao = mDaoMap.get(className);
        if (dao == null){
            dao = getRuntimeExceptionDao(clazz);
            mDaoMap.put(className, dao);
        }
        return dao;
    }


    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        mDaoMap.clear();
    }


    /**
     * 把类分别添加到【需要创建的数据库表集合】和【清除缓存是可以清空的数据库表集合】
     *
     * @param tableClassList 需要创建数据库表的类集合
     * @param clearCacheTableClassList 清除缓存时可以清除的数据库表
     */
    protected abstract void addTableClassToList(List<Class> tableClassList,List<Class> clearCacheTableClassList);

    /**
     * onUpgrad的方法的包装调用类
     * @throws SQLException
     */
    protected abstract ORMLiteUpdateHelper createUpgradeHelper(ORMLiteOpenHelper openHelper) throws SQLException;


    /**
     * 当更新失败时，drop掉所有的数据库表
     * @param db
     * @param connectionSource
     * @throws SQLException
     */
    protected void dropAllTables(SQLiteDatabase db, ConnectionSource connectionSource) throws SQLException{
        for (Class clazz : mTableClassList){
            TableUtils.dropTable(connectionSource, clazz, false);
        }
    }

    /**
     * 当清除缓存时需要清除的数据库表
     *
     * 清空所有与账户无关的数据库表
     * 以产品需求为主，如果产品需求要删除所有表，则全删除
     * @param db
     * @param connectionSource
     * @throws SQLException
     */
    protected void clearTablesWhenClearCache(SQLiteDatabase db, ConnectionSource connectionSource) throws SQLException{
        for (Class clazz : mClearCacheTableClassList){
            TableUtils.clearTable(connectionSource, clazz);
        }

    }




    /**
     * Ormlite事物处理
     *
     * @param callback
     * @return
     */
    public <T> T callInTransaction(Callable<T> callback) {
        try {
            TransactionManager manager = new TransactionManager(getConnectionSource());
            return manager.callInTransaction(callback);
        } catch (SQLException e) {
            LogUtil.e(TAG, "执行事务异常",e);
//            throw new RuntimeException(e);
            if (context!=null){
                CustomCrashHandler.saveExceptionLog(context,"ORMLiteOpenHelper 在callInTransaction的时候崩溃",e);
            }
        }

        return null;
    }
}
