package net.skimap.database;

import java.util.ArrayList;

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
		count += mDatabase.delete(DatabaseHelper.TAB_SKICENTRE, "1", null);
		return count;
	}
	
	
	public long insertSkicentre(SkicentreShort skicentre)
	{
		ContentValues values = valuesSkicentreShort(skicentre);
		return mDatabase.insert(DatabaseHelper.TAB_SKICENTRE, null, values);
	}
	
	
	public int updateSkicentre(SkicentreShort skicentre)
	{
		ContentValues values = valuesSkicentreShort(skicentre);
		return mDatabase.update(DatabaseHelper.TAB_SKICENTRE, values, DatabaseHelper.TAB_SKICENTRE_API_ID + "='" + skicentre.getId() + "'", null);
	}
	
	
	public int deleteSkicentre(int id)
	{
		return mDatabase.delete(DatabaseHelper.TAB_SKICENTRE, DatabaseHelper.TAB_SKICENTRE_API_ID + "='" + id + "'", null);
	}
	
	
	public int deleteAllSkicentres()
	{
		return mDatabase.delete(DatabaseHelper.TAB_SKICENTRE, "1", null);
	}
	
	
	private Cursor fetchSkicentre(int id) throws SQLException
	{
		String []cols = { DatabaseHelper.TAB_SKICENTRE_API_ID,
				DatabaseHelper.TAB_SKICENTRE_API_NAME,
				DatabaseHelper.TAB_SKICENTRE_API_LATITUDE,
				DatabaseHelper.TAB_SKICENTRE_API_LONGITUDE};
		return mDatabase.query(true, DatabaseHelper.TAB_SKICENTRE, cols, DatabaseHelper.TAB_SKICENTRE_API_ID + "='" + id + "'", null, null, null, null, null);
	}
	
	
	public SkicentreShort getSkicentre(int id)
	{
		Cursor cursor = fetchSkicentre(id);
		SkicentreShort skicentre = null;
		
		if (cursor != null && cursor.moveToFirst())
		{
			int colName = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_NAME);
			int colLatitude = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LATITUDE);
			int colLongitude = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LONGITUDE);

			String name = cursor.getString(colName);
			double latitude = cursor.getDouble(colLatitude);
			double longitude = cursor.getDouble(colLongitude);
			
			skicentre = new SkicentreShort(id, name, latitude, longitude);
		}
	
		cursor.close();
		return skicentre;
	}
	
	
	private Cursor fetchAllSkicentres()
	{
		String []cols = { DatabaseHelper.TAB_SKICENTRE_API_ID,
				DatabaseHelper.TAB_SKICENTRE_API_NAME,
				DatabaseHelper.TAB_SKICENTRE_API_LATITUDE,
				DatabaseHelper.TAB_SKICENTRE_API_LONGITUDE};
		return mDatabase.query(DatabaseHelper.TAB_SKICENTRE, cols, null, null, null, null, DatabaseHelper.TAB_SKICENTRE_API_NAME + " ASC", null);
	}
	
	
	public ArrayList<SkicentreShort> getAllSkicentres()
	{
		Cursor cursor = fetchAllSkicentres();
		ArrayList<SkicentreShort> list = new ArrayList<SkicentreShort>();
		
		while (cursor.moveToNext()) 
	    {
			int colId = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_ID);
			int colName = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_NAME);
			int colLatitude = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LATITUDE);
			int colLongitude = cursor.getColumnIndex(DatabaseHelper.TAB_SKICENTRE_API_LONGITUDE);

			int id = (int) cursor.getInt(colId);
			String name = cursor.getString(colName);
			double latitude = cursor.getDouble(colLatitude);
			double longitude = cursor.getDouble(colLongitude);
			
			SkicentreShort skicentre = new SkicentreShort(id, name, latitude, longitude);
			list.add(skicentre);
	    }
		
		cursor.close();
		return list;
	}
	
	
	private ContentValues valuesSkicentreShort(SkicentreShort skicentre)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.TAB_SKICENTRE_API_ID, skicentre.getId());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_NAME, skicentre.getName());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_LATITUDE, skicentre.getLatitude());
		values.put(DatabaseHelper.TAB_SKICENTRE_API_LONGITUDE, skicentre.getLongitude());
		return values;
	}
}
