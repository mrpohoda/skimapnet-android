package net.skimap.activities;

import net.skimap.R;
import net.skimap.fragments.MapFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.view.Window;

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
    
    
    @Override
	public void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		setIntent(intent);
		MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
		fragment.handleSearchIntent(intent);
	}
    
    
    private void setActionBar()
    {
    	// zapnuti loga a vypnuti titulku
    	ActionBar bar = getSupportActionBar();
    	bar.setDisplayUseLogoEnabled(true);
    	bar.setDisplayShowTitleEnabled(false);
    	
    	// inicializace progress baru
    	boolean synchro = ((SkimapApplication) getApplicationContext()).isSynchronizing();
    	setProgressBarIndeterminateVisibility(synchro ? Boolean.TRUE : Boolean.FALSE);
    }
}
