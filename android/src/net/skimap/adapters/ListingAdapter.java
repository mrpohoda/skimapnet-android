package net.skimap.adapters;

import java.util.ArrayList;

import net.skimap.R;
import net.skimap.data.SkicentreShort;
import net.skimap.fragments.ListingFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

		// nacteni dat z kurzoru
		SkicentreShort skicentre = mList.get(position);
		int id = skicentre.getId();
		String name = skicentre.getName();
		
		// textove pole
		TextView text = (TextView) view.findViewById(R.id.layout_listing_item_textview);
		text.setText(name);

		// nastaveni onclick
		view.setOnClickListener(new OnItemClickListener(position));
		
		// vraceni view
		view.setId(id);
		return view;
    }
    
    
    // klik
 	public class OnItemClickListener implements OnClickListener
 	{
 	    private int mPosition;
 	    
 	    OnItemClickListener(int position)
 	    {
 	    	this.mPosition = position;
 	    }
 	    
 	    public void onClick(View view) 
 	    {
 	    	// TODO: resit pres listener
 	    	mFragment.showDetail(mPosition);
 	    }
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
}
