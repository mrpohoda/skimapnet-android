package net.skimap.database;

import java.util.ArrayList;
import java.util.Iterator;

import net.skimap.data.Country;
import net.skimap.data.SkicentreShort;
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
	//private final String DATE_FORMAT = "yyyy-MM-dd";
	
	
	public Database(Context context)
	{
		mContext = context;
	}
	
	
	public Database open() throws SQLException
	{
		mHelper = new DatabaseHelper(mContext);
		mDatabase = mHelper.getWritableDatabase();
		return this;
	}
	
	
	public void close()
	{
		mHelper.close();
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
	
	
	// TODO: overit funkcnost upsert
	public long upsertSkicentre(SkicentreShort skicentre)
	{
		ContentValues values = valuesSkicentreShort(skicentre);
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
	
	
	public SkicentreShort getSkicentre(int id)
	{
		Cursor cursor = mDatabase.query(true, DatabaseHelper.TAB_SKICENTRE, DatabaseHelper.COLS_SKICENTRE, DatabaseHelper.TAB_SKICENTRE_API_ID + "='" + id + "'", null, null, null, null, null);
		SkicentreShort skicentre = null;
		
		if (cursor != null && cursor.moveToFirst())
		{
			skicentre = cursorSkicentreShort(cursor);
		}
		
		cursor.close();
		return skicentre;
	}
	
	
	public ArrayList<SkicentreShort> getAllSkicentres()
	{
		Cursor cursor = mDatabase.query(DatabaseHelper.TAB_SKICENTRE, DatabaseHelper.COLS_SKICENTRE, null, null, null, null, DatabaseHelper.TAB_SKICENTRE_API_NAME + " COLLATE LOCALIZED ASC", null);
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
		int colLatitude = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LATITUDE);
		int colLongitude = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LONGITUDE);
		int colCountry = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_FK);
		int colOpened = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_OPENED);
		int colSnow = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_SNOW);

		int id = (int) cursor.getInt(colId);
		String name = cursor.getString(colName);
		double latitude = cursor.getDouble(colLatitude);
		double longitude = cursor.getDouble(colLongitude);
		int country = cursor.getInt(colCountry);
		boolean opened = cursor.getInt(colOpened)==1;
		int snow = cursor.getInt(colSnow);
		
		SkicentreShort skicentre = new SkicentreShort(id, name, latitude, longitude, country, opened, snow);
		return skicentre;
	}
	

	private ContentValues valuesSkicentreShort(SkicentreShort skicentre)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.TAB_SKICENTRE_API_ID, skicentre.getId());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_NAME, skicentre.getName());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_LATITUDE, skicentre.getLatitude());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_LONGITUDE, skicentre.getLongitude());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_FK, skicentre.getCountry());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_OPENED, skicentre.getOpened());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_SNOW, skicentre.getSnow());
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
	
	
	public ArrayList<Country> getAllCountries()
	{
		Cursor cursor = mDatabase.query(DatabaseHelper.TAB_COUNTRY, DatabaseHelper.COLS_COUNTRY, null, null, null, null, DatabaseHelper.TAB_COUNTRY_API_ID + " ASC", null);
		ArrayList<Country> list = new ArrayList<Country>();
		
		while (cursor.moveToNext()) 
	    {
			Country country = cursorCountry(cursor);
			list.add(country);
	    }
		
		cursor.close();
		return list;
	}
	

	private Country cursorCountry(Cursor cursor)
	{
		int colId = cursor.getColumnIndex(DatabaseHelper.TAB_COUNTRY_API_ID);
		int colName = cursor.getColumnIndex(DatabaseHelper.TAB_COUNTRY_API_NAME);

		int id = (int) cursor.getInt(colId);
		String name = cursor.getString(colName);
		
		Country country = new Country(id, name);
		return country;
	}
	

	private ContentValues valuesCountry(Country country)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.TAB_COUNTRY_API_ID, country.getId());
		values.put(DatabaseHelper.TAB_COUNTRY_API_NAME, country.getName());
		return values;
	}
}
