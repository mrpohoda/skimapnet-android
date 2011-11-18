package net.skimap.fragments;

import net.skimap.R;
import net.skimap.activities.ListingActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MapFragment extends Fragment 
{
	public static final String ITEM_ID = "item_id";
	private final int ZOOM_DEFAULT = 13;
	private enum MapLocationMode { CURRENT_POSITION, NEAREST_SKI_CENTRE };
	
	private MapView mMapView;
	
	
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
		
		mMapView = (MapView)view.findViewById(R.id.layout_map_mapview);
		mMapView.setBuiltInZoomControls(true);
		setMapLocation(MapLocationMode.CURRENT_POSITION);
		
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
				
	    	case R.id.ab_button_location_current:
	    		setMapLocation(MapLocationMode.CURRENT_POSITION);
				return true;
				
	    	case R.id.ab_button_location_nearest:
	    		setMapLocation(MapLocationMode.NEAREST_SKI_CENTRE);
				return true;
				
	    	case R.id.ab_button_layers_normal:
	    		mMapView.setSatellite(false);
				return true;
				
	    	case R.id.ab_button_layers_satellite:
	    		mMapView.setSatellite(true);
				return true;
				
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
	
	
	private void setMapLocation(MapLocationMode mode)
	{
		// nastaveni zoomu
		final MapController controller = mMapView.getController();		
		controller.setZoom(ZOOM_DEFAULT);
		
		// posledni znama poloha z BTS
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		GeoPoint currentPoint = null;
		if(location != null)
		{
			currentPoint = new GeoPoint( (int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6) );
		}
		
		switch (mode) 
    	{
			// aktualni poloha
	    	case CURRENT_POSITION:
	    		if(currentPoint!=null) controller.animateTo(currentPoint);
	    		break;
	    		
	    	// poloha nejblizsiho skicentra k aktualni poloze
	    	case NEAREST_SKI_CENTRE:
		        // TODO
	    		break;
    	}
	}
}
