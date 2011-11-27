package net.skimap.adapters;

import java.util.ArrayList;

import net.skimap.R;
import net.skimap.data.SkicentreShort;
import net.skimap.fragments.ListingFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ListingAdapter extends BaseAdapter
{
	private ListingFragment mFragment;
	private ArrayList<SkicentreShort> mList;    
	
	
    public ListingAdapter(ListingFragment fragment, ArrayList<SkicentreShort> list)
    {
        mFragment = fragment;
        mList = list;
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
		SkicentreShort skicentre = mList.get(position);
		String name = skicentre.getName();
		
		// textove pole
		TextView text = (TextView) view.findViewById(R.id.layout_listing_item_textview);
		text.setText(name);
		
		// vraceni view
		return view;
    }


	@Override
	public int getCount() 
	{
		return mList.size();
	}


	@Override
	public Object getItem(int position) 
	{
		return mList.get(position);
	}


	@Override
	public long getItemId(int position)
	{
		return position;
	}
	
	
	public void refill(ArrayList<SkicentreShort> list)
	{
		mList.clear();
		mList.addAll(list);
	    notifyDataSetChanged();
	}
}
