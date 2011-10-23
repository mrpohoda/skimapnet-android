package net.skimap.activities;

import net.skimap.R;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItem;
import android.widget.Toast;

public class DetailActivity extends FragmentActivity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setActionBar();
    }
    
    
    private void setActionBar()
    {
    	// vypnuti loga, vypnuti titulku a zapnuti home
    	ActionBar bar = getSupportActionBar();
    	bar.setDisplayUseLogoEnabled(false);
    	bar.setDisplayShowTitleEnabled(false);
    	bar.setDisplayShowHomeEnabled(true);
    	bar.setDisplayHomeAsUpEnabled(true);
    }
    
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
    	// nastaveni chovani tlacitek
    	switch (item.getItemId()) 
    	{
	    	case R.id.ab_button_share:				
	    		Toast.makeText(this, "SHARE", Toast.LENGTH_LONG).show();
				return true;
				
	    	case R.id.ab_button_favourite:
	    		Toast.makeText(this, "FAV", Toast.LENGTH_LONG).show();
				return true;
				
	    	case R.id.ab_button_camera:
	    		Toast.makeText(this, "CAMERA", Toast.LENGTH_LONG).show();
				return true;
				
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
}
