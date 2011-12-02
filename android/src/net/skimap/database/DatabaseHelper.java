package net.skimap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper 
{
	private static final String DATABASE_NAME = "skimap.db";
	private static final int DATABASE_VERSION = 1;
	
	// COUNTRY ////////////////////////////////////////////////////////////////////////////////////
	
	public static final String TAB_COUNTRY = "countries";
	public static final String TAB_COUNTRY_API_ID = "_id";
	public static final String TAB_COUNTRY_API_NAME = "name";
	public static final String TAB_COUNTRY_API_ISO = "iso";
	
	// SKICENTRE //////////////////////////////////////////////////////////////////////////////////
	
	public static final String TAB_SKICENTRE = "skicentres";
	public static final String TAB_SKICENTRE_API_ID = "_id";
	public static final String TAB_SKICENTRE_API_NAME = "name";
	public static final String TAB_SKICENTRE_API_COUNTRY_ID = "country";
	
	public static final String TAB_SKICENTRE_API_INFO_PEREX = "";
	public static final String TAB_SKICENTRE_API_INFO_PHONE = "";
	
	public static final String TAB_SKICENTRE_API_LOCATION_LATITUDE = "latitude";
	public static final String TAB_SKICENTRE_API_LOCATION_LONGITUDE = "longitude";
	public static final String TAB_SKICENTRE_API_LOCATION_ALTITUDE_UNDERMOST = "";
	public static final String TAB_SKICENTRE_API_LOCATION_ALTITUDE_TOPMOST = "";
	
	public static final String TAB_SKICENTRE_API_COUNT_LIFTS_OPENED = "";
	public static final String TAB_SKICENTRE_API_COUNT_DOWNHILLS_OPENED = "";
	
	public static final String TAB_SKICENTRE_API_DATE_SEASON_START = "";
	public static final String TAB_SKICENTRE_API_DATE_SEASON_END = "";
	
	public static final String TAB_SKICENTRE_API_LENGTH_CROSSCOUNTRY = "";
	public static final String TAB_SKICENTRE_API_LENGTH_DOWNHILLS_TOTAL = "";
	public static final String TAB_SKICENTRE_API_LENGTH_DOWNHILLS_BLUE_PERCENT = "";
	public static final String TAB_SKICENTRE_API_LENGTH_DOWNHILLS_RED_PERCENT = "";
	public static final String TAB_SKICENTRE_API_LENGTH_DOWNHILLS_BLACK_PERCENT = "";

	public static final String TAB_SKICENTRE_API_FLAG_OPENED = "opened";
	public static final String TAB_SKICENTRE_API_FLAG_NIGHTSKI = "";
	public static final String TAB_SKICENTRE_API_FLAG_VALLEY = "";
	public static final String TAB_SKICENTRE_API_FLAG_SNOWPARK = "";
	public static final String TAB_SKICENTRE_API_FLAG_HALFPIPE = "";
	
	public static final String TAB_SKICENTRE_API_PRICE_ADULTS_1 = "";
	public static final String TAB_SKICENTRE_API_PRICE_ADULTS_6 = "";
	public static final String TAB_SKICENTRE_API_PRICE_CHILDREN_1 = "";
	public static final String TAB_SKICENTRE_API_PRICE_CHILDREN_6 = "";
	public static final String TAB_SKICENTRE_API_PRICE_YOUNG_1 = "";
	public static final String TAB_SKICENTRE_API_PRICE_YOUNG_6 = "";
	public static final String TAB_SKICENTRE_API_PRICE_SENIORS_1 = "";
	public static final String TAB_SKICENTRE_API_PRICE_SENIORS_6 = "";
	public static final String TAB_SKICENTRE_API_PRICE_CURRENCY = "";

	public static final String TAB_SKICENTRE_API_URL_HOMEPAGE = "";
	public static final String TAB_SKICENTRE_API_URL_ATLAS = "";
	public static final String TAB_SKICENTRE_API_URL_WEBCAMS = "";
	public static final String TAB_SKICENTRE_API_URL_SNOW_REPORT = "";
	public static final String TAB_SKICENTRE_API_URL_WEATHER_REPORT = "";
	public static final String TAB_SKICENTRE_API_URL_IMG_MAP = "";
	public static final String TAB_SKICENTRE_API_URL_IMG_METEOGRAM = "";
	public static final String TAB_SKICENTRE_API_URL_IMG_WEBCAM = "";
	
	public static final String TAB_SKICENTRE_API_SNOW_MIN = "snow";
	public static final String TAB_SKICENTRE_API_SNOW_MAX = "";
	public static final String TAB_SKICENTRE_API_SNOW_LENGTH_COVERING = "";
	public static final String TAB_SKICENTRE_API_SNOW_DATE_LAST_SNOW = "";
	public static final String TAB_SKICENTRE_API_SNOW_DATE_LAST_UPDATE = "";
	
	public static final String TAB_SKICENTRE_API_WEATHER_1_DATE = "";
	public static final String TAB_SKICENTRE_API_WEATHER_1_SYMBOL_NAME = "";
	public static final String TAB_SKICENTRE_API_WEATHER_1_SYMBOL_URL = "";
	public static final String TAB_SKICENTRE_API_WEATHER_1_PRECIPATION = "";
	public static final String TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MIN = "";
	public static final String TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MAX = "";

	public static final String TAB_SKICENTRE_APP_FLAG_FAVOURITE = "";
	public static final String TAB_SKICENTRE_APP_DATE_LAST_UPDATE = "";
	
	
	public static final String[] COLS_COUNTRY =
	{
		DatabaseHelper.TAB_COUNTRY_API_ID,
		DatabaseHelper.TAB_COUNTRY_API_NAME
	};
	
	public static final String[] COLS_SKICENTRE_SHORT =
	{
		DatabaseHelper.TAB_SKICENTRE_API_ID,
		DatabaseHelper.TAB_SKICENTRE_API_NAME,
		DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LATITUDE,
		DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LONGITUDE,
		DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_ID,
		DatabaseHelper.TAB_SKICENTRE_API_FLAG_OPENED,
		DatabaseHelper.TAB_SKICENTRE_API_SNOW_MIN
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
		TAB_SKICENTRE_API_LOCATION_LATITUDE + " REAL," +
		TAB_SKICENTRE_API_LOCATION_LONGITUDE + " REAL," +
		TAB_SKICENTRE_API_COUNTRY_ID + " INTEGER," +
		TAB_SKICENTRE_API_FLAG_OPENED + " NUMERIC," +
		TAB_SKICENTRE_API_SNOW_MIN + " INTEGER," +
		"FOREIGN KEY(" + TAB_SKICENTRE_API_COUNTRY_ID + ") REFERENCES " + TAB_COUNTRY + "(" + TAB_COUNTRY_API_ID + "));";

		// vykonani SQL prikazu
		db.execSQL("PRAGMA foreign_keys=ON;");
		db.execSQL(sqlCountry);
		db.execSQL(sqlSkicentre);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// zde bude kod pro osetreni zmeny struktury sqlite databaze pri update
	}
}
