package net.skimap.activities;

import net.skimap.R;
import net.skimap.fragments.DetailFragment;
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
        
        // dual view
        boolean dualView = true;
        Bundle extras = getIntent().getExtras();
		if(extras != null && extras.containsKey(DetailFragment.DUAL_VIEW))
		{
			dualView = extras.getBoolean(DetailFragment.DUAL_VIEW);
		}
		
		// v dual view ukonci aktivitu
 		if(dualView)
 		{
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
    			this.finish();
				return true;
				
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
}
