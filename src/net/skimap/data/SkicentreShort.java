package net.skimap.data;

public class SkicentreShort 
{
	public static enum Open { CLOSED, OPENED, UNKNOWN };
	
	protected int mId;
	protected String mName;
	protected int mArea;
	protected int mCountry;
	protected double mLocationLatitude;
	protected double mLocationLongitude;
	protected Open mFlagOpened;
	protected int mSnowMax;
	
	public SkicentreShort(int id, String name) 
	{
		mId = id;
		mName = name;
	}
	
	public SkicentreShort(int id, String name, int area, int country, double locationLatitude, double locationLongitude, Open flagOpened, int snowMax)
	{
		mId = id;
		mName = name;
		mArea = area;
		mCountry = country;
		mLocationLatitude = locationLatitude;
		mLocationLongitude = locationLongitude;
		mFlagOpened = flagOpened;
		mSnowMax = snowMax;
	}

	public void setId(int id) { mId = id; }
	public void setName(String name) { mName = name; }
	public void setArea(int area) { mArea = area; }
	public void setCountry(int country) { mCountry = country; }
	public void setLocationLatitude(double locationLatitude) { mLocationLatitude = locationLatitude; }
	public void setLocationLongitude(double locationLongitude) { mLocationLongitude = locationLongitude; }
	public void setFlagOpened(Open flagOpened) { mFlagOpened = flagOpened; }
	public void setSnowMax(int snowMax) { mSnowMax = snowMax; }
	
	public int getId() { return mId; }
	public String getName() { return mName; }
	public int getArea() { return mArea; }
	public int getCountry() { return mCountry; }
	public double getLocationLatitude() { return mLocationLatitude; }
	public double getLocationLongitude() { return mLocationLongitude; }
	public Open getFlagOpened() { return mFlagOpened; }
	public int getSnowMax() { return mSnowMax; }
	
	public static Open intToOpen(int value)
	{
		SkicentreShort.Open values[] = SkicentreShort.Open.values();
		return values[value];
	}
}
