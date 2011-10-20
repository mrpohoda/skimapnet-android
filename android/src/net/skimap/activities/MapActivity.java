package net.skimap.activities;

import net.skimap.R;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentMapActivity;
import android.view.View;

public class MapActivity extends FragmentMapActivity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        setActionBar();
    }
    
    @Override
    protected boolean isRouteDisplayed() 
    {
        return false;
    }
    
    private void setActionBar()
    {
    	// zapnuti loga a vypnuti titulku
    	ActionBar bar = getSupportActionBar();
    	bar.setDisplayUseLogoEnabled(true);
    	bar.setDisplayShowTitleEnabled(false);
    	
    	// logo nebude klikatelne
    	View homeLayout = findViewById(com.actionbarsherlock.R.id.abs__home_wrapper);
    	homeLayout.setClickable(false);
        homeLayout.setFocusable(false);
    }
}
