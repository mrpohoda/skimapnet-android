package net.skimap.data;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SkicentreLong extends SkicentreShort
{
	private final String DATE_FORMAT = "yyyy-MM-dd";
	
	
	protected String mInfoPerex;
	protected Date mDateSeasonStart;
	protected Date mDateSeasonEnd;
	protected int mLocationAltitudeUndermost;
	protected int mLocationAltitudeTopmost;
	protected int mCountLiftsOpened;
	protected int mCountDownhillsOpened;
	protected int mLengthCrosscountry;
	protected int mLengthDownhillsTotal;
	protected boolean mFlagNightski;
	protected boolean mFlagValley;
	protected boolean mFlagSnowpark;
	protected boolean mFlagHalfpipe;
	protected int mPriceAdults1;
	protected int mPriceAdults6;
	protected int mPriceChildren1;
	protected int mPriceChildren6;
	protected int mPriceYoung1;
	protected int mPriceYoung6;
	protected int mPriceSeniors1;
	protected int mPriceSeniors6;
	protected String mPriceCurrency;
	protected String mUrlSkimap;
	protected String mUrlHomepage;
	protected String mUrlWebcams;
	protected String mUrlSnowReport;
	protected String mUrlWeatherReport;
	protected String mUrlImgMap;
	protected String mUrlImgMeteogram;
	protected String mUrlImgWebcam;
	protected int mSnowMin;
	protected Date mSnowDateLastSnow;
	protected Date mSnowDateLastUpdate;
	protected Date mWeather1Date;
	protected Weather.Type mWeather1Symbol;
	protected int mWeather1TemperatureMin;
	protected int mWeather1TemperatureMax;
	protected Date mWeather2Date;
	protected Weather.Type mWeather2Symbol;
	protected int mWeather2TemperatureMin;
	protected int mWeather2TemperatureMax;
	protected Date mWeather3Date;
	protected Weather.Type mWeather3Symbol;
	protected int mWeather3TemperatureMin;
	protected int mWeather3TemperatureMax;
	protected Date mWeather4Date;
	protected Weather.Type mWeather4Symbol;
	protected int mWeather4TemperatureMin;
	protected int mWeather4TemperatureMax;
	protected Date mWeather5Date;
	protected Weather.Type mWeather5Symbol;
	protected int mWeather5TemperatureMin;
	protected int mWeather5TemperatureMax;
	protected Date mWeather6Date;
	protected Weather.Type mWeather6Symbol;
	protected int mWeather6TemperatureMin;
	protected int mWeather6TemperatureMax;
	protected boolean mFlagFavourite;
	protected Date mDateLastUpdate;
	
	
	public SkicentreLong(int id, String name)
	{
		super(id, name);
		mId = id;
		mName = name;
	}
	
	
	private String dateToString(Date date)
	{
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		return format.format(date);
	}
	
	
	private Date stringToDate(String str)
	{
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		Date date = null;
		try { date = (Date) format.parse(str); }
		catch (ParseException e) { e.printStackTrace(); }
		return date;
	}
	

	public String getInfoPerex() { return mInfoPerex; }
	public void setInfoPerex(String mInfoPerex) { this.mInfoPerex = mInfoPerex; }
	public Date getDateSeasonStart() { return mDateSeasonStart; }
	public String getDateSeasonStartString() { return dateToString(mDateSeasonStart); }
	public void setDateSeasonStart(Date mDateSeasonStart) { this.mDateSeasonStart = mDateSeasonStart; }
	public void setDateSeasonStart(String mDateSeasonStart) { this.mDateSeasonStart = stringToDate(mDateSeasonStart); }
	public Date getDateSeasonEnd() { return mDateSeasonEnd; }
	public String getDateSeasonEndString() { return dateToString(mDateSeasonEnd); }
	public void setDateSeasonEnd(Date mDateSeasonEnd) { this.mDateSeasonEnd = mDateSeasonEnd; }
	public void setDateSeasonEnd(String mDateSeasonEnd) { this.mDateSeasonEnd = stringToDate(mDateSeasonEnd); }
	public int getLocationAltitudeUndermost() { return mLocationAltitudeUndermost; }
	public void setLocationAltitudeUndermost(int mLocationAltitudeUndermost) { this.mLocationAltitudeUndermost = mLocationAltitudeUndermost; }
	public int getLocationAltitudeTopmost() { return mLocationAltitudeTopmost; }
	public void setLocationAltitudeTopmost(int mLocationAltitudeTopmost) { this.mLocationAltitudeTopmost = mLocationAltitudeTopmost; }
	public int getCountLiftsOpened() { return mCountLiftsOpened; }
	public void setCountLiftsOpened(int mCountLiftsOpened) { this.mCountLiftsOpened = mCountLiftsOpened; }
	public int getCountDownhillsOpened() { return mCountDownhillsOpened; }
	public void setCountDownhillsOpened(int mCountDownhillsOpened) { this.mCountDownhillsOpened = mCountDownhillsOpened; }
	public int getLengthCrosscountry() { return mLengthCrosscountry; }
	public void setLengthCrosscountry(int mLengthCrosscountry) { this.mLengthCrosscountry = mLengthCrosscountry; }
	public int getLengthDownhillsTotal() { return mLengthDownhillsTotal; }
	public void setLengthDownhillsTotal(int mLengthDownhillsTotal) { this.mLengthDownhillsTotal = mLengthDownhillsTotal; }
	public boolean isFlagNightski() { return mFlagNightski; }
	public void setFlagNightski(boolean mFlagNightski) { this.mFlagNightski = mFlagNightski; }
	public boolean isFlagValley() { return mFlagValley; }
	public void setFlagValley(boolean mFlagValley) { this.mFlagValley = mFlagValley; }
	public boolean isFlagSnowpark() { return mFlagSnowpark; }
	public void setFlagSnowpark(boolean mFlagSnowpark) { this.mFlagSnowpark = mFlagSnowpark; }
	public boolean isFlagHalfpipe() { return mFlagHalfpipe; }
	public void setFlagHalfpipe(boolean mFlagHalfpipe) { this.mFlagHalfpipe = mFlagHalfpipe; }
	public int getPriceAdults1() { return mPriceAdults1; }
	public void setPriceAdults1(int mPriceAdults1) { this.mPriceAdults1 = mPriceAdults1; }
	public int getPriceAdults6() { return mPriceAdults6; }
	public void setPriceAdults6(int mPriceAdults6) { this.mPriceAdults6 = mPriceAdults6; }
	public int getPriceChildren1() { return mPriceChildren1; }
	public void setPriceChildren1(int mPriceChildren1) { this.mPriceChildren1 = mPriceChildren1; }
	public int getPriceChildren6() { return mPriceChildren6; }
	public void setPriceChildren6(int mPriceChildren6) { this.mPriceChildren6 = mPriceChildren6; }
	public int getPriceYoung1() { return mPriceYoung1; }
	public void setPriceYoung1(int mPriceYoung1) { this.mPriceYoung1 = mPriceYoung1; }
	public int getPriceYoung6() { return mPriceYoung6; }
	public void setPriceYoung6(int mPriceYoung6) { this.mPriceYoung6 = mPriceYoung6; }
	public int getPriceSeniors1() { return mPriceSeniors1; }
	public void setPriceSeniors1(int mPriceSeniors1) { this.mPriceSeniors1 = mPriceSeniors1; }
	public int getPriceSeniors6() { return mPriceSeniors6; }
	public void setPriceSeniors6(int mPriceSeniors6) { this.mPriceSeniors6 = mPriceSeniors6; }
	public String getPriceCurrency() { return mPriceCurrency; }
	public void setPriceCurrency(String mPriceCurrency) { this.mPriceCurrency = mPriceCurrency; }
	public String getUrlSkimap() { return mUrlSkimap; }
	public void setUrlSkimap(String mUrlSkimap) { this.mUrlSkimap = mUrlSkimap; }
	public String getUrlHomepage() { return mUrlHomepage; }
	public void setUrlHomepage(String mUrlHomepage) { this.mUrlHomepage = mUrlHomepage; }
	public String getUrlWebcams() { return mUrlWebcams; }
	public void setUrlWebcams(String mUrlWebcams) { this.mUrlWebcams = mUrlWebcams; }
	public String getUrlSnowReport() { return mUrlSnowReport; }
	public void setUrlSnowReport(String mUrlSnowReport) { this.mUrlSnowReport = mUrlSnowReport; }
	public String getUrlWeatherReport() { return mUrlWeatherReport; }
	public void setUrlWeatherReport(String mUrlWeatherReport) { this.mUrlWeatherReport = mUrlWeatherReport; }
	public String getUrlImgMap() { return mUrlImgMap; }
	public void setUrlImgMap(String mUrlImgMap) { this.mUrlImgMap = mUrlImgMap; }
	public String getUrlImgMeteogram() { return mUrlImgMeteogram; }
	public void setUrlImgMeteogram(String mUrlImgMeteogram) { this.mUrlImgMeteogram = mUrlImgMeteogram; }
	public String getUrlImgWebcam() { return mUrlImgWebcam; }
	public void setUrlImgWebcam(String mUrlImgWebcam) { this.mUrlImgWebcam = mUrlImgWebcam; }
	public int getSnowMin() { return mSnowMin; }
	public void setSnowMin(int mSnowMin) { this.mSnowMin = mSnowMin; }
	public Date getSnowDateLastSnow() { return mSnowDateLastSnow; }
	public String getSnowDateLastSnowString() { return dateToString(mSnowDateLastSnow); }
	public void setSnowDateLastSnow(Date mSnowDateLastSnow) { this.mSnowDateLastSnow = mSnowDateLastSnow; }
	public void setSnowDateLastSnow(String mSnowDateLastSnow) { this.mSnowDateLastSnow = stringToDate(mSnowDateLastSnow); }
	public Date getSnowDateLastUpdate() { return mSnowDateLastUpdate; }
	public String getSnowDateLastUpdateString() { return dateToString(mSnowDateLastUpdate); }
	public void setSnowDateLastUpdate(Date mSnowDateLastUpdate) { this.mSnowDateLastUpdate = mSnowDateLastUpdate; }
	public void setSnowDateLastUpdate(String mSnowDateLastUpdate) { this.mSnowDateLastUpdate = stringToDate(mSnowDateLastUpdate); }
	public Date getWeather1Date() { return mWeather1Date; }
	public String getWeather1DateString() { return dateToString(mWeather1Date); }
	public void setWeather1Date(Date mWeather1Date) { this.mWeather1Date = mWeather1Date; }
	public void setWeather1Date(String mWeather1Date) { this.mWeather1Date = stringToDate(mWeather1Date); }
	public Weather.Type getWeather1Symbol() { return mWeather1Symbol; }
	public void setWeather1Symbol(Weather.Type mWeather1Symbol) { this.mWeather1Symbol = mWeather1Symbol; }
	public int getWeather1TemperatureMin() { return mWeather1TemperatureMin; }
	public void setWeather1TemperatureMin(int mWeather1TemperatureMin) { this.mWeather1TemperatureMin = mWeather1TemperatureMin; }
	public int getWeather1TemperatureMax() { return mWeather1TemperatureMax; }
	public void setWeather1TemperatureMax(int mWeather1TemperatureMax) { this.mWeather1TemperatureMax = mWeather1TemperatureMax; }
	public Date getWeather2Date() { return mWeather2Date; }
	public String getWeather2DateString() { return dateToString(mWeather2Date); }
	public void setWeather2Date(Date mWeather2Date) { this.mWeather2Date = mWeather2Date; }
	public void setWeather2Date(String mWeather2Date) { this.mWeather2Date = stringToDate(mWeather2Date); }
	public Weather.Type getWeather2Symbol() { return mWeather2Symbol; }
	public void setWeather2Symbol(Weather.Type mWeather2Symbol) { this.mWeather2Symbol = mWeather2Symbol; }
	public int getWeather2TemperatureMin() { return mWeather2TemperatureMin; }
	public void setWeather2TemperatureMin(int mWeather2TemperatureMin) { this.mWeather2TemperatureMin = mWeather2TemperatureMin; }
	public int getWeather2TemperatureMax() { return mWeather2TemperatureMax; }
	public void setWeather2TemperatureMax(int mWeather2TemperatureMax) { this.mWeather2TemperatureMax = mWeather2TemperatureMax; }
	public Date getWeather3Date() { return mWeather3Date; }
	public String getWeather3DateString() { return dateToString(mWeather3Date); }
	public void setWeather3Date(Date mWeather3Date) { this.mWeather3Date = mWeather3Date; }
	public void setWeather3Date(String mWeather3Date) { this.mWeather3Date = stringToDate(mWeather3Date); }
	public Weather.Type getWeather3Symbol() { return mWeather3Symbol; }
	public void setWeather3Symbol(Weather.Type mWeather3Symbol) { this.mWeather3Symbol = mWeather3Symbol; }
	public int getWeather3TemperatureMin() { return mWeather3TemperatureMin; }
	public void setWeather3TemperatureMin(int mWeather3TemperatureMin) { this.mWeather3TemperatureMin = mWeather3TemperatureMin; }
	public int getWeather3TemperatureMax() { return mWeather3TemperatureMax; }
	public void setWeather3TemperatureMax(int mWeather3TemperatureMax) { this.mWeather3TemperatureMax = mWeather3TemperatureMax; }
	public Date getWeather4Date() { return mWeather4Date; }
	public String getWeather4DateString() { return dateToString(mWeather4Date); }
	public void setWeather4Date(Date mWeather4Date) { this.mWeather4Date = mWeather4Date; }
	public void setWeather4Date(String mWeather4Date) { this.mWeather4Date = stringToDate(mWeather4Date); }
	public Weather.Type getWeather4Symbol() { return mWeather4Symbol; }
	public void setWeather4Symbol(Weather.Type mWeather4Symbol) { this.mWeather4Symbol = mWeather4Symbol; }
	public int getWeather4TemperatureMin() { return mWeather4TemperatureMin; }
	public void setWeather4TemperatureMin(int mWeather4TemperatureMin) { this.mWeather4TemperatureMin = mWeather4TemperatureMin; }
	public int getWeather4TemperatureMax() { return mWeather4TemperatureMax; }
	public void setWeather4TemperatureMax(int mWeather4TemperatureMax) { this.mWeather4TemperatureMax = mWeather4TemperatureMax; }
	public Date getWeather5Date() { return mWeather5Date; }
	public String getWeather5DateString() { return dateToString(mWeather5Date); }
	public void setWeather5Date(Date mWeather5Date) { this.mWeather5Date = mWeather5Date; }
	public void setWeather5Date(String mWeather5Date) { this.mWeather5Date = stringToDate(mWeather5Date); }
	public Weather.Type getWeather5Symbol() { return mWeather5Symbol; }
	public void setWeather5Symbol(Weather.Type mWeather5Symbol) { this.mWeather5Symbol = mWeather5Symbol; }
	public int getWeather5TemperatureMin() { return mWeather5TemperatureMin; }
	public void setWeather5TemperatureMin(int mWeather5TemperatureMin) { this.mWeather5TemperatureMin = mWeather5TemperatureMin; }
	public int getWeather5TemperatureMax() { return mWeather5TemperatureMax; }
	public void setWeather5TemperatureMax(int mWeather5TemperatureMax) { this.mWeather5TemperatureMax = mWeather5TemperatureMax; }
	public Date getWeather6Date() { return mWeather6Date; }
	public String getWeather6DateString() { return dateToString(mWeather6Date); }
	public void setWeather6Date(Date mWeather6Date) { this.mWeather6Date = mWeather6Date; }
	public void setWeather6Date(String mWeather6Date) { this.mWeather6Date = stringToDate(mWeather6Date); }
	public Weather.Type getWeather6Symbol() { return mWeather6Symbol; }
	public void setWeather6Symbol(Weather.Type mWeather6Symbol) { this.mWeather6Symbol = mWeather6Symbol; }
	public int getWeather6TemperatureMin() { return mWeather6TemperatureMin; }
	public void setWeather6TemperatureMin(int mWeather6TemperatureMin) { this.mWeather6TemperatureMin = mWeather6TemperatureMin; }
	public int getWeather6TemperatureMax() { return mWeather6TemperatureMax; }
	public void setWeather6TemperatureMax(int mWeather6TemperatureMax) { this.mWeather6TemperatureMax = mWeather6TemperatureMax; }
	public boolean isFlagFavourite() { return mFlagFavourite; }
	public void setFlagFavourite(boolean mFlagFavourite) { this.mFlagFavourite = mFlagFavourite; }
	public Date getDateLastUpdate() { return mDateLastUpdate; }
	public String getDateLastUpdateString() { return dateToString(mDateLastUpdate); }
	public void setDateLastUpdate(Date mDateLastUpdate) { this.mDateLastUpdate = mDateLastUpdate; }
	public void setDateLastUpdate(String mDateLastUpdate) { this.mDateLastUpdate = stringToDate(mDateLastUpdate); }
}
