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
import android.widget.Toast;


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
		View v = LayoutInflater.from(mFragment.getActivity()).inflate(R.layout.layout_listing_item, null);
		v.setId(position);
		
		// textove pole
		TextView text = (TextView) v.findViewById(R.id.layout_listing_item_textview);
		text.setText(mList.get(position));

        // nastaveni pozadi a onclick
        v.setOnClickListener(new OnItemClickListener(position));
        return v;
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
 	    	Toast.makeText(mFragment.getActivity(), "CLICK " + mPosition, Toast.LENGTH_LONG).show();
 	    	mFragment.showDetail(mPosition);
 	    }
 	}

    
    public void onClick(View v) {}
    public int getCount() { return mList.size(); }
    public Object getItem(int position) { return mList.get(position); }
    public long getItemId(int position) { return position; }
}
