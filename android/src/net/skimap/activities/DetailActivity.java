package net.skimap.activities;

import net.skimap.R;
import net.skimap.fragments.DetailFragment;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;
import android.widget.Toast;

public class DetailActivity extends FragmentActivity implements SkimapApplication.OnSynchroListener
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
 		
 		// naslouchani synchronizace
        ((SkimapApplication) getApplicationContext()).setSynchroListener(this);

        // nastaveni layoutu
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
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
    	
    	// inicializace progress baru
    	boolean synchro = ((SkimapApplication) getApplicationContext()).getSynchro();
    	setProgressBarIndeterminateVisibility(synchro ? Boolean.TRUE : Boolean.FALSE);
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
	}
}
