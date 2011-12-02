package net.skimap.fragments;

import net.skimap.R;
import net.skimap.activities.MapActivity;
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

public class DetailFragment extends Fragment
{
	public static final String ITEM_ID = "item_id";
	public static final String DUAL_VIEW = "dual_view";
	private final int EMPTY_ID = -1;
	
	@SuppressWarnings("unused")
	private boolean mDualView;
	private View mRootView;
	private int mItemId;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // nastaveni extras
        Bundle extras = getSupportActivity().getIntent().getExtras();
        setExtras(extras, savedInstanceState);
        
        // nastaveni nejblizsiho skicentra
        if(mItemId == EMPTY_ID) searchNearestSkicentre();
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		// nastaveni view
		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.layout_detail, container, false);
		setView();
		return mRootView;
	}
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
		// ulozeni id
        super.onSaveInstanceState(outState);
        outState.putInt(ITEM_ID, mItemId);
    }


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// vytvoreni menu
		inflater.inflate(R.menu.menu_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// nastaveni chovani tlacitek
    	switch (item.getItemId()) 
    	{
	    	case R.id.ab_button_share:				
	    		Toast.makeText(getActivity(), "SHARE", Toast.LENGTH_SHORT).show();
				return true;
				
	    	case R.id.ab_button_favourite:
	    		Toast.makeText(getActivity(), "FAV", Toast.LENGTH_SHORT).show();
				return true;
				
	    	case R.id.ab_button_camera:
	    		Toast.makeText(getActivity(), "CAMERA", Toast.LENGTH_SHORT).show();
				return true;
				
	    	case R.id.ab_button_map:	
	    		Intent intent = new Intent();
		        intent.setClass(getActivity(), MapActivity.class);
		        intent.putExtra(MapFragment.ITEM_ID, mItemId);
		        startActivity(intent);
				return true;
				
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
	
	
	private void setExtras(Bundle extras, Bundle savedInstanceState)
	{
		// dual view
		if(extras != null && extras.containsKey(DUAL_VIEW))
		{
			mDualView = extras.getBoolean(DUAL_VIEW);
		}
		else
		{
			mDualView = true;
		}
        
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
	
	
	public void searchNearestSkicentre()
	{
		// TODO
		Toast.makeText(getActivity(), "NEAREST SKICENTRE", Toast.LENGTH_SHORT).show();
		// mItemId = id;
	}
	
	
	public void setView()
	{
		setView(mItemId);
	}
	
	
	public void setView(int id)
	{
		// TODO
		// TODO: formatovani textu: http://stackoverflow.com/questions/1529068/is-it-possible-to-have-multiple-styles-inside-a-textview
		// TODO: pridat toasty s napovedou na flagy
		mItemId = id;
		
		//TextView textView = (TextView) mRootView.findViewById(R.id.layout_detail_textview);
		//textView.setText("Detail: " + id);
	}
}
