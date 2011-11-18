package net.skimap.fragments;

import java.util.ArrayList;

import net.skimap.R;
import net.skimap.activities.DetailActivity;
import net.skimap.activities.MapActivity;
import net.skimap.adapters.ListingAdapter;
import net.skimap.data.SkicentreShort;
import net.skimap.database.Database;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class ListingFragment extends Fragment
{
	private final String SAVED_CHOICE_CHECKED = "choice_checked";
	private final String SAVED_CHOICE_SHOWN = "choice_shown";
	private final int DEFAULT_CHOICE_CHECKED = 0;
	private final int DEFAULT_CHOICE_SHOWN = 0;
      
    private int mItemIdChecked = DEFAULT_CHOICE_CHECKED;
    private int mItemIdShown = DEFAULT_CHOICE_SHOWN;
    private boolean mDualView;
    private View mRootView;
    private ArrayList<SkicentreShort> mList;
    

	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        loadSkicentres();
    }

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.layout_listing, container, false);
		setView();
		return mRootView;
	}
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);

        // je k dispozici detail fragment?
        View detailFrame = getActivity().findViewById(R.id.fragment_detail);
        mDualView = (detailFrame != null) && (detailFrame.getVisibility() == View.VISIBLE);

        // nahrani posledni pouzite pozice
        if (savedInstanceState != null) 
        {
        	mItemIdChecked = savedInstanceState.getInt(SAVED_CHOICE_CHECKED, DEFAULT_CHOICE_CHECKED);
        	mItemIdShown = savedInstanceState.getInt(SAVED_CHOICE_SHOWN, DEFAULT_CHOICE_SHOWN);
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
		View detailFrame = getActivity().findViewById(R.id.fragment_detail);
		boolean dualView = (detailFrame != null) && (detailFrame.getVisibility() == View.VISIBLE);		 
		if(!dualView)
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
	    	case R.id.ab_button_search:
	    		Toast.makeText(this.getActivity(), "SEARCH", Toast.LENGTH_LONG).show();
				return true;
				
	    	case R.id.ab_button_map:
	    		Intent intent = new Intent();
		        intent.setClass(this.getActivity(), MapActivity.class);
		        intent.putExtra(MapFragment.ITEM_ID, mItemIdShown);
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
				return true;

    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
	
	
	private void loadSkicentres()
	{
		// otevreni databaze
		Database db = new Database(getActivity());
		db.open();
		
		// ulozeni dat
		db.removeAll();
		db.insertSkicentre(new SkicentreShort(1, "Bormio", 0, 0));
		db.insertSkicentre(new SkicentreShort(2, "Monínec", 0, 0));
		db.insertSkicentre(new SkicentreShort(3, "Pec pod Snìžkou", 0, 0));
		db.insertSkicentre(new SkicentreShort(4, "Špindlerùv Mlýn", 0, 0));
		db.insertSkicentre(new SkicentreShort(5, "Adamov", 0, 0));
		db.insertSkicentre(new SkicentreShort(6, "Brno", 0, 0));
		
		// nacteni dat do pole
		mList = db.getAllSkicentres();
		db.close();
	}
	
	
	private void setView()
	{
		// seznam skicenter
		ListView listView = (ListView) mRootView.findViewById(R.id.layout_listing_listview);
		
		// naplneni skicenter
		listView.setAdapter(null);
		ListingAdapter adapter = new ListingAdapter(this, mList);
		listView.setAdapter(adapter);

		// nastaveni oznaceni polozky v listu
		if (mDualView) 
        {
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
		
		// TODO: odstranit modry obrazek v listview - kdyz pretahnu za okraj
	}
	
	
	public void showDetail(int index)
	{
		mItemIdChecked = index;

        // dual view
        if (mDualView)
        {
        	// klik na jinou polozku nez je zobrazena
            if (mItemIdShown != mItemIdChecked)
            {
            	// TODO: zvyrazneni vybrane polozky
            	ListView listView = (ListView) mRootView.findViewById(R.id.layout_listing_listview);
            	listView.setItemChecked(index, true);

            	// nastaveni view v detail fragmentu
            	DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.fragment_detail);
            	detailFragment.setView(index);
            	            	
            	mItemIdShown = index;
            }
        }
        
        // mono view
        else 
        {
            // nova aktivita
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailActivity.class);
            intent.putExtra(DetailFragment.ITEM_ID, index);
            intent.putExtra(DetailFragment.DUAL_VIEW, false);
            startActivity(intent);
        }
    }
}
