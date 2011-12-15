package net.skimap.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import net.skimap.R;
import net.skimap.data.Area;
import net.skimap.data.Country;
import net.skimap.data.SkicentreShort;
import net.skimap.database.DatabaseHelper;
import net.skimap.fragments.ListingFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ListingAdapter extends BaseAdapter
{
	private ListingFragment mFragment;
	private ArrayList<SkicentreShort> mSkicentreList;
	private HashMap<Integer, Area> mAreaList;
	private HashMap<Integer, Country> mCountryList;
	

    public ListingAdapter(ListingFragment fragment, ArrayList<SkicentreShort> skicentreList, HashMap<Integer, Area> areaList, HashMap<Integer, Country> countryList)
    {
        mFragment = fragment;
        mSkicentreList = skicentreList;
        mAreaList = areaList;
        mCountryList = countryList;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
    	// nastaveni view
		View view = convertView;
		if (view == null) 
		{
			LayoutInflater inflater = (LayoutInflater) mFragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.layout_listing_item, null);			
		}

		// nacteni dat z listu
		SkicentreShort skicentre = mSkicentreList.get(position);
		String name = skicentre.getName();
		int area = skicentre.getArea();
		int country = skicentre.getCountry();
		boolean flagOpened = skicentre.isFlagOpened();
		int snowMax = skicentre.getSnowMax();
		
		// reference na widgety
		ImageView imageOpened = (ImageView) view.findViewById(R.id.layout_listing_item_opened);
		ImageView imageFavourite = (ImageView) view.findViewById(R.id.layout_listing_item_favourite);
		TextView textName = (TextView) view.findViewById(R.id.layout_listing_item_name);
		TextView textSub = (TextView) view.findViewById(R.id.layout_listing_item_sub);
		
		// nastaveni obsahu widgetu
		imageOpened.setImageResource(flagOpened ? R.drawable.presence_online : R.drawable.presence_busy);
		imageFavourite.setImageResource(flagOpened ? R.drawable.btn_star_on : R.drawable.btn_star_off);
		imageFavourite.setVisibility(View.GONE); // TODO: osetrit favourite
		textName.setText(name);
		
		// text druheho radku
		String areaString=DatabaseHelper.NULL_STRING;
		try { areaString = mAreaList.get(area).getName(); }
		catch(Exception e) {}
		
		String countryString=DatabaseHelper.NULL_STRING;
		try { countryString = mCountryList.get(country).getName(); }
		catch(Exception e) {}
		
		String secondLine = createSecondLine(
			name,
			areaString, 
			countryString, 
			snowMax, 
			mFragment.getString(R.string.layout_listing_item_snow)
		);
		textSub.setText(secondLine);
		
		// vraceni view
		return view;
    }


	@Override
	public int getCount() 
	{
		return mSkicentreList.size();
	}


	@Override
	public Object getItem(int position) 
	{
		return mSkicentreList.get(position);
	}


	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	
	public void refill(ArrayList<SkicentreShort> skicentreList, HashMap<Integer, Area> areaList, HashMap<Integer, Country> countryList)
	{
		mAreaList.clear();
		mAreaList.putAll(areaList);
		mCountryList.clear();
		mCountryList.putAll(countryList);
		mSkicentreList.clear();
		mSkicentreList.addAll(skicentreList);
	    notifyDataSetChanged();
	}
	
	
	public static String createSecondLine(String skicentreName, String areaName, String countryName, int snowMax, String snowSuffixText)
	{
		// stat
		String countryString = "";
		if(countryString!=DatabaseHelper.NULL_STRING)
		{
			countryString = countryName;
		}
		
		// oblast
		String areaString = areaName;
		if(areaString!=DatabaseHelper.NULL_STRING && !skicentreName.contains(areaName) && !areaString.contentEquals(countryString) && !areaString.contentEquals(""))
		{
			if(countryString!=DatabaseHelper.NULL_STRING) countryString += ", ";
			countryString += areaString;
		}
		
		// mnozstvi snehu
		if(snowMax>DatabaseHelper.NULL_INT)
		{
			if(countryString!=DatabaseHelper.NULL_STRING || areaString!=DatabaseHelper.NULL_STRING) countryString += ", ";
			countryString += snowMax + " " + snowSuffixText;
		}
		
		return countryString;
	}
}
