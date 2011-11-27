package net.skimap.activities;

import net.skimap.R;
import net.skimap.adapters.TabsAdapter;
import net.skimap.fragments.DetailFragment;
import net.skimap.fragments.ListingAllFragment;
import net.skimap.fragments.ListingFavFragment;
import net.skimap.fragments.ListingFragment;
import net.skimap.fragments.ListingRecFragment;
import net.skimap.network.Synchronization;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.Window;
import android.view.View;
import android.widget.Toast;

public class ListingActivity extends FragmentActivity implements ListingFragment.OnItemSelectedListener, SkimapApplication.OnSynchroListener
{
	private final String SAVED_TAB_INDEX = "tab_index";
	
	private ViewPager  mViewPager;
	private TabsAdapter mTabsAdapter;

	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // naslouchani synchronizace
        ((SkimapApplication) getApplicationContext()).setSynchroListener(this);

        // nastaveni layoutu
        setContentView(R.layout.activity_listing);
        setActionBar();
        
        // nacteni pozice zalozky
        if (savedInstanceState != null) 
        {
        	getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(SAVED_TAB_INDEX));
        }
        
        // TODO: synchronizace dat jednou za den, explicit. refresh jen pokud neprobiha synchronizace
        Synchronization synchro = new Synchronization((SkimapApplication) getApplicationContext());
        synchro.trySynchronizeShortData();
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
    	
    	ActionBar.Tab tab1 = bar.newTab().setText(R.string.ab_tab_all);
        ActionBar.Tab tab2 = bar.newTab().setText(R.string.ab_tab_favourites);
        ActionBar.Tab tab3 = bar.newTab().setText(R.string.ab_tab_recommended);
               
        mTabsAdapter.addTab(tab1, ListingAllFragment.class);
        mTabsAdapter.addTab(tab2, ListingFavFragment.class);
        mTabsAdapter.addTab(tab3, ListingRecFragment.class);

        // inicializace progress baru
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    	boolean synchro = ((SkimapApplication) getApplicationContext()).getSynchro();
    	setProgressBarIndeterminateVisibility(synchro ? Boolean.TRUE : Boolean.FALSE);
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
    	// ulozeni pozice zalozky
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_TAB_INDEX, getSupportActionBar().getSelectedNavigationIndex());
    }


	@Override
	public void onItemSelected(int id)
	{
		// je k dispozici detail fragment?
        View detailFrame = findViewById(R.id.fragment_detail);
        boolean dualView = (detailFrame != null) && (detailFrame.getVisibility() == View.VISIBLE);

		if(dualView)
		{
			// aktualizace view v detail fragmentu
        	DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
        	detailFragment.setView(id);
        } 
		else 
		{
			// nova aktivita detail
			Intent intent = new Intent();
			intent.setClass(this, DetailActivity.class);
			intent.putExtra(DetailFragment.ITEM_ID, id);
			intent.putExtra(DetailFragment.DUAL_VIEW, dualView);
			startActivity(intent);
        }
	}


	@Override
	public void onSynchroStart()
	{
		// zapnuti progress baru
		setProgressBarIndeterminateVisibility(Boolean.TRUE);
		
		// start
		Toast.makeText(this, "SYNCHRO START", Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onSynchroStop()
	{
		// vypnuti progress baru
		setProgressBarIndeterminateVisibility(Boolean.FALSE);
		
		// hotovo
		Toast.makeText(this, "SYNCHRO DONE", Toast.LENGTH_SHORT).show();
		
		// aktualizace listview
		refreshListViews();
	}
	
	
	private void refreshListViews()
	{
		// TODO: ziskat referenci ke vsem list fragmentum a zavolat pro ne metodu refreshListView()
	}
}
