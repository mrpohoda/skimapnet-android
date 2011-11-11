package net.skimap.activities;

import net.skimap.R;
import net.skimap.adapters.TabsAdapter;
import net.skimap.fragments.ListingFragment;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ListingActivity extends FragmentActivity
{
	private final String SAVED_TAB_INDEX = "tab_index";
	
	private ViewPager  mViewPager;
	private TabsAdapter mTabsAdapter;

	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        setActionBar();
        
        // nacteni pozice zalozky
        if (savedInstanceState != null) 
        {
        	getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(SAVED_TAB_INDEX));
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
    	// ulozeni pozice zalozky
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_TAB_INDEX, getSupportActionBar().getSelectedNavigationIndex());
    }
}
