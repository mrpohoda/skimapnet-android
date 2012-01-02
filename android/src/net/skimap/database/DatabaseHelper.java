package net.skimap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper 
{
	private static final String DATABASE_NAME = "skimap.db";
	private static final int DATABASE_VERSION = 1;
	
	// AREA ///////////////////////////////////////////////////////////////////////////////////////
	
	public static final String TAB_AREA = "areas";
	public static final String TAB_AREA_API_ID = "id";
	public static final String TAB_AREA_API_NAME = "name";
	
	// COUNTRY ////////////////////////////////////////////////////////////////////////////////////
	
	public static final String TAB_COUNTRY = "countries";
	public static final String TAB_COUNTRY_API_ID = "id";
	public static final String TAB_COUNTRY_API_NAME = "name";
	public static final String TAB_COUNTRY_API_ISO_CODE = "iso_code";
	
	// SKICENTRE //////////////////////////////////////////////////////////////////////////////////
	
	public static final String TAB_SKICENTRE = "skicentres";
	public static final String TAB_SKICENTRE_API_ID = "id";
	public static final String TAB_SKICENTRE_API_NAME = "name";
	public static final String TAB_SKICENTRE_API_AREA_ID = "area_id";
	public static final String TAB_SKICENTRE_API_COUNTRY_ID = "country_id";
	
	public static final String TAB_SKICENTRE_API_INFO_PEREX = "info_perex";
	public static final String TAB_SKICENTRE_API_INFO_PHONE = "info_phone";
	
	public static final String TAB_SKICENTRE_API_DATE_SEASON_START = "date_season_start";
	public static final String TAB_SKICENTRE_API_DATE_SEASON_END = "date_season_end";
	
	public static final String TAB_SKICENTRE_API_LOCATION_LATITUDE = "lat";
	public static final String TAB_SKICENTRE_API_LOCATION_LONGITUDE = "lng";
	public static final String TAB_SKICENTRE_API_LOCATION_ALTITUDE_UNDERMOST = "location_altitude_undermost";
	public static final String TAB_SKICENTRE_API_LOCATION_ALTITUDE_TOPMOST = "location_altitude_topmost";

	public static final String TAB_SKICENTRE_API_COUNT_LIFTS_OPENED = "count_lifts_opened";
	public static final String TAB_SKICENTRE_API_COUNT_DOWNHILLS_OPENED = "count_downhills_opened";

	public static final String TAB_SKICENTRE_API_LENGTH_CROSSCOUNTRY = "length_crosscountry";
	public static final String TAB_SKICENTRE_API_LENGTH_DOWNHILLS_TOTAL = "length_downhills_total";
	public static final String TAB_SKICENTRE_API_LENGTH_DOWNHILLS_BLUE_PERCENT = "length_downhills_blue_percent";
	public static final String TAB_SKICENTRE_API_LENGTH_DOWNHILLS_RED_PERCENT = "length_downhills_red_percent";
	public static final String TAB_SKICENTRE_API_LENGTH_DOWNHILLS_BLACK_PERCENT = "length_downhills_black_percent";

	public static final String TAB_SKICENTRE_API_FLAG_OPENED = "flag_opened";
	public static final String TAB_SKICENTRE_API_FLAG_NIGHTSKI = "flag_nightski";
	public static final String TAB_SKICENTRE_API_FLAG_VALLEY = "flag_valley";
	public static final String TAB_SKICENTRE_API_FLAG_SNOWPARK = "flag_snowpark";
	public static final String TAB_SKICENTRE_API_FLAG_HALFPIPE = "flag_halfpipe";
	
	public static final String TAB_SKICENTRE_API_PRICE_ADULTS_1 = "price_adults_1";
	public static final String TAB_SKICENTRE_API_PRICE_ADULTS_6 = "price_adults_6";
	public static final String TAB_SKICENTRE_API_PRICE_CHILDREN_1 = "price_children_1";
	public static final String TAB_SKICENTRE_API_PRICE_CHILDREN_6 = "price_children_6";
	public static final String TAB_SKICENTRE_API_PRICE_YOUNG_1 = "price_young_1";
	public static final String TAB_SKICENTRE_API_PRICE_YOUNG_6 = "price_young_6";
	public static final String TAB_SKICENTRE_API_PRICE_SENIORS_1 = "price_seniors_1";
	public static final String TAB_SKICENTRE_API_PRICE_SENIORS_6 = "price_seniors_6";
	public static final String TAB_SKICENTRE_API_PRICE_CURRENCY = "price_currency";

	public static final String TAB_SKICENTRE_API_URL_SKIMAP = "url_skimap";
	public static final String TAB_SKICENTRE_API_URL_HOMEPAGE = "url_homepage";
	public static final String TAB_SKICENTRE_API_URL_ATLAS = "url_atlas";
	public static final String TAB_SKICENTRE_API_URL_WEBCAMS = "url_webcams";
	public static final String TAB_SKICENTRE_API_URL_SNOW_REPORT = "url_snow_report";
	public static final String TAB_SKICENTRE_API_URL_WEATHER_REPORT = "url_weather_report";
	public static final String TAB_SKICENTRE_API_URL_IMG_MAP = "url_img_map";
	public static final String TAB_SKICENTRE_API_URL_IMG_METEOGRAM = "url_img_meteogram";
	public static final String TAB_SKICENTRE_API_URL_IMG_WEBCAM = "url_img_webcam";
	
	public static final String TAB_SKICENTRE_API_SNOW_MIN = "snow_min";
	public static final String TAB_SKICENTRE_API_SNOW_MAX = "snow_max";
	public static final String TAB_SKICENTRE_API_SNOW_LENGTH_COVERING = "snow_length_covering";
	public static final String TAB_SKICENTRE_API_SNOW_DATE_LAST_SNOW = "snow_date_last_snow";
	public static final String TAB_SKICENTRE_API_SNOW_DATE_LAST_UPDATE = "snow_date_last_update";
	
	public static final String TAB_SKICENTRE_API_WEATHER_1_DATE = "weather_1_date";
	public static final String TAB_SKICENTRE_API_WEATHER_1_SYMBOL_NAME = "weather_1_symbol_name";
	public static final String TAB_SKICENTRE_API_WEATHER_1_SYMBOL_URL = "weather_1_symbol_url";
	public static final String TAB_SKICENTRE_API_WEATHER_1_PRECIPATION = "weather_1_precipation";
	public static final String TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MIN = "weather_1_temperature_min";
	public static final String TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MAX = "weather_1_temperature_max";
	public static final String TAB_SKICENTRE_API_WEATHER_2_DATE = "weather_2_date";
	public static final String TAB_SKICENTRE_API_WEATHER_2_SYMBOL_NAME = "weather_2_symbol_name";
	public static final String TAB_SKICENTRE_API_WEATHER_2_SYMBOL_URL = "weather_2_symbol_url";
	public static final String TAB_SKICENTRE_API_WEATHER_2_PRECIPATION = "weather_2_precipation";
	public static final String TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MIN = "weather_2_temperature_min";
	public static final String TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MAX = "weather_2_temperature_max";
	public static final String TAB_SKICENTRE_API_WEATHER_3_DATE = "weather_3_date";
	public static final String TAB_SKICENTRE_API_WEATHER_3_SYMBOL_NAME = "weather_3_symbol_name";
	public static final String TAB_SKICENTRE_API_WEATHER_3_SYMBOL_URL = "weather_3_symbol_url";
	public static final String TAB_SKICENTRE_API_WEATHER_3_PRECIPATION = "weather_3_precipation";
	public static final String TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MIN = "weather_3_temperature_min";
	public static final String TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MAX = "weather_3_temperature_max";
	public static final String TAB_SKICENTRE_API_WEATHER_4_DATE = "weather_4_date";
	public static final String TAB_SKICENTRE_API_WEATHER_4_SYMBOL_NAME = "weather_4_symbol_name";
	public static final String TAB_SKICENTRE_API_WEATHER_4_SYMBOL_URL = "weather_4_symbol_url";
	public static final String TAB_SKICENTRE_API_WEATHER_4_PRECIPATION = "weather_4_precipation";
	public static final String TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MIN = "weather_4_temperature_min";
	public static final String TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MAX = "weather_4_temperature_max";
	public static final String TAB_SKICENTRE_API_WEATHER_5_DATE = "weather_5_date";
	public static final String TAB_SKICENTRE_API_WEATHER_5_SYMBOL_NAME = "weather_5_symbol_name";
	public static final String TAB_SKICENTRE_API_WEATHER_5_SYMBOL_URL = "weather_5_symbol_url";
	public static final String TAB_SKICENTRE_API_WEATHER_5_PRECIPATION = "weather_5_precipation";
	public static final String TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MIN = "weather_5_temperature_min";
	public static final String TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MAX = "weather_5_temperature_max";
	public static final String TAB_SKICENTRE_API_WEATHER_6_DATE = "weather_6_date";
	public static final String TAB_SKICENTRE_API_WEATHER_6_SYMBOL_NAME = "weather_6_symbol_name";
	public static final String TAB_SKICENTRE_API_WEATHER_6_SYMBOL_URL = "weather_6_symbol_url";
	public static final String TAB_SKICENTRE_API_WEATHER_6_PRECIPATION = "weather_6_precipation";
	public static final String TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MIN = "weather_6_temperature_min";
	public static final String TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MAX = "weather_6_temperature_max";

	public static final String TAB_SKICENTRE_APP_FLAG_FAVOURITE = "flag_favourite";
	public static final String TAB_SKICENTRE_APP_DATE_LAST_UPDATE = "date_last_update";
	
	// NULL VALUES ////////////////////////////////////////////////////////////////////////////////
	
	public static final int NULL_INT = -1;
	public static final String NULL_STRING = null;
	public static final double NULL_DOUBLE = -1;
	public static final boolean NULL_BOOLEAN = false;
	
	
	public static final String[] COLS_AREA =
	{
		DatabaseHelper.TAB_AREA_API_ID,
		DatabaseHelper.TAB_AREA_API_NAME,
	};
	
	
	public static final String[] COLS_COUNTRY =
	{
		DatabaseHelper.TAB_COUNTRY_API_ID,
		DatabaseHelper.TAB_COUNTRY_API_NAME,
		DatabaseHelper.TAB_COUNTRY_API_ISO_CODE
	};
	
	
	public static final String[] COLS_SKICENTRE_SHORT =
	{
		DatabaseHelper.TAB_SKICENTRE_API_ID,
		DatabaseHelper.TAB_SKICENTRE_API_NAME,
		DatabaseHelper.TAB_SKICENTRE_API_AREA_ID,
		DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_ID,
		DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LATITUDE,
		DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LONGITUDE,
		DatabaseHelper.TAB_SKICENTRE_API_FLAG_OPENED,
		DatabaseHelper.TAB_SKICENTRE_API_SNOW_MAX
	};
	
	
	public static final String[] COLS_SKICENTRE_LONG =
	{
		DatabaseHelper.TAB_SKICENTRE_API_ID,
		DatabaseHelper.TAB_SKICENTRE_API_NAME,
		DatabaseHelper.TAB_SKICENTRE_API_AREA_ID,
		DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_ID,
		DatabaseHelper.TAB_SKICENTRE_API_INFO_PEREX,
		//DatabaseHelper.TAB_SKICENTRE_API_INFO_PHONE,
		DatabaseHelper.TAB_SKICENTRE_API_DATE_SEASON_START,
		DatabaseHelper.TAB_SKICENTRE_API_DATE_SEASON_END,
		DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LATITUDE,
		DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LONGITUDE,
		DatabaseHelper.TAB_SKICENTRE_API_LOCATION_ALTITUDE_UNDERMOST,
		DatabaseHelper.TAB_SKICENTRE_API_LOCATION_ALTITUDE_TOPMOST,
		DatabaseHelper.TAB_SKICENTRE_API_COUNT_LIFTS_OPENED,
		DatabaseHelper.TAB_SKICENTRE_API_COUNT_DOWNHILLS_OPENED,
		DatabaseHelper.TAB_SKICENTRE_API_LENGTH_CROSSCOUNTRY,
		DatabaseHelper.TAB_SKICENTRE_API_LENGTH_DOWNHILLS_TOTAL,
		//DatabaseHelper.TAB_SKICENTRE_API_LENGTH_DOWNHILLS_BLUE_PERCENT,
		//DatabaseHelper.TAB_SKICENTRE_API_LENGTH_DOWNHILLS_RED_PERCENT,
		//DatabaseHelper.TAB_SKICENTRE_API_LENGTH_DOWNHILLS_BLACK_PERCENT,
		DatabaseHelper.TAB_SKICENTRE_API_FLAG_OPENED,
		DatabaseHelper.TAB_SKICENTRE_API_FLAG_NIGHTSKI,
		DatabaseHelper.TAB_SKICENTRE_API_FLAG_VALLEY,
		DatabaseHelper.TAB_SKICENTRE_API_FLAG_SNOWPARK,
		DatabaseHelper.TAB_SKICENTRE_API_FLAG_HALFPIPE,
		DatabaseHelper.TAB_SKICENTRE_API_PRICE_ADULTS_1,
		DatabaseHelper.TAB_SKICENTRE_API_PRICE_ADULTS_6,
		DatabaseHelper.TAB_SKICENTRE_API_PRICE_CHILDREN_1,
		DatabaseHelper.TAB_SKICENTRE_API_PRICE_CHILDREN_6,
		DatabaseHelper.TAB_SKICENTRE_API_PRICE_YOUNG_1,
		DatabaseHelper.TAB_SKICENTRE_API_PRICE_YOUNG_6,
		DatabaseHelper.TAB_SKICENTRE_API_PRICE_SENIORS_1,
		DatabaseHelper.TAB_SKICENTRE_API_PRICE_SENIORS_6,
		DatabaseHelper.TAB_SKICENTRE_API_PRICE_CURRENCY,
		DatabaseHelper.TAB_SKICENTRE_API_URL_SKIMAP,
		DatabaseHelper.TAB_SKICENTRE_API_URL_HOMEPAGE,
		//DatabaseHelper.TAB_SKICENTRE_API_URL_ATLAS,
		DatabaseHelper.TAB_SKICENTRE_API_URL_WEBCAMS,
		DatabaseHelper.TAB_SKICENTRE_API_URL_SNOW_REPORT,
		DatabaseHelper.TAB_SKICENTRE_API_URL_WEATHER_REPORT,
		DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_MAP,
		DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_METEOGRAM,
		DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_WEBCAM,
		DatabaseHelper.TAB_SKICENTRE_API_SNOW_MIN,
		DatabaseHelper.TAB_SKICENTRE_API_SNOW_MAX,
		//DatabaseHelper.TAB_SKICENTRE_API_SNOW_LENGTH_COVERING,
		DatabaseHelper.TAB_SKICENTRE_API_SNOW_DATE_LAST_SNOW,
		DatabaseHelper.TAB_SKICENTRE_API_SNOW_DATE_LAST_UPDATE,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_DATE,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_SYMBOL_NAME,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_SYMBOL_URL,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_PRECIPATION,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MIN,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MAX,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_DATE,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_SYMBOL_NAME,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_SYMBOL_URL,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_PRECIPATION,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MIN,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MAX,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_DATE,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_SYMBOL_NAME,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_SYMBOL_URL,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_PRECIPATION,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MIN,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MAX,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_DATE,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_SYMBOL_NAME,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_SYMBOL_URL,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_PRECIPATION,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MIN,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MAX,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_DATE,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_SYMBOL_NAME,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_SYMBOL_URL,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_PRECIPATION,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MIN,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MAX,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_DATE,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_SYMBOL_NAME,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_SYMBOL_URL,
		//DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_PRECIPATION,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MIN,
		DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MAX,
		DatabaseHelper.TAB_SKICENTRE_APP_FLAG_FAVOURITE,
		DatabaseHelper.TAB_SKICENTRE_APP_DATE_LAST_UPDATE,
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
		// tabulka oblasti
		String sqlArea = "CREATE TABLE " + TAB_AREA + "(" +
		TAB_AREA_API_ID + " INTEGER PRIMARY KEY," +
		TAB_AREA_API_NAME + " TEXT NOT NULL);";
				
		// tabulka statu
		String sqlCountry = "CREATE TABLE " + TAB_COUNTRY + "(" +
		TAB_COUNTRY_API_ID + " INTEGER PRIMARY KEY," +
		TAB_COUNTRY_API_NAME + " TEXT NOT NULL," +
		TAB_COUNTRY_API_ISO_CODE + " TEXT);";
			
		// tabulka skicenter
		//String sqlSkicentre = "CREATE VIRTUAL TABLE " + TAB_SKICENTRE + " USING FTS3 (" +
		String sqlSkicentre = "CREATE TABLE " + TAB_SKICENTRE + " (" +
		TAB_SKICENTRE_API_ID + " INTEGER PRIMARY KEY," +
		TAB_SKICENTRE_API_NAME + " TEXT NOT NULL," +
		TAB_SKICENTRE_API_COUNTRY_ID + " INTEGER," +
		TAB_SKICENTRE_API_AREA_ID + " INTEGER," +
		TAB_SKICENTRE_API_INFO_PEREX + " TEXT," +
		TAB_SKICENTRE_API_DATE_SEASON_START + " NUMERIC," +
		TAB_SKICENTRE_API_DATE_SEASON_END + " NUMERIC," +
		TAB_SKICENTRE_API_LOCATION_LATITUDE + " REAL," +
		TAB_SKICENTRE_API_LOCATION_LONGITUDE + " REAL," +
		TAB_SKICENTRE_API_LOCATION_ALTITUDE_UNDERMOST + " INTEGER DEFAULT " + NULL_INT + "," +
		TAB_SKICENTRE_API_LOCATION_ALTITUDE_TOPMOST + " INTEGER DEFAULT " + NULL_INT + "," +
		TAB_SKICENTRE_API_COUNT_LIFTS_OPENED + " INTEGER DEFAULT " + NULL_INT + "," +
		TAB_SKICENTRE_API_COUNT_DOWNHILLS_OPENED + " INTEGER DEFAULT " + NULL_INT + "," +
		TAB_SKICENTRE_API_LENGTH_CROSSCOUNTRY + " INTEGER DEFAULT " + NULL_INT + "," +
		TAB_SKICENTRE_API_LENGTH_DOWNHILLS_TOTAL + " INTEGER DEFAULT " + NULL_INT + "," +
		TAB_SKICENTRE_API_FLAG_OPENED + " NUMERIC," +
		TAB_SKICENTRE_API_FLAG_NIGHTSKI + " NUMERIC," +
		TAB_SKICENTRE_API_FLAG_VALLEY + " NUMERIC," +
		TAB_SKICENTRE_API_FLAG_SNOWPARK + " NUMERIC," +
		TAB_SKICENTRE_API_FLAG_HALFPIPE + " NUMERIC," +
		TAB_SKICENTRE_API_PRICE_ADULTS_1 + " INTEGER," +
		TAB_SKICENTRE_API_PRICE_ADULTS_6 + " INTEGER," +
		TAB_SKICENTRE_API_PRICE_CHILDREN_1 + " INTEGER," +
		TAB_SKICENTRE_API_PRICE_CHILDREN_6 + " INTEGER," +
		TAB_SKICENTRE_API_PRICE_YOUNG_1 + " INTEGER," +
		TAB_SKICENTRE_API_PRICE_YOUNG_6 + " INTEGER," +
		TAB_SKICENTRE_API_PRICE_SENIORS_1 + " INTEGER," +
		TAB_SKICENTRE_API_PRICE_SENIORS_6 + " INTEGER," +
		TAB_SKICENTRE_API_PRICE_CURRENCY + " TEXT," +
		TAB_SKICENTRE_API_URL_SKIMAP + " TEXT," +
		TAB_SKICENTRE_API_URL_HOMEPAGE + " TEXT," +
		TAB_SKICENTRE_API_URL_WEBCAMS + " TEXT," +
		TAB_SKICENTRE_API_URL_SNOW_REPORT + " TEXT," +
		TAB_SKICENTRE_API_URL_WEATHER_REPORT + " TEXT," +
		TAB_SKICENTRE_API_URL_IMG_MAP + " TEXT," +
		TAB_SKICENTRE_API_URL_IMG_METEOGRAM + " TEXT," +
		TAB_SKICENTRE_API_URL_IMG_WEBCAM + " TEXT," +
		TAB_SKICENTRE_API_SNOW_MIN + " INTEGER DEFAULT " + NULL_INT + "," +
		TAB_SKICENTRE_API_SNOW_MAX + " INTEGER DEFAULT " + NULL_INT + "," +
		TAB_SKICENTRE_API_SNOW_DATE_LAST_SNOW + " NUMERIC," +
		TAB_SKICENTRE_API_SNOW_DATE_LAST_UPDATE + " NUMERIC," +
		TAB_SKICENTRE_API_WEATHER_1_DATE + " NUMERIC," +
		TAB_SKICENTRE_API_WEATHER_1_SYMBOL_NAME + " TEXT," +
		TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MIN + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MAX + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_2_DATE + " NUMERIC," +
		TAB_SKICENTRE_API_WEATHER_2_SYMBOL_NAME + " TEXT," +
		TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MIN + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MAX + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_3_DATE + " NUMERIC," +
		TAB_SKICENTRE_API_WEATHER_3_SYMBOL_NAME + " TEXT," +
		TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MIN + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MAX + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_4_DATE + " NUMERIC," +
		TAB_SKICENTRE_API_WEATHER_4_SYMBOL_NAME + " TEXT," +
		TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MIN + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MAX + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_5_DATE + " NUMERIC," +
		TAB_SKICENTRE_API_WEATHER_5_SYMBOL_NAME + " TEXT," +
		TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MIN + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MAX + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_6_DATE + " NUMERIC," +
		TAB_SKICENTRE_API_WEATHER_6_SYMBOL_NAME + " TEXT," +
		TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MIN + " INTEGER," +
		TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MAX + " INTEGER," +
		TAB_SKICENTRE_APP_FLAG_FAVOURITE + " NUMERIC," +
		TAB_SKICENTRE_APP_DATE_LAST_UPDATE + " NUMERIC" +
		");";
		//"FOREIGN KEY(" + TAB_SKICENTRE_API_AREA_ID + ") REFERENCES " + TAB_AREA + "(" + TAB_AREA_API_ID + ")," +
		//"FOREIGN KEY(" + TAB_SKICENTRE_API_COUNTRY_ID + ") REFERENCES " + TAB_COUNTRY + "(" + TAB_COUNTRY_API_ID + "));";

		// vykonani SQL prikazu
		db.execSQL("PRAGMA foreign_keys=ON;");
		db.execSQL(sqlArea);
		db.execSQL(sqlCountry);
		db.execSQL(sqlSkicentre);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// zde bude kod pro osetreni zmeny struktury sqlite databaze pri update
	}
}
