package net.skimap.adapters;

import java.util.ArrayList;

import net.skimap.R;
import net.skimap.fragments.ListingFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ListingAdapter extends BaseAdapter implements OnClickListener 
{
    private ArrayList<String> mList;
	private ListingFragment mFragment;
    
	
    // konstruktor
    public ListingAdapter(ListingFragment fragment, ArrayList<String> list) 
    {
        this.mFragment = fragment;
        this.mList = list;
    }

    
    // nastaveni view
    public View getView(int position, View convertView, ViewGroup parent)
    {		
		// nastaveni xml layoutu
		View view = LayoutInflater.from(mFragment.getActivity()).inflate(R.layout.layout_listing_item, null);
		view.setId(position);
		
		// textove pole
		TextView text = (TextView) view.findViewById(R.id.layout_listing_item_textview);
		text.setText(mList.get(position));
		
        // nastaveni onclick
		view.setOnClickListener(new OnItemClickListener(position));
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
 	    
 	    public void onClick(View arg0) 
 	    {
 	    	mFragment.showDetail(mPosition);
 	    }
 	}

    
    public void onClick(View v) {}
    public int getCount() { return mList.size(); }
    public Object getItem(int position) { return mList.get(position); }
    public long getItemId(int position) { return position; }
}
