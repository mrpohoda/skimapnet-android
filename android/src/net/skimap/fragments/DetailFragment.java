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
import android.widget.TextView;
import android.widget.Toast;

public class DetailFragment extends Fragment
{
	public static final String ITEM_ID = "item_id";
	public static final String DUAL_VIEW = "dual_view";
	
//	private boolean mDualView;
	private View mRootView;
	private int mItemId;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
//		// dual view
		Bundle extras = getActivity().getIntent().getExtras();
//		if(extras != null && extras.containsKey(DUAL_VIEW))
//		{
//			mDualView = extras.getBoolean(DUAL_VIEW);
//		}
//		else
//		{
//			mDualView = true;
//		}
		
		// nastaveni id detailu
		int id; 		
		// nahrani id z bundle aktivity
		if(extras != null && extras.containsKey(ITEM_ID))
		{
			id = extras.getInt(ITEM_ID);
		}		
		// nahrani posledniho pouziteho id
		else if (savedStateInstance != null && savedStateInstance.containsKey(ITEM_ID))
        {
			id = savedStateInstance.getInt(ITEM_ID, 0);
        }		
		// vychozi id
		else
		{
			id = 0; // TODO: nastavit geograficky nejblizsi skicentrum
		}
		
		// nastaveni view
		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.layout_detail, container, false);
		setView(id);
		return mRootView;
	}
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
		// ulozeni pozice
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
	    		Toast.makeText(this.getActivity(), "SHARE", Toast.LENGTH_LONG).show();
				return true;
				
	    	case R.id.ab_button_favourite:
	    		Toast.makeText(this.getActivity(), "FAV", Toast.LENGTH_LONG).show();
				return true;
				
	    	case R.id.ab_button_camera:
	    		Toast.makeText(this.getActivity(), "CAMERA", Toast.LENGTH_LONG).show();
				return true;
				
	    	case R.id.ab_button_map:	
	    		Intent intent = new Intent();
		        intent.setClass(this.getActivity(), MapActivity.class);
		        intent.putExtra(MapFragment.ITEM_ID, mItemId);
		        startActivity(intent);
				return true;
				
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
	
	
	public void setView(int id)
	{
		mItemId = id;
		
		TextView textView = (TextView) mRootView.findViewById(R.id.layout_detail_textview);
		textView.setText("Detail: " + id);
	}
}
