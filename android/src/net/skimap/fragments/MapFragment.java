package net.skimap.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.skimap.R;
import net.skimap.activities.ListingActivity;
import net.skimap.activities.SettingsActivity;
import net.skimap.activities.SkimapApplication;
import net.skimap.adapters.ListingAdapter;
import net.skimap.content.CustomSuggestionProvider;
import net.skimap.data.Area;
import net.skimap.data.Country;
import net.skimap.data.Placemark;
import net.skimap.data.SkicentreLong;
import net.skimap.data.SkicentreShort;
import net.skimap.database.Database;
import net.skimap.database.DatabaseHelper;
import net.skimap.map.CustomMapView;
import net.skimap.map.PathUtility;
import net.skimap.map.PopupItemizedOverlay;
import net.skimap.network.Synchronization;
import net.skimap.utililty.Localytics;
import net.skimap.utililty.Version;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.SubMenu;
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
import com.localytics.android.LocalyticsSession;

public class MapFragment extends Fragment implements SkimapApplication.OnSynchroListener, CustomMapView.OnPanAndZoomListener
{
	public static final String ITEM_ID = "item_id";
	private final int EMPTY_ID = -1;
	private final int ZOOM_DEFAULT = 14;
	private final int ZOOM_MINIMUM_FOR_DRAW = 13;
	private final int SEARCH_MAX = 10;
	private enum MapLocationMode { DEVICE_POSITION, LAST_SKICENTRE, NEAREST_SKICENTRE, SKICENTRE_POSITION };
	
	private CustomMapView mMapView;
	private int mItemId;
	private int[] mPathsDataBounds; // souradnice hranicnich bodu datasetu v E6 formatu, poradi jako hodiny - top, right, bottom, left
	private Database mDatabase;
	private LocalyticsSession mLocalyticsSession;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // Localytics
	    this.mLocalyticsSession = new LocalyticsSession(getActivity().getApplicationContext(), Localytics.KEY);
	    this.mLocalyticsSession.open(); // otevre session
	    this.mLocalyticsSession.upload(); // upload dat
	    // At this point, Localytics Initialization is done.  After uploads complete nothing
	    // more will happen due to Localytics until the next time you call it.
	    
	    // otevreni databaze
	    mDatabase = new Database(getActivity());
    	mDatabase.open(true);
        
        // nastaveni extras
        Bundle extras = getSupportActivity().getIntent().getExtras();
        setExtras(extras, savedInstanceState);
        
		// pokus zobrazeni intro dialogu
		Version.tryShowIntoDialog(getActivity(), mLocalyticsSession);
		
		// vyhledavani
		handleSearchIntent(getSupportActivity().getIntent());
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		setHasOptionsMenu(true);
		
		// nastaveni view
		View view = inflater.inflate(R.layout.layout_map, container, false);
		
		// inicializace bounds
		mPathsDataBounds = new int[4];
		resetBounds();
		
		// nastaveni mapy
		mMapView = (CustomMapView) view.findViewById(R.id.layout_map_mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setPanListenerSensitivity(10000);
		
        // naslouchani manipulaci s mapou
        mMapView.setOnPanListener(this);

		// zamereni skicentra na mape
		if(mItemId != EMPTY_ID) setMapLocation(MapLocationMode.SKICENTRE_POSITION);
		
		return view;
	}
	
	
	@Override
    public void onResume()
	{
        super.onResume();
        
        // Localytics
        this.mLocalyticsSession.open(); // otevre session pokud neni jiz otevrena
        
        // otevreni databaze
        if(!mDatabase.isOpen()) mDatabase.open(true);

        // naslouchani synchronizace
        ((SkimapApplication) getSupportActivity().getApplicationContext()).setSynchroListener(this);

        // aktualizace stavu progress baru
    	boolean synchronizing = ((SkimapApplication) getSupportActivity().getApplicationContext()).isSynchronizing();
    	getSupportActivity().setProgressBarIndeterminateVisibility(synchronizing ? Boolean.TRUE : Boolean.FALSE);
    	
    	// vykresleni skicenter, sjezdovek a vleku
		resetBounds();
		refreshPois();
		tryRedrawPaths();
		
		// pokus o automatickou synchronizaci
		Synchronization synchro = new Synchronization((SkimapApplication) getSupportActivity().getApplicationContext(), mDatabase);
        synchro.trySynchronizeShortDataAuto(mLocalyticsSession, Localytics.VALUE_SYNCHRO_FROM_MAP);

        Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
        localyticsValues.put(Localytics.ATTR_ACTIVITY_FRAGMENT, Localytics.VALUE_ACTIVITY_FRAGMENT_MAP); // Localytics atribut
		mLocalyticsSession.tagEvent(Localytics.TAG_ACTIVITY, localyticsValues); // Localytics
    }
	
	
	@Override
	public void onPause()
	{
		// Localytics
	    this.mLocalyticsSession.close();
	    this.mLocalyticsSession.upload();
	    
	    // zavreni database
	    mDatabase.close();
	    
	    super.onPause();
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
		
		// polozka location last	
		SubMenu submenu = menu.findItem(R.id.ab_button_location).getSubMenu();
		if(mItemId!=EMPTY_ID) submenu.add(Menu.NONE, R.id.ab_button_location_last, 2, R.string.ab_button_location_last);			
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// nastaveni chovani tlacitek
    	Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
    	
    	switch (item.getItemId()) 
    	{
	    	case R.id.ab_button_list:	
	    		Intent listIntent = new Intent();
	    		listIntent.setClass(getActivity(), ListingActivity.class);
	    		listIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(listIntent);
		        localyticsValues.put(Localytics.ATTR_BUTTON_LIST, Localytics.VALUE_BUTTON_FROM_MAP); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
	    	case R.id.ab_button_search:
	    		getSupportActivity().onSearchRequested();
	    		localyticsValues.put(Localytics.ATTR_BUTTON_SEARCH, Localytics.VALUE_BUTTON_FROM_MAP); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
	    	case R.id.ab_button_refresh:
	    		Synchronization synchro = new Synchronization((SkimapApplication) getSupportActivity().getApplicationContext(), mDatabase);
	            synchro.trySynchronizeShortData();
	            // TODO: aktualizovat skicentre data a overlays
	            tryRedrawPaths();
	            mMapView.invalidate();
	            localyticsValues.put(Localytics.ATTR_BUTTON_REFRESH, Localytics.VALUE_BUTTON_FROM_MAP); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
	    	case R.id.ab_button_preferences:
	    		Intent preferencesIntent = new Intent();
	    		preferencesIntent.setClass(getActivity(), SettingsActivity.class);
		        startActivity(preferencesIntent);
		        localyticsValues.put(Localytics.ATTR_BUTTON_PREFERENCES, Localytics.VALUE_BUTTON_FROM_MAP); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
	    	case R.id.ab_button_location_current:
	    		setMapLocation(MapLocationMode.DEVICE_POSITION);
	    		localyticsValues.put(Localytics.ATTR_BUTTON_LOCATION, Localytics.VALUE_BUTTON_LOCATION_CURRENT); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
	    	case R.id.ab_button_location_last:
	    		setMapLocation(MapLocationMode.LAST_SKICENTRE);
	    		localyticsValues.put(Localytics.ATTR_BUTTON_LOCATION, Localytics.VALUE_BUTTON_LOCATION_LAST); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
			// TODO
//	    	case R.id.ab_button_location_nearest:
//	    		setMapLocation(MapLocationMode.NEAREST_SKICENTRE);
//				return true;
				
	    	case R.id.ab_button_layers_normal:
	    		mMapView.setSatellite(false);
	    		localyticsValues.put(Localytics.ATTR_BUTTON_LAYER, Localytics.VALUE_BUTTON_LAYER_NORMAL); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
	    	case R.id.ab_button_layers_satellite:
	    		mMapView.setSatellite(true);
	    		localyticsValues.put(Localytics.ATTR_BUTTON_LAYER, Localytics.VALUE_BUTTON_LAYER_SATELLITE); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
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
	}


	@Override
	public void onSynchroStop(int result)
	{
		// vypnuti progress baru
		getSupportActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
		
		// aktualizace view
		resetBounds();
		refreshPois();
		tryRedrawPaths();
		refreshViewsAfterSynchro();
		
		Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		
		// toast pro offline rezim nebo error
		if(result==Synchronization.STATUS_OFFLINE) 
		{
			Toast.makeText(getActivity(), R.string.toast_synchro_offline, Toast.LENGTH_LONG).show();
			localyticsValues.put(Localytics.ATTR_SYNCHRO_SHORT_STATUS, Localytics.VALUE_SYNCHRO_STATUS_OFFLINE); // Localytics atribut
		}
		else if(result==Synchronization.STATUS_CANCELED) 
		{
			Toast.makeText(getActivity(), R.string.toast_synchro_canceled, Toast.LENGTH_LONG).show();
			localyticsValues.put(Localytics.ATTR_SYNCHRO_SHORT_STATUS, Localytics.VALUE_SYNCHRO_STATUS_CANCELED); // Localytics atribut
		}
		else if(result==Synchronization.STATUS_UNKNOWN) 
		{
			Toast.makeText(getActivity(), R.string.toast_synchro_error, Toast.LENGTH_LONG).show();
			localyticsValues.put(Localytics.ATTR_SYNCHRO_SHORT_STATUS, Localytics.VALUE_SYNCHRO_STATUS_ERROR); // Localytics atribut
		}
		else
		{
			localyticsValues.put(Localytics.ATTR_SYNCHRO_SHORT_STATUS, Localytics.VALUE_SYNCHRO_STATUS_ONLINE); // Localytics atribut
		}
		
		mLocalyticsSession.tagEvent(Localytics.TAG_SYNCHRO, localyticsValues); // Localytics
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
		
		Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
        localyticsValues.put(Localytics.ATTR_MAP_ZOOM, Integer.toString(mMapView.getZoomLevel())); // Localytics atribut
		mLocalyticsSession.tagEvent(Localytics.TAG_MAP, localyticsValues); // Localytics
	}
	
	
	private void refreshViewsAfterSynchro()
	{
		// TODO: ziskat referenci ke vsem list fragmentum a zavolat pro ne metodu refreshListView(), obnovit map view
	}
	
	
	public void handleSearchIntent(Intent intent)
	{
		// zpracovani search query
		if (Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			// ziskani query
			String query = intent.getStringExtra(SearchManager.QUERY);
			
			// ulozeni query pro naseptavac
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
					CustomSuggestionProvider.AUTHORITY,
					CustomSuggestionProvider.MODE);
	        suggestions.saveRecentQuery(query, null);

	        // vyhledani mista v mape
			searchLocation(query);
			
			Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
	        localyticsValues.put(Localytics.ATTR_SEARCH_LOCATION, Localytics.VALUE_SEARCH_FROM_MAP); // Localytics atribut
			mLocalyticsSession.tagEvent(Localytics.TAG_SEARCH, localyticsValues); // Localytics
		}
	}
	
	
	private void searchLocation(String area)
    {  
        try
        {
        	// vyhledani geolokace dle nazvu
	        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault()); 
	        final List<android.location.Address> result = geocoder.getFromLocationName(area, SEARCH_MAX); 
	        if(result.size()==1)
	        {
	        	android.location.Address address = result.get(0);
	        	if(address.hasLatitude() && address.hasLongitude())
	        	{
	        		double lat = result.get(0).getLatitude();
	                double lon = result.get(0).getLongitude();
			        setMapLocation(lat, lon);
	        	}
	        }
	        else if(result.size()>1)
	        {
	        	// seznam adres
	        	ArrayList<CharSequence> itemList = new ArrayList<CharSequence>();
	        	Iterator<android.location.Address> iterator = result.iterator();
	    		while(iterator.hasNext())
	    		{
	    			android.location.Address address = iterator.next();
	    			if(address.hasLatitude() && address.hasLongitude())
	    			{
	    				StringBuilder name = new StringBuilder();
	    				if(address.getFeatureName()!=null) name.append(address.getFeatureName());
	    				else name.append(area);
	    				if(address.getAdminArea()!=null) name.append(", " + address.getAdminArea());
	    				if(address.getCountryCode()!=null) name.append(", " + address.getCountryCode());
	    				itemList.add(name.toString());
	    			}
	    		}
	    		
	    		final String[] items = new String[itemList.size()];
	    		itemList.toArray(items);

	    		// dialog s vice vysledky vyhledavani
	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            builder.setTitle(R.string.search_result);
	            builder.setItems(items, new DialogInterface.OnClickListener()
	            {
	                public void onClick(DialogInterface dialog, int item)
	                {
	                    double lat = result.get(item).getLatitude();
	                    double lon = result.get(item).getLongitude();
	    		        setMapLocation(lat, lon);
	                }
	            });
	            builder.show();
	        }   
	        else
	        {	        	
	        	Toast.makeText(getActivity(), "'" + area + "' " + getString(R.string.search_error_not_found), Toast.LENGTH_LONG).show();
	        	return;
	        }
        }
        catch(IOException e)
        {
        	Toast.makeText(getActivity(), R.string.search_error_connection, Toast.LENGTH_LONG).show();
        	return;
        }
    }
	
	
	private void setMapLocation(double latitude, double longitude)
	{
		// souradnice mista
        GeoPoint myLocation = new GeoPoint((int) (latitude*1E6), (int) (longitude*1E6));
 
        // nastaveni pozice nalezeneho mista v mape
        if(mMapView!=null)
        {
        	MapController controller = mMapView.getController();
            controller.setZoom(ZOOM_DEFAULT);
            controller.setCenter(myLocation);
        }
        
        // prekresleni sjezdovek
		tryRedrawPaths();
	}
	

	private void setMapLocation(MapLocationMode mode)
	{
		// nastaveni zoomu
		final MapController controller = mMapView.getController();		
		controller.setZoom(ZOOM_DEFAULT);
	
		switch (mode) 
    	{
			// aktualni poloha zarizeni
	    	case DEVICE_POSITION:
	    		GeoPoint deviceLocation;
	    		deviceLocation = getDeviceLocation();
	    		if(deviceLocation!=null) controller.setCenter(deviceLocation);
	    		break;
	    		
	    	// poloha nejblizsiho skicentra k aktualni poloze
	    	case NEAREST_SKICENTRE:
		        // TODO
	    		//Toast.makeText(getActivity(), "NEAREST SKICENTRE", Toast.LENGTH_SHORT).show();
	    		break;
	    		
	    	// poloha posledniho zobrazeneho skicentra
	    	case LAST_SKICENTRE:
	    		
	    	// poloha aktualniho skicentra dle mItemId
	    	case SKICENTRE_POSITION:
	    		if(mItemId==EMPTY_ID) break;
	    	
	    		// nacteni skicentra z databaze
	    		SkicentreLong skicentre = null;
	    		try
	    		{
	    			if(mDatabase.isOpen()) skicentre = mDatabase.getSkicentre(mItemId);
	    		}
	    		catch(IllegalStateException e)
	    		{
	    			e.printStackTrace();
	    		}
	    		catch(Exception e)
	    		{
	    			e.printStackTrace();
	    		}
	    		
	    		// zamereni skicentra v mape
	    		if(skicentre!=null)
	    		{
		    		GeoPoint point = new GeoPoint( (int)(skicentre.getLocationLatitude()*1E6), (int)(skicentre.getLocationLongitude()*1E6) );
		    		controller.setCenter(point);
	    		}
	    		break;
    	}
		
		// prekresleni sjezdovek
        tryRedrawPaths();
        mMapView.invalidate();
	}
	
	
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
	
	
	private void resetBounds()
	{
		mPathsDataBounds[0] = 0;
		mPathsDataBounds[1] = 0;
		mPathsDataBounds[2] = 0;
		mPathsDataBounds[3] = 0;
	}
	
	
	private void refreshPois()
	{
		// smazani vsech vrstev mapy
		mMapView.getOverlays().clear();
		
		// pridani POI
		addPois();
	}
	
	
	private void addPois()
	{
		// vlakno
		new OverlayTask().execute();
	}
	
	
	private void addPoisThread()
	{
		// kontrola platnosti kontextu
		if(!this.isAdded()) return;
		
		// ikona skicentra
		Drawable drawableOn = getResources().getDrawable(R.drawable.ic_map_skicentre);
		Drawable drawableOff = getResources().getDrawable(R.drawable.ic_map_skicentre_disabled);
		
		// vlastni overlay vrstvy
		PopupItemizedOverlay itemizedOverlayOn;
		PopupItemizedOverlay itemizedOverlayOff;
		try
		{
			itemizedOverlayOn = new PopupItemizedOverlay(drawableOn, mMapView);
			itemizedOverlayOff = new PopupItemizedOverlay(drawableOff, mMapView);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		// nacteni skicenter a statu z databaze
		ArrayList<SkicentreShort> skicentres = null;
		HashMap<Integer, Area> areas = null;
		HashMap<Integer, Country> countries = null;
		try
		{
			if(mDatabase.isOpen())
			{
				skicentres = mDatabase.getAllSkicentres(Database.Sort.NAME);
				areas = mDatabase.getAllAreas();
				countries = mDatabase.getAllCountries();
			}
			else
			{
				return;
			}
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
			return;
		}
		
		// pridani POI do vrstvy
		Iterator<SkicentreShort> iterator = skicentres.iterator();
		ArrayList<OverlayItem> overlaysOn = new ArrayList<OverlayItem>();
		ArrayList<OverlayItem> overlaysOff = new ArrayList<OverlayItem>();
		ArrayList<Integer> idsOn = new ArrayList<Integer>();
		ArrayList<Integer> idsOff = new ArrayList<Integer>();
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
			if(skicentre.isFlagOpened())
			{
				overlaysOn.add(overlayItem);
				idsOn.add(skicentre.getId());
			}
			else 
			{
				overlaysOff.add(overlayItem);
				idsOff.add(skicentre.getId());
			}
			//if(skicentre.isFlagOpened()) itemizedOverlayOn.addOverlay(overlayItem, skicentre.getId());
			//else itemizedOverlayOff.addOverlay(overlayItem, skicentre.getId());
		}
		
		// seznam overlay vrstev
		try
		{
			List<Overlay> mapOverlays = mMapView.getOverlays();
			itemizedOverlayOff.updateOverlays(overlaysOff, idsOff);
			itemizedOverlayOn.updateOverlays(overlaysOn, idsOn);
			mapOverlays.add(itemizedOverlayOff);
			mapOverlays.add(itemizedOverlayOn);
			mMapView.postInvalidate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private void tryRedrawPaths()
	{
		// kontrola zoom
		if(mMapView.getZoomLevel()<ZOOM_MINIMUM_FOR_DRAW) return;	
			
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
            		try
            		{
            			removePaths();
            		}
            		catch(Exception e)
            		{
            			e.printStackTrace();
            		}
        			
            		// vykresleni cesty
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
        		builder.append("&downhill[]=black&downhill[]=red&downhill[]=blue&downhill[]=kabinky&downhill[]=lanovky&downhill[]=vleky&downhill[]=vlaky&downhill[]=metro");
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
	

	//http://stackoverflow.com/questions/2553410/nullpointerexception-in-itemizedoverlay-getindextodraw
	//http://stackoverflow.com/questions/2870743/android-2-1-googlemaps-itemizedoverlay-concurrentmodificationexception
	//http://stackoverflow.com/questions/4374100/android-maps-array-index-out-of-bound-exception
	//https://github.com/commonsguy/cw-advandroid/blob/master/Maps/NooYawkAsync/src/com/commonsware/android/mapasync/NooYawk.java
	class OverlayTask extends AsyncTask<Void, Void, Void>
	{
	    @Override
	    public void onPreExecute()
	    {
	    }
	    
	    // UI vlakno
	    @Override
	    public Void doInBackground(Void... unused)
	    {
	    	addPoisThread();
	    	return(null);
	    }
	    
	    @Override
	    public void onPostExecute(Void unused)
	    {
	    }
	}
}
