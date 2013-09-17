package com.youtell.backdoor.models;

import java.sql.SQLException;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.squareup.otto.Produce;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database extends OrmLiteSqliteOpenHelper {
	// name of the database file for your application -- change to something appropriate for your app
	private static String databaseName = null;
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;
	public static final int REMOTE_ID_NULL = -1;

	static {
		OpenHelperManager.setOpenHelperClass(Database.class);
	}

	@SuppressLint("DefaultLocale")
	public static void setDatabaseForUser(int userID)
	{
		databaseName = String.format("backdoor-%d.db", userID);
	}	

	public Database(Context context) {
		super(context, databaseName, null, DATABASE_VERSION);
		Log.v("ORM", String.format("ORM DB Constructed with trace"));
		try {
			createDAOs();
			ModelBus.events.register(this);
		} catch(SQLException e) {
			Log.v("ORM", "DAO CREATE FAILED", e);
		}
	}

	@Produce public Database produceDB()
	{
		return this;
	}

	@Override
	public void close()
	{
		super.close();
		ModelBus.events.post(new DBClosedEvent());
		ModelBus.events.unregister(this);
		closeDAOs();		
	}

	@Override
	public void onOpen(SQLiteDatabase DB) 
	{
		Log.v("ORM", "Open");
		try {
			
			AndroidConnectionSource dsource = new AndroidConnectionSource(DB);
			dsource.saveSpecialConnection(new AndroidDatabaseConnection(DB, true));
			dropTables(dsource);
			createTables(dsource);
		}
		catch(Exception e) {
			android.util.Log.e("ORM," , "Your Message", e);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase DB, ConnectionSource connectionSource) {
		try {
			Log.v("ORM", "onCreate");

			createTables(connectionSource);			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase DB, ConnectionSource connection, int oldV,
			int newV) {
		try {		
			dropTables(connection);								
			onCreate(DB, connection);			
		} catch(SQLException e) {
			e.printStackTrace();
		}

	}

	public Dao<Gab, Integer> gabDAO;
	public Dao<Message, Integer> messageDAO;
	public Dao<Friend, Integer> friendDAO;
	
	private void createDAOs() throws SQLException {
		gabDAO = getDao(Gab.class);
		messageDAO = getDao(Message.class);
		friendDAO = getDao(Friend.class);
	}
	
	private void closeDAOs() {
		gabDAO = null;
		messageDAO = null;
		friendDAO = null;
	}
	
	private void createTables(ConnectionSource connectionSource) throws SQLException {
		TableUtils.createTable(connectionSource, Gab.class);
		TableUtils.createTable(connectionSource, Message.class);
		TableUtils.createTable(connectionSource, Friend.class);		
	}

	private void dropTables(ConnectionSource connection) throws SQLException {
		TableUtils.dropTable(connection, Gab.class, true);
		TableUtils.dropTable(connection, Message.class, true);
		TableUtils.dropTable(connection, Friend.class, true);	
	}

}
