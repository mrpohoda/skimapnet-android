package net.skimap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper 
{
	private static final String DATABASE_NAME = "skimap.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TAB_COUNTRY = "countries";
	public static final String TAB_COUNTRY_API_ID = "_id";
	public static final String TAB_COUNTRY_API_NAME = "name";
	
	public static final String TAB_SKICENTRE = "skicentres";
	public static final String TAB_SKICENTRE_API_ID = "_id";
	public static final String TAB_SKICENTRE_API_NAME = "name";
	public static final String TAB_SKICENTRE_API_LATITUDE = "latitude";
	public static final String TAB_SKICENTRE_API_LONGITUDE = "longitude";
	public static final String TAB_SKICENTRE_API_COUNTRY_FK = "country";
	public static final String TAB_SKICENTRE_API_OPENED = "opened";
	public static final String TAB_SKICENTRE_API_SNOW = "snow";
	
	public static final String[] COLS_COUNTRY =
	{
		DatabaseHelper.TAB_COUNTRY_API_ID,
		DatabaseHelper.TAB_COUNTRY_API_NAME
	};
	
	public static final String[] COLS_SKICENTRE =
	{
		DatabaseHelper.TAB_SKICENTRE_API_ID,
		DatabaseHelper.TAB_SKICENTRE_API_NAME,
		DatabaseHelper.TAB_SKICENTRE_API_LATITUDE,
		DatabaseHelper.TAB_SKICENTRE_API_LONGITUDE,
		DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_FK,
		DatabaseHelper.TAB_SKICENTRE_API_OPENED,
		DatabaseHelper.TAB_SKICENTRE_API_SNOW
	};

	
	public DatabaseHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	
	@Override
	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);
		
		if (!db.isReadOnly())
		{
			// podpora cizich klicu
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}


	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		// tabulka statu
		String sqlCountry = "CREATE TABLE " + TAB_COUNTRY + "(" +
		TAB_COUNTRY_API_ID + " INTEGER PRIMARY KEY," +
		TAB_COUNTRY_API_NAME + " TEXT NOT NULL);";
				
		// tabulka skicenter
		String sqlSkicentre = "CREATE TABLE " + TAB_SKICENTRE + "(" +
		TAB_SKICENTRE_API_ID + " INTEGER PRIMARY KEY," +
		TAB_SKICENTRE_API_NAME + " TEXT NOT NULL," +
		TAB_SKICENTRE_API_LATITUDE + " REAL," +
		TAB_SKICENTRE_API_LONGITUDE + " REAL," +
		TAB_SKICENTRE_API_COUNTRY_FK + " INTEGER," +
		TAB_SKICENTRE_API_OPENED + " NUMERIC," +
		TAB_SKICENTRE_API_SNOW + " INTEGER," +
		"FOREIGN KEY(" + TAB_SKICENTRE_API_COUNTRY_FK + ") REFERENCES " + TAB_COUNTRY + "(" + TAB_COUNTRY_API_ID + "));";

		// vykonani SQL prikazu
		db.execSQL("PRAGMA foreign_keys=ON;");
		db.execSQL(sqlCountry);
		db.execSQL(sqlSkicentre);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
	}
}
