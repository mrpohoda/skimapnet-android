package net.skimap.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.skimap.R;
import net.skimap.activities.MapActivity;
import net.skimap.activities.SettingsActivity;
import net.skimap.activities.SkimapApplication;
import net.skimap.adapters.ListingAdapter;
import net.skimap.content.CustomSuggestionProvider;
import net.skimap.data.Area;
import net.skimap.data.Country;
import net.skimap.data.SkicentreShort;
import net.skimap.database.Database;
import net.skimap.network.Synchronization;
import net.skimap.utililty.Localytics;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.localytics.android.LocalyticsSession;

public class ListingFragment extends Fragment implements SkimapApplication.OnSynchroListener
{
	// TODO: nastavit init hodnotu mItemIdShown
	// TODO: pod action barem se zobrazuje rusivy transparentni prechod
	// TODO: pri refresh nenabehne progress bar, a neukoncuje se
	private final String SAVED_CHOICE_CHECKED = "choice_checked";
	private final String SAVED_CHOICE_SHOWN = "choice_shown";
	private final int DEFAULT_CHOICE_CHECKED = -1;
	private final int DEFAULT_CHOICE_SHOWN = -1;
     
    private int mItemIdChecked = DEFAULT_CHOICE_CHECKED;
    private int mItemIdShown = DEFAULT_CHOICE_SHOWN;
    private boolean mDualView;
    private View mRootView;
    private ArrayList<SkicentreShort> mSkicentreList;
    private HashMap<Integer, Area> mAreaList; // TODO: redundance v ramci fragmentu, slo by presunout do aktivity
    private HashMap<Integer, Country> mCountryList; // TODO: redundance v ramci fragmentu, slo by presunout do aktivity
    private OnItemSelectedListener mClickListener;
    private boolean mLoadingFromDatabase;
    private Database mDatabase;
    private LocalyticsSession mLocalyticsSession;

    
    @Override
    public void onAttach(SupportActivity activity)
	{
        super.onAttach(activity);
        try
        {
        	mClickListener = (OnItemSelectedListener) activity;
        } 
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
        }
    }
    

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
	    
    	// nacteni view
        mLoadingFromDatabase = true;
        refreshData();
    }

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		// nastaveni view
		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.layout_listing, container, false);
		if(!mLoadingFromDatabase) setView(null);
		return mRootView;
	}
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);

        // nahrani posledni pouzite pozice
        if (savedInstanceState != null) 
        {
        	mItemIdChecked = savedInstanceState.getInt(SAVED_CHOICE_CHECKED, DEFAULT_CHOICE_CHECKED);
        	mItemIdShown = savedInstanceState.getInt(SAVED_CHOICE_SHOWN, DEFAULT_CHOICE_SHOWN);
        }
        
        // je k dispozici detail fragment?
        View detailFrame = getSupportActivity().findViewById(R.id.fragment_detail);
        mDualView = (detailFrame != null) && (detailFrame.getVisibility() == View.VISIBLE);
        
        // aktualizace options menu po vytvoreni layoutu
        getSupportActivity().invalidateOptionsMenu();
        
        Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
        localyticsValues.put(Localytics.ATTR_VIEW_DUAL, mDualView ? Localytics.VALUE_VIEW_DUAL_TRUE : Localytics.VALUE_VIEW_DUAL_FALSE); // Localytics atribut
		mLocalyticsSession.tagEvent(Localytics.TAG_VIEW, localyticsValues); // Localytics
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
    	
    	// pokus o automatickou synchronizaci
		Synchronization synchro = new Synchronization((SkimapApplication) getSupportActivity().getApplicationContext(), mDatabase);
        synchro.trySynchronizeShortDataAuto(mLocalyticsSession, Localytics.VALUE_SYNCHRO_FROM_LIST);

        Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
        localyticsValues.put(Localytics.ATTR_ACTIVITY_FRAGMENT, Localytics.VALUE_ACTIVITY_FRAGMENT_LIST); // Localytics atribut
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
		// ulozeni pozice
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_CHOICE_CHECKED, mItemIdChecked);
        outState.putInt(SAVED_CHOICE_SHOWN, mItemIdShown);
    }
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// vytvoreni menu
		inflater.inflate(R.menu.menu_listing, menu);
		super.onCreateOptionsMenu(menu, inflater);

		// tlacitko s mapou
		if(!mDualView)
		{
			MenuItem preferencesItem = menu.add(Menu.NONE, R.id.ab_button_preferences, 21, R.string.ab_button_preferences);
			preferencesItem.setIcon(R.drawable.ic_menu_preferences);
			preferencesItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			
			MenuItem mapItem = menu.add(Menu.NONE, R.id.ab_button_map, 23, R.string.ab_button_map);
			mapItem.setIcon(R.drawable.ic_menu_mapmode);
			mapItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// nastaveni chovani tlacitek
		Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		
    	switch (item.getItemId()) 
    	{
	    	case R.id.ab_button_search:
	    		getSupportActivity().onSearchRequested();
	    		localyticsValues.put(Localytics.ATTR_BUTTON_SEARCH, Localytics.VALUE_BUTTON_FROM_LIST); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
	    		return true;
				
	    	case R.id.ab_button_map:
	    		Intent mapIntent = new Intent();
	    		mapIntent.setClass(getActivity(), MapActivity.class);
		        if(mDualView) 
	        	{
	        		mapIntent.putExtra(MapFragment.ITEM_ID, mItemIdShown);
	        	}
		        else
		        {
		        	mapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        }
		        startActivity(mapIntent);
		        localyticsValues.put(Localytics.ATTR_BUTTON_MAP, Localytics.VALUE_BUTTON_FROM_LIST); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
	    	case R.id.ab_button_refresh:
	    		// naslouchani synchronizace
	            ((SkimapApplication) getSupportActivity().getApplicationContext()).setSynchroListener(this);
	    		// synchronizace
	    		Synchronization synchro = new Synchronization((SkimapApplication) getSupportActivity().getApplicationContext(), mDatabase);
	            synchro.trySynchronizeShortData();
	            localyticsValues.put(Localytics.ATTR_BUTTON_REFRESH, Localytics.VALUE_BUTTON_FROM_LIST); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
	    	case R.id.ab_button_preferences:
	    		Intent preferencesIntent = new Intent();
	    		preferencesIntent.setClass(getActivity(), SettingsActivity.class);
		        startActivity(preferencesIntent);
		        localyticsValues.put(Localytics.ATTR_BUTTON_PREFERENCES, Localytics.VALUE_BUTTON_FROM_LIST); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
	    	// TODO: pridat razeni a groupovani
//	    	case R.id.ab_button_sort_alphabet:
//	    		Toast.makeText(getActivity(), "SORT ALPHABET", Toast.LENGTH_SHORT).show();
//				return true;
//				
//	    	case R.id.ab_button_sort_country:
//	    		Toast.makeText(getActivity(), "SORT COUNTRY", Toast.LENGTH_SHORT).show();
//				return true;
//				
//	    	case R.id.ab_button_sort_distance:
//	    		Toast.makeText(getActivity(), "SORT DISTANCE", Toast.LENGTH_SHORT).show();
//				return true;
//				
//	    	case R.id.ab_button_sort_snow:
//	    		Toast.makeText(getActivity(), "SORT SNOW", Toast.LENGTH_SHORT).show();
//				return true;

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
		
		// aktualizace listview
		refreshData();
		refreshDataAfterSynchro();

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
	
	
	private void refreshDataAfterSynchro()
	{
		// TODO: ziskat referenci ke vsem list fragmentum a zavolat pro ne metodu refreshListView(), obnovit map view
	}
	
	
	public void handleSearchIntent(Intent intent)
	{
		// otevreni databaze
        if(!mDatabase.isOpen()) mDatabase.open(true);
        
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

	        // zobrazeni vysledku vyhledavani
	        refreshData(query);
			
			Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
	        localyticsValues.put(Localytics.ATTR_SEARCH_SKICENTRE, Localytics.VALUE_SEARCH_FROM_LIST); // Localytics atribut
			mLocalyticsSession.tagEvent(Localytics.TAG_SEARCH, localyticsValues); // Localytics
		}
	}
	
	
	private void refreshData()
	{
		refreshData(null);
	}
	
	
	private void refreshData(final String keyword)
	{
		// odchyceni zpravy z vlakna
		final Handler handler = new Handler()
	    {
            @Override
            public void handleMessage(Message message) 
            {
            	setView(keyword);
            	mLoadingFromDatabase = false;
            }
	    };
			    
		// vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		refreshDataThread(keyword);
        		Message message = new Message();
        		handler.sendMessage(message);
		    }
        }.start();
	}
	
	
	private void refreshDataThread(final String keyword)
	{
		loadAllAreas();
		loadAllCountries();
		if(keyword==null) loadAllSkicentres();
		else loadSkicentresByKeyword(keyword);
	}
	
	
	private void loadSkicentresByKeyword(final String keyword)
	{	
		// promazani pole
		if(mSkicentreList != null) 
		{
			mSkicentreList.clear();
			mSkicentreList = null;
		}
		
		// nacteni dat do pole
		try
		{
			if(mDatabase.isOpen()) mSkicentreList = mDatabase.getSkicentresByKeyword(keyword, Database.Sort.NAME);
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
			return;
		}
	}


	private void loadAllSkicentres()
	{	
		// promazani pole
		if(mSkicentreList != null) 
		{
			mSkicentreList.clear();
			mSkicentreList = null;
		}
		
		// nacteni dat do pole
		try
		{
			if(mDatabase.isOpen()) mSkicentreList = mDatabase.getAllSkicentres(Database.Sort.NAME);
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	
	private void loadAllAreas()
	{
		// promazani pole
		if(mAreaList != null) 
		{
			mAreaList.clear();
			mAreaList = null;
		}
		
		// nacteni dat do pole
		try
		{
			if(mDatabase.isOpen()) mAreaList = mDatabase.getAllAreas();
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	
	private void loadAllCountries()
	{
		// promazani pole
		if(mCountryList != null) 
		{
			mCountryList.clear();
			mCountryList = null;
		}
		
		// nacteni dat do pole
		try
		{
			if(mDatabase.isOpen()) mCountryList = mDatabase.getAllCountries();
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	
	private void setView(final String keyword)
	{
		// seznam skicenter
		if(mRootView==null || mSkicentreList==null) return;
		ListView listView = (ListView) mRootView.findViewById(R.id.layout_listing_listview);

		// naplneni skicenter
		if(listView.getAdapter()==null || keyword!=null)
		{
			ListingAdapter adapter = new ListingAdapter(this, mSkicentreList, mAreaList, mCountryList);
			try
			{
				listView.setAdapter(adapter);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return;
			}
		} 
		else 
		{
			try
			{
				((ListingAdapter) listView.getAdapter()).refill(mSkicentreList, mAreaList, mCountryList);
				BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
				adapter.notifyDataSetChanged();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return;
			}
		}
		
		// nastaveni onclick
		listView.setItemsCanFocus(false);
		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) 
			{
				int id = mSkicentreList.get(position).getId();
				showDetail(id);
			}
		});
		// TODO
//		listView.setOnItemLongClickListener(new OnItemLongClickListener() 
//		{
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long viewId) 
//			{
//				
//				Toast.makeText(getActivity(), "LONG CLICK", Toast.LENGTH_SHORT).show();
//				return true;
//			}
//		});
		
		// nastaveni vyhledavaciho infoboxu
		TextView infobox = (TextView) mRootView.findViewById(R.id.layout_listing_infobox);
		if(keyword==null)
		{
			infobox.setVisibility(View.GONE);
		}
		else
		{
			StringBuilder builder = new StringBuilder();
			builder.append(getString(R.string.search_result));
			builder.append(" '");
			builder.append(keyword);
			builder.append("': ");
			builder.append(mSkicentreList.size());
			builder.append(" ");
			if(mSkicentreList.size()==1) builder.append(getString(R.string.search_result_skicentre_1));
			else if(mSkicentreList.size()>0 && mSkicentreList.size()<5) builder.append(getString(R.string.search_result_skicentre_234));
			else builder.append(getString(R.string.search_result_skicentre_5));
			builder.append("\n");
			builder.append(getString(R.string.search_result_cancel));
			infobox.setVisibility(View.VISIBLE);
			infobox.setText(builder.toString());
			infobox.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					refreshData();
				}
			});
		}
		
		// nastaveni oznaceni polozky v listu
		if (mDualView) 
        {
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
		
		// TODO: odstranit modry obrazek v listview - kdyz pretahnu za okraj
	}
	
	
	private void showDetail(int id)
	{
		mItemIdChecked = id;
		
        // dual view
        if (mDualView)
        {
        	// klik na jinou polozku nez je zobrazena
            if (mItemIdShown != mItemIdChecked)
            {
            	// TODO: zvyrazneni vybrane polozky
            	ListView listView = (ListView) mRootView.findViewById(R.id.layout_listing_listview);
            	listView.setItemChecked(id, true);
            	
            	mClickListener.onItemSelected(id);
            	mItemIdShown = id;
            }
        }
        else
        {
        	mClickListener.onItemSelected(id);
        }
    }
	

	public interface OnItemSelectedListener 
	{
        public void onItemSelected(int id);
    }
}
