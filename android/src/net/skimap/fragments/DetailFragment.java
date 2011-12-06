package net.skimap.fragments;

import net.skimap.R;
import net.skimap.activities.MapActivity;
import net.skimap.activities.SkimapApplication;
import net.skimap.data.SkicentreLong;
import net.skimap.data.Weather;
import net.skimap.database.Database;
import net.skimap.network.Synchronization;
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
import android.widget.TextView;
import android.widget.Toast;

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
	private SkicentreLong mSkicentre;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // nastaveni extras
        Bundle extras = getSupportActivity().getIntent().getExtras();
        setExtras(extras, savedInstanceState);
        
        // nastaveni nejblizsiho skicentra
        if(mItemId == EMPTY_ID) searchNearestSkicentre();
        
        // synchronizace
        Synchronization synchro = new Synchronization((SkimapApplication) getSupportActivity().getApplicationContext());
        synchro.trySynchronizeLongData();
        
        // nacteni dat z databaze
        refreshData(mItemId);
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		// nastaveni view
		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.layout_detail, container, false);
		//setView();
		return mRootView;
	}
	
	
	@Override
    public void onResume()
	{
        super.onResume();

        // naslouchani synchronizace
        ((SkimapApplication) getSupportActivity().getApplicationContext()).setSynchroListener(this);
        
        // aktualizace stavu progress baru
    	boolean synchro = ((SkimapApplication) getSupportActivity().getApplicationContext()).isSynchro();
    	getSupportActivity().setProgressBarIndeterminateVisibility(synchro ? Boolean.TRUE : Boolean.FALSE);
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
    	// nastaveni chovani tlacitek
    	switch (item.getItemId()) 
    	{
	    	case R.id.ab_button_share:
	    		String url = mSkicentre.getUrlSkimap()!=null ? mSkicentre.getUrlSkimap() : getString(R.string.layout_detail_share_url_full);
	    		String subject = mSkicentre.getName() + " - " + getString(R.string.layout_detail_share_url);
	    		String text = getString(R.string.layout_detail_share_skicentre) + " " + mSkicentre.getName() + ": " + url;
	    		
	    		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
	    		shareIntent.setType("text/plain");
	    		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
	    		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);	
	    		startActivity(Intent.createChooser(shareIntent, getString(R.string.layout_detail_share)));
				return true;
				
	    	case R.id.ab_button_favourite:
	    		Toast.makeText(getActivity(), "FAV", Toast.LENGTH_SHORT).show();
				return true;
				
	    	case R.id.ab_button_preferences:
	    		Toast.makeText(getActivity(), "PREFERENCES", Toast.LENGTH_SHORT).show();
				return true;
				
	    	case R.id.ab_button_map:	
	    		Intent mapIntent = new Intent();
	    		mapIntent.setClass(getActivity(), MapActivity.class);
	    		mapIntent.putExtra(MapFragment.ITEM_ID, mItemId);
		        startActivity(mapIntent);
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
		
		// start
		Toast.makeText(getActivity(), "SYNCHRO START", Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onSynchroStop()
	{
		// vypnuti progress baru
		getSupportActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
		
		// hotovo
		Toast.makeText(getActivity(), "SYNCHRO DONE", Toast.LENGTH_SHORT).show();
		
		// aktualizace view
		refreshData(mItemId);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void refreshData(final int id)
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
		// otevreni databaze
		Database db = new Database(getActivity());
		db.open(false);
		
		// promazani objektu
		mSkicentre = null;
		
		// nacteni dat do pole
		mSkicentre = db.getSkicentre(10); // TODO
		db.close();
	}
	
	
	private void setView()
	{
		// TODO
		// TODO: formatovani textu: http://stackoverflow.com/questions/1529068/is-it-possible-to-have-multiple-styles-inside-a-textview
		// TODO: pridat toasty s napovedou na flagy
		// TODO: osetrovat null hodnoty - DatabaseHelper.NULL_STRING apod., pripadne schovat dany widget
		mPerexMore = false;
		
		// reference na textove pole
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

		
		// zpracovani nadpisu
		String skicentreTitle = mSkicentre.getName();
		
		// zpracovani nadpisu
		String skicentreArea = getAreaName(mSkicentre.getArea());
		String skicentreCountry = getCountryName(mSkicentre.getCountry());
		String skicentrePlace = skicentreCountry + ", " + skicentreArea;
				
		// zpracovani sezony
		String skicentreSeasonStart = mSkicentre.getDateSeasonStartView();
		String skicentreSeasonEnd = mSkicentre.getDateSeasonEndView();
		String skicentreSeason = getString(R.string.layout_detail_skicentre_season) + " " + skicentreSeasonStart + " - " + skicentreSeasonEnd;
				
		// zpracovani nadmorske vysky
		int skicentreLocationUndermost = mSkicentre.getLocationAltitudeUndermost();
		int skicentreLocationTopmost = mSkicentre.getLocationAltitudeTopmost();
		String skicentreLocation = getString(R.string.layout_detail_skicentre_altitude) + " " + skicentreLocationUndermost + " " + getString(R.string.unit_m) + " - " + skicentreLocationTopmost + " " + getString(R.string.unit_m);
		
		// zpracovani perex
		final String skicentrePerexLong = mSkicentre.getInfoPerex();
		int lastSpace = skicentrePerexLong.lastIndexOf(" ", PEREX_SHORT_LENGTH);
		final String skicentrePerexShort = skicentrePerexLong.subSequence(0, lastSpace).toString().concat("…") + " " + getString(R.string.layout_detail_skicentre_perex_more);
		
		// zpracovani mnozstvi snehu
		int skiingSnowQuantityMin = mSkicentre.getSnowMin();
		int skiingSnowQuantityMax = mSkicentre.getSnowMax();
		String skiingSnowQuantity = getString(R.string.layout_detail_skiing_snow_quantity) + " " + skiingSnowQuantityMin + " " + getString(R.string.unit_cm) + " - " + skiingSnowQuantityMax + " " + getString(R.string.unit_cm);
				
		// zpracovani otevrenych lanovek
		int skiingLiftsOpened = mSkicentre.getCountLiftsOpened();
		String skiingLiftsCount = getString(R.string.layout_detail_skiing_lifts_count) + " " + skiingLiftsOpened;
				
		// zpracovani otevrenych sjezdovek
		int skiingDownhillsOpened = mSkicentre.getCountDownhillsOpened();
		String skiingDownhillsCount = getString(R.string.layout_detail_skiing_downhills_count) + " " + skiingDownhillsOpened;
		
		// zpracovani delky sjezdovek
		int skiingDownhillsLengthTotal = mSkicentre.getLengthDownhillsTotal();
		String skiingDownhillsLength = getString(R.string.layout_detail_skiing_downhills_length) + " " + skiingDownhillsLengthTotal + " " + getString(R.string.unit_km);
				
		// zpracovani delky bezeckych trati
		int skiingCrosscountryLengthTotal = mSkicentre.getLengthCrosscountry();
		String skiingCrosscountryLength = getString(R.string.layout_detail_skiing_crosscountry_length) + " " + skiingCrosscountryLengthTotal + " " + getString(R.string.unit_km);
		
		// zpracovani data pocasi
		String weather1Date = mSkicentre.getWeather1DateView() + " " + getDayName(mSkicentre.getWeather1Date().getDay());
		String weather2Date = mSkicentre.getWeather2DateView() + " " + getDayName(mSkicentre.getWeather2Date().getDay());
		String weather3Date = mSkicentre.getWeather3DateView() + " " + getDayName(mSkicentre.getWeather3Date().getDay());
		String weather4Date = mSkicentre.getWeather4DateView() + " " + getDayName(mSkicentre.getWeather4Date().getDay());
		String weather5Date = mSkicentre.getWeather5DateView() + " " + getDayName(mSkicentre.getWeather5Date().getDay());
		String weather6Date = mSkicentre.getWeather6DateView() + " " + getDayName(mSkicentre.getWeather6Date().getDay());
		
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
		
		// zpracovani cen
		int priceAdults1 = mSkicentre.getPriceAdults1();
		int priceAdults6 = mSkicentre.getPriceAdults6();
		int priceChildren1 = mSkicentre.getPriceChildren1();
		int priceChildren6 = mSkicentre.getPriceChildren6();
		int priceYoung1 = mSkicentre.getPriceYoung1();
		int priceYoung6 = mSkicentre.getPriceYoung6();
		int priceSeniors1 = mSkicentre.getPriceSeniors1();
		int priceSeniors6 = mSkicentre.getPriceSeniors6();
		String priceCurrency = mSkicentre.getPriceCurrency();
		String pricesAdults = getString(R.string.layout_detail_price_adults_1) + " " + priceAdults1 + " " + priceCurrency + "<br>" + getString(R.string.layout_detail_price_adults_6) + " " + priceAdults6 + " " + priceCurrency;
		String pricesChildren = getString(R.string.layout_detail_price_children_1) + " " + priceChildren1 + " " + priceCurrency + "<br>" + getString(R.string.layout_detail_price_children_6) + " " + priceChildren6 + " " + priceCurrency;
		String pricesYoung = getString(R.string.layout_detail_price_young_1) + " " + priceYoung1 + " " + priceCurrency + "<br>" + getString(R.string.layout_detail_price_young_6) + " " + priceYoung6 + " " + priceCurrency;
		String pricesSeniors = getString(R.string.layout_detail_price_seniors_1) + " " + priceSeniors1 + " " + priceCurrency + "<br>" + getString(R.string.layout_detail_price_seniors_6) + " " + priceSeniors6 + " " + priceCurrency;
		
		
		// nastaveni textu
		textSkicentreTitle.setText(Html.fromHtml(skicentreTitle));
		textSkicentrePlace.setText(Html.fromHtml(skicentrePlace));
		textSkicentreSeason.setText(Html.fromHtml(skicentreSeason));
		textSkicentreLocation.setText(Html.fromHtml(skicentreLocation));
		textSkicentrePerex.setText(Html.fromHtml(skicentrePerexShort));
		textSkiingSnowQuantity.setText(Html.fromHtml(skiingSnowQuantity));
		textSkiingLiftsCount.setText(Html.fromHtml(skiingLiftsCount));
		textSkiingDownhillsCount.setText(Html.fromHtml(skiingDownhillsCount));
		textSkiingDownhillsLength.setText(Html.fromHtml(skiingDownhillsLength));
		textSkiingCrosscountryLength.setText(Html.fromHtml(skiingCrosscountryLength));
		textWeather1Date.setText(Html.fromHtml(weather1Date));
		textWeather2Date.setText(Html.fromHtml(weather2Date));
		textWeather3Date.setText(Html.fromHtml(weather3Date));
		textWeather4Date.setText(Html.fromHtml(weather4Date));
		textWeather5Date.setText(Html.fromHtml(weather5Date));
		textWeather6Date.setText(Html.fromHtml(weather6Date));
		textWeather1Min.setText(Html.fromHtml(weather1TemperatureMin));
		textWeather2Min.setText(Html.fromHtml(weather2TemperatureMin));
		textWeather3Min.setText(Html.fromHtml(weather3TemperatureMin));
		textWeather4Min.setText(Html.fromHtml(weather4TemperatureMin));
		textWeather5Min.setText(Html.fromHtml(weather5TemperatureMin));
		textWeather6Min.setText(Html.fromHtml(weather6TemperatureMin));
		textWeather1Max.setText(Html.fromHtml(weather1TemperatureMax));
		textWeather2Max.setText(Html.fromHtml(weather2TemperatureMax));
		textWeather3Max.setText(Html.fromHtml(weather3TemperatureMax));
		textWeather4Max.setText(Html.fromHtml(weather4TemperatureMax));
		textWeather5Max.setText(Html.fromHtml(weather5TemperatureMax));
		textWeather6Max.setText(Html.fromHtml(weather6TemperatureMax));
		textPricesAdults.setText(Html.fromHtml(pricesAdults));
		textPricesChildren.setText(Html.fromHtml(pricesChildren));
		textPricesYoung.setText(Html.fromHtml(pricesYoung));
		textPricesSeniors.setText(Html.fromHtml(pricesSeniors));
		
		
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
		
		imageImagesMap.setVisibility(true ? View.VISIBLE : View.GONE); // TODO
		imageImagesChart.setVisibility(true ? View.VISIBLE : View.GONE);
		imageImagesCamera.setVisibility(true ? View.VISIBLE : View.GONE);
		imageLinksSkimap.setVisibility(true ? View.VISIBLE : View.GONE);
		imageLinksHome.setVisibility(true ? View.VISIBLE : View.GONE);
		imageLinksSnowReport.setVisibility(true ? View.VISIBLE : View.GONE);
		imageLinksWeatherReport.setVisibility(true ? View.VISIBLE : View.GONE);
		imageLinksCamera.setVisibility(true ? View.VISIBLE : View.GONE);
		
		
		// onclick napovedy
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
		imageSkicentreOpened.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), R.string.toast_help_opened, Toast.LENGTH_SHORT).show();
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
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlImgMap()));
				startActivity(viewIntent);
			}
		});
		imageImagesChart.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlImgMeteogram()));
				startActivity(viewIntent);
			}
		});
		imageImagesCamera.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlImgWebcam()));
				startActivity(viewIntent);
			}
		});
		imageLinksSkimap.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlSkimap()));
				startActivity(viewIntent);
			}
		});
		imageLinksHome.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlHomepage()));
				startActivity(viewIntent);
			}
		});
		imageLinksSnowReport.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlSnowReport()));
				startActivity(viewIntent);
			}
		});
		imageLinksWeatherReport.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlWeatherReport()));
				startActivity(viewIntent);
			}
		});
		imageLinksCamera.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(mSkicentre.getUrlWebcams()));
				startActivity(viewIntent);
			}
		});
	}
	
	
	private String getAreaName(int id)
	{
		// otevreni databaze
		Database db = new Database(getActivity());
		db.open(false);
		String area = db.getArea(id).getName();
		db.close();
		return area;
	}
	
	
	private String getCountryName(int id)
	{
		// otevreni databaze
		Database db = new Database(getActivity());
		db.open(false);
		String country = db.getCountry(id).getName();
		db.close();
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
