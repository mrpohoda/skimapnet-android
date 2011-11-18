package net.skimap.data;

public class SkicentreShort 
{
	private int mId;
	private String mName;
	private double mLatitude;
	private double mLongitude;
	
	public SkicentreShort(int id, String name, double lat, double lon)
	{
		mId = id;
		mName = name;
		mLatitude = lat;
		mLongitude = lon;
	}
	
	public void setId(int id) { mId = id; }
	public void setName(String name) { mName = name; }
	public void setLatitude(double lat) { mLatitude = lat; }
	public void setLongitude(double lon) { mLongitude = lon; }
	
	public int getId() { return mId; }
	public String getName() { return mName; }
	public double getLatitude() { return mLatitude; }
	public double getLongitude() { return mLongitude; }
}
