package net.skimap.activities;

import net.skimap.R;
import net.skimap.database.Database;
import net.skimap.utililty.Settings;
import net.skimap.utililty.Version;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        // nastaveni layoutu
	    addPreferencesFromResource(R.layout.layout_settings);
	    try
	    {
	    	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	    	if(currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB)
	    	{
	    		setActionBar();
	    	}
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    
	    // nastaveni tlacitek
	    setPreferences();
    }
    
    
    private void setActionBar()
    {
    	// zapnuti loga, vypnuti titulku a zapnuti home
    	ActionBar bar = getActionBar();
    	bar.setDisplayUseLogoEnabled(true);
    	bar.setDisplayShowTitleEnabled(false);
    	bar.setDisplayShowHomeEnabled(true);
    	bar.setDisplayHomeAsUpEnabled(true);
    	
    	// inicializace progress baru
    	boolean synchro = ((SkimapApplication) getApplicationContext()).isSynchronizing();
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
    
    
    private void setPreferences()
	{
    	// napoveda	    
	    CharSequence helpKey = getResources().getString(R.string.settings_key_help);
	    Preference help = (Preference) findPreference(helpKey);
	    help.setOnPreferenceClickListener(new OnPreferenceClickListener() 
	    {			    	
			public boolean onPreferenceClick(Preference preference) 
			{
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, HelpActivity.class);
		        startActivity(intent);
				return false;
			}
	    });
	    
	    
	    // cache	    
	    CharSequence cacheKey = getResources().getString(R.string.settings_key_clear_cache);
	    Preference cache = (Preference) findPreference(cacheKey);
	    cache.setOnPreferenceClickListener(new OnPreferenceClickListener() 
	    {			    	
			public boolean onPreferenceClick(Preference preference) 
			{
				// promazani databaze
				Database db = new Database(SettingsActivity.this);
				db.open(true);
				db.deleteAllSkicentres();
				db.deleteAllCountries();
				db.deleteAllAreas();
				db.close();
				
				// vynulovani last synchro
				Settings settings = new Settings(SettingsActivity.this);
				settings.setLastSynchro(Settings.NULL_LONG);
				
				// toast
				Toast.makeText(SettingsActivity.this, R.string.settings_clear_cache_toast, Toast.LENGTH_SHORT).show();
				return false;
			}
	    });
	    
	    
	    // zpetna vazba	    
	    CharSequence feedbackKey = getResources().getString(R.string.settings_key_feedback);
	    Preference feedback = (Preference) findPreference(feedbackKey);
	    feedback.setOnPreferenceClickListener(new OnPreferenceClickListener() 
	    {			    	
			public boolean onPreferenceClick(Preference preference) 
			{
				// informace o aplikaci a telefonu
				StringBuilder info = new StringBuilder();
				info.append(getString(R.string.settings_feedback_app_version) + " ");
				info.append(getString(R.string.app_name) + " ");
				info.append(Version.getApplicationVersion(SettingsActivity.this, MapActivity.class));
				info.append("\n");
				info.append(getString(R.string.settings_feedback_os_version) + " ");
				info.append(android.os.Build.VERSION.RELEASE);
				info.append(" " + getString(R.string.settings_feedback_api) + " ");
				info.append(android.os.Build.VERSION.SDK_INT);
				info.append("\n");
				info.append(getString(R.string.settings_feedback_device) + " ");
				info.append(android.os.Build.MANUFACTURER);
				info.append(" ");
				info.append(android.os.Build.MODEL);
				info.append("\n");
				info.append(getString(R.string.settings_feedback_message) + " ");
				info.append("\n");

				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.settings_feedback_email)});
				intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.settings_feedback_subject));
				intent.putExtra(android.content.Intent.EXTRA_TEXT, info.toString());
				startActivity(Intent.createChooser(intent, getString(R.string.settings_feedback)));
				return false;
			}
	    });
	}
}
