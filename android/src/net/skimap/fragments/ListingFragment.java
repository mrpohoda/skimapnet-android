package net.skimap.fragments;

import java.util.ArrayList;

import net.skimap.R;
import net.skimap.activities.DetailActivity;
import net.skimap.activities.MapActivity;
import net.skimap.adapters.ListingAdapter;
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
    private boolean mHasDetailsFrame;
    private int mPositionChecked = 0;
    private int mPositionShown = -1;
    private View mRootView;
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    }
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);

        // je k dispozici detail fragment?
        View detailFrame = getActivity().findViewById(R.id.fragment_detail);
        mHasDetailsFrame = (detailFrame != null) && (detailFrame.getVisibility() == View.VISIBLE);

        // nahrani posledni pozice
        if (savedInstanceState != null) 
        {
            // Restore last state for checked position.
            mPositionChecked = savedInstanceState.getInt("curChoice", 0);
            mPositionShown = savedInstanceState.getInt("shownChoice", -1);
        }
    }
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mPositionChecked);
        outState.putInt("shownChoice", mPositionShown);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.menu_listing, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// nastaveni chovani tlacitek
    	Intent intent = new Intent();
    	
    	switch (item.getItemId()) 
    	{
	    	case R.id.ab_button_map:				
		        intent.setClass(this.getActivity(), MapActivity.class);
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
	
	
	private void setView()
	{
		// seznam skicenter
		ListView listView = (ListView) mRootView.findViewById(R.id.layout_listing_listview);
		
		// vyprazdneni seznamu
		listView.setAdapter(null);
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("Bormio");
		list.add("Pec pod Snìžkou");
		list.add("Špindlerùv Mlýn");
		list.add("Kvilda");
		list.add("Kouty nad Desnou");
		list.add("Monínec");
		list.add("Èerná hora");
		list.add("Javorina");
		list.add("Bormio");
		list.add("Pec pod Snìžkou");
		list.add("Špindlerùv Mlýn");
		list.add("Kvilda");
		list.add("Kouty nad Desnou");
		list.add("Monínec");
		list.add("Èerná hora");
		list.add("Javorina");
		list.add("Bormio");
		list.add("Pec pod Snìžkou");
		list.add("Špindlerùv Mlýn");
		list.add("Kvilda");
		list.add("Kouty nad Desnou");
		list.add("Monínec");
		list.add("Èerná hora");
		list.add("Javorina");
		
		// nastaveni view seznamu skicenter
		ListingAdapter adapter =  new ListingAdapter(this, list);
		listView.setAdapter(adapter);
		
		// nastaveni detail fragment
		if (mHasDetailsFrame) 
        {
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			showDetail(mPositionChecked);
        }
	}
	
	
	// TODO: predani indexu do detail fragmentu
	// TODO: pri vypnuti (kliknuti na home) detail fragmentu pamatovat pozici zalozky z predchoziho kroku
	public void showDetail(int index)
	{
        mPositionChecked = index;

        if (mHasDetailsFrame) 
        {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
        	ListView listView = (ListView) mRootView.findViewById(R.id.layout_listing_listview);
        	listView.setItemChecked(index, true);

            if (mPositionShown != mPositionChecked)
            {
//            	FragmentManager manager = getFragmentManager();
//            	Fragment fragment = manager.findFragmentById(R.id.fragment_detail);
//            	View fragmentView = fragment.getView();
//            	           	
//            	TextView textView = (TextView) fragmentView.findViewById(R.id.layout_detail_textview);
//            	textView.setText(mPositionChecked);

            	
//                // If we are not currently showing a fragment for the new
//                // position, we need to create and install a new one.
//                DetailFragment fragment = DetailFragment.newInstance(index);
//                //DetailFragment fragment = new DetailFragment();
//
//                // Execute a transaction, replacing any existing fragment
//                // with this one inside the frame.
//                
//                // fragment manazer
//                FragmentManager manager = getFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                
//                // smazani stareho fragmentu a pridani noveho
//                Fragment oldFragment = manager.findFragmentById(R.id.fragment_detail);
//                transaction.remove(oldFragment);
//                transaction.add(R.id.fragment_detail, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                
//                // nahrazeni fragmentu
//                // transaction.replace(R.id.fragment_detail, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//
//                // odeslani zmen
//                transaction.commit();

                mPositionShown = index;
            }
        } 
        else 
        {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }
}
