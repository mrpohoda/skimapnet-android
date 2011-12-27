package net.skimap.activities;

import net.skimap.R;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.view.Window;
import android.view.View;

public class MapActivity extends FragmentMapActivity
{
	// TODO - opravit error
	// http://stackoverflow.com/questions/6006835/android-mapactivity-couldnt-get-connection-factory-client
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        // nastaveni layoutu
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_map);
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
    	try
    	{
	    	View homeLayout = findViewById(com.actionbarsherlock.R.id.abs__home_wrapper);
	    	homeLayout.setClickable(false);
	        homeLayout.setFocusable(false);
    	}
    	catch(Exception e)
    	{
    	}
    	
    	// inicializace progress baru
    	boolean synchro = ((SkimapApplication) getApplicationContext()).isSynchronizing();
    	setProgressBarIndeterminateVisibility(synchro ? Boolean.TRUE : Boolean.FALSE);
    }
}
