package net.skimap.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import net.skimap.map.CustomMapView;
import net.skimap.map.PathUtility;
import net.skimap.map.PopupItemizedOverlay;
import net.skimap.network.Synchronization;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapFragment extends Fragment implements SkimapApplication.OnSynchroListener, CustomMapView.OnPanAndZoomListener
{
	public static final String ITEM_ID = "item_id";
	private final int EMPTY_ID = -1;
	private final int ZOOM_DEFAULT = 13;
	private final int ZOOM_MINIMUM_FOR_DRAW = 12;
	private enum MapLocationMode { DEVICE_POSITION, NEAREST_SKICENTRE, SKICENTRE_POSITION };
	
	private CustomMapView mMapView;
	private int mItemId;
	private int[] mPathsDataBounds; // souradnice hranicnich bodu datasetu v E6 formatu, poradi jako hodiny - top, right, bottom, left
	
	
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
		mMapView = (CustomMapView) view.findViewById(R.id.layout_map_mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setPanListenerSensitivity(10000);
		
        // naslouchani manipulaci s mapou
        mMapView.setOnPanListener(this);

		// lokace na mape
		if(mItemId == EMPTY_ID) setMapLocation(MapLocationMode.DEVICE_POSITION);
		else setMapLocation(MapLocationMode.SKICENTRE_POSITION);
		
		// inicializace bounds
		mPathsDataBounds = new int[4];
		mPathsDataBounds[0] = 0;
		mPathsDataBounds[1] = 0;
		mPathsDataBounds[2] = 0;
		mPathsDataBounds[3] = 0;
		
		// pridani POI
		addPois();
		
		// vykresleni sjezdovek a vleku
		tryRedrawPaths();
		mMapView.invalidate();
		
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
	
	
	@Override
	public void onPan()
	{
		if(mMapView.getZoomLevel()>=ZOOM_MINIMUM_FOR_DRAW)
		{
			tryRedrawPaths();
		}
	}


	@Override
	public void onZoom()
	{
		TextView infoBox = (TextView) getSupportActivity().findViewById(R.id.layout_map_infobox);
		if(mMapView.getZoomLevel()>=ZOOM_MINIMUM_FOR_DRAW)
		{
			tryRedrawPaths();
			infoBox.setVisibility(View.GONE);
		}
		else
		{
			infoBox.setVisibility(View.VISIBLE);
		}
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
		drawable = getResources().getDrawable(R.drawable.ic_map_skicentre);
		
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
	
	
	private void tryRedrawPaths()
	{
		// sirka a delka mapview
		int spanLatitudeE6 = mMapView.getLatitudeSpan();
		int spanLongitudeE6 = mMapView.getLongitudeSpan();
		
		// nejde zjistit bounds
		if ((spanLatitudeE6 == 0) && (spanLongitudeE6 == 360000000)) return;
			
		int halfLatitudeE6 = spanLatitudeE6/2;
		int halfLongitudeE6 = spanLongitudeE6/2;

		// hranicni body mapview
		GeoPoint centerPoint = mMapView.getMapCenter();
		int viewBoundLeftE6 = centerPoint.getLongitudeE6()-halfLongitudeE6;
		int viewBoundBottomE6 = centerPoint.getLatitudeE6()-halfLatitudeE6;
		int viewBoundRightE6 = centerPoint.getLongitudeE6()+halfLongitudeE6;
		int viewBoundTopE6 = centerPoint.getLatitudeE6()+halfLatitudeE6;
		
		// hranicni body vykresleneho datasetu, poradi jako hodiny - top, right, bottom, left
		int dataBoundLeftE6 = mPathsDataBounds[3];
		int dataBoundBottomE6 = mPathsDataBounds[2];
		int dataBoundRightE6 = mPathsDataBounds[1];
		int dataBoundTopE6 = mPathsDataBounds[0];
		
		// mapview je cele uvnitr datasetu, neni potreba prekreslovat cesty
		if(dataBoundLeftE6 < viewBoundLeftE6 &&
			dataBoundRightE6 > viewBoundRightE6 &&
			dataBoundBottomE6 < viewBoundBottomE6 &&
			dataBoundTopE6 > viewBoundTopE6)
		{
			//Toast.makeText(getActivity(), "NEPREKRESLUJ", Toast.LENGTH_SHORT).show();
		}
		// je potreba prekreslit
		else
		{
			//Toast.makeText(getActivity(), "PREKRESLUJ", Toast.LENGTH_SHORT).show();
			
			// vypocet novych hranicnich bodu datasetu
			mPathsDataBounds[3] = viewBoundLeftE6 - halfLongitudeE6;
			mPathsDataBounds[2] = viewBoundBottomE6 - halfLatitudeE6;
			mPathsDataBounds[1] = viewBoundRightE6 + halfLongitudeE6;
			mPathsDataBounds[0] = viewBoundTopE6 + halfLatitudeE6;

			// prekresleni starych cest za nove
			redrawPaths();
		}
	}
	

	//http://stackoverflow.com/questions/2013443/on-zoom-event-for-google-maps-on-android
	private void redrawPaths()
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
            	if(placemarks!=null)
            	{
            		// smazani predchozich cest
            		removePaths();
        			
	            	Iterator<Placemark> iterator = placemarks.iterator();
	        		while(iterator.hasNext())
	        		{
	        			Placemark placemark = iterator.next();
	        	        PathUtility.drawPath(placemark, mMapView);
	        		}
	        		
	        		// aktualizace overlays v mape
	        		mMapView.invalidate();
            	}
            }
	    };
			    
		// vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		// ziskani url adresy
                //String url = "http://ski-map.net/skimapnet/php/common.php?fce=lines_list&nelat=50.76669766709482&nelng=15.796450982910073&swlat=50.68193459347409&swlng=15.422229181152261";
        		StringBuilder builder = new StringBuilder();
        		builder.append("http://ski-map.net/skimapnet/php/common.php?fce=lines_list");
        		builder.append("&nelat=");
        		builder.append(((double) mPathsDataBounds[0]/1000000));
        		builder.append("&nelng=");
        		builder.append(((double) mPathsDataBounds[1]/1000000));
        		builder.append("&swlat=");
        		builder.append(((double) mPathsDataBounds[2]/1000000));
        		builder.append("&swlng=");
        		builder.append(((double) mPathsDataBounds[3]/1000000));
        		String url = builder.toString();

                // ziskani placemarks
                ArrayList<Placemark> placemarks = PathUtility.getPlacemarks(url);
                
                // zaslani zpravy handleru
        		Message message = new Message();
        		message.obj = placemarks;
        		handler.sendMessage(message);
		    }
        }.start();
	}


	private void removePaths()
	{
		//Log.d("SKIMAP", "BEFORE REMOVE OVERLAYS:" + mMapView.getOverlays().size());
		
		Collection<Overlay> overlaysToAddAgain = new ArrayList<Overlay>();
		Iterator<Overlay> iterator = mMapView.getOverlays().iterator();
		while(iterator.hasNext())
		{
			Object object = iterator.next();
			
			// jedna se o popup overlay
	        if (PopupItemizedOverlay.class.getName().contentEquals(object.getClass().getName()))
	        {
	        	overlaysToAddAgain.add((Overlay) object);
	        }
		}
		
		// smazani vsech overlays
		mMapView.getOverlays().clear();
		
		// pridani popup overlay
		mMapView.getOverlays().addAll(overlaysToAddAgain);
		
		//Log.d("SKIMAP", "AFTER REMOVE OVERLAYS:" + mMapView.getOverlays().size());
	}
}
