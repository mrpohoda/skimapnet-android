package net.skimap.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import net.skimap.R;
import net.skimap.activities.MapActivity;
import net.skimap.adapters.ListingAdapter;
import net.skimap.data.Area;
import net.skimap.data.Country;
import net.skimap.data.SkicentreShort;
import net.skimap.database.Database;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.SupportActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ListingFragment extends Fragment
{
	// TODO: nastavit init hodnotu mItemIdShown
	// TODO: pod action barem se zobrazuje rusivy transparentni prechod
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


	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        // TODO: beh ve vlakne, protoze prace s databazi je casove narocna
        loadAllAreas();
        loadAllCountries();
        loadAllSkicentres();
    }

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		// nastaveni view
		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.layout_listing, container, false);
		setView();
		
		// je k dispozici detail fragment?
        View detailFrame = getSupportActivity().findViewById(R.id.fragment_detail);
        mDualView = (detailFrame != null) && (detailFrame.getVisibility() == View.VISIBLE);
        
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
    }
	
	
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
			MenuItem mapItem = menu.add(Menu.NONE, R.id.ab_button_map, 11, R.string.ab_button_map);
			mapItem.setIcon(R.drawable.ic_menu_mapmode);
			mapItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// nastaveni chovani tlacitek
    	switch (item.getItemId()) 
    	{
    		// TODO: pridat razeni a groupovani
	    	case R.id.ab_button_search:
	    		Toast.makeText(getActivity(), "SEARCH", Toast.LENGTH_SHORT).show();
				return true;
				
	    	case R.id.ab_button_map:
	    		Intent intent = new Intent();
		        intent.setClass(getActivity(), MapActivity.class);
		        if(mDualView) intent.putExtra(MapFragment.ITEM_ID, mItemIdShown);
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
				return true;
				
	    	case R.id.ab_button_sort_alphabet:
	    		Toast.makeText(getActivity(), "SORT ALPHABET", Toast.LENGTH_SHORT).show();
				return true;
				
	    	case R.id.ab_button_sort_country:
	    		Toast.makeText(getActivity(), "SORT COUNTRY", Toast.LENGTH_SHORT).show();
				return true;
				
	    	case R.id.ab_button_sort_distance:
	    		Toast.makeText(getActivity(), "SORT DISTANCE", Toast.LENGTH_SHORT).show();
				return true;
				
	    	case R.id.ab_button_sort_snow:
	    		Toast.makeText(getActivity(), "SORT SNOW", Toast.LENGTH_SHORT).show();
				return true;

    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
	
	
	public void refreshListView()
	{
		// TODO: beh ve vlakne, protoze prace s databazi je casove narocna
		loadAllAreas();
		loadAllCountries();
		loadAllSkicentres();
		setView();
	}


	private void loadAllSkicentres()
	{
		// otevreni databaze
		Database db = new Database(getActivity());
		db.open(false);
		
		// promazani pole
		if(mSkicentreList != null) 
		{
			mSkicentreList.clear();
			mSkicentreList = null;
		}
		
		// nacteni dat do pole
		mSkicentreList = db.getAllSkicentres();
		db.close();
	}
	
	
	private void loadAllAreas()
	{
		// otevreni databaze
		Database db = new Database(getActivity());
		db.open(false);
		
		// promazani pole
		if(mAreaList != null) 
		{
			mAreaList.clear();
			mAreaList = null;
		}
		
		// nacteni dat do pole
		mAreaList = db.getAllAreas();
		db.close();
	}
	
	
	private void loadAllCountries()
	{
		// otevreni databaze
		Database db = new Database(getActivity());
		db.open(false);
		
		// promazani pole
		if(mCountryList != null) 
		{
			mCountryList.clear();
			mCountryList = null;
		}
		
		// nacteni dat do pole
		mCountryList = db.getAllCountries();
		db.close();
	}
	
	
	private void setView()
	{
		// seznam skicenter
		ListView listView = (ListView) mRootView.findViewById(R.id.layout_listing_listview);

		// naplneni skicenter
		if (listView.getAdapter()==null) 
		{
			ListingAdapter adapter = new ListingAdapter(this, mSkicentreList, mAreaList, mCountryList);
			listView.setAdapter(adapter);
		} 
		else 
		{
		    ((ListingAdapter) listView.getAdapter()).refill(mSkicentreList, mCountryList);
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
		listView.setOnItemLongClickListener(new OnItemLongClickListener() 
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long viewId) 
			{
				// TODO
				Toast.makeText(getActivity(), "LONG CLICK", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		
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
