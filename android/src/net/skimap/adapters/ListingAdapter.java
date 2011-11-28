package net.skimap.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import net.skimap.R;
import net.skimap.data.Country;
import net.skimap.data.SkicentreShort;
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
	private HashMap<Integer, Country> mCountryList;
	
	
    public ListingAdapter(ListingFragment fragment, ArrayList<SkicentreShort> skicentreList, HashMap<Integer, Country> countryList)
    {
        mFragment = fragment;
        mSkicentreList = skicentreList;
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
		int country = skicentre.getCountry();
		boolean opened = skicentre.getOpened();
		int snow = skicentre.getSnow();
		
		// reference na widgety
		ImageView imageOpened = (ImageView) view.findViewById(R.id.layout_listing_item_opened);
		TextView textName = (TextView) view.findViewById(R.id.layout_listing_item_name);
		TextView textCountry = (TextView) view.findViewById(R.id.layout_listing_item_country);
		
		// nastaveni obsahu widgetu
		imageOpened.setImageResource(opened ? R.drawable.presence_online : R.drawable.presence_busy);
		textName.setText(name);
		
		String secondLine = mCountryList.get(country).getName();
		if(snow>0) secondLine += ", " + snow + " " + mFragment.getString(R.string.layout_listing_item_snow);
		textCountry.setText(secondLine);
		
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
	
	
	public void refill(ArrayList<SkicentreShort> skicentreList, HashMap<Integer, Country> countryList)
	{
		mSkicentreList.clear();
		mSkicentreList.addAll(skicentreList);
		mCountryList.clear();
		mCountryList.putAll(countryList);
	    notifyDataSetChanged();
	}
}
