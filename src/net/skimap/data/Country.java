package net.skimap.data;

public class Country 
{
	private int mId;
	private String mName;
	private String mIsoCode;
	
	public Country(int id, String name, String isoCode)
	{
		mId = id;
		mName = name;
		mIsoCode = isoCode;
	}
	
	public void setId(int id) { mId = id; }
	public void setName(String name) { mName = name; }
	public void setIsoCode(String isoCode) { mIsoCode = isoCode; }
	
	public int getId() { return mId; }
	public String getName() { return mName; }
	public String getIsoCode() { return mIsoCode; }
}
