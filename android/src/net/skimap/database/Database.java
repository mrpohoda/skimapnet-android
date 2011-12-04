package net.skimap.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.skimap.data.Area;
import net.skimap.data.Country;
import net.skimap.data.SkicentreLong;
import net.skimap.data.SkicentreShort;
import net.skimap.data.Weather;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


// http://www.vogella.de/articles/AndroidSQLite/article.html#overview_sqliteopenhelper
public class Database 
{
	private Context mContext;
	private DatabaseHelper mHelper;
	private SQLiteDatabase mDatabase;
	
	
	// TODO: synchronizace vlaken pri praci s databazi, kvuli kolizi
	// TODO: http://www.touchlab.co/blog/android-sqlite-locking/
	public Database(Context context)
	{
		mContext = context;
	}
	
	
	public Database open(boolean writeable) throws SQLException
	{
		mHelper = new DatabaseHelper(mContext);
		// TODO: readable nebo writable dle parametru
		if(writeable) mDatabase = mHelper.getWritableDatabase();
		else mDatabase = mHelper.getReadableDatabase();
		return this;
	}
	
	
	public void close()
	{
		if(mHelper!=null) mHelper.close();
	}
	
	
	protected void finalize() 
	{
		close();
	}
	
	
	public void executeSql(String sql)
	{
		mDatabase.execSQL(sql);
	}
	
	
	public int removeAll()
	{
		int count = 0;
		count += mDatabase.delete(DatabaseHelper.TAB_AREA, "1", null);
		count += mDatabase.delete(DatabaseHelper.TAB_COUNTRY, "1", null);
		count += mDatabase.delete(DatabaseHelper.TAB_SKICENTRE, "1", null);
		return count;
	}
	
	
	// SKICENTRE //////////////////////////////////////////////////////////////////////////////////
	
	
	public long insertSkicentre(SkicentreShort skicentre)
	{
		ContentValues values = valuesSkicentreShort(skicentre);
		return mDatabase.insert(DatabaseHelper.TAB_SKICENTRE, null, values);
	}
	
	
	public long insertSkicentre(SkicentreLong skicentre)
	{
		ContentValues values = valuesSkicentreLong(skicentre);
		return mDatabase.insert(DatabaseHelper.TAB_SKICENTRE, null, values);
	}
	
	
	public int insertSkicentres(ArrayList<SkicentreShort> skicentres)
	{
		int count = 0;
		if(skicentres==null) return count; 
			
		Iterator<SkicentreShort> iterator = skicentres.iterator();
		while(iterator.hasNext())
		{
			SkicentreShort skicentre = iterator.next();
			long result = insertSkicentre(skicentre);
			if(result>=0) count++;
		}
		
		return count;
	}
	
	
	public int updateSkicentre(SkicentreShort skicentre)
	{
		ContentValues values = valuesSkicentreShort(skicentre);
		return mDatabase.update(DatabaseHelper.TAB_SKICENTRE, values, DatabaseHelper.TAB_SKICENTRE_API_ID + "='" + skicentre.getId() + "'", null);
	}
	
	
	public int updateSkicentre(SkicentreLong skicentre)
	{
		ContentValues values = valuesSkicentreLong(skicentre);
		return mDatabase.update(DatabaseHelper.TAB_SKICENTRE, values, DatabaseHelper.TAB_SKICENTRE_API_ID + "='" + skicentre.getId() + "'", null);
	}
	
	
	// TODO: overit funkcnost upsert
	public long upsertSkicentre(SkicentreShort skicentre)
	{
		ContentValues values = valuesSkicentreShort(skicentre);
		return mDatabase.replace(DatabaseHelper.TAB_SKICENTRE, null, values);
	}
	
	
	// TODO: overit funkcnost upsert
	public long upsertSkicentre(SkicentreLong skicentre)
	{
		ContentValues values = valuesSkicentreLong(skicentre);
		return mDatabase.replace(DatabaseHelper.TAB_SKICENTRE, null, values);
	}
	
	
	public int deleteSkicentre(int id)
	{
		return mDatabase.delete(DatabaseHelper.TAB_SKICENTRE, DatabaseHelper.TAB_SKICENTRE_API_ID + "='" + id + "'", null);
	}
	
	
	public int deleteAllSkicentres()
	{
		return mDatabase.delete(DatabaseHelper.TAB_SKICENTRE, "1", null);
	}
	
	
	public SkicentreLong getSkicentre(int id)
	{
		Cursor cursor = mDatabase.query(true, DatabaseHelper.TAB_SKICENTRE, DatabaseHelper.COLS_SKICENTRE_LONG, DatabaseHelper.TAB_SKICENTRE_API_ID + "='" + id + "'", null, null, null, null, null);
		SkicentreLong skicentre = null;
		
		if (cursor != null && cursor.moveToFirst())
		{
			skicentre = cursorSkicentreLong(cursor);
		}
		
		cursor.close();
		return skicentre;
	}
	
	
	public ArrayList<SkicentreShort> getAllSkicentres()
	{
		Cursor cursor = mDatabase.query(DatabaseHelper.TAB_SKICENTRE, DatabaseHelper.COLS_SKICENTRE_SHORT, null, null, null, null, DatabaseHelper.TAB_SKICENTRE_API_NAME + " COLLATE LOCALIZED ASC", null);
		ArrayList<SkicentreShort> list = new ArrayList<SkicentreShort>();
		
		while (cursor.moveToNext()) 
	    {
			SkicentreShort skicentre = cursorSkicentreShort(cursor);
			list.add(skicentre);
	    }
		
		cursor.close();
		return list;
	}
	

	private SkicentreShort cursorSkicentreShort(Cursor cursor)
	{
		int colId = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_ID);
		int colName = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_NAME);
		int colArea = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_AREA_ID);
		int colCountry = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_ID);
		int colLocationLatitude = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LATITUDE);
		int colLocationLongitude = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LONGITUDE);
		int colFlagOpened = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_FLAG_OPENED);
		int colSnowMin = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_SNOW_MIN);

		int id = cursor.getInt(colId);
		String name = cursor.getString(colName);
		int area = cursor.getInt(colArea);
		int country = cursor.getInt(colCountry);
		double locationLatitude = cursor.getDouble(colLocationLatitude);
		double locationLongitude = cursor.getDouble(colLocationLongitude);
		boolean flagOpened = cursor.getInt(colFlagOpened)==1;
		int snowMin = cursor.getInt(colSnowMin);
		
		SkicentreShort skicentre = new SkicentreShort(id, name, area, country, locationLatitude, locationLongitude, flagOpened, snowMin);
		return skicentre;
	}
	
	
	private SkicentreLong cursorSkicentreLong(Cursor cursor)
	{
		SkicentreLong skicentre = (SkicentreLong) cursorSkicentreShort(cursor);
		
		int colInfoPerex = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_INFO_PEREX);
		int colDateSeasonStart = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_DATE_SEASON_START);
		int colDateSeasonEnd = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_DATE_SEASON_END);
		int colLocationAltitudeUndermost = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_ALTITUDE_UNDERMOST);
		int colLocationAltitudeTopmost = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_ALTITUDE_TOPMOST);
		int colCountLiftsOpened = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_COUNT_LIFTS_OPENED);
		int colCountDownhillsOpened = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_COUNT_DOWNHILLS_OPENED);
		int colLengthCrosscountry = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LENGTH_CROSSCOUNTRY);
		int colLengthDownhillsTotal = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LENGTH_DOWNHILLS_TOTAL);
		int colFlagNightski = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_FLAG_NIGHTSKI);
		int colFlagValley = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_FLAG_VALLEY);
		int colFlagSnowpark = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_FLAG_SNOWPARK);
		int colFlagHalfpipe = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_FLAG_HALFPIPE);
		int colPriceAdults1 = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_PRICE_ADULTS_1);
		int colPriceAdults6 = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_PRICE_ADULTS_6);
		int colPriceChildren1 = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CHILDREN_1);
		int colPriceChildren6 = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CHILDREN_6);
		int colPriceYoung1 = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_PRICE_YOUNG_1);
		int colPriceYoung6 = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_PRICE_YOUNG_6);
		int colPriceSeniors1 = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_PRICE_SENIORS_1);
		int colPriceSeniors6 = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_PRICE_SENIORS_6);
		int colPriceCurrency = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CURRENCY);
		int colUrlSkimap = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_URL_SKIMAP);
		int colUrlHomepage = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_URL_HOMEPAGE);
		int colUrlWebcams = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_URL_WEBCAMS);
		int colUrlSnowReport = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_URL_SNOW_REPORT);
		int colUrlWeatherReport = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_URL_WEATHER_REPORT);
		int colUrlImgMap = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_MAP);
		int colUrlImgMeteogram = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_METEOGRAM);
		int colUrlImgWebcam = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_WEBCAM);
		int colSnowMax = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_SNOW_MAX);
		int colSnowDateLastSnow = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_SNOW_DATE_LAST_SNOW);
		int colSnowDateLastUpdate = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_SNOW_DATE_LAST_UPDATE);
		int colWeather1Date = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_DATE);
		int colWeather1Symbol = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_SYMBOL_NAME);
		int colWeather1TemperatureMin = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MIN);
		int colWeather1TemperatureMax = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MAX);
		int colWeather2Date = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_DATE);
		int colWeather2Symbol = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_SYMBOL_NAME);
		int colWeather2TemperatureMin = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MIN);
		int colWeather2TemperatureMax = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MAX);
		int colWeather3Date = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_DATE);
		int colWeather3Symbol = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_SYMBOL_NAME);
		int colWeather3TemperatureMin = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MIN);
		int colWeather3TemperatureMax = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MAX);
		int colWeather4Date = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_DATE);
		int colWeather4Symbol = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_SYMBOL_NAME);
		int colWeather4TemperatureMin = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MIN);
		int colWeather4TemperatureMax = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MAX);
		int colWeather5Date = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_DATE);
		int colWeather5Symbol = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_SYMBOL_NAME);
		int colWeather5TemperatureMin = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MIN);
		int colWeather5TemperatureMax = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MAX);
		int colWeather6Date = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_DATE);
		int colWeather6Symbol = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_SYMBOL_NAME);
		int colWeather6TemperatureMin = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MIN);
		int colWeather6TemperatureMax = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MAX);
		int colFlagFavourite = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_APP_FLAG_FAVOURITE);
		int colDateLastUpdate = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_APP_DATE_LAST_UPDATE);
		
		skicentre.setInfoPerex( cursor.getString(colInfoPerex) );
		skicentre.setDateSeasonStart( cursor.getString(colDateSeasonStart) );
		skicentre.setDateSeasonEnd( cursor.getString(colDateSeasonEnd) );
		skicentre.setLocationAltitudeUndermost( cursor.getInt(colLocationAltitudeUndermost) );
		skicentre.setLocationAltitudeTopmost( cursor.getInt(colLocationAltitudeTopmost) );
		skicentre.setCountLiftsOpened( cursor.getInt(colCountLiftsOpened) );
		skicentre.setCountDownhillsOpened( cursor.getInt(colCountDownhillsOpened) );
		skicentre.setLengthCrosscountry( cursor.getInt(colLengthCrosscountry) );
		skicentre.setLengthDownhillsTotal( cursor.getInt(colLengthDownhillsTotal) );
		skicentre.setFlagNightski( cursor.getInt(colFlagNightski)==1 );
		skicentre.setFlagValley( cursor.getInt(colFlagValley)==1 );
		skicentre.setFlagSnowpark( cursor.getInt(colFlagSnowpark)==1 );
		skicentre.setFlagHalfpipe( cursor.getInt(colFlagHalfpipe)==1 );
		skicentre.setPriceAdults1( cursor.getInt(colPriceAdults1) );
		skicentre.setPriceAdults6( cursor.getInt(colPriceAdults6) );
		skicentre.setPriceChildren1( cursor.getInt(colPriceChildren1) );
		skicentre.setPriceChildren6( cursor.getInt(colPriceChildren6) );
		skicentre.setPriceYoung1( cursor.getInt(colPriceYoung1) );
		skicentre.setPriceYoung6( cursor.getInt(colPriceYoung6) );
		skicentre.setPriceSeniors1( cursor.getInt(colPriceSeniors1) );
		skicentre.setPriceSeniors6( cursor.getInt(colPriceSeniors6) );
		skicentre.setPriceCurrency( cursor.getString(colPriceCurrency) );
		skicentre.setUrlSkimap( cursor.getString(colUrlSkimap) );
		skicentre.setUrlHomepage( cursor.getString(colUrlHomepage) );
		skicentre.setUrlWebcams( cursor.getString(colUrlWebcams) );
		skicentre.setUrlSnowReport( cursor.getString(colUrlSnowReport) );
		skicentre.setUrlWeatherReport( cursor.getString(colUrlWeatherReport) );
		skicentre.setUrlImgMap( cursor.getString(colUrlImgMap) );
		skicentre.setUrlImgMeteogram( cursor.getString(colUrlImgMeteogram) );
		skicentre.setUrlImgWebcam( cursor.getString(colUrlImgWebcam) );
		skicentre.setSnowMax( cursor.getInt(colSnowMax) );
		skicentre.setSnowDateLastSnow( cursor.getString(colSnowDateLastSnow) );
		skicentre.setSnowDateLastUpdate( cursor.getString(colSnowDateLastUpdate) );
		skicentre.setWeather1Date( cursor.getString(colWeather1Date) );
		skicentre.setWeather1Symbol( Weather.stringToType(cursor.getString(colWeather1Symbol)) );
		skicentre.setWeather1TemperatureMin( cursor.getInt(colWeather1TemperatureMin) );
		skicentre.setWeather1TemperatureMax( cursor.getInt(colWeather1TemperatureMax) );
		skicentre.setWeather2Date( cursor.getString(colWeather2Date) );
		skicentre.setWeather2Symbol( Weather.stringToType(cursor.getString(colWeather2Symbol)) );
		skicentre.setWeather2TemperatureMin( cursor.getInt(colWeather2TemperatureMin) );
		skicentre.setWeather2TemperatureMax( cursor.getInt(colWeather2TemperatureMax) );
		skicentre.setWeather3Date( cursor.getString(colWeather3Date) );
		skicentre.setWeather3Symbol( Weather.stringToType(cursor.getString(colWeather3Symbol)) );
		skicentre.setWeather3TemperatureMin( cursor.getInt(colWeather3TemperatureMin) );
		skicentre.setWeather3TemperatureMax( cursor.getInt(colWeather3TemperatureMax) );
		skicentre.setWeather4Date( cursor.getString(colWeather4Date) );
		skicentre.setWeather4Symbol( Weather.stringToType(cursor.getString(colWeather4Symbol)) );
		skicentre.setWeather4TemperatureMin( cursor.getInt(colWeather4TemperatureMin) );
		skicentre.setWeather4TemperatureMax( cursor.getInt(colWeather4TemperatureMax) );
		skicentre.setWeather5Date( cursor.getString(colWeather5Date) );
		skicentre.setWeather5Symbol( Weather.stringToType(cursor.getString(colWeather5Symbol)) );
		skicentre.setWeather5TemperatureMin( cursor.getInt(colWeather5TemperatureMin) );
		skicentre.setWeather5TemperatureMax( cursor.getInt(colWeather5TemperatureMax) );
		skicentre.setWeather6Date( cursor.getString(colWeather6Date) );
		skicentre.setWeather6Symbol( Weather.stringToType(cursor.getString(colWeather6Symbol)) );
		skicentre.setWeather6TemperatureMin( cursor.getInt(colWeather6TemperatureMin) );
		skicentre.setWeather6TemperatureMax( cursor.getInt(colWeather6TemperatureMax) );
		skicentre.setFlagFavourite( cursor.getInt(colFlagFavourite)==1 );
		skicentre.setDateLastUpdate( cursor.getString(colDateLastUpdate) );
		
		return skicentre;
	}
	

	private ContentValues valuesSkicentreShort(SkicentreShort skicentre)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.TAB_SKICENTRE_API_ID, skicentre.getId());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_NAME, skicentre.getName());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_AREA_ID, skicentre.getArea());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_ID, skicentre.getCountry());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LATITUDE, skicentre.getLocationLatitude());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LONGITUDE, skicentre.getLocationLongitude());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_FLAG_OPENED, skicentre.isFlagOpened());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_SNOW_MIN, skicentre.getSnowMin());
		return values;
	}
	
	
	private ContentValues valuesSkicentreLong(SkicentreLong skicentre)
	{
		ContentValues values = valuesSkicentreShort(skicentre);
		values.put(DatabaseHelper.TAB_SKICENTRE_API_INFO_PEREX, skicentre.getInfoPerex());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_DATE_SEASON_START, skicentre.getDateSeasonStartString());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_DATE_SEASON_END, skicentre.getDateSeasonEndString());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_ALTITUDE_UNDERMOST, skicentre.getLocationAltitudeUndermost());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_ALTITUDE_TOPMOST, skicentre.getLocationAltitudeTopmost());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_COUNT_LIFTS_OPENED, skicentre.getCountLiftsOpened());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_COUNT_DOWNHILLS_OPENED, skicentre.getCountDownhillsOpened());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_LENGTH_CROSSCOUNTRY, skicentre.getLengthCrosscountry());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_LENGTH_DOWNHILLS_TOTAL, skicentre.getLengthDownhillsTotal());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_FLAG_NIGHTSKI, skicentre.isFlagNightski());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_FLAG_VALLEY, skicentre.isFlagValley());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_FLAG_SNOWPARK, skicentre.isFlagSnowpark());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_FLAG_HALFPIPE, skicentre.isFlagHalfpipe());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_PRICE_ADULTS_1, skicentre.getPriceAdults1());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_PRICE_ADULTS_6, skicentre.getPriceAdults6());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CHILDREN_1, skicentre.getPriceChildren1());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CHILDREN_6, skicentre.getPriceChildren6());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_PRICE_YOUNG_1, skicentre.getPriceYoung1());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_PRICE_YOUNG_6, skicentre.getPriceYoung6());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_PRICE_SENIORS_1, skicentre.getPriceSeniors1());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_PRICE_SENIORS_6, skicentre.getPriceSeniors6());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CURRENCY, skicentre.getPriceCurrency());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_URL_SKIMAP, skicentre.getUrlSkimap());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_URL_HOMEPAGE, skicentre.getUrlHomepage());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_URL_WEBCAMS, skicentre.getUrlWebcams());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_URL_SNOW_REPORT, skicentre.getUrlSnowReport());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_URL_WEATHER_REPORT, skicentre.getUrlWeatherReport());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_MAP, skicentre.getUrlImgMap());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_METEOGRAM, skicentre.getUrlImgMeteogram());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_WEBCAM, skicentre.getUrlImgWebcam());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_SNOW_MAX, skicentre.getSnowMax());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_SNOW_DATE_LAST_SNOW, skicentre.getSnowDateLastSnowString());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_SNOW_DATE_LAST_UPDATE, skicentre.getSnowDateLastUpdateString());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_DATE, skicentre.getWeather1DateString());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_SYMBOL_NAME, skicentre.getWeather1Symbol().ordinal());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MIN, skicentre.getWeather1TemperatureMin());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MAX, skicentre.getWeather1TemperatureMax());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_DATE, skicentre.getWeather2DateString());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_SYMBOL_NAME, skicentre.getWeather2Symbol().ordinal());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MIN, skicentre.getWeather2TemperatureMin());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MAX, skicentre.getWeather2TemperatureMax());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_DATE, skicentre.getWeather3DateString());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_SYMBOL_NAME, skicentre.getWeather3Symbol().ordinal());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MIN, skicentre.getWeather3TemperatureMin());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MAX, skicentre.getWeather3TemperatureMax());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_DATE, skicentre.getWeather4DateString());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_SYMBOL_NAME, skicentre.getWeather4Symbol().ordinal());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MIN, skicentre.getWeather4TemperatureMin());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MAX, skicentre.getWeather4TemperatureMax());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_DATE, skicentre.getWeather5DateString());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_SYMBOL_NAME, skicentre.getWeather5Symbol().ordinal());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MIN, skicentre.getWeather5TemperatureMin());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MAX, skicentre.getWeather5TemperatureMax());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_DATE, skicentre.getWeather6DateString());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_SYMBOL_NAME, skicentre.getWeather6Symbol().ordinal());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MIN, skicentre.getWeather6TemperatureMin());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MAX, skicentre.getWeather6TemperatureMax());
		values.put(DatabaseHelper.TAB_SKICENTRE_APP_FLAG_FAVOURITE, skicentre.isFlagFavourite());
		values.put(DatabaseHelper.TAB_SKICENTRE_APP_DATE_LAST_UPDATE, skicentre.getDateLastUpdateString());
		return values;
	}
	
	
	// AREA ///////////////////////////////////////////////////////////////////////////////////////
	
	
	public long insertArea(Area area)
	{
		ContentValues values = valuesArea(area);
		return mDatabase.insert(DatabaseHelper.TAB_AREA, null, values);
	}
	
	
	public int insertAreas(ArrayList<Area> areas)
	{
		int count = 0;
		if(areas==null) return count; 
			
		Iterator<Area> iterator = areas.iterator();
		while(iterator.hasNext())
		{
			Area area = iterator.next();
			long result = insertArea(area);
			if(result>=0) count++;
		}
		
		return count;
	}
	
	
	public int updateArea(Area area)
	{
		ContentValues values = valuesArea(area);
		return mDatabase.update(DatabaseHelper.TAB_AREA, values, DatabaseHelper.TAB_AREA_API_ID + "='" + area.getId() + "'", null);
	}
	
	
	public long upsertArea(Area area)
	{
		ContentValues values = valuesArea(area);
		return mDatabase.replace(DatabaseHelper.TAB_AREA, null, values);
	}
	
	
	public int deleteArea(int id)
	{
		return mDatabase.delete(DatabaseHelper.TAB_AREA, DatabaseHelper.TAB_AREA_API_ID + "='" + id + "'", null);
	}
	
	
	public int deleteAllAreas()
	{
		return mDatabase.delete(DatabaseHelper.TAB_AREA, "1", null);
	}
	
	
	public Area getArea(int id)
	{
		Cursor cursor = mDatabase.query(true, DatabaseHelper.TAB_AREA, DatabaseHelper.COLS_AREA, DatabaseHelper.TAB_AREA_API_ID + "='" + id + "'", null, null, null, null, null);
		Area area = null;
		
		if (cursor != null && cursor.moveToFirst())
		{
			area = cursorArea(cursor);
		}
		
		cursor.close();
		return area;
	}
	
	
	public HashMap<Integer, Area> getAllAreas()
	{
		Cursor cursor = mDatabase.query(DatabaseHelper.TAB_AREA, DatabaseHelper.COLS_AREA, null, null, null, null, DatabaseHelper.TAB_AREA_API_NAME + " COLLATE LOCALIZED ASC", null);
		HashMap<Integer, Area> map = new HashMap<Integer, Area>();
		
		while (cursor.moveToNext()) 
	    {
			Area area = cursorArea(cursor);
			map.put(area.getId(), area);
	    }
		
		cursor.close();
		return map;
	}
	

	private Area cursorArea(Cursor cursor)
	{
		int colId = cursor.getColumnIndex(DatabaseHelper.TAB_AREA_API_ID);
		int colName = cursor.getColumnIndex(DatabaseHelper.TAB_AREA_API_NAME);

		int id = cursor.getInt(colId);
		String name = cursor.getString(colName);
		
		Area area = new Area(id, name);
		return area;
	}
	

	private ContentValues valuesArea(Area area)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.TAB_AREA_API_ID, area.getId());
		values.put(DatabaseHelper.TAB_AREA_API_NAME, area.getName());
		return values;
	}
	
	
	// COUNTRY ////////////////////////////////////////////////////////////////////////////////////
	
	
	public long insertCountry(Country country)
	{
		ContentValues values = valuesCountry(country);
		return mDatabase.insert(DatabaseHelper.TAB_COUNTRY, null, values);
	}
	
	
	public int insertCountries(ArrayList<Country> countries)
	{
		int count = 0;
		if(countries==null) return count; 
			
		Iterator<Country> iterator = countries.iterator();
		while(iterator.hasNext())
		{
			Country country = iterator.next();
			long result = insertCountry(country);
			if(result>=0) count++;
		}
		
		return count;
	}
	
	
	public int updateCountry(Country country)
	{
		ContentValues values = valuesCountry(country);
		return mDatabase.update(DatabaseHelper.TAB_COUNTRY, values, DatabaseHelper.TAB_COUNTRY_API_ID + "='" + country.getId() + "'", null);
	}
	
	
	public long upsertCountry(Country country)
	{
		ContentValues values = valuesCountry(country);
		return mDatabase.replace(DatabaseHelper.TAB_COUNTRY, null, values);
	}
	
	
	public int deleteCountry(int id)
	{
		return mDatabase.delete(DatabaseHelper.TAB_COUNTRY, DatabaseHelper.TAB_COUNTRY_API_ID + "='" + id + "'", null);
	}
	
	
	public int deleteAllCountries()
	{
		return mDatabase.delete(DatabaseHelper.TAB_COUNTRY, "1", null);
	}
	
	
	public Country getCountry(int id)
	{
		Cursor cursor = mDatabase.query(true, DatabaseHelper.TAB_COUNTRY, DatabaseHelper.COLS_COUNTRY, DatabaseHelper.TAB_COUNTRY_API_ID + "='" + id + "'", null, null, null, null, null);
		Country country = null;
		
		if (cursor != null && cursor.moveToFirst())
		{
			country = cursorCountry(cursor);
		}
		
		cursor.close();
		return country;
	}
	
	
	public HashMap<Integer, Country> getAllCountries()
	{
		Cursor cursor = mDatabase.query(DatabaseHelper.TAB_COUNTRY, DatabaseHelper.COLS_COUNTRY, null, null, null, null, DatabaseHelper.TAB_COUNTRY_API_NAME + " COLLATE LOCALIZED ASC", null);
		HashMap<Integer, Country> map = new HashMap<Integer, Country>();
		
		while (cursor.moveToNext()) 
	    {
			Country country = cursorCountry(cursor);
			map.put(country.getId(), country);
	    }
		
		cursor.close();
		return map;
	}
	

	private Country cursorCountry(Cursor cursor)
	{
		int colId = cursor.getColumnIndex(DatabaseHelper.TAB_COUNTRY_API_ID);
		int colName = cursor.getColumnIndex(DatabaseHelper.TAB_COUNTRY_API_NAME);
		int colIsoCode = cursor.getColumnIndex(DatabaseHelper.TAB_COUNTRY_API_ISO_CODE);

		int id = cursor.getInt(colId);
		String name = cursor.getString(colName);
		String isoCode = cursor.getString(colIsoCode);
		
		Country country = new Country(id, name, isoCode);
		return country;
	}
	

	private ContentValues valuesCountry(Country country)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.TAB_COUNTRY_API_ID, country.getId());
		values.put(DatabaseHelper.TAB_COUNTRY_API_NAME, country.getName());
		values.put(DatabaseHelper.TAB_COUNTRY_API_ISO_CODE, country.getIsoCode());
		return values;
	}
}
