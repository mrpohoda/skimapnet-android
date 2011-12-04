package net.skimap.fragments;

import net.skimap.R;
import net.skimap.activities.MapActivity;
import net.skimap.database.DatabaseHelper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DetailFragment extends Fragment
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
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // nastaveni extras
        Bundle extras = getSupportActivity().getIntent().getExtras();
        setExtras(extras, savedInstanceState);
        
        // nastaveni nejblizsiho skicentra
        if(mItemId == EMPTY_ID) searchNearestSkicentre();
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		// nastaveni view
		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.layout_detail, container, false);
		setView();
		return mRootView;
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
	    		// TODO
	    		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
	    		shareIntent.setType("text/plain");
	    		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Pec pod Snìžkou - Ski-map.net");
	    		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Skicentrum Pec pod Snìžkou: http://www.lorem.cz/" + mItemId);
	    		startActivity(Intent.createChooser(shareIntent, "Sdílet skicentrum"));
				return true;
				
	    	case R.id.ab_button_favourite:
	    		Toast.makeText(getActivity(), "FAV", Toast.LENGTH_SHORT).show();
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
	
	
	public void searchNearestSkicentre()
	{
		// TODO
		Toast.makeText(getActivity(), "NEAREST SKICENTRE", Toast.LENGTH_SHORT).show();
		// mItemId = id;
	}
	
	
	public void setView()
	{
		setView(mItemId);
	}
	
	
	public void setView(int id)
	{
		// TODO
		// TODO: formatovani textu: http://stackoverflow.com/questions/1529068/is-it-possible-to-have-multiple-styles-inside-a-textview
		// TODO: pridat toasty s napovedou na flagy
		// TODO: osetrovat null hodnoty - DatabaseHelper.NULL_STRING apod., pripadne schovat dany widget
		mItemId = id;
		mPerexMore = false;
		
		// zpracovani dat
		String perexLong = "Tradièní horská vesnièka Adelboden leží na úpatí rozsáhlého areálu prostírajícího se na bezmála dvoutisícových oblých høebenech, jimž kulisu tvoøí tøítisícové skalnaté vrcholky Bernských Alp. Na opaèné stranì centrálního lyžaøského høebene leží v údolí støedisko Lenk. Sjezdovky potìší „støední tøídu“ i milovníka safari, nìkterá zákoutí pak i nároènìjšího jezdce èi ménì dobrodružného freeridera. Skipas pokrývá i nìkolik menších areálù v okolí dostupných skibusem.";
		int lastSpace = perexLong.lastIndexOf(" ", PEREX_SHORT_LENGTH);
		String perexShort = perexLong.subSequence(0, lastSpace).toString().concat("…");

		// reference na textove pole
		TextView textSkicentreTitle = (TextView) mRootView.findViewById(R.id.layout_detail_skicentre_title);
		TextView textSkicentreCountry = (TextView) mRootView.findViewById(R.id.layout_detail_skicentre_country);
		TextView textSkicentreSeason = (TextView) mRootView.findViewById(R.id.layout_detail_skicentre_season);
		TextView textSkicentreLocation = (TextView) mRootView.findViewById(R.id.layout_detail_skicentre_location);
		TextView textSkicentrePerex = (TextView) mRootView.findViewById(R.id.layout_detail_skicentre_perex);
		
		TextView textSkiingTitle = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_title);
		TextView textSkiingSnowQuantity = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_snow_quantity);
		TextView textSkiingLiftsCount = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_lifts_count);
		TextView textSkiingDownhillsCount = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_downhills_count);
		TextView textSkiingDownhillsLength = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_downhills_length);
		TextView textSkiingCrosscountryLength = (TextView) mRootView.findViewById(R.id.layout_detail_skiing_crosscountry_length);
		
		TextView textPricesTitle = (TextView) mRootView.findViewById(R.id.layout_detail_prices_title);
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
		
		// nastaveni textu
		textSkicentreSeason.setText(Html.fromHtml("<b>Sezóna:</b> 17.12.2011 - 24.4.2012"));
		textSkicentreLocation.setText(Html.fromHtml("<b>Nadmoøská výška:</b> 1075 m - 2357 m"));
		textSkicentrePerex.setText(Html.fromHtml(perexShort + " <u>more</u>"));
		
		textSkiingSnowQuantity.setText(Html.fromHtml("<b>Množství snìhu:</b> 0 cm - 60 cm"));
		textSkiingLiftsCount.setText(Html.fromHtml("<b>Otevøené lanovky:</b> 12"));
		textSkiingDownhillsCount.setText(Html.fromHtml("<b>Otevøené sjezdovky:</b> 16"));
		textSkiingDownhillsLength.setText(Html.fromHtml("<b>Délka sjezdovek:</b> 54 km"));
		textSkiingCrosscountryLength.setText(Html.fromHtml("<b>Délka bìžeckých tratí:</b> 104 km"));
		
		textPricesAdults.setText(Html.fromHtml("<b>Dospìlí 1 den:</b> 34 EUR<br><b>Dospìlí 6 dní:</b> 151 EUR"));
		textPricesChildren.setText(Html.fromHtml("<b>Dìti 1 den:</b> 14 EUR<br><b>Dìti 6 dní:</b> 131 EUR"));
		textPricesYoung.setText(Html.fromHtml("<b>Mládež 1 den:</b> 24 EUR<br><b>Mládež 6 dní:</b> 141 EUR"));
		textPricesSeniors.setText(Html.fromHtml("<b>Senioøi 1 den:</b> 24 EUR<br><b>Senioøi 6 dní:</b> 131 EUR"));
		
		// onclick
		textSkicentrePerex.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View view)
			{
				String perexLong = "Tradièní horská vesnièka Adelboden leží na úpatí rozsáhlého areálu prostírajícího se na bezmála dvoutisícových oblých høebenech, jimž kulisu tvoøí tøítisícové skalnaté vrcholky Bernských Alp. Na opaèné stranì centrálního lyžaøského høebene leží v údolí støedisko Lenk. Sjezdovky potìší „støední tøídu“ i milovníka safari, nìkterá zákoutí pak i nároènìjšího jezdce èi ménì dobrodružného freeridera. Skipas pokrývá i nìkolik menších areálù v okolí dostupných skibusem.";
				
				if(mPerexMore)
				{
					((TextView) view).setText(Html.fromHtml(perexLong + " <u>less</u>"));
				}
				else
				{
					int lastSpace = perexLong.lastIndexOf(" ", PEREX_SHORT_LENGTH);
					String perexShort = perexLong.subSequence(0, lastSpace).toString().concat("…");
					((TextView) view).setText(Html.fromHtml(perexShort + " <u>more</u>"));
				}
				
				mPerexMore = !mPerexMore;
			}
		});
		
		imageSkicentreOpened.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), "Skicentrum v provozu", Toast.LENGTH_SHORT).show();
			}
		});
		
		imageSkicentreNightski.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), "Noèní lyžování", Toast.LENGTH_SHORT).show();
			}
		});
		
		imageSkicentreValley.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), "Sjezd do údolí", Toast.LENGTH_SHORT).show();
			}
		});
		
		imageSkicentreSnowpark.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), "Snowpark", Toast.LENGTH_SHORT).show();
			}
		});
		
		imageSkicentreHalfpipe.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getActivity(), "U-rampa", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
