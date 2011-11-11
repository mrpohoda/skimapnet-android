package net.skimap.fragments;

import net.skimap.R;
import net.skimap.activities.ListingActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.maps.MapView;

public class MapFragment extends Fragment 
{
	public static final String ITEM_ID = "item_id";
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		setHasOptionsMenu(true);
		
		View view = inflater.inflate(R.layout.layout_map, container, false);
		
		MapView mapView = (MapView)view.findViewById(R.id.layout_map_mapview);
		mapView.setBuiltInZoomControls(true);
		
		return view;
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// vytvoreni menu
		inflater.inflate(R.menu.menu_map, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// nastaveni chovani tlacitek
    	Intent intent = new Intent();
    	
    	switch (item.getItemId()) 
    	{
	    	case R.id.ab_button_list:				
		        intent.setClass(this.getActivity(), ListingActivity.class);
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
				return true;
				
	    	case R.id.ab_button_search:
	    		Toast.makeText(this.getActivity(), "SEARCH", Toast.LENGTH_LONG).show();
				return true;
				
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
}
