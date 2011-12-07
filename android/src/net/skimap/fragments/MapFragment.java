package net.skimap.fragments;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.skimap.R;
import net.skimap.activities.ListingActivity;
import net.skimap.activities.SkimapApplication;
import net.skimap.adapters.ListingAdapter;
import net.skimap.data.Area;
import net.skimap.data.Country;
import net.skimap.data.Placemark;
import net.skimap.data.SkicentreShort;
import net.skimap.database.Database;
import net.skimap.database.DatabaseHelper;
import net.skimap.map.PathUtility;
import net.skimap.map.PopupItemizedOverlay;
import net.skimap.map.PathOverlay;
import net.skimap.network.Synchronization;
import net.skimap.parser.KmlParser;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
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

public class MapFragment extends Fragment implements SkimapApplication.OnSynchroListener
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
		//addPois(); // TODO
		
		// pridani sjezdovek a vleku
		addDownhills();
		
		return view;
	}
	
	
	@Override
    public void onResume()
	{
        super.onResume();

        // naslouchani synchronizace
        ((SkimapApplication) getSupportActivity().getApplicationContext()).setSynchroListener(this);
        
        // aktualizace stavu progress baru
    	boolean synchro = ((SkimapApplication) getSupportActivity().getApplicationContext()).isSynchro();
    	getSupportActivity().setProgressBarIndeterminateVisibility(synchro ? Boolean.TRUE : Boolean.FALSE);
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
				
	    	case R.id.ab_button_refresh:
	    		Toast.makeText(getActivity(), "REFRESH", Toast.LENGTH_SHORT).show();
	    		Synchronization synchro = new Synchronization((SkimapApplication) getSupportActivity().getApplicationContext());
	            synchro.trySynchronizeShortData();
				return true;
				
	    	case R.id.ab_button_preferences:
	    		Toast.makeText(getActivity(), "PREFERENCES", Toast.LENGTH_SHORT).show();
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
	
	
	@Override
	public void onSynchroStart()
	{
		// zapnuti progress baru
		getSupportActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);
		
		// start
		Toast.makeText(getActivity(), "SYNCHRO START", Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onSynchroStop()
	{
		// vypnuti progress baru
		getSupportActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
		
		// hotovo
		Toast.makeText(getActivity(), "SYNCHRO DONE", Toast.LENGTH_SHORT).show();
		
		// aktualizace listview
		refreshViewsAfterSynchro();
	}
	
	
	private void refreshViewsAfterSynchro()
	{
		// TODO: ziskat referenci ke vsem list fragmentum a zavolat pro ne metodu refreshListView(), obnovit map view
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
	    		deviceLocation = getDeviceLocation();
	    		if(deviceLocation!=null) controller.animateTo(deviceLocation);
	    		break;
	    		
	    	// poloha nejblizsiho skicentra k aktualni poloze
	    	case NEAREST_SKICENTRE:
		        // TODO
	    		deviceLocation = getDeviceLocation();
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
	private GeoPoint getDeviceLocation()
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
		PopupItemizedOverlay itemizedOverlay;

		// seznam overlay vrstev
		mapOverlays = mMapView.getOverlays();
		
		// ikona skicentra
		drawable = getResources().getDrawable(R.drawable.icon_skicentre);
		
		// vlastni overlay vrstva
		itemizedOverlay = new PopupItemizedOverlay(drawable, mMapView);
		
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
			if(!this.isAdded()) break;
			SkicentreShort skicentre = iterator.next();
			
			// text druheho radku
			String areaString=DatabaseHelper.NULL_STRING;
			try { areaString = areas.get(skicentre.getArea()).getName(); }
			catch(Exception e) {}
			
			String countryString=DatabaseHelper.NULL_STRING;
			try { countryString = countries.get(skicentre.getCountry()).getName(); }
			catch(Exception e) {}
			
			String snowSuffixText=DatabaseHelper.NULL_STRING;
			try { snowSuffixText = getString(R.string.layout_listing_item_snow); }
			catch(Exception e) {}
			
			String secondLine = ListingAdapter.createSecondLine(
				skicentre.getName(),
				areaString,
				countryString,
				skicentre.getSnowMax(), 
				snowSuffixText
			);
			
			// POI
			GeoPoint point = new GeoPoint((int)(skicentre.getLocationLatitude()*1E6),(int)(skicentre.getLocationLongitude()*1E6));
			OverlayItem overlayItem = new OverlayItem(point, skicentre.getName(), secondLine);
			itemizedOverlay.addOverlay(overlayItem);
		}
		mapOverlays.add(itemizedOverlay);
	}
	
	
	
	
	

	
	
	
	
	// TODO: vykreslovat cary pri zoom >= 11
	// TODO: vykreslit kdyz neposouvam mapu a zmenil jsem pozici o vetsi kus
	// TODO: prekreslit pri zoomu, pri prekreslovani smazat predchozi overlays: mMapView.getOverlays().clear();
	//http://ski-map.net/skimapnet/php/common.php?fce=lines_list&nelat=50.76669766709482&nelng=15.796450982910073&swlat=50.68193459347409&swlng=15.422229181152261
	//http://maps.google.com/maps?f=d&hl=en&saddr=49.1857979,16.593846775&daddr=49.2857979,16.693846775&ie=UTF8&0&om=0&output=kml
	private void addDownhills()
	{
		// odchyceni zpravy z vlakna
		final Handler handler = new Handler() 
	    {
            @Override
            public void handleMessage(Message message) 
            {
            	@SuppressWarnings("unchecked")
				ArrayList<Placemark> placemarks = (ArrayList<Placemark>) message.obj;
            	
            	// vykresleni placemarks
            	Iterator<Placemark> iterator = placemarks.iterator();
        		while(iterator.hasNext())
        		{
        			Placemark placemark = iterator.next();
        	        PathUtility.drawPath(placemark, mMapView);
        		}
            }
	    };
			    
		// vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		// ziskani url adresy
                String url = "http://ski-map.net/skimapnet/php/common.php?fce=lines_list&nelat=50.76669766709482&nelng=15.796450982910073&swlat=50.68193459347409&swlng=15.422229181152261";

                // ziskani placemarks
                ArrayList<Placemark> placemarks = PathUtility.getPlacemarks(url);
                
                // zaslani zpravy handleru
        		Message message = new Message();
        		message.obj = placemarks;
        		handler.sendMessage(message);
		    }
        }.start();
	}
	

	
	
	
	
	
	
		
// bounding box		
//		LocationManager locationManager = (LocationManager) getSupportActivity().getSystemService(Context.LOCATION_SERVICE);
//		String locationProvider = LocationManager.NETWORK_PROVIDER;
//		Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
//        double dest[] = { lastKnownLocation.getLatitude()+0.1, lastKnownLocation.getLongitude()+0.1 };
		
//        // find boundary by using itemized overlay
//        GeoPoint destPoint = new GeoPoint( new Double(dest[0]*1E6).intValue(), new Double(dest[1]*1E6).intValue() );
//        GeoPoint currentPoint = new GeoPoint( new Double(lastKnownLocation.getLatitude()*1E6).intValue() ,new Double(lastKnownLocation.getLongitude()*1E6).intValue() );
//
//        Drawable dot = this.getResources().getDrawable(R.drawable.pixel);
//        MapItemizedOverlay bgItemizedOverlay = new MapItemizedOverlay(dot);
//        OverlayItem currentPixel = new OverlayItem(destPoint, null, null );
//        OverlayItem destPixel = new OverlayItem(currentPoint, null, null );
//        bgItemizedOverlay.addOverlay(currentPixel);
//        bgItemizedOverlay.addOverlay(destPixel);
//
//        // center and zoom in the map
//        MapController mc = mMapView.getController();
//        mc.zoomToSpan(bgItemizedOverlay.getLatSpanE6()*2,bgItemizedOverlay.getLonSpanE6()*2);
//        mc.animateTo(new GeoPoint(
//            (currentPoint.getLatitudeE6() + destPoint.getLatitudeE6()) / 2,
//            (currentPoint.getLongitudeE6() + destPoint.getLongitudeE6()) / 2
//        ));
		
		
		
		
// overlay vrstvy
//	    Collection<Overlay> overlaysToAddAgain = new ArrayList<Overlay>();
//	    for (Iterator<Overlay> iter = mapView.getOverlays().iterator(); iter.hasNext();)
//	    {
//	        Object o = iter.next();
//	        //Log.d(myapp.APP, "overlay type: " + o.getClass().getName());
//	        if (!RouteOverlay.class.getName().equals(o.getClass().getName()))
//	        {
//	            // mMapView01.getOverlays().remove(o);
//	            overlaysToAddAgain.add((Overlay) o);
//	        }
//	    }
//	    mapView.getOverlays().addAll(overlaysToAddAgain);
    
}
