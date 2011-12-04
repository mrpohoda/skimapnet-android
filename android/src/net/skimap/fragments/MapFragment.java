package net.skimap.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.skimap.R;
import net.skimap.activities.ListingActivity;
import net.skimap.adapters.ListingAdapter;
import net.skimap.data.Area;
import net.skimap.data.Country;
import net.skimap.data.SkicentreShort;
import net.skimap.database.Database;
import net.skimap.database.DatabaseHelper;
import net.skimap.map.MyItemizedOverlay;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapFragment extends Fragment 
{
	public static final String ITEM_ID = "item_id";
	private final int EMPTY_ID = -1;
	private final int ZOOM_DEFAULT = 13;
	private enum MapLocationMode { DEVICE_POSITION, NEAREST_SKICENTRE, SKICENTRE_POSITION };
	
	private MapView mMapView;
	private int mItemId;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // nastaveni extras
        Bundle extras = getSupportActivity().getIntent().getExtras();
        setExtras(extras, savedInstanceState);
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		setHasOptionsMenu(true);
		
		// nastaveni view
		View view = inflater.inflate(R.layout.layout_map, container, false);
		
		// nastaveni mapy
		mMapView = (MapView) view.findViewById(R.id.layout_map_mapview);
		mMapView.setBuiltInZoomControls(true);
		
		// lokace na mape
		if(mItemId == EMPTY_ID) setMapLocation(MapLocationMode.DEVICE_POSITION);
		else setMapLocation(MapLocationMode.SKICENTRE_POSITION);
		
		// pridani POI
		addPois();
		
		return view;
	}
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);
    }
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
		// ulozeni id
        super.onSaveInstanceState(outState);
        outState.putInt(ITEM_ID, mItemId);
        
        // TODO: zapamatovat pozici na mape
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
		        intent.setClass(getActivity(), ListingActivity.class);
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
				return true;
				
	    	case R.id.ab_button_search:
	    		Toast.makeText(getActivity(), "SEARCH", Toast.LENGTH_SHORT).show();
				return true;
				
	    	case R.id.ab_button_location_current:
	    		setMapLocation(MapLocationMode.DEVICE_POSITION);
				return true;
				
	    	case R.id.ab_button_location_nearest:
	    		setMapLocation(MapLocationMode.NEAREST_SKICENTRE);
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
		
		// poloha zarizeni
		GeoPoint deviceLocation;
				
		switch (mode) 
    	{
			// aktualni poloha
	    	case DEVICE_POSITION:
	    		deviceLocation = getDevicePosition();
	    		if(deviceLocation!=null) controller.animateTo(deviceLocation);
	    		break;
	    		
	    	// poloha nejblizsiho skicentra k aktualni poloze
	    	case NEAREST_SKICENTRE:
		        // TODO
	    		deviceLocation = getDevicePosition();
	    		Toast.makeText(getActivity(), "NEAREST SKICENTRE", Toast.LENGTH_SHORT).show();
	    		break;
	    		
	    	// poloha aktualniho skicentra dle mItemId
	    	case SKICENTRE_POSITION:
		        // TODO
	    		Toast.makeText(getActivity(), "SKICENTRE: " + mItemId, Toast.LENGTH_SHORT).show();
	    		break;
    	}
	}
	
	
	// TODO: ziskavat pozici lepsim zpusobem, getLastKnownLocation zlobi
	private GeoPoint getDevicePosition()
	{
		LocationManager locationManager = (LocationManager) getSupportActivity().getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		GeoPoint deviceLocation = null;
		if(location != null)
		{
			deviceLocation = new GeoPoint( (int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6) );
		}
		return deviceLocation;
	}
	
	
	private void setExtras(Bundle extras, Bundle savedInstanceState)
	{
        // nastaveni id detailu
 		int id; 		
 		// nahrani id z bundle aktivity
 		if(extras != null && extras.containsKey(ITEM_ID))
 		{
 			id = extras.getInt(ITEM_ID);
 		}		
 		// nahrani posledniho pouziteho id
 		else if (savedInstanceState != null && savedInstanceState.containsKey(ITEM_ID))
        {
 			id = savedInstanceState.getInt(ITEM_ID, EMPTY_ID);
        }		
 		// vychozi id
 		else
 		{
 			id = EMPTY_ID;
 		}
 		
 		mItemId = id;
	}
	
	
	private void addPois()
	{
		// vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		addPoisThread();
		    }
        }.start();
	}
	
	
	private void addPoisThread()
	{
		List<Overlay> mapOverlays;
		Drawable drawable;
		MyItemizedOverlay itemizedOverlay;

		// seznam overlay vrstev
		mapOverlays = mMapView.getOverlays();
		
		// ikona skicentra
		drawable = getResources().getDrawable(R.drawable.icon_skicentre);
		
		// vlastni overlay vrstva
		itemizedOverlay = new MyItemizedOverlay(drawable, mMapView);
		
		// nacteni skicenter a statu z databaze
		Database database = new Database(getActivity());
		database.open(false);
		ArrayList<SkicentreShort> skicentres = database.getAllSkicentres();
		HashMap<Integer, Area> areas = database.getAllAreas();
		HashMap<Integer, Country> countries = database.getAllCountries();
		database.close();
		
		// pridani POI do vrstvy
		Iterator<SkicentreShort> iterator = skicentres.iterator();
		while(iterator.hasNext())
		{
			SkicentreShort skicentre = iterator.next();
			
			// text druheho radku
			String areaString=DatabaseHelper.NULL_STRING;
			try { areaString = areas.get(skicentre.getArea()).getName(); }
			catch(Exception e) {}
			
			String countryString=DatabaseHelper.NULL_STRING;
			try { countryString = countries.get(skicentre.getCountry()).getName(); }
			catch(Exception e) {}
			
			String secondLine = ListingAdapter.createSecondLine(
				skicentre.getName(),
				areaString,
				countryString,
				skicentre.getSnowMin(), 
				getString(R.string.layout_listing_item_snow)
			);
			
			// POI
			GeoPoint point = new GeoPoint((int)(skicentre.getLocationLatitude()*1E6),(int)(skicentre.getLocationLongitude()*1E6));
			OverlayItem overlayItem = new OverlayItem(point, skicentre.getName(), secondLine);
			itemizedOverlay.addOverlay(overlayItem);
		}
		mapOverlays.add(itemizedOverlay);
	}
}
