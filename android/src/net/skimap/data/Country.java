package net.skimap.data;

public class Country 
{
	private int mId;
	private String mName;
	
	public Country(int id, String name)
	{
		mId = id;
		mName = name;
	}
	
	public void setId(int id) { mId = id; }
	public void setName(String name) { mName = name; }
	
	public int getId() { return mId; }
	public String getName() { return mName; }
}
