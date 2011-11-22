package net.skimap.parser;

import net.skimap.data.SkicentreShort;
import net.skimap.database.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;


public class JsonParser
{
	private Database mDatabase;
	
	
	public JsonParser(Context context)
	{
		mDatabase = new Database(context);
	}
	
	
	public int storeSkicentresShort(String jsonData)
	{
		int count = 0;
		
		// TODO: umazat logy, overit funkcnost JSON na serveru
		Log.d("SKIMAP", "JSON strlen:" + jsonData.length());
		Log.d("SKIMAP", "JSON start:" + jsonData.substring(0, 50));
		Log.d("SKIMAP", "JSON end:" + jsonData.substring(jsonData.length()-50, jsonData.length()));

		JSONArray jsonRoot = null;

		// ziskej skicentra a uloz do databaze
		try
		{
			// nacti JSON pole
			jsonRoot = new JSONArray(jsonData);	
			Log.d("SKIMAP", "JSON arraylength:" + jsonRoot.length());
			
			// otevreni databaze
			mDatabase.open();
			mDatabase.removeAll();
			
			for(int i=0;i<jsonRoot.length();i++)
			{
				JSONObject object = jsonRoot.getJSONObject(i);
				
				int id = object.getInt("id");
				String name = object.getString("name");
				double latitude = object.getDouble("lat");
				double longitude = object.getDouble("lng");
				//String type = object.getString("type");
				
				SkicentreShort skicentre = new SkicentreShort(id, name, latitude, longitude);
				mDatabase.insertSkicentre(skicentre);
				count++;
			}
		}
		catch (SQLiteException e) 
		{			
			e.printStackTrace();
			return count;
		}
		catch (JSONException e) 
		{			
			e.printStackTrace();
			return count;
		}
		finally
		{
			mDatabase.close();
		}
				
		return count;
	}
}
