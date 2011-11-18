package net.skimap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper 
{
	private static final String DATABASE_NAME = "skimap.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TAB_SKICENTRE = "skicentres";
	public static final String TAB_SKICENTRE_API_ID = "_id";
	public static final String TAB_SKICENTRE_API_NAME = "name";
	public static final String TAB_SKICENTRE_API_LATITUDE = "latitude";
	public static final String TAB_SKICENTRE_API_LONGITUDE = "longitude";
	
	
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
		// tabulka skicenter
		String sql1 = "CREATE TABLE " + TAB_SKICENTRE + "(" +
		TAB_SKICENTRE_API_ID + " INTEGER PRIMARY KEY," +
		TAB_SKICENTRE_API_NAME + " TEXT NOT NULL," +
		TAB_SKICENTRE_API_LATITUDE + " REAL," +
		TAB_SKICENTRE_API_LONGITUDE + " REAL);";
		
		// vykonani SQL prikazu
		db.execSQL("PRAGMA foreign_keys=ON;");
		db.execSQL(sql1);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
	}
}
