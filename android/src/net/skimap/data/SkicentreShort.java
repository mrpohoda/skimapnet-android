package net.skimap.data;

public class SkicentreShort 
{
	private int mId;
	private String mName;
	private double mLatitude;
	private double mLongitude;
	private int mCountry;
	private boolean mOpened;
	private int mSnow;
	
	public SkicentreShort(int id, String name, double lat, double lon, int country, boolean opened, int snow)
	{
		mId = id;
		mName = name;
		mLatitude = lat;
		mLongitude = lon;
		mCountry = country;
		mOpened = opened;
		mSnow = snow;
	}
	
	public void setId(int id) { mId = id; }
	public void setName(String name) { mName = name; }
	public void setLatitude(double lat) { mLatitude = lat; }
	public void setLongitude(double lon) { mLongitude = lon; }
	public void setCountry(int country) { mCountry = country; }
	public void setOpened(boolean opened) { mOpened = opened; }
	public void setSnow(int snow) { mSnow = snow; }
	
	public int getId() { return mId; }
	public String getName() { return mName; }
	public double getLatitude() { return mLatitude; }
	public double getLongitude() { return mLongitude; }
	public int getCountry() { return mCountry; }
	public boolean getOpened() { return mOpened; }
	public int getSnow() { return mSnow; }
}
