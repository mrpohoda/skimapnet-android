package net.skimap.activities;

import net.skimap.R;
import net.skimap.adapters.TabsAdapter;
import net.skimap.fragments.ListingFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

public class ListingActivity extends FragmentActivity
{
	ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        setActionBar();
        
        if (savedInstanceState != null) 
        {
        	getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("index"));
        }
    }
    
    
    private void setActionBar()
    {
    	// zapnuti loga a vypnuti titulku
    	ActionBar bar = getSupportActionBar();
    	bar.setDisplayUseLogoEnabled(true);
    	bar.setDisplayShowTitleEnabled(false);
    	
    	// logo nebude klikatelne
    	try
    	{
	    	View homeLayout = findViewById(com.actionbarsherlock.R.id.abs__home_wrapper);
	    	homeLayout.setClickable(false);
	        homeLayout.setFocusable(false);
    	}
    	catch(Exception e)
    	{
    	}
    	
    	// tabs
    	bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	mViewPager = (ViewPager) findViewById(R.id.layout_listing_pager);
        mTabsAdapter = new TabsAdapter(this, bar, mViewPager);
    	
    	ActionBar.Tab tab1 = bar.newTab().setText(R.string.ab_tab_nearest);
        ActionBar.Tab tab2 = bar.newTab().setText(R.string.ab_tab_favourites);
        ActionBar.Tab tab3 = bar.newTab().setText(R.string.ab_tab_recommended);
               
        mTabsAdapter.addTab(tab1, ListingFragment.class);
        mTabsAdapter.addTab(tab2, ListingFragment.class);
        mTabsAdapter.addTab(tab3, ListingFragment.class);
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("index", getSupportActionBar().getSelectedNavigationIndex());
    }
    
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// nastaveni chovani tlacitek
    	Intent intent = new Intent();
    	
    	switch (item.getItemId()) 
    	{
	    	case R.id.ab_button_map:				
		        intent.setClass(this, MapActivity.class);
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
				return true;
				
	    	case R.id.ab_button_search:
	    		Toast.makeText(this, "SEARCH", Toast.LENGTH_LONG).show();
				return true;
				
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
}
