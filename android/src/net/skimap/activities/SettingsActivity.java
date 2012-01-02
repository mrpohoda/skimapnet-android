package net.skimap.activities;

import java.util.HashMap;
import java.util.Map;

import net.skimap.R;
import net.skimap.content.CustomSuggestionProvider;
import net.skimap.database.Database;
import net.skimap.utililty.Localytics;
import net.skimap.utililty.Settings;
import net.skimap.utililty.Version;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.SearchRecentSuggestions;
import android.view.MenuItem;
import android.widget.Toast;

import com.localytics.android.LocalyticsSession;

public class SettingsActivity extends PreferenceActivity
{
	private Database mDatabase;
	private LocalyticsSession mLocalyticsSession;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // Localytics
	    this.mLocalyticsSession = new LocalyticsSession(getApplicationContext(), Localytics.KEY);
	    this.mLocalyticsSession.open(); // otevre session
	    this.mLocalyticsSession.upload(); // upload dat
	    // At this point, Localytics Initialization is done.  After uploads complete nothing
	    // more will happen due to Localytics until the next time you call it.
	    
	    // otevreni databaze
	    mDatabase = new Database(this);
    	mDatabase.open(true);

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
    
    
    @Override
    public void onResume()
	{
        super.onResume();
        
        // Localytics
        this.mLocalyticsSession.open(); // otevre session pokud neni jiz otevrena
        
        Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
        localyticsValues.put(Localytics.ATTR_ACTIVITY_FRAGMENT, Localytics.VALUE_ACTIVITY_FRAGMENT_PREFERENCES); // Localytics atribut
		mLocalyticsSession.tagEvent(Localytics.TAG_ACTIVITY, localyticsValues); // Localytics
		
		// otevreni databaze
		if(!mDatabase.isOpen()) mDatabase.open(true);
    }
	
	
	@Override
	public void onPause()
	{
		// Localytics
	    this.mLocalyticsSession.close();
	    this.mLocalyticsSession.upload();
	    
	    // zavreni database
	    mDatabase.close();
	    
	    super.onPause();
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
		        
		        Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_PREFERENCE_HELP, Localytics.VALUE_PREFERENCE_FROM_PREFERENCES); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_PREFERENCE, localyticsValues); // Localytics
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
				try
				{
					mDatabase.deleteAllSkicentres();
					mDatabase.deleteAllCountries();
					mDatabase.deleteAllAreas();
				}
				catch(IllegalStateException e)
				{
					e.printStackTrace();
				}
				
				// smazani queries pro naseptavac
				SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SettingsActivity.this,
						CustomSuggestionProvider.AUTHORITY,
						CustomSuggestionProvider.MODE);
		        suggestions.clearHistory();
				
				// vynulovani last synchro
				Settings settings = new Settings(SettingsActivity.this);
				settings.setLastSynchro(Settings.NULL_LONG);
				
				// toast
				Toast.makeText(SettingsActivity.this, R.string.settings_clear_cache_toast, Toast.LENGTH_SHORT).show();
				
				Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_PREFERENCE_CACHE, Localytics.VALUE_PREFERENCE_FROM_PREFERENCES); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_PREFERENCE, localyticsValues); // Localytics
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
				
				Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_PREFERENCE_FEEDBACK, Localytics.VALUE_PREFERENCE_FROM_PREFERENCES); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_PREFERENCE, localyticsValues); // Localytics
				return false;
			}
	    });
	}
}
