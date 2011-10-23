package net.skimap.activities;

import net.skimap.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ListingActivity extends FragmentActivity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        setActionBar();
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
