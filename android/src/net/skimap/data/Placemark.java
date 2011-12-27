package net.skimap.data;

import android.graphics.Color;

public class Placemark
{
	private String mCoordinates = null;
	private String mStyle = null;
	
	public Placemark()
	{
	}
	
	public Placemark(String coordinates, String style)
	{
		mCoordinates = coordinates;
		mStyle = style;
	}
	
	public String getCoordinates() { return mCoordinates; }
	public void setCoordinates(String coordinates) { mCoordinates = coordinates; }
	
	public String getStyle() { return mStyle; }
	public void setStyle(String style) { mStyle = style; }
	
	public int getColor()
	{
		int color = Color.GRAY;
		try
		{
			// ziskani hexa-barvy z mStyle
			int start = mStyle.length()-6;
			int end = mStyle.length();
			String hexa = "#" + (new StringBuffer(mStyle.substring(start, end)).reverse().toString());
			color = Color.parseColor(hexa);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return color;
	}
}
