package net.skimap.data;

import net.skimap.R;

public class Weather 
{
	public static enum Type 
	{
		SUN_CLEAR_SKY, FAIR, PARTLY_CLOUDY, CLOUDY, FOG, RAIN, RAIN_SHOWERS, HEAVY_RAIN, RAIN_AND_THUNDER, RAIN_SHOWERS_WITH_THUNDER, SLEET, SLEET_SHOWERS, SNOW, SNOW_SHOWERS, SNOW_AND_THUNDER, UNKNOWN
	}
	
	
	public static final String SUN_CLEAR_SKY = "Sun/clear sky";
	public static final String FAIR = "Fair";
	public static final String PARTLY_CLOUDY = "Partly cloudy";
	public static final String CLOUDY = "Cloudy";
	public static final String FOG = "Fog";
	public static final String RAIN = "Rain";
	public static final String RAIN_SHOWERS = "Rain showers";
	public static final String HEAVY_RAIN = "Heavy rain";
	public static final String RAIN_AND_THUNDER = "Rain and thunder";
	public static final String RAIN_SHOWERS_WITH_THUNDER = "Rain showers with thunder";
	public static final String SLEET = "Sleet";
	public static final String SLEET_SHOWERS = "Sleet showers";
	public static final String SNOW = "Snow";
	public static final String SNOW_SHOWERS = "Snow showers";
	public static final String SNOW_AND_THUNDER = "Snow and thunder";
	
	
	public static Weather.Type intToType(int i)
	{
		Weather.Type[] types = Weather.Type.values();
		return types[i];
	}
	
	
	public static Weather.Type stringToType(String str)
	{
		if(str.equalsIgnoreCase(SUN_CLEAR_SKY)) return Weather.Type.SUN_CLEAR_SKY;
		else if(str.contentEquals(FAIR)) return Weather.Type.FAIR;
		else if(str.contentEquals(PARTLY_CLOUDY)) return Weather.Type.PARTLY_CLOUDY;
		else if(str.contentEquals(CLOUDY)) return Weather.Type.CLOUDY;
		else if(str.contentEquals(FOG)) return Weather.Type.FOG;
		else if(str.contentEquals(RAIN)) return Weather.Type.RAIN;
		else if(str.contentEquals(RAIN_SHOWERS)) return Weather.Type.RAIN_SHOWERS;
		else if(str.contentEquals(HEAVY_RAIN)) return Weather.Type.HEAVY_RAIN;
		else if(str.contentEquals(RAIN_AND_THUNDER)) return Weather.Type.RAIN_AND_THUNDER;
		else if(str.contentEquals(RAIN_SHOWERS_WITH_THUNDER)) return Weather.Type.RAIN_SHOWERS_WITH_THUNDER;
		else if(str.contentEquals(SLEET)) return Weather.Type.SLEET;
		else if(str.contentEquals(SLEET_SHOWERS)) return Weather.Type.SLEET_SHOWERS;
		else if(str.contentEquals(SNOW)) return Weather.Type.SNOW;
		else if(str.contentEquals(SNOW_SHOWERS)) return Weather.Type.SNOW_SHOWERS;
		else if(str.contentEquals(SNOW_AND_THUNDER)) return Weather.Type.SNOW_AND_THUNDER;
		else return Weather.Type.UNKNOWN;
	}
	

	public static int typeToDrawable(Weather.Type type)
	{
		if(type == Weather.Type.SUN_CLEAR_SKY) return R.drawable.weather_sun_clear_sky;
		else if(type == Weather.Type.FAIR) return R.drawable.weather_fair;
		else if(type == Weather.Type.PARTLY_CLOUDY) return R.drawable.weather_partly_cloudy;
		else if(type == Weather.Type.CLOUDY) return R.drawable.weather_cloudy;
		else if(type == Weather.Type.FOG) return R.drawable.weather_fog;
		else if(type == Weather.Type.RAIN) return R.drawable.weather_rain;
		else if(type == Weather.Type.RAIN_SHOWERS) return R.drawable.weather_rain_showers;
		else if(type == Weather.Type.HEAVY_RAIN) return R.drawable.weather_heavy_rain;
		else if(type == Weather.Type.RAIN_AND_THUNDER) return R.drawable.weather_rain_and_thunder;
		else if(type == Weather.Type.RAIN_SHOWERS_WITH_THUNDER) return R.drawable.weather_rain_showers_with_thunder;
		else if(type == Weather.Type.SLEET) return R.drawable.weather_sleet;
		else if(type == Weather.Type.SLEET_SHOWERS) return R.drawable.weather_sleet_showers;
		else if(type == Weather.Type.SNOW) return R.drawable.weather_snow;
		else if(type == Weather.Type.SNOW_SHOWERS) return R.drawable.weather_snow_showers;
		else if(type == Weather.Type.SNOW_AND_THUNDER) return R.drawable.weather_snow_and_thunder;
		else return -1;
	}
}
