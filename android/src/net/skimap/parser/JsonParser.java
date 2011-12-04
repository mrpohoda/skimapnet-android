package net.skimap.parser;

import net.skimap.data.Area;
import net.skimap.data.Country;
import net.skimap.data.SkicentreLong;
import net.skimap.data.SkicentreShort;
import net.skimap.data.Weather;
import net.skimap.database.Database;
import net.skimap.database.DatabaseHelper;

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
		// TODO: umazat logy, overit funkcnost JSON na serveru
		Log.d("SKIMAP", "JSON strlen:" + jsonData.length());
		Log.d("SKIMAP", "JSON start:" + jsonData.substring(0, 50));
		Log.d("SKIMAP", "JSON end:" + jsonData.substring(jsonData.length()-50, jsonData.length()));
				
		int count = 0;
		JSONArray jsonRoot = null;

		// ziskej skicentra a uloz do databaze
		try
		{
			// nacti JSON pole
			jsonRoot = new JSONArray(jsonData);	
			Log.d("SKIMAP", "JSON arraylength:" + jsonRoot.length());
			
			// otevreni databaze
			mDatabase.open(true);
			
			for(int i=0;i<jsonRoot.length();i++)
			{
				JSONObject object = jsonRoot.getJSONObject(i);
				
				int id = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_ID);
				String name = object.getString(DatabaseHelper.TAB_SKICENTRE_API_NAME);

				int area = DatabaseHelper.NULL_INT;
				try { area = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_AREA_ID); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int country = DatabaseHelper.NULL_INT;
				try { country = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_ID); }
				catch(JSONException e) { /* hodnota je null */ }

				double locationLatitude = DatabaseHelper.NULL_DOUBLE;
				try { locationLatitude = object.getDouble(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LATITUDE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				double locationLongitude = DatabaseHelper.NULL_DOUBLE;
				try { locationLongitude = object.getDouble(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LONGITUDE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				boolean flagOpened = DatabaseHelper.NULL_BOOLEAN;
				try { flagOpened = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_FLAG_OPENED)==1; }
				catch(JSONException e) { /* hodnota je null */ }
				
				int snowMin = DatabaseHelper.NULL_INT;
				try { snowMin = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_SNOW_MIN); }
				catch(JSONException e) { /* hodnota je null */ }
				
				SkicentreShort skicentre = new SkicentreShort(id, name, area, country, locationLatitude, locationLongitude, flagOpened, snowMin);
				mDatabase.upsertSkicentre(skicentre);
				count++;
			}
		}
		catch (SQLiteException e) 
		{			
			e.printStackTrace();
		}
		catch (JSONException e) 
		{			
			e.printStackTrace();
		}
		finally
		{
			mDatabase.close();
		}
				
		return count;
	}
	
	
	public boolean storeSkicentreLong(String jsonData)
	{
		// TODO: overit funkcnost JSON na serveru
		boolean state = false;
		JSONArray jsonRoot = null;

		// ziskej skicentrum a uloz do databaze
		try
		{
			// nacti JSON pole
			jsonRoot = new JSONArray(jsonData);	
			
			// otevreni databaze
			mDatabase.open(true);
			if(jsonRoot.length()>0)
			{
				JSONObject object = jsonRoot.getJSONObject(0);
				
				int id = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_ID);
				String name = object.getString(DatabaseHelper.TAB_SKICENTRE_API_NAME);

				int area = DatabaseHelper.NULL_INT;
				try { area = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_AREA_ID); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int country = DatabaseHelper.NULL_INT;
				try { country = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_ID); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String infoPerex = DatabaseHelper.NULL_STRING;
				try { infoPerex = object.getString(DatabaseHelper.TAB_SKICENTRE_API_INFO_PEREX); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String dateSeasonStart = DatabaseHelper.NULL_STRING;
				try { dateSeasonStart = object.getString(DatabaseHelper.TAB_SKICENTRE_API_DATE_SEASON_START); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String dateSeasonEnd = DatabaseHelper.NULL_STRING;
				try { dateSeasonEnd = object.getString(DatabaseHelper.TAB_SKICENTRE_API_DATE_SEASON_END); }
				catch(JSONException e) { /* hodnota je null */ }

				double locationLatitude = DatabaseHelper.NULL_DOUBLE;
				try { locationLatitude = object.getDouble(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LATITUDE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				double locationLongitude = DatabaseHelper.NULL_DOUBLE;
				try { locationLongitude = object.getDouble(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LONGITUDE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int locationAltitudeUndermost = DatabaseHelper.NULL_INT;
				try { locationAltitudeUndermost = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_ALTITUDE_UNDERMOST); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int locationAltitudeTopmost = DatabaseHelper.NULL_INT;
				try { locationAltitudeTopmost = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_ALTITUDE_TOPMOST); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int countLiftsOpened = DatabaseHelper.NULL_INT;
				try { countLiftsOpened = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_COUNT_LIFTS_OPENED); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int countDownhillsOpened = DatabaseHelper.NULL_INT;
				try { countDownhillsOpened = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_COUNT_DOWNHILLS_OPENED); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int lengthCrosscountry = DatabaseHelper.NULL_INT;
				try { lengthCrosscountry = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_LENGTH_CROSSCOUNTRY); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int lengthDownhillsTotal = DatabaseHelper.NULL_INT;
				try { lengthDownhillsTotal = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_LENGTH_DOWNHILLS_TOTAL); }
				catch(JSONException e) { /* hodnota je null */ }

				boolean flagOpened = DatabaseHelper.NULL_BOOLEAN;
				try { flagOpened = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_FLAG_OPENED)==1; }
				catch(JSONException e) { /* hodnota je null */ }
				
				boolean flagNightski = DatabaseHelper.NULL_BOOLEAN;
				try { flagNightski = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_FLAG_NIGHTSKI)==1; }
				catch(JSONException e) { /* hodnota je null */ }
				
				boolean flagValley = DatabaseHelper.NULL_BOOLEAN;
				try { flagValley = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_FLAG_VALLEY)==1; }
				catch(JSONException e) { /* hodnota je null */ }
				
				boolean flagSnowpark = DatabaseHelper.NULL_BOOLEAN;
				try { flagSnowpark = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_FLAG_SNOWPARK)==1; }
				catch(JSONException e) { /* hodnota je null */ }
				
				boolean flagHalfpipe = DatabaseHelper.NULL_BOOLEAN;
				try { flagHalfpipe = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_FLAG_HALFPIPE)==1; }
				catch(JSONException e) { /* hodnota je null */ }
				
				int priceAdults1 = DatabaseHelper.NULL_INT;
				try { priceAdults1 = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_PRICE_ADULTS_1); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int priceAdults6 = DatabaseHelper.NULL_INT;
				try { priceAdults6 = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_PRICE_ADULTS_6); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int priceChildren1 = DatabaseHelper.NULL_INT;
				try { priceChildren1 = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CHILDREN_1); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int priceChildren6 = DatabaseHelper.NULL_INT;
				try { priceChildren6 = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CHILDREN_6); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int priceYoung1 = DatabaseHelper.NULL_INT;
				try { priceYoung1 = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_PRICE_YOUNG_1); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int priceYoung6 = DatabaseHelper.NULL_INT;
				try { priceYoung6 = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_PRICE_YOUNG_6); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int priceSeniors1 = DatabaseHelper.NULL_INT;
				try { priceSeniors1 = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_PRICE_SENIORS_1); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int priceSeniors6 = DatabaseHelper.NULL_INT;
				try { priceSeniors6 = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_PRICE_SENIORS_6); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String priceCurrency = DatabaseHelper.NULL_STRING;
				try { priceCurrency = object.getString(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CURRENCY); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String urlSkimap = DatabaseHelper.NULL_STRING;
				try { urlSkimap = object.getString(DatabaseHelper.TAB_SKICENTRE_API_URL_SKIMAP); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String urlHomepage = DatabaseHelper.NULL_STRING;
				try { urlHomepage = object.getString(DatabaseHelper.TAB_SKICENTRE_API_URL_HOMEPAGE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String urlWebcams = DatabaseHelper.NULL_STRING;
				try { urlWebcams = object.getString(DatabaseHelper.TAB_SKICENTRE_API_URL_WEBCAMS); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String urlSnowReport = DatabaseHelper.NULL_STRING;
				try { urlSnowReport = object.getString(DatabaseHelper.TAB_SKICENTRE_API_URL_SNOW_REPORT); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String urlWeatherReport = DatabaseHelper.NULL_STRING;
				try { urlWeatherReport = object.getString(DatabaseHelper.TAB_SKICENTRE_API_URL_WEATHER_REPORT); }
				catch(JSONException e) { /* hodnota je null */ }

				String urlImgMap = DatabaseHelper.NULL_STRING;
				try { urlImgMap = object.getString(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_MAP); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String urlImgMeteogram = DatabaseHelper.NULL_STRING;
				try { urlImgMeteogram = object.getString(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_METEOGRAM); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String urlImgWebcam = DatabaseHelper.NULL_STRING;
				try { urlImgWebcam = object.getString(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_WEBCAM); }
				catch(JSONException e) { /* hodnota je null */ }

				int snowMin = DatabaseHelper.NULL_INT;
				try { snowMin = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_SNOW_MIN); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int snowMax = DatabaseHelper.NULL_INT;
				try { snowMax = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_SNOW_MAX); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String snowDateLastSnow = DatabaseHelper.NULL_STRING;
				try { snowDateLastSnow = object.getString(DatabaseHelper.TAB_SKICENTRE_API_SNOW_DATE_LAST_SNOW); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String snowDateLastUpdate = DatabaseHelper.NULL_STRING;
				try { snowDateLastUpdate = object.getString(DatabaseHelper.TAB_SKICENTRE_API_SNOW_DATE_LAST_UPDATE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather1Date = DatabaseHelper.NULL_STRING;
				try { weather1Date = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_DATE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather1Symbol = DatabaseHelper.NULL_STRING;
				try { weather1Symbol = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_SYMBOL_NAME); }
				catch(JSONException e) { /* hodnota je null */ }

				int weather1TemperatureMin = DatabaseHelper.NULL_INT;
				try { weather1TemperatureMin = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MIN); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int weather1TemperatureMax = DatabaseHelper.NULL_INT;
				try { weather1TemperatureMax = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MAX); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather2Date = DatabaseHelper.NULL_STRING;
				try { weather2Date = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_DATE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather2Symbol = DatabaseHelper.NULL_STRING;
				try { weather2Symbol = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_SYMBOL_NAME); }
				catch(JSONException e) { /* hodnota je null */ }

				int weather2TemperatureMin = DatabaseHelper.NULL_INT;
				try { weather2TemperatureMin = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MIN); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int weather2TemperatureMax = DatabaseHelper.NULL_INT;
				try { weather2TemperatureMax = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MAX); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather3Date = DatabaseHelper.NULL_STRING;
				try { weather3Date = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_DATE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather3Symbol = DatabaseHelper.NULL_STRING;
				try { weather3Symbol = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_SYMBOL_NAME); }
				catch(JSONException e) { /* hodnota je null */ }

				int weather3TemperatureMin = DatabaseHelper.NULL_INT;
				try { weather3TemperatureMin = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MIN); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int weather3TemperatureMax = DatabaseHelper.NULL_INT;
				try { weather3TemperatureMax = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MAX); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather4Date = DatabaseHelper.NULL_STRING;
				try { weather4Date = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_DATE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather4Symbol = DatabaseHelper.NULL_STRING;
				try { weather4Symbol = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_SYMBOL_NAME); }
				catch(JSONException e) { /* hodnota je null */ }

				int weather4TemperatureMin = DatabaseHelper.NULL_INT;
				try { weather4TemperatureMin = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MIN); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int weather4TemperatureMax = DatabaseHelper.NULL_INT;
				try { weather4TemperatureMax = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MAX); }
				catch(JSONException e) { /* hodnota je null */ }

				String weather5Date = DatabaseHelper.NULL_STRING;
				try { weather5Date = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_DATE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather5Symbol = DatabaseHelper.NULL_STRING;
				try { weather5Symbol = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_SYMBOL_NAME); }
				catch(JSONException e) { /* hodnota je null */ }

				int weather5TemperatureMin = DatabaseHelper.NULL_INT;
				try { weather5TemperatureMin = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MIN); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int weather5TemperatureMax = DatabaseHelper.NULL_INT;
				try { weather5TemperatureMax = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MAX); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather6Date = DatabaseHelper.NULL_STRING;
				try { weather6Date = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_DATE); }
				catch(JSONException e) { /* hodnota je null */ }
				
				String weather6Symbol = DatabaseHelper.NULL_STRING;
				try { weather6Symbol = object.getString(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_SYMBOL_NAME); }
				catch(JSONException e) { /* hodnota je null */ }

				int weather6TemperatureMin = DatabaseHelper.NULL_INT;
				try { weather6TemperatureMin = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MIN); }
				catch(JSONException e) { /* hodnota je null */ }
				
				int weather6TemperatureMax = DatabaseHelper.NULL_INT;
				try { weather6TemperatureMax = object.getInt(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MAX); }
				catch(JSONException e) { /* hodnota je null */ }
				
				
				SkicentreLong skicentre = new SkicentreLong(id, name);
				skicentre.setArea(area);
				skicentre.setCountry(country);
				skicentre.setInfoPerex(infoPerex);
				skicentre.setDateSeasonStart(dateSeasonStart);
				skicentre.setDateSeasonEnd(dateSeasonEnd);
				skicentre.setLocationLatitude(locationLatitude);
				skicentre.setLocationLongitude(locationLongitude);
				skicentre.setLocationAltitudeUndermost(locationAltitudeUndermost);
				skicentre.setLocationAltitudeTopmost(locationAltitudeTopmost);
				skicentre.setCountLiftsOpened(countLiftsOpened);
				skicentre.setCountDownhillsOpened(countDownhillsOpened);
				skicentre.setLengthCrosscountry(lengthCrosscountry);
				skicentre.setLengthDownhillsTotal(lengthDownhillsTotal);
				skicentre.setFlagOpened(flagOpened);
				skicentre.setFlagNightski(flagNightski);
				skicentre.setFlagValley(flagValley);
				skicentre.setFlagSnowpark(flagSnowpark);
				skicentre.setFlagHalfpipe(flagHalfpipe);
				skicentre.setPriceAdults1(priceAdults1);
				skicentre.setPriceAdults6(priceAdults6);
				skicentre.setPriceChildren1(priceChildren1);
				skicentre.setPriceChildren6(priceChildren6);
				skicentre.setPriceYoung1(priceYoung1);
				skicentre.setPriceYoung6(priceYoung6);
				skicentre.setPriceSeniors1(priceSeniors1);
				skicentre.setPriceSeniors6(priceSeniors6);
				skicentre.setPriceCurrency(priceCurrency);
				skicentre.setUrlSkimap(urlSkimap);
				skicentre.setUrlHomepage(urlHomepage);
				skicentre.setUrlWebcams(urlWebcams);
				skicentre.setUrlSnowReport(urlSnowReport);
				skicentre.setUrlWeatherReport(urlWeatherReport);
				skicentre.setUrlImgMap(urlImgMap);
				skicentre.setUrlImgMeteogram(urlImgMeteogram);
				skicentre.setUrlImgWebcam(urlImgWebcam);
				skicentre.setSnowMin(snowMin);
				skicentre.setSnowMax(snowMax);
				skicentre.setSnowDateLastSnow(snowDateLastSnow);
				skicentre.setSnowDateLastUpdate(snowDateLastUpdate);
				skicentre.setWeather1Date(weather1Date);
				skicentre.setWeather1Symbol(Weather.stringToType(weather1Symbol));
				skicentre.setWeather1TemperatureMin(weather1TemperatureMin);
				skicentre.setWeather1TemperatureMax(weather1TemperatureMax);
				skicentre.setWeather2Date(weather2Date);
				skicentre.setWeather2Symbol(Weather.stringToType(weather2Symbol));
				skicentre.setWeather2TemperatureMin(weather2TemperatureMin);
				skicentre.setWeather2TemperatureMax(weather2TemperatureMax);
				skicentre.setWeather3Date(weather3Date);
				skicentre.setWeather3Symbol(Weather.stringToType(weather3Symbol));
				skicentre.setWeather3TemperatureMin(weather3TemperatureMin);
				skicentre.setWeather3TemperatureMax(weather3TemperatureMax);
				skicentre.setWeather4Date(weather4Date);
				skicentre.setWeather4Symbol(Weather.stringToType(weather4Symbol));
				skicentre.setWeather4TemperatureMin(weather4TemperatureMin);
				skicentre.setWeather4TemperatureMax(weather4TemperatureMax);
				skicentre.setWeather5Date(weather5Date);
				skicentre.setWeather5Symbol(Weather.stringToType(weather5Symbol));
				skicentre.setWeather5TemperatureMin(weather5TemperatureMin);
				skicentre.setWeather5TemperatureMax(weather5TemperatureMax);
				skicentre.setWeather6Date(weather6Date);
				skicentre.setWeather6Symbol(Weather.stringToType(weather6Symbol));
				skicentre.setWeather6TemperatureMin(weather6TemperatureMin);
				skicentre.setWeather6TemperatureMax(weather6TemperatureMax);

				// TODO: osetrit favourite a last update
				
				mDatabase.upsertSkicentre(skicentre);
				state = true;
			}
			else
			{
				state = false;
			}
		}
		catch (SQLiteException e) 
		{			
			e.printStackTrace();
			state = false;
		}
		catch (JSONException e) 
		{			
			e.printStackTrace();
			state = false;
		}
		finally
		{
			mDatabase.close();
		}
				
		return state;
	}
	
	
	public int storeAreas(String jsonData)
	{
		// TODO: overit funkcnost JSON na serveru
		int count = 0;
		JSONArray jsonRoot = null;

		// ziskej oblasti a uloz do databaze
		try
		{
			// nacti JSON pole
			jsonRoot = new JSONArray(jsonData);
			
			// otevreni databaze
			mDatabase.open(true);
			
			for(int i=0;i<jsonRoot.length();i++)
			{
				JSONObject object = jsonRoot.getJSONObject(i);
				
				int id = object.getInt(DatabaseHelper.TAB_AREA_API_ID);
				String name = object.getString(DatabaseHelper.TAB_AREA_API_NAME);
				
				Area area = new Area(id, name);
				mDatabase.upsertArea(area);
				count++;
			}
		}
		catch (SQLiteException e) 
		{			
			e.printStackTrace();
		}
		catch (JSONException e) 
		{			
			e.printStackTrace();
		}
		finally
		{
			if(mDatabase!=null) mDatabase.close();
		}
				
		return count;
	}
	
	
	public int storeCountries(String jsonData)
	{
		// TODO: overit funkcnost JSON na serveru
		int count = 0;
		JSONArray jsonRoot = null;

		// ziskej staty a uloz do databaze
		try
		{
			// nacti JSON pole
			jsonRoot = new JSONArray(jsonData);
			
			// otevreni databaze
			mDatabase.open(true);
			
			for(int i=0;i<jsonRoot.length();i++)
			{
				JSONObject object = jsonRoot.getJSONObject(i);
				
				int id = object.getInt(DatabaseHelper.TAB_COUNTRY_API_ID);
				String name = object.getString(DatabaseHelper.TAB_COUNTRY_API_NAME);
				String isoCode = object.getString(DatabaseHelper.TAB_COUNTRY_API_ISO_CODE);
				
				Country country = new Country(id, name, isoCode);
				mDatabase.upsertCountry(country);
				count++;
			}
		}
		catch (SQLiteException e) 
		{			
			e.printStackTrace();
		}
		catch (JSONException e) 
		{			
			e.printStackTrace();
		}
		finally
		{
			if(mDatabase!=null) mDatabase.close();
		}
				
		return count;
	}
}
