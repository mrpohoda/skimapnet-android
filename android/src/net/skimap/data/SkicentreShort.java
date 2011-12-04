package net.skimap.data;

public class SkicentreShort 
{
	protected int mId;
	protected String mName;
	protected int mArea;
	protected int mCountry;
	protected double mLocationLatitude;
	protected double mLocationLongitude;
	protected boolean mFlagOpened;
	protected int mSnowMin;
	
	public SkicentreShort(int id, String name) 
	{
		mId = id;
		mName = name;
	}
	
	public SkicentreShort(int id, String name, int area, int country, double locationLatitude, double locationLongitude, boolean flagOpened, int snowMin)
	{
		mId = id;
		mName = name;
		mArea = area;
		mCountry = country;
		mLocationLatitude = locationLatitude;
		mLocationLongitude = locationLongitude;
		mFlagOpened = flagOpened;
		mSnowMin = snowMin;
	}

	public void setId(int id) { mId = id; }
	public void setName(String name) { mName = name; }
	public void setArea(int area) { mArea = area; }
	public void setCountry(int country) { mCountry = country; }
	public void setLocationLatitude(double locationLatitude) { mLocationLatitude = locationLatitude; }
	public void setLocationLongitude(double locationLongitude) { mLocationLongitude = locationLongitude; }
	public void setFlagOpened(boolean flagOpened) { mFlagOpened = flagOpened; }
	public void setSnowMin(int snowMin) { mSnowMin = snowMin; }
	
	public int getId() { return mId; }
	public String getName() { return mName; }
	public int getArea() { return mArea; }
	public int getCountry() { return mCountry; }
	public double getLocationLatitude() { return mLocationLatitude; }
	public double getLocationLongitude() { return mLocationLongitude; }
	public boolean isFlagOpened() { return mFlagOpened; }
	public int getSnowMin() { return mSnowMin; }
}
