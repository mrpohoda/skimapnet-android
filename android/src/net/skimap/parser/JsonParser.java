package net.skimap.parser;

import java.io.IOException;
import java.net.URL;

import net.skimap.data.Area;
import net.skimap.data.Country;
import net.skimap.data.SkicentreLong;
import net.skimap.data.SkicentreShort;
import net.skimap.data.Weather;
import net.skimap.database.Database;
import net.skimap.database.DatabaseHelper;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonToken;

import android.content.Context;


public class JsonParser
{
	private final String VALUE_NULL = "null";
	
	private Database mDatabase;
	
	
	public JsonParser(Context context, Database database)
	{
		mDatabase = database;
	}
	
	
	public int storeSkicentresShort(String url) throws IOException
	{
		int count = 0;
		
		// stazeni JSON a vytvoreni parseru
		JsonFactory factory = new JsonFactory();
		org.codehaus.jackson.JsonParser parser = null;
		parser = factory.createJsonParser(new URL(url));
		
		// zacatek transakce
		mDatabase.beginTransaction();
		
		// prochazeni JSON
		if(parser.nextToken() == JsonToken.START_ARRAY) // zacatek pole
		while(parser.nextToken() != JsonToken.END_ARRAY)
		{
			// zacatek objektu
			if(parser.getCurrentToken() == JsonToken.START_OBJECT)
			{
				// defaultni hodnoty
				int id = DatabaseHelper.NULL_INT;
				String name = DatabaseHelper.NULL_STRING;
				int area = DatabaseHelper.NULL_INT;
				int country = DatabaseHelper.NULL_INT;
				double locationLatitude = DatabaseHelper.NULL_DOUBLE;
				double locationLongitude = DatabaseHelper.NULL_DOUBLE;
				SkicentreShort.Open flagOpened = SkicentreShort.Open.UNKNOWN;
				int snowMax = DatabaseHelper.NULL_INT;
				
				// prochazej objekt az do konce
				while(parser.nextToken() != JsonToken.END_OBJECT)
				{
					// aktualni token je nazev polozky
					if(parser.getCurrentToken() == JsonToken.FIELD_NAME) continue;
					
					// ziskej nazev polozky
					String field = parser.getCurrentName();
					
					// over nazev polozky a uloz hodnotu
					if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_ID))
					{
						try { id = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_NAME))
					{
						name = parser.getText();
						if(name.equals(VALUE_NULL)) name=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_AREA_ID))
					{
						try { area = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_ID))
					{
						try { country = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LATITUDE))
					{
						try { locationLatitude = Double.parseDouble(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LONGITUDE))
					{
						try { locationLongitude = Double.parseDouble(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_FLAG_OPENED))
					{
						try { flagOpened = SkicentreShort.intToOpen( Integer.parseInt(parser.getText()) ); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_SNOW_MAX))
					{
						try { snowMax = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
				}
				
				// vytvor objekt a uloz do databaze
				SkicentreShort skicentre = new SkicentreShort(id, name, area, country, locationLatitude, locationLongitude, flagOpened, snowMax);
				
				// TODO: osetrit favourite a last update
				
				mDatabase.upsertSkicentre(skicentre);
				count++;
				//Log.d("SKIMAP", "SKICENTRE SHORT: " + id + "|" + name);
			}
		}
		
		// konec transakce
		mDatabase.endTransaction();

		// konec parsovani
		if(parser!=null) parser.close();
		return count;
	}
	
	
	public int storeSkicentreLong(String url) throws IOException
	{
		int count = 0;
		
		// stazeni JSON a vytvoreni parseru
		JsonFactory factory = new JsonFactory();
		org.codehaus.jackson.JsonParser parser = null;
		parser = factory.createJsonParser(new URL(url));
		
		// zacatek transakce
		mDatabase.beginTransaction();
		
		// prochazeni JSON
		if(parser.nextToken() == JsonToken.START_ARRAY) // zacatek pole
		while(parser.nextToken() != JsonToken.END_ARRAY)
		{
			// zacatek objektu
			if(parser.getCurrentToken() == JsonToken.START_OBJECT)
			{
				// defaultni hodnoty
				int id = DatabaseHelper.NULL_INT;
				String name = DatabaseHelper.NULL_STRING;
				int area = DatabaseHelper.NULL_INT;
				int country = DatabaseHelper.NULL_INT;
				String infoPerex = DatabaseHelper.NULL_STRING;
				String dateSeasonStart = DatabaseHelper.NULL_STRING;
				String dateSeasonEnd = DatabaseHelper.NULL_STRING;
				double locationLatitude = DatabaseHelper.NULL_DOUBLE;
				double locationLongitude = DatabaseHelper.NULL_DOUBLE;
				int locationAltitudeUndermost = DatabaseHelper.NULL_INT;
				int locationAltitudeTopmost = DatabaseHelper.NULL_INT;
				int countLiftsOpened = DatabaseHelper.NULL_INT;
				int countDownhillsOpened = DatabaseHelper.NULL_INT;
				int lengthCrosscountry = DatabaseHelper.NULL_INT;
				int lengthDownhillsTotal = DatabaseHelper.NULL_INT;
				SkicentreShort.Open flagOpened = SkicentreShort.Open.UNKNOWN;
				boolean flagNightski = DatabaseHelper.NULL_BOOLEAN;
				boolean flagValley = DatabaseHelper.NULL_BOOLEAN;
				boolean flagSnowpark = DatabaseHelper.NULL_BOOLEAN;
				boolean flagHalfpipe = DatabaseHelper.NULL_BOOLEAN;
				int priceAdults1 = DatabaseHelper.NULL_INT;
				int priceAdults6 = DatabaseHelper.NULL_INT;
				int priceChildren1 = DatabaseHelper.NULL_INT;
				int priceChildren6 = DatabaseHelper.NULL_INT;
				int priceYoung1 = DatabaseHelper.NULL_INT;
				int priceYoung6 = DatabaseHelper.NULL_INT;
				int priceSeniors1 = DatabaseHelper.NULL_INT;
				int priceSeniors6 = DatabaseHelper.NULL_INT;
				String priceCurrency = DatabaseHelper.NULL_STRING;
				String urlSkimap = DatabaseHelper.NULL_STRING;
				String urlHomepage = DatabaseHelper.NULL_STRING;
				String urlWebcams = DatabaseHelper.NULL_STRING;
				String urlSnowReport = DatabaseHelper.NULL_STRING;
				String urlWeatherReport = DatabaseHelper.NULL_STRING;
				String urlImgMap = DatabaseHelper.NULL_STRING;
				String urlImgMeteogram = DatabaseHelper.NULL_STRING;
				String urlImgWebcam = DatabaseHelper.NULL_STRING;
				int snowMin = DatabaseHelper.NULL_INT;
				int snowMax = DatabaseHelper.NULL_INT;
				String snowDateLastSnow = DatabaseHelper.NULL_STRING;
				String snowDateLastUpdate = DatabaseHelper.NULL_STRING;
				String weather1Date = DatabaseHelper.NULL_STRING;
				String weather1Symbol = DatabaseHelper.NULL_STRING;
				int weather1TemperatureMin = DatabaseHelper.NULL_INT;
				int weather1TemperatureMax = DatabaseHelper.NULL_INT;
				String weather2Date = DatabaseHelper.NULL_STRING;
				String weather2Symbol = DatabaseHelper.NULL_STRING;
				int weather2TemperatureMin = DatabaseHelper.NULL_INT;
				int weather2TemperatureMax = DatabaseHelper.NULL_INT;
				String weather3Date = DatabaseHelper.NULL_STRING;
				String weather3Symbol = DatabaseHelper.NULL_STRING;
				int weather3TemperatureMin = DatabaseHelper.NULL_INT;
				int weather3TemperatureMax = DatabaseHelper.NULL_INT;
				String weather4Date = DatabaseHelper.NULL_STRING;
				String weather4Symbol = DatabaseHelper.NULL_STRING;
				int weather4TemperatureMin = DatabaseHelper.NULL_INT;
				int weather4TemperatureMax = DatabaseHelper.NULL_INT;
				String weather5Date = DatabaseHelper.NULL_STRING;
				String weather5Symbol = DatabaseHelper.NULL_STRING;
				int weather5TemperatureMin = DatabaseHelper.NULL_INT;
				int weather5TemperatureMax = DatabaseHelper.NULL_INT;
				String weather6Date = DatabaseHelper.NULL_STRING;
				String weather6Symbol = DatabaseHelper.NULL_STRING;
				int weather6TemperatureMin = DatabaseHelper.NULL_INT;
				int weather6TemperatureMax = DatabaseHelper.NULL_INT;
				
				// prochazej objekt az do konce
				while(parser.nextToken() != JsonToken.END_OBJECT)
				{
					// aktualni token je nazev polozky
					if(parser.getCurrentToken() == JsonToken.FIELD_NAME) continue;
					
					// ziskej nazev polozky
					String field = parser.getCurrentName();
					
					// over nazev polozky a uloz hodnotu
					if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_ID))
					{
						try { id = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_NAME))
					{
						name = parser.getText();
						if(name.equals(VALUE_NULL)) name=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_AREA_ID))
					{
						try { area = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_COUNTRY_ID))
					{
						try { country = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_INFO_PEREX))
					{
						infoPerex = parser.getText();
						if(infoPerex.equals(VALUE_NULL)) infoPerex=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_DATE_SEASON_START))
					{
						dateSeasonStart = parser.getText();
						if(dateSeasonStart.equals(VALUE_NULL)) dateSeasonStart=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_DATE_SEASON_END))
					{
						dateSeasonEnd = parser.getText();
						if(dateSeasonEnd.equals(VALUE_NULL)) dateSeasonEnd=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LATITUDE))
					{
						try { locationLatitude = Double.parseDouble(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_LONGITUDE))
					{
						try { locationLongitude = Double.parseDouble(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_ALTITUDE_UNDERMOST))
					{
						try { locationAltitudeUndermost = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_LOCATION_ALTITUDE_TOPMOST))
					{
						try { locationAltitudeTopmost = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_COUNT_LIFTS_OPENED))
					{
						try { countLiftsOpened = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_COUNT_DOWNHILLS_OPENED))
					{
						try { countDownhillsOpened = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_LENGTH_CROSSCOUNTRY))
					{
						try { lengthCrosscountry = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_LENGTH_DOWNHILLS_TOTAL))
					{
						try { lengthDownhillsTotal = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_FLAG_OPENED))
					{
						try { flagOpened = SkicentreShort.intToOpen( Integer.parseInt(parser.getText()) ); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_FLAG_NIGHTSKI))
					{
						try { flagNightski = Integer.parseInt(parser.getText())==1; }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_FLAG_VALLEY))
					{
						try { flagValley = Integer.parseInt(parser.getText())==1; }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_FLAG_SNOWPARK))
					{
						try { flagSnowpark = Integer.parseInt(parser.getText())==1; }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_FLAG_HALFPIPE))
					{
						try { flagHalfpipe = Integer.parseInt(parser.getText())==1; }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_PRICE_ADULTS_1))
					{
						try { priceAdults1 = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_PRICE_ADULTS_6))
					{
						try { priceAdults6 = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CHILDREN_1))
					{
						try { priceChildren1 = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CHILDREN_6))
					{
						try { priceChildren6 = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_PRICE_YOUNG_1))
					{
						try { priceYoung1 = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_PRICE_YOUNG_6))
					{
						try { priceYoung6 = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_PRICE_SENIORS_1))
					{
						try { priceSeniors1 = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_PRICE_SENIORS_6))
					{
						try { priceSeniors6 = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_PRICE_CURRENCY))
					{
						priceCurrency = parser.getText();
						if(priceCurrency.equals(VALUE_NULL)) priceCurrency=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_URL_SKIMAP))
					{
						urlSkimap = parser.getText();
						if(urlSkimap.equals(VALUE_NULL)) urlSkimap=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_URL_HOMEPAGE))
					{
						urlHomepage = parser.getText();
						if(urlHomepage.equals(VALUE_NULL)) urlHomepage=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_URL_WEBCAMS))
					{
						urlWebcams = parser.getText();
						if(urlWebcams.equals(VALUE_NULL)) urlWebcams=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_URL_SNOW_REPORT))
					{
						urlSnowReport = parser.getText();
						if(urlSnowReport.equals(VALUE_NULL)) urlSnowReport=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_URL_WEATHER_REPORT))
					{
						urlWeatherReport = parser.getText();
						if(urlWeatherReport.equals(VALUE_NULL)) urlWeatherReport=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_MAP))
					{
						urlImgMap = parser.getText();
						if(urlImgMap.equals(VALUE_NULL)) urlImgMap=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_METEOGRAM))
					{
						urlImgMeteogram = parser.getText();
						if(urlImgMeteogram.equals(VALUE_NULL)) urlImgMeteogram=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_URL_IMG_WEBCAM))
					{
						urlImgWebcam = parser.getText();
						if(urlImgWebcam.equals(VALUE_NULL)) urlImgWebcam=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_SNOW_MIN))
					{
						try { snowMin = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_SNOW_MAX))
					{
						try { snowMax = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_SNOW_DATE_LAST_SNOW))
					{
						snowDateLastSnow = parser.getText();
						if(snowDateLastSnow.equals(VALUE_NULL)) snowDateLastSnow=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_SNOW_DATE_LAST_UPDATE))
					{
						snowDateLastUpdate = parser.getText();
						if(snowDateLastUpdate.equals(VALUE_NULL)) snowDateLastUpdate=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_DATE))
					{
						weather1Date = parser.getText();
						if(weather1Date.equals(VALUE_NULL)) weather1Date=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_SYMBOL_NAME))
					{
						weather1Symbol = parser.getText();
						if(weather1Symbol.equals(VALUE_NULL)) weather1Symbol=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MIN))
					{
						try { weather1TemperatureMin = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_1_TEMPERATURE_MAX))
					{
						try { weather1TemperatureMax = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_DATE))
					{
						weather2Date = parser.getText();
						if(weather2Date.equals(VALUE_NULL)) weather2Date=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_SYMBOL_NAME))
					{
						weather2Symbol = parser.getText();
						if(weather2Symbol.equals(VALUE_NULL)) weather2Symbol=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MIN))
					{
						try { weather2TemperatureMin = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_2_TEMPERATURE_MAX))
					{
						try { weather2TemperatureMax = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_DATE))
					{
						weather3Date = parser.getText();
						if(weather3Date.equals(VALUE_NULL)) weather3Date=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_SYMBOL_NAME))
					{
						weather3Symbol = parser.getText();
						if(weather3Symbol.equals(VALUE_NULL)) weather3Symbol=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MIN))
					{
						try { weather3TemperatureMin = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_3_TEMPERATURE_MAX))
					{
						try { weather3TemperatureMax = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_DATE))
					{
						weather4Date = parser.getText();
						if(weather4Date.equals(VALUE_NULL)) weather4Date=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_SYMBOL_NAME))
					{
						weather4Symbol = parser.getText();
						if(weather4Symbol.equals(VALUE_NULL)) weather4Symbol=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MIN))
					{
						try { weather4TemperatureMin = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_4_TEMPERATURE_MAX))
					{
						try { weather4TemperatureMax = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_DATE))
					{
						weather5Date = parser.getText();
						if(weather5Date.equals(VALUE_NULL)) weather5Date=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_SYMBOL_NAME))
					{
						weather5Symbol = parser.getText();
						if(weather5Symbol.equals(VALUE_NULL)) weather5Symbol=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MIN))
					{
						try { weather5TemperatureMin = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_5_TEMPERATURE_MAX))
					{
						try { weather5TemperatureMax = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_DATE))
					{
						weather6Date = parser.getText();
						if(weather6Date.equals(VALUE_NULL)) weather6Date=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_SYMBOL_NAME))
					{
						weather6Symbol = parser.getText();
						if(weather6Symbol.equals(VALUE_NULL)) weather6Symbol=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MIN))
					{
						try { weather6TemperatureMin = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_SKICENTRE_API_WEATHER_6_TEMPERATURE_MAX))
					{
						try { weather6TemperatureMax = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
				}
				
				// vytvor objekt a uloz do databaze
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
				count++;
				//Log.d("SKIMAP", "SKICENTRE LONG: " + id + "|" + name);
			}
		}
		
		// konec transakce
		mDatabase.endTransaction();

		// konec parsovani
		if(parser!=null) parser.close();
		return count;
	}
	
	
	public int storeCountries(String url) throws IOException
	{
		int count = 0;
		
		// stazeni JSON a vytvoreni parseru
		JsonFactory factory = new JsonFactory();
		org.codehaus.jackson.JsonParser parser = null;
		parser = factory.createJsonParser(new URL(url));
		
		// zacatek transakce
		mDatabase.beginTransaction();
		
		// prochazeni JSON
		if(parser.nextToken() == JsonToken.START_ARRAY) // zacatek pole
		while(parser.nextToken() != JsonToken.END_ARRAY)
		{
			// zacatek objektu
			if(parser.getCurrentToken() == JsonToken.START_OBJECT)
			{
				// defaultni hodnoty
				int id = DatabaseHelper.NULL_INT;
				String name = DatabaseHelper.NULL_STRING;
				String isoCode = DatabaseHelper.NULL_STRING;
				
				// prochazej objekt az do konce
				while(parser.nextToken() != JsonToken.END_OBJECT)
				{
					// aktualni token je nazev polozky
					if(parser.getCurrentToken() == JsonToken.FIELD_NAME) continue;
					
					// ziskej nazev polozky
					String field = parser.getCurrentName();
					
					// over nazev polozky a uloz hodnotu
					if (field.equals(DatabaseHelper.TAB_COUNTRY_API_ID))
					{
						try { id = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_COUNTRY_API_NAME))
					{
						name = parser.getText();
						if(name.equals(VALUE_NULL)) name=DatabaseHelper.NULL_STRING;
					}
					else if (field.equals(DatabaseHelper.TAB_COUNTRY_API_ISO_CODE))
					{
						isoCode = parser.getText();
						if(isoCode.equals(VALUE_NULL)) isoCode=DatabaseHelper.NULL_STRING;
					}
				}
				
				// vytvor objekt a uloz do databaze
				final Country country = new Country(id, name, isoCode);
				mDatabase.upsertCountry(country);
				count++;
				//Log.d("SKIMAP", "COUNTRY: " + id + "|" + name + "|" + isoCode);
			}
		}
		
		// konec transakce
		mDatabase.endTransaction();

		// konec parsovani
		if(parser!=null) parser.close();
		return count;
	}
	
	
	public int storeAreas(String url) throws IOException
	{
		int count = 0;
		
		// stazeni JSON a vytvoreni parseru
		JsonFactory factory = new JsonFactory();
		org.codehaus.jackson.JsonParser parser = null;
		parser = factory.createJsonParser(new URL(url));
		
		// zacatek transakce
		mDatabase.beginTransaction();
		
		// prochazeni JSON
		if(parser.nextToken() == JsonToken.START_ARRAY) // zacatek pole
		while(parser.nextToken() != JsonToken.END_ARRAY)
		{
			// zacatek objektu
			if(parser.getCurrentToken() == JsonToken.START_OBJECT)
			{
				// defaultni hodnoty
				int id = DatabaseHelper.NULL_INT;
				String name = DatabaseHelper.NULL_STRING;
				
				// prochazej objekt az do konce
				while(parser.nextToken() != JsonToken.END_OBJECT)
				{
					// aktualni token je nazev polozky
					if(parser.getCurrentToken() == JsonToken.FIELD_NAME) continue;
					
					// ziskej nazev polozky
					String field = parser.getCurrentName();
					
					// over nazev polozky a uloz hodnotu
					if (field.equals(DatabaseHelper.TAB_AREA_API_ID))
					{
						try { id = Integer.parseInt(parser.getText()); }
						catch(NumberFormatException e) { /* hodnota je null */ }
					}
					else if (field.equals(DatabaseHelper.TAB_AREA_API_NAME))
					{
						name = parser.getText();
						if(name.equals(VALUE_NULL)) name=DatabaseHelper.NULL_STRING;
					}
				}
				
				// vytvor objekt a uloz do databaze
				final Area area = new Area(id, name);
				mDatabase.upsertArea(area);
				count++;
				//Log.d("SKIMAP", "AREA: " + id + "|" + name);
			}
		}
		
		// konec transakce
		mDatabase.endTransaction();

		// konec parsovani
		if(parser!=null) parser.close();
		return count;
	}
}
