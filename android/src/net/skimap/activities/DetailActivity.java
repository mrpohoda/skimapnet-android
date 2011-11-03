package net.skimap.activities;

import net.skimap.R;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItem;

public class DetailActivity extends FragmentActivity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }
        
        setContentView(R.layout.activity_detail);
        setActionBar();
    }
    
    
    private void setActionBar()
    {
    	// vypnuti loga, zapnuti titulku a zapnuti home
    	ActionBar bar = getSupportActionBar();
    	bar.setDisplayUseLogoEnabled(false);
    	bar.setDisplayShowTitleEnabled(true);
    	bar.setDisplayShowHomeEnabled(true);
    	bar.setDisplayHomeAsUpEnabled(true);
    }
    
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// nastaveni chovani tlacitek
    	switch (item.getItemId()) 
    	{
    		case android.R.id.home:
				Intent intent = new Intent(this, ListingActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
				
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
}
