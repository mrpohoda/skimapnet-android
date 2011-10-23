package net.skimap.adapters;

import java.util.ArrayList;

import net.skimap.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class ListingAdapter extends BaseAdapter implements OnClickListener 
{
	private Context mContext;
    private ArrayList<String> mList;
	 
    
    // konstruktor
    public ListingAdapter(Context context, ArrayList<String> list) 
    {
        this.mContext = context;
        this.mList = list;
    }

    
    // nastaveni view
    public View getView(int position, View convertView, ViewGroup parent)
    {		
		// nastaveni xml layoutu
		View v = LayoutInflater.from(mContext).inflate(R.layout.layout_listing_item, null);
		v.setId(position);
		
		// textove pole
		TextView text = (TextView) v.findViewById(R.id.layout_listing_item_textview);
		text.setText(mList.get(position));

        // nastaveni pozadi a onclick
        v.setOnClickListener(new OnItemClickListener(position));
        return v;
    }
    
    
    // klik
	private class OnItemClickListener implements OnClickListener
	{
	    private int mPosition;
	    OnItemClickListener(int position)
	    {
            this.mPosition = position;
	    }
	    public void onClick(View arg0) 
	    {
	    	Toast.makeText(mContext, "CLICK " + mPosition, Toast.LENGTH_LONG).show();
	    }
	}
 
	
	
    public void onClick(View v) {}
    public int getCount() { return mList.size(); }
    public Object getItem(int position) { return mList.get(position); }
    public long getItemId(int position) { return position; }
}
