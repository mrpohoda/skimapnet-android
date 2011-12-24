package net.skimap.fragments;

import java.util.HashMap;
import java.util.Map;

import net.skimap.R;
import net.skimap.activities.MapActivity;
import net.skimap.activities.SettingsActivity;
import net.skimap.activities.SkimapApplication;
import net.skimap.data.SkicentreLong;
import net.skimap.data.Weather;
import net.skimap.database.Database;
import net.skimap.database.DatabaseHelper;
import net.skimap.network.Synchronization;
import net.skimap.utililty.Localytics;
import net.skimap.utililty.Version;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.localytics.android.LocalyticsSession;

public class DetailFragment extends Fragment implements SkimapApplication.OnSynchroListener
{
	public static final String ITEM_ID = "item_id";
	public static final String DUAL_VIEW = "dual_view";
	private final int EMPTY_ID = -1;
	private final int PEREX_SHORT_LENGTH = 110;
	
	@SuppressWarnings("unused")
	private boolean mDualView;
	private View mRootView;
	private int mItemId;
	private boolean mPerexMore;
	private boolean mOffline = false;
	private SkicentreLong mSkicentre;
	private Database mDatabase;
	private LocalyticsSession mLocalyticsSession;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // Localytics
	    this.mLocalyticsSession = new LocalyticsSession(getActivity().getApplicationContext(), Localytics.KEY);
	    this.mLocalyticsSession.open(); // otevre session
	    this.mLocalyticsSession.upload(); // upload dat
	    // At this point, Localytics Initialization is done.  After uploads complete nothing
	    // more will happen due to Localytics until the next time you call it.
	    
	    // otevreni databaze
	    mDatabase = new Database(getActivity());
    	mDatabase.open(true);
        
        // nastaveni extras
        Bundle extras = getSupportActivity().getIntent().getExtras();
        setExtras(extras, savedInstanceState);
        
        // nastaveni nejblizsiho skicentra
        if(mItemId == EMPTY_ID) searchNearestSkicentre();
        
        // nacteni dat z databaze (pro offline stav) a nasledne spusteni synchronizace
        refreshDataAndSynchronize(mItemId);
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		// nastaveni view
		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.layout_detail, container, false);
		return mRootView;
	}
	
	
	@Override
    public void onResume()
	{
        super.onResume();
        
        // Localytics
        this.mLocalyticsSession.open(); // otevre session pokud neni jiz otevrena
        
        // otevreni databaze
        if(!mDatabase.isOpen()) mDatabase.open(true);

        // naslouchani synchronizace
        ((SkimapApplication) getSupportActivity().getApplicationContext()).setSynchroListener(this);
        
        // aktualizace stavu progress baru
    	boolean synchro = ((SkimapApplication) getSupportActivity().getApplicationContext()).isSynchronizing();
    	getSupportActivity().setProgressBarIndeterminateVisibility(synchro ? Boolean.TRUE : Boolean.FALSE);

        Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
        localyticsValues.put(Localytics.ATTR_ACTIVITY_FRAGMENT, Localytics.VALUE_ACTIVITY_FRAGMENT_DETAIL); // Localytics atribut
		mLocalyticsSession.tagEvent(Localytics.TAG_ACTIVITY, localyticsValues); // Localytics
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
	
	
	@Override
    public void onSaveInstanceState(Bundle outState) 
	{
		// ulozeni id
        super.onSaveInstanceState(outState);
        outState.putInt(ITEM_ID, mItemId);
    }


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// vytvoreni menu
		inflater.inflate(R.menu.menu_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
		Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		
    	// nastaveni chovani tlacitek
    	switch (item.getItemId()) 
    	{
	    	case R.id.ab_button_share:
	    		if(mSkicentre==null) return true;
	    		String url = mSkicentre.getUrlSkimap()!=null ? mSkicentre.getUrlSkimap() : getString(R.string.layout_detail_share_url_full);
	    		String subject = mSkicentre.getName() + " - " + getString(R.string.layout_detail_share_url);
	    		String text = getString(R.string.layout_detail_share_skicentre) + " " + mSkicentre.getName() + ": " + url;
	    		
	    		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
	    		shareIntent.setType("text/plain");
	    		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
	    		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);	
	    		startActivity(Intent.createChooser(shareIntent, getString(R.string.layout_detail_share)));
	    		
	    		localyticsValues.put(Localytics.ATTR_BUTTON_SHARE, Localytics.VALUE_BUTTON_FROM_DETAIL); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
			// TODO
//	    	case R.id.ab_button_favourite:
//	    		Toast.makeText(getActivity(), "FAV", Toast.LENGTH_SHORT).show();
//				return true;
				
	    	case R.id.ab_button_preferences:
	    		Intent intent = new Intent();
	    		intent.setClass(getActivity(), SettingsActivity.class);
		        startActivity(intent);
		        localyticsValues.put(Localytics.ATTR_BUTTON_PREFERENCES, Localytics.VALUE_BUTTON_FROM_DETAIL); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
	    	case R.id.ab_button_map:	
	    		Intent mapIntent = new Intent();
	    		mapIntent.setClass(getActivity(), MapActivity.class);
	    		mapIntent.putExtra(MapFragment.ITEM_ID, mItemId);
		        startActivity(mapIntent);
		        localyticsValues.put(Localytics.ATTR_BUTTON_MAP, Localytics.VALUE_BUTTON_FROM_DETAIL); // Localytics atribut
	    		mLocalyticsSession.tagEvent(Localytics.TAG_BUTTON, localyticsValues); // Localytics
				return true;
				
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
	
	
	@Override
	public void onSynchroStart()
	{
		// zapnuti progress baru
		getSupportActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);
	}


	@Override
	public void onSynchroStop(int result)
	{
		// vypnuti progress baru
		getSupportActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
		
		// aktualizace view
		refreshData(mItemId);
		
		Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		
		// toast pro offline rezim nebo error
		if(result==Synchronization.STATUS_OFFLINE)
		{
			mOffline = true;
			localyticsValues.put(Localytics.ATTR_SYNCHRO_LONG_STATUS, Localytics.VALUE_SYNCHRO_STATUS_OFFLINE); // Localytics atribut
		}
		else if(result==Synchronization.STATUS_CANCELED) 
		{
			Toast.makeText(getActivity(), R.string.toast_synchro_canceled, Toast.LENGTH_LONG).show();
			localyticsValues.put(Localytics.ATTR_SYNCHRO_SHORT_STATUS, Localytics.VALUE_SYNCHRO_STATUS_CANCELED); // Localytics atribut
		}
		else if(result==Synchronization.STATUS_UNKNOWN)
		{
			Toast.makeText(getActivity(), R.string.toast_synchro_error, Toast.LENGTH_LONG).show();
			mOffline = false;
			localyticsValues.put(Localytics.ATTR_SYNCHRO_LONG_STATUS, Localytics.VALUE_SYNCHRO_STATUS_ERROR); // Localytics atribut
		}
		else
		{
			mOffline = false;
			localyticsValues.put(Localytics.ATTR_SYNCHRO_LONG_STATUS, Localytics.VALUE_SYNCHRO_STATUS_ONLINE); // Localytics atribut
		}

		mLocalyticsSession.tagEvent(Localytics.TAG_SYNCHRO, localyticsValues); // Localytics
	}
	
	
	public void refreshDataAndSynchronize(final int id)
	{
		mItemId = id;
		
		// odchyceni zpravy z vlakna
		final Handler handler = new Handler()
	    {
            @Override
            public void handleMessage(Message message) 
            {
            	setView();
            	
            	// synchronizace
                Synchronization synchro = new Synchronization((SkimapApplication) getSupportActivity().getApplicationContext(), mDatabase);
                synchro.trySynchronizeLongData(mItemId);
            }
	    };
			    
		// vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		refreshDataThread(id);
        		Message message = new Message();
        		handler.sendMessage(message);
		    }
        }.start();
	}
	
	
	private void refreshData(final int id)
	{
		mItemId = id;
		
		// odchyceni zpravy z vlakna
		final Handler handler = new Handler()
	    {
            @Override
            public void handleMessage(Message message) 
            {
            	setView();
            }
	    };
			    
		// vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		refreshDataThread(id);
        		Message message = new Message();
        		handler.sendMessage(message);
		    }
        }.start();
	}
	
	
	private void refreshDataThread(int id)
	{
		// promazani objektu
		mSkicentre = null;
		
		// nacteni dat do pole
		try
		{
			if(mDatabase.isOpen()) mSkicentre = mDatabase.getSkicentre(mItemId);
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	
	private void setView()
	{
		// TODO: formatovani textu: http://stackoverflow.com/questions/1529068/is-it-possible-to-have-multiple-styles-inside-a-textview
		// TODO: pridat toasty s napovedou na flagy
		// TODO: osetrovat null hodnoty - DatabaseHelper.NULL_STRING apod., pripadne schovat dany widget
		// TODO: roztridit kousky kodu do dilcich funkci, pro kazde jednotlive view
		if(mSkicentre==null) return;
		mPerexMore = false;
		
		// reference na textove pole
		TextView textOffline = (TextView) mRootView.findViewById(R.id.layout_detail_offline);
		TextView textSkicentreTitle = (TextView) mRootView.findViewById(R.id.layout_detail_skicentre_title);
		TextView textSkicentrePlace = (TextView) mRootView.findViewById(R.id.layout_detail_skicentre_country);
		TextView textSkicentreSeason = (TextView) mRootView.findViewById(R.id.layout_detail_skicentre_season);
		TextView textSkicentreLocation = (TextView) mRootView.findViewById(R.id.layout_detail_skicentre_location);
		TextView textSkicentrePerex = (TextView) mRootView.findViewById(R.id.layout_detail_skicentre_perex);
		//TextView textSkiingTitle = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_title);
		TextView textSkiingSnowQuantity = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_snow_quantity);
		TextView textSkiingLiftsCount = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_lifts_count);
		TextView textSkiingDownhillsCount = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_downhills_count);
		TextView textSkiingDownhillsLength = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_downhills_length);
		TextView textSkiingCrosscountryLength = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_crosscountry_length);
		TextView textWeather1Date = (TextView) mRootView.findViewById(R.id.layout_detail_weather_1_date);
		TextView textWeather1Min = (TextView) mRootView.findViewById(R.id.layout_detail_weather_1_min);
		TextView textWeather1Max = (TextView) mRootView.findViewById(R.id.layout_detail_weather_1_max);
		TextView textWeather2Date = (TextView) mRootView.findViewById(R.id.layout_detail_weather_2_date);
		TextView textWeather2Min = (TextView) mRootView.findViewById(R.id.layout_detail_weather_2_min);
		TextView textWeather2Max = (TextView) mRootView.findViewById(R.id.layout_detail_weather_2_max);
		TextView textWeather3Date = (TextView) mRootView.findViewById(R.id.layout_detail_weather_3_date);
		TextView textWeather3Min = (TextView) mRootView.findViewById(R.id.layout_detail_weather_3_min);
		TextView textWeather3Max = (TextView) mRootView.findViewById(R.id.layout_detail_weather_3_max);
		TextView textWeather4Date = (TextView) mRootView.findViewById(R.id.layout_detail_weather_4_date);
		TextView textWeather4Min = (TextView) mRootView.findViewById(R.id.layout_detail_weather_4_min);
		TextView textWeather4Max = (TextView) mRootView.findViewById(R.id.layout_detail_weather_4_max);
		TextView textWeather5Date = (TextView) mRootView.findViewById(R.id.layout_detail_weather_5_date);
		TextView textWeather5Min = (TextView) mRootView.findViewById(R.id.layout_detail_weather_5_min);
		TextView textWeather5Max = (TextView) mRootView.findViewById(R.id.layout_detail_weather_5_max);
		TextView textWeather6Date = (TextView) mRootView.findViewById(R.id.layout_detail_weather_6_date);
		TextView textWeather6Min = (TextView) mRootView.findViewById(R.id.layout_detail_weather_6_min);
		TextView textWeather6Max = (TextView) mRootView.findViewById(R.id.layout_detail_weather_6_max);
		//TextView textPricesTitle = (TextView) mRootView.findViewById(R.id.layout_detail_prices_title);
		TextView textPricesAdults = (TextView) mRootView.findViewById(R.id.layout_detail_prices_adults);
		TextView textPricesChildren = (TextView) mRootView.findViewById(R.id.layout_detail_prices_children);
		TextView textPricesYoung = (TextView) mRootView.findViewById(R.id.layout_detail_prices_young);
		TextView textPricesSeniors = (TextView) mRootView.findViewById(R.id.layout_detail_prices_seniors);
		
		
		// reference na obrazky
		ImageView imageSkicentreOpened = (ImageView) mRootView.findViewById(R.id.layout_detail_skicentre_opened);
		ImageView imageSkicentreNightski = (ImageView) mRootView.findViewById(R.id.layout_detail_skiing_flag_nightski);
		ImageView imageSkicentreValley = (ImageView) mRootView.findViewById(R.id.layout_detail_skiing_flag_valley);
		ImageView imageSkicentreSnowpark = (ImageView) mRootView.findViewById(R.id.layout_detail_skiing_flag_snowpark);
		ImageView imageSkicentreHalfpipe = (ImageView) mRootView.findViewById(R.id.layout_detail_skiing_flag_halfpipe);
		ImageView imageWeather1 = (ImageView) mRootView.findViewById(R.id.layout_detail_weather_1_symbol);
		ImageView imageWeather2 = (ImageView) mRootView.findViewById(R.id.layout_detail_weather_2_symbol);
		ImageView imageWeather3 = (ImageView) mRootView.findViewById(R.id.layout_detail_weather_3_symbol);
		ImageView imageWeather4 = (ImageView) mRootView.findViewById(R.id.layout_detail_weather_4_symbol);
		ImageView imageWeather5 = (ImageView) mRootView.findViewById(R.id.layout_detail_weather_5_symbol);
		ImageView imageWeather6 = (ImageView) mRootView.findViewById(R.id.layout_detail_weather_6_symbol);
		ImageView imageImagesMap = (ImageView) mRootView.findViewById(R.id.layout_detail_images_url_map);
		ImageView imageImagesChart = (ImageView) mRootView.findViewById(R.id.layout_detail_images_url_chart);
		ImageView imageImagesCamera = (ImageView) mRootView.findViewById(R.id.layout_detail_images_url_camera);
		ImageView imageLinksSkimap = (ImageView) mRootView.findViewById(R.id.layout_detail_links_url_skimap);
		ImageView imageLinksHome = (ImageView) mRootView.findViewById(R.id.layout_detail_links_url_home);
		ImageView imageLinksSnowReport = (ImageView) mRootView.findViewById(R.id.layout_detail_links_url_snow_report);
		ImageView imageLinksWeatherReport = (ImageView) mRootView.findViewById(R.id.layout_detail_links_url_weather_report);
		ImageView imageLinksCamera = (ImageView) mRootView.findViewById(R.id.layout_detail_links_url_camera);
		
		
		// reference na radky tabulky
		TableRow rowWeather1 = (TableRow) mRootView.findViewById(R.id.layout_detail_weather_1);
		TableRow rowWeather2 = (TableRow) mRootView.findViewById(R.id.layout_detail_weather_2);
		TableRow rowWeather3 = (TableRow) mRootView.findViewById(R.id.layout_detail_weather_3);
		TableRow rowWeather4 = (TableRow) mRootView.findViewById(R.id.layout_detail_weather_4);
		TableRow rowWeather5 = (TableRow) mRootView.findViewById(R.id.layout_detail_weather_5);
		TableRow rowWeather6 = (TableRow) mRootView.findViewById(R.id.layout_detail_weather_6);

		
		// zpracovani offline stavu
		textOffline.setVisibility(mOffline ? View.VISIBLE : View.GONE);
		
		// zpracovani nadpisu
		String skicentreTitle = mSkicentre.getName();
		textSkicentreTitle.setText(Html.fromHtml(skicentreTitle));
		
		// zpracovani mista
		String skicentreArea = getAreaName(mSkicentre.getArea());
		String skicentreCountry = getCountryName(mSkicentre.getCountry());
		if(skicentreArea==DatabaseHelper.NULL_STRING && skicentreCountry==DatabaseHelper.NULL_STRING) textSkicentrePlace.setVisibility(View.GONE);
		else
		{
			String skicentrePlace = getString(R.string.layout_detail_skicentre_country) + " ";
			if(skicentreCountry!=DatabaseHelper.NULL_STRING) skicentrePlace += skicentreCountry;
			if(skicentreCountry!=DatabaseHelper.NULL_STRING && skicentreArea!=DatabaseHelper.NULL_STRING) skicentrePlace += ", ";
			if(skicentreArea!=DatabaseHelper.NULL_STRING) skicentrePlace += skicentreArea;
			textSkicentrePlace.setText(Html.fromHtml(skicentrePlace));
			textSkicentrePlace.setVisibility(View.VISIBLE);
		}
				
		// zpracovani sezony
		String skicentreSeasonStart = mSkicentre.getDateSeasonStartView();
		String skicentreSeasonEnd = mSkicentre.getDateSeasonEndView();
		if(skicentreSeasonStart==DatabaseHelper.NULL_STRING || skicentreSeasonEnd==DatabaseHelper.NULL_STRING) textSkicentreSeason.setVisibility(View.GONE);
		else
		{
			String skicentreSeason = getString(R.string.layout_detail_skicentre_season) + " " + skicentreSeasonStart + " - " + skicentreSeasonEnd;
			textSkicentreSeason.setText(Html.fromHtml(skicentreSeason));
			textSkicentreSeason.setVisibility(View.VISIBLE);
		}
				
		// zpracovani nadmorske vysky
		int skicentreLocationUndermost = mSkicentre.getLocationAltitudeUndermost();
		int skicentreLocationTopmost = mSkicentre.getLocationAltitudeTopmost();
		if(skicentreLocationUndermost<=0 || skicentreLocationTopmost<=0) textSkicentreLocation.setVisibility(View.GONE);
		else
		{
			String skicentreLocation = getString(R.string.layout_detail_skicentre_altitude) + " " + skicentreLocationUndermost + " " + getString(R.string.unit_m) + " - " + skicentreLocationTopmost + " " + getString(R.string.unit_m);
			textSkicentreLocation.setText(Html.fromHtml(skicentreLocation));
			textSkicentreLocation.setVisibility(View.VISIBLE);
		}
		
		// zpracovani perex
		final String skicentrePerexLong = mSkicentre.getInfoPerex();
		if(skicentrePerexLong==DatabaseHelper.NULL_STRING || 
				skicentrePerexLong.trim().contentEquals("") ||
				!(Version.getLanguage().contentEquals(Version.LANGUAGE_CS) || Version.getLanguage().contentEquals(Version.LANGUAGE_SK))
		)
		{
			textSkicentrePerex.setVisibility(View.GONE);
		}
		else
		{
			int lastSpace = skicentrePerexLong.lastIndexOf(" ", PEREX_SHORT_LENGTH);
			final String skicentrePerexShort = skicentrePerexLong.subSequence(0, lastSpace).toString().concat("…") + " " + getString(R.string.layout_detail_skicentre_perex_more);
			textSkicentrePerex.setText(Html.fromHtml(skicentrePerexShort));
			textSkicentrePerex.setVisibility(View.VISIBLE);
			textSkicentrePerex.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View view)
				{				
					if(mPerexMore)
					{
						((TextView) view).setText(Html.fromHtml(skicentrePerexLong + " " + getString(R.string.layout_detail_skicentre_perex_less)));
					}
					else
					{
						((TextView) view).setText(Html.fromHtml(skicentrePerexShort));
					}
					
					mPerexMore = !mPerexMore;
				}
			});
		}
		
		// zpracovani mnozstvi snehu
		int skiingSnowQuantityMin = mSkicentre.getSnowMin();
		int skiingSnowQuantityMax = mSkicentre.getSnowMax();
		if(skiingSnowQuantityMin<=DatabaseHelper.NULL_INT || skiingSnowQuantityMax<=DatabaseHelper.NULL_INT) textSkiingSnowQuantity.setVisibility(View.GONE);
		else
		{
			String skiingSnowQuantity = getString(R.string.layout_detail_skiing_snow_quantity) + " " + skiingSnowQuantityMin + " " + getString(R.string.unit_cm) + " - " + skiingSnowQuantityMax + " " + getString(R.string.unit_cm);
			textSkiingSnowQuantity.setText(Html.fromHtml(skiingSnowQuantity));
			textSkiingSnowQuantity.setVisibility(View.VISIBLE);
		}

		// zpracovani otevrenych lanovek
		int skiingLiftsOpened = mSkicentre.getCountLiftsOpened();
		if(skiingLiftsOpened<=DatabaseHelper.NULL_INT) textSkiingLiftsCount.setVisibility(View.GONE);
		else
		{
			String skiingLiftsCount = getString(R.string.layout_detail_skiing_lifts_count) + " " + skiingLiftsOpened;
			textSkiingLiftsCount.setText(Html.fromHtml(skiingLiftsCount));
			textSkiingLiftsCount.setVisibility(View.VISIBLE);
		}

		// zpracovani otevrenych sjezdovek
		int skiingDownhillsOpened = mSkicentre.getCountDownhillsOpened();
		if(skiingDownhillsOpened<=DatabaseHelper.NULL_INT) textSkiingDownhillsCount.setVisibility(View.GONE);
		else
		{
			String skiingDownhillsCount = getString(R.string.layout_detail_skiing_downhills_count) + " " + skiingDownhillsOpened;
			textSkiingDownhillsCount.setText(Html.fromHtml(skiingDownhillsCount));
			textSkiingDownhillsCount.setVisibility(View.VISIBLE);
		}

		// zpracovani delky sjezdovek
		int skiingDownhillsLengthTotal = mSkicentre.getLengthDownhillsTotal();
		if(skiingDownhillsLengthTotal<=DatabaseHelper.NULL_INT) textSkiingDownhillsLength.setVisibility(View.GONE);
		else
		{
			String skiingDownhillsLength = getString(R.string.layout_detail_skiing_downhills_length) + " " + skiingDownhillsLengthTotal + " " + getString(R.string.unit_km);
			textSkiingDownhillsLength.setText(Html.fromHtml(skiingDownhillsLength));
			textSkiingDownhillsLength.setVisibility(View.VISIBLE);
		}

		// zpracovani delky bezeckych trati
		int skiingCrosscountryLengthTotal = mSkicentre.getLengthCrosscountry();
		if(skiingCrosscountryLengthTotal<=DatabaseHelper.NULL_INT) textSkiingCrosscountryLength.setVisibility(View.GONE);
		else
		{
			String skiingCrosscountryLength = getString(R.string.layout_detail_skiing_crosscountry_length) + " " + skiingCrosscountryLengthTotal + " " + getString(R.string.unit_km);
			textSkiingCrosscountryLength.setText(Html.fromHtml(skiingCrosscountryLength));
			textSkiingCrosscountryLength.setVisibility(View.VISIBLE);
		}
		
		// zpracovani pocasi
		String weather1Date = mSkicentre.getWeather1DateView();
		String weather2Date = mSkicentre.getWeather2DateView();
		String weather3Date = mSkicentre.getWeather3DateView();
		String weather4Date = mSkicentre.getWeather4DateView();
		String weather5Date = mSkicentre.getWeather5DateView();
		String weather6Date = mSkicentre.getWeather6DateView();
		
		// zpracovani data pocasi a zneviditelneni v pripade ze datum neni zadano
		if(weather1Date!=DatabaseHelper.NULL_STRING)
		{
			weather1Date = weather1Date + " " + getDayName(mSkicentre.getWeather1Date().getDay());
			textWeather1Date.setText(Html.fromHtml(weather1Date));
			rowWeather1.setVisibility(View.VISIBLE);
		}
		else rowWeather1.setVisibility(View.GONE);
		if(weather2Date!=DatabaseHelper.NULL_STRING)
		{
			weather2Date = weather2Date + " " + getDayName(mSkicentre.getWeather2Date().getDay());
			textWeather2Date.setText(Html.fromHtml(weather2Date));
			rowWeather2.setVisibility(View.VISIBLE);
		}
		else rowWeather2.setVisibility(View.GONE);
		if(weather3Date!=DatabaseHelper.NULL_STRING)
		{
			weather3Date = weather3Date + " " + getDayName(mSkicentre.getWeather3Date().getDay());
			textWeather3Date.setText(Html.fromHtml(weather3Date));
			rowWeather3.setVisibility(View.VISIBLE);
		}
		else rowWeather3.setVisibility(View.GONE);
		if(weather4Date!=DatabaseHelper.NULL_STRING)
		{
			weather4Date = weather4Date + " " + getDayName(mSkicentre.getWeather4Date().getDay());
			textWeather4Date.setText(Html.fromHtml(weather4Date));
			rowWeather4.setVisibility(View.VISIBLE);
		}
		else rowWeather4.setVisibility(View.GONE);
		if(weather5Date!=DatabaseHelper.NULL_STRING)
		{
			weather5Date = weather5Date + " " + getDayName(mSkicentre.getWeather5Date().getDay());
			textWeather5Date.setText(Html.fromHtml(weather5Date));
			rowWeather5.setVisibility(View.VISIBLE);
		}
		else rowWeather5.setVisibility(View.GONE);
		if(weather6Date!=DatabaseHelper.NULL_STRING)
		{
			weather6Date = weather6Date + " " + getDayName(mSkicentre.getWeather6Date().getDay());
			textWeather6Date.setText(Html.fromHtml(weather6Date));
			rowWeather6.setVisibility(View.VISIBLE);
		}
		else rowWeather6.setVisibility(View.GONE);

		// zpracovani minimalni teploty pocasi
		int weather1Min = mSkicentre.getWeather1TemperatureMin();
		String weather1TemperatureMin = (weather1Min>0 ? "+" + weather1Min : weather1Min) + getString(R.string.unit_celsius);
		int weather2Min = mSkicentre.getWeather2TemperatureMin();
		String weather2TemperatureMin = (weather2Min>0 ? "+" + weather2Min : weather2Min) + getString(R.string.unit_celsius);
		int weather3Min = mSkicentre.getWeather3TemperatureMin();
		String weather3TemperatureMin = (weather3Min>0 ? "+" + weather3Min : weather3Min) + getString(R.string.unit_celsius);
		int weather4Min = mSkicentre.getWeather4TemperatureMin();
		String weather4TemperatureMin = (weather4Min>0 ? "+" + weather4Min : weather4Min) + getString(R.string.unit_celsius);
		int weather5Min = mSkicentre.getWeather5TemperatureMin();
		String weather5TemperatureMin = (weather5Min>0 ? "+" + weather5Min : weather5Min) + getString(R.string.unit_celsius);
		int weather6Min = mSkicentre.getWeather6TemperatureMin();
		String weather6TemperatureMin = (weather6Min>0 ? "+" + weather6Min : weather6Min) + getString(R.string.unit_celsius);
		textWeather1Min.setText(Html.fromHtml(weather1TemperatureMin));
		textWeather2Min.setText(Html.fromHtml(weather2TemperatureMin));
		textWeather3Min.setText(Html.fromHtml(weather3TemperatureMin));
		textWeather4Min.setText(Html.fromHtml(weather4TemperatureMin));
		textWeather5Min.setText(Html.fromHtml(weather5TemperatureMin));
		textWeather6Min.setText(Html.fromHtml(weather6TemperatureMin));
		
		// zpracovani maximalni teploty pocasi
		int weather1Max = mSkicentre.getWeather1TemperatureMax();
		String weather1TemperatureMax = (weather1Max>0 ? "+" + weather1Max : weather1Max) + getString(R.string.unit_celsius);
		int weather2Max = mSkicentre.getWeather2TemperatureMax();
		String weather2TemperatureMax = (weather2Max>0 ? "+" + weather2Max : weather2Max) + getString(R.string.unit_celsius);
		int weather3Max = mSkicentre.getWeather3TemperatureMax();
		String weather3TemperatureMax = (weather3Max>0 ? "+" + weather3Max : weather3Max) + getString(R.string.unit_celsius);
		int weather4Max = mSkicentre.getWeather4TemperatureMax();
		String weather4TemperatureMax = (weather4Max>0 ? "+" + weather4Max : weather4Max) + getString(R.string.unit_celsius);
		int weather5Max = mSkicentre.getWeather5TemperatureMax();
		String weather5TemperatureMax = (weather5Max>0 ? "+" + weather5Max : weather5Max) + getString(R.string.unit_celsius);
		int weather6Max = mSkicentre.getWeather6TemperatureMax();
		String weather6TemperatureMax = (weather6Max>0 ? "+" + weather6Max : weather6Max) + getString(R.string.unit_celsius);
		textWeather1Max.setText(Html.fromHtml(weather1TemperatureMax));
		textWeather2Max.setText(Html.fromHtml(weather2TemperatureMax));
		textWeather3Max.setText(Html.fromHtml(weather3TemperatureMax));
		textWeather4Max.setText(Html.fromHtml(weather4TemperatureMax));
		textWeather5Max.setText(Html.fromHtml(weather5TemperatureMax));
		textWeather6Max.setText(Html.fromHtml(weather6TemperatureMax));
		
		// zpracovani cen
		String priceCurrency = mSkicentre.getPriceCurrency();
		
		int priceAdults1 = mSkicentre.getPriceAdults1();
		int priceAdults6 = mSkicentre.getPriceAdults6();
		if(priceAdults1<=0 && priceAdults6<=0) textPricesAdults.setVisibility(View.GONE);
		else
		{
			String pricesAdults = "";
			if(priceAdults1>0) pricesAdults += getString(R.string.layout_detail_price_adults_1) + " " + priceAdults1 + " " + priceCurrency;
			if(priceAdults1>0 && priceAdults6>0) pricesAdults += "<br>";
			if(priceAdults6>0) pricesAdults += getString(R.string.layout_detail_price_adults_6) + " " + priceAdults6 + " " + priceCurrency;
			textPricesAdults.setText(Html.fromHtml(pricesAdults));
			textPricesAdults.setVisibility(View.VISIBLE);
		}
		
		int priceChildren1 = mSkicentre.getPriceChildren1();
		int priceChildren6 = mSkicentre.getPriceChildren6();
		if(priceChildren1<=0 && priceChildren6<=0) textPricesChildren.setVisibility(View.GONE);
		else
		{
			String pricesChildren = "";
			if(priceChildren1>0) pricesChildren += getString(R.string.layout_detail_price_children_1) + " " + priceChildren1 + " " + priceCurrency;
			if(priceChildren1>0 && priceChildren6>0) pricesChildren += "<br>";
			if(priceChildren6>0) pricesChildren += getString(R.string.layout_detail_price_children_6) + " " + priceChildren6 + " " + priceCurrency;
			textPricesChildren.setText(Html.fromHtml(pricesChildren));
			textPricesChildren.setVisibility(View.VISIBLE);
		}

		int priceYoung1 = mSkicentre.getPriceYoung1();
		int priceYoung6 = mSkicentre.getPriceYoung6();
		if(priceYoung1<=0 && priceYoung6<=0) textPricesYoung.setVisibility(View.GONE);
		else
		{
			String pricesYoung = "";
			if(priceYoung1>0) pricesYoung += getString(R.string.layout_detail_price_young_1) + " " + priceYoung1 + " " + priceCurrency;
			if(priceYoung1>0 && priceYoung6>0) pricesYoung += "<br>";
			if(priceYoung6>0) pricesYoung += getString(R.string.layout_detail_price_young_6) + " " + priceYoung6 + " " + priceCurrency;
			textPricesYoung.setText(Html.fromHtml(pricesYoung));
			textPricesYoung.setVisibility(View.VISIBLE);
		}

		int priceSeniors1 = mSkicentre.getPriceSeniors1();
		int priceSeniors6 = mSkicentre.getPriceSeniors6();
		if(priceSeniors1<=0 && priceSeniors6<=0) textPricesSeniors.setVisibility(View.GONE);
		else
		{
			String pricesSeniors = "";
			if(priceSeniors1>0) pricesSeniors += getString(R.string.layout_detail_price_seniors_1) + " " + priceSeniors1 + " " + priceCurrency;
			if(priceSeniors1>0 && priceSeniors6>0) pricesSeniors += "<br>";
			if(priceSeniors6>0) pricesSeniors += getString(R.string.layout_detail_price_seniors_6) + " " + priceSeniors6 + " " + priceCurrency;
			textPricesSeniors.setText(Html.fromHtml(pricesSeniors));
			textPricesSeniors.setVisibility(View.VISIBLE);
		}

		
		// nastaveni obrazku
		imageSkicentreOpened.setImageResource(mSkicentre.isFlagOpened() ? R.drawable.presence_online : R.drawable.presence_busy);
		
		imageSkicentreNightski.setVisibility(mSkicentre.isFlagNightski() ? View.VISIBLE : View.GONE);
		imageSkicentreValley.setVisibility(mSkicentre.isFlagValley() ? View.VISIBLE : View.GONE);
		imageSkicentreSnowpark.setVisibility(mSkicentre.isFlagSnowpark() ? View.VISIBLE : View.GONE);
		imageSkicentreHalfpipe.setVisibility(mSkicentre.isFlagHalfpipe() ? View.VISIBLE : View.GONE);
		
		setDrawable(imageWeather1, mSkicentre.getWeather1Symbol());
		setDrawable(imageWeather2, mSkicentre.getWeather2Symbol());
		setDrawable(imageWeather3, mSkicentre.getWeather3Symbol());
		setDrawable(imageWeather4, mSkicentre.getWeather4Symbol());
		setDrawable(imageWeather5, mSkicentre.getWeather5Symbol());
		setDrawable(imageWeather6, mSkicentre.getWeather6Symbol());
		
		imageImagesMap.setVisibility((mSkicentre.getUrlImgMap()==DatabaseHelper.NULL_STRING || mSkicentre.getUrlImgMap().trim().contentEquals("")) ? View.GONE : View.VISIBLE);
		imageImagesChart.setVisibility((mSkicentre.getUrlImgMeteogram()==DatabaseHelper.NULL_STRING || mSkicentre.getUrlImgMeteogram().trim().contentEquals("")) ? View.GONE : View.VISIBLE);
		imageImagesCamera.setVisibility((mSkicentre.getUrlImgWebcam()==DatabaseHelper.NULL_STRING || mSkicentre.getUrlImgWebcam().trim().contentEquals("")) ? View.GONE : View.VISIBLE);
		imageLinksSkimap.setVisibility((mSkicentre.getUrlSkimap()==DatabaseHelper.NULL_STRING || mSkicentre.getUrlSkimap().trim().contentEquals("")) ? View.GONE : View.VISIBLE);
		imageLinksHome.setVisibility((mSkicentre.getUrlHomepage()==DatabaseHelper.NULL_STRING || mSkicentre.getUrlHomepage().trim().contentEquals("")) ? View.GONE : View.VISIBLE);
		imageLinksSnowReport.setVisibility((mSkicentre.getUrlSnowReport()==DatabaseHelper.NULL_STRING || mSkicentre.getUrlSnowReport().trim().contentEquals("")) ? View.GONE : View.VISIBLE);
		imageLinksWeatherReport.setVisibility((mSkicentre.getUrlWeatherReport()==DatabaseHelper.NULL_STRING || mSkicentre.getUrlWeatherReport().trim().contentEquals("")) ? View.GONE : View.VISIBLE);
		imageLinksCamera.setVisibility((mSkicentre.getUrlWebcams()==DatabaseHelper.NULL_STRING || mSkicentre.getUrlWebcams().trim().contentEquals("")) ? View.GONE : View.VISIBLE);
		
		
		// onclick napovedy
		imageSkicentreOpened.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), (mSkicentre.isFlagOpened() ? R.string.toast_help_opened : R.string.toast_help_closed), Toast.LENGTH_SHORT).show();
			}
		});
		imageSkicentreNightski.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), R.string.toast_help_nightski, Toast.LENGTH_SHORT).show();
			}
		});
		imageSkicentreValley.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), R.string.toast_help_valley, Toast.LENGTH_SHORT).show();
			}
		});
		imageSkicentreSnowpark.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), R.string.toast_help_snowpark, Toast.LENGTH_SHORT).show();
			}
		});
		imageSkicentreHalfpipe.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), R.string.toast_help_halfpipe, Toast.LENGTH_SHORT).show();
			}
		});
		imageImagesMap.setOnLongClickListener(new OnLongClickListener() 
		{
			@Override
			public boolean onLongClick(View v) 
			{
				Toast.makeText(getActivity(), R.string.toast_help_url_img_map, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		imageImagesChart.setOnLongClickListener(new OnLongClickListener() 
		{
			@Override
			public boolean onLongClick(View v) 
			{
				Toast.makeText(getActivity(), R.string.toast_help_url_img_meteogram, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		imageImagesCamera.setOnLongClickListener(new OnLongClickListener() 
		{
			@Override
			public boolean onLongClick(View v) 
			{
				Toast.makeText(getActivity(), R.string.toast_help_url_img_webcam, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		imageLinksSkimap.setOnLongClickListener(new OnLongClickListener() 
		{
			@Override
			public boolean onLongClick(View v) 
			{
				Toast.makeText(getActivity(), R.string.toast_help_url_skimap, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		imageLinksHome.setOnLongClickListener(new OnLongClickListener() 
		{
			@Override
			public boolean onLongClick(View v) 
			{
				Toast.makeText(getActivity(), R.string.toast_help_url_homepage, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		imageLinksSnowReport.setOnLongClickListener(new OnLongClickListener() 
		{
			@Override
			public boolean onLongClick(View v) 
			{
				Toast.makeText(getActivity(), R.string.toast_help_url_snow_report, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		imageLinksWeatherReport.setOnLongClickListener(new OnLongClickListener() 
		{
			@Override
			public boolean onLongClick(View v) 
			{
				Toast.makeText(getActivity(), R.string.toast_help_url_weather_report, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		imageLinksCamera.setOnLongClickListener(new OnLongClickListener() 
		{
			@Override
			public boolean onLongClick(View v) 
			{
				Toast.makeText(getActivity(), R.string.toast_help_url_webcams, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		
		
		// onclick web
		imageImagesMap.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_LINK_IMAGE, Localytics.VALUE_LINK_IMAGE_MAP); // Localytics atribut
				mLocalyticsSession.tagEvent(Localytics.TAG_LINK, localyticsValues); // Localytics
				
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlImgMap()));
				startActivity(viewIntent);
			}
		});
		imageImagesChart.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_LINK_IMAGE, Localytics.VALUE_LINK_IMAGE_METEOGRAM); // Localytics atribut
				mLocalyticsSession.tagEvent(Localytics.TAG_LINK, localyticsValues); // Localytics
				
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlImgMeteogram()));
				startActivity(viewIntent);
			}
		});
		imageImagesCamera.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_LINK_IMAGE, Localytics.VALUE_LINK_IMAGE_WEBCAM); // Localytics atribut
				mLocalyticsSession.tagEvent(Localytics.TAG_LINK, localyticsValues); // Localytics
				
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlImgWebcam()));
				startActivity(viewIntent);
			}
		});
		imageLinksSkimap.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_LINK_WEB, Localytics.VALUE_LINK_WEB_SKIMAP); // Localytics atribut
				mLocalyticsSession.tagEvent(Localytics.TAG_LINK, localyticsValues); // Localytics
				
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlSkimap()));
				startActivity(viewIntent);
			}
		});
		imageLinksHome.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_LINK_WEB, Localytics.VALUE_LINK_WEB_HOME); // Localytics atribut
				mLocalyticsSession.tagEvent(Localytics.TAG_LINK, localyticsValues); // Localytics
				
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlHomepage()));
				startActivity(viewIntent);
			}
		});
		imageLinksSnowReport.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_LINK_WEB, Localytics.VALUE_LINK_WEB_SNOW); // Localytics atribut
				mLocalyticsSession.tagEvent(Localytics.TAG_LINK, localyticsValues); // Localytics
				
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlSnowReport()));
				startActivity(viewIntent);
			}
		});
		imageLinksWeatherReport.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_LINK_WEB, Localytics.VALUE_LINK_WEB_WEATHER); // Localytics atribut
				mLocalyticsSession.tagEvent(Localytics.TAG_LINK, localyticsValues); // Localytics
				
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlWeatherReport()));
				startActivity(viewIntent);
			}
		});
		imageLinksCamera.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
		        localyticsValues.put(Localytics.ATTR_LINK_WEB, Localytics.VALUE_LINK_WEB_WEBCAMS); // Localytics atribut
				mLocalyticsSession.tagEvent(Localytics.TAG_LINK, localyticsValues); // Localytics
				
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlWebcams()));
				startActivity(viewIntent);
			}
		});
	}
	
	
	private String getAreaName(int id)
	{
		String area = DatabaseHelper.NULL_STRING;
		try
		{
			area = mDatabase.getArea(id).getName();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return area;
	}
	
	
	private String getCountryName(int id)
	{
		String country = DatabaseHelper.NULL_STRING;
		try
		{
			country = mDatabase.getCountry(id).getName();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return country;
	}
	
	
	private String getDayName(int id)
	{
		switch(id)
		{
			case 0:
				return getResources().getString(R.string.day_sun);
			case 1:
				return getResources().getString(R.string.day_mon);
			case 2:
				return getResources().getString(R.string.day_tue);
			case 3:
				return getResources().getString(R.string.day_wed);
			case 4:
				return getResources().getString(R.string.day_thu);
			case 5:
				return getResources().getString(R.string.day_fri);
			case 6:
				return getResources().getString(R.string.day_sat);
			default:
				return "";
		}
	}
	
	
	private void setDrawable(ImageView view, Weather.Type type)
	{
		if(type == Weather.Type.SUN_CLEAR_SKY) view.setImageResource(R.drawable.weather_sun_clear_sky);
		else if(type == Weather.Type.FAIR) view.setImageResource(R.drawable.weather_fair);
		else if(type == Weather.Type.PARTLY_CLOUDY) view.setImageResource(R.drawable.weather_partly_cloudy);
		else if(type == Weather.Type.CLOUDY) view.setImageResource(R.drawable.weather_cloudy);
		else if(type == Weather.Type.FOG) view.setImageResource(R.drawable.weather_fog);
		else if(type == Weather.Type.RAIN) view.setImageResource(R.drawable.weather_rain);
		else if(type == Weather.Type.RAIN_SHOWERS) view.setImageResource(R.drawable.weather_rain_showers);
		else if(type == Weather.Type.HEAVY_RAIN) view.setImageResource(R.drawable.weather_heavy_rain);
		else if(type == Weather.Type.RAIN_AND_THUNDER) view.setImageResource(R.drawable.weather_rain_and_thunder);
		else if(type == Weather.Type.RAIN_SHOWERS_WITH_THUNDER) view.setImageResource(R.drawable.weather_rain_showers_with_thunder);
		else if(type == Weather.Type.SLEET) view.setImageResource(R.drawable.weather_sleet);
		else if(type == Weather.Type.SLEET_SHOWERS) view.setImageResource(R.drawable.weather_sleet_showers);
		else if(type == Weather.Type.SNOW) view.setImageResource(R.drawable.weather_snow);
		else if(type == Weather.Type.SNOW_SHOWERS) view.setImageResource(R.drawable.weather_snow_showers);
		else if(type == Weather.Type.SNOW_AND_THUNDER) view.setImageResource(R.drawable.weather_snow_and_thunder);
	}
	
	
	private void setExtras(Bundle extras, Bundle savedInstanceState)
	{
		// dual view
		if(extras != null && extras.containsKey(DUAL_VIEW))
		{
			mDualView = extras.getBoolean(DUAL_VIEW);
		}
		else
		{
			mDualView = true;
		}
        
        // nastaveni id detailu
 		int id; 		
 		// nahrani id z bundle aktivity
 		if(extras != null && extras.containsKey(ITEM_ID))
 		{
 			id = extras.getInt(ITEM_ID);
 		}		
 		// nahrani posledniho pouziteho id
 		else if (savedInstanceState != null && savedInstanceState.containsKey(ITEM_ID))
        {
 			id = savedInstanceState.getInt(ITEM_ID, EMPTY_ID);
        }		
 		// vychozi id
 		else
 		{
 			id = EMPTY_ID;
 		}
 		
 		mItemId = id;
	}
	
	
	private void searchNearestSkicentre()
	{
		// TODO
		Toast.makeText(getActivity(), "NEAREST SKICENTRE", Toast.LENGTH_SHORT).show();
		// mItemId = id;
	}
}
