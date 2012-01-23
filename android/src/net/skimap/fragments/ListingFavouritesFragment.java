package net.skimap.fragments;

import net.skimap.R;
import net.skimap.adapters.ListingAdapter;
import net.skimap.database.Database;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ListingFavouritesFragment extends ListingFragment
{
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        LAYOUT_LISTING = R.layout.layout_listing_favourites;
    	ID_LAYOUT_LISTING_LISTVIEW = R.id.layout_listing_favourites_listview;
    	ID_LAYOUT_LISTING_INFOBOX = R.id.layout_listing_favourites_infobox;
    }
	
	
	protected void loadSkicentresByKeyword(final String keyword)
	{	
		// promazani pole
		if(mSkicentreList != null) 
		{
			mSkicentreList.clear();
			mSkicentreList = null;
		}
		
		// nacteni dat do pole
		try
		{
			if(mDatabase.isOpen()) mSkicentreList = mDatabase.getFavouriteSkicentresByKeyword(keyword, Database.Sort.NAME);
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
			return;
		}
	}


	protected void loadAllSkicentres()
	{	
		// promazani pole
		if(mSkicentreList != null) 
		{
			mSkicentreList.clear();
			mSkicentreList = null;
		}
		
		// nacteni dat do pole
		try
		{
			if(mDatabase.isOpen()) mSkicentreList = mDatabase.getFavouriteSkicentres(Database.Sort.NAME);
		}
		catch(IllegalStateException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
	
	protected void setView(final String keyword)
	{
		if(mRootView==null || mSkicentreList==null) return;
		
		// prazdny seznam
		TextView emptyView = (TextView) mRootView.findViewById(R.id.layout_listing_favourites_empty);
		if(mSkicentreList.size()<1 && keyword==null) emptyView.setVisibility(View.VISIBLE);
		else emptyView.setVisibility(View.GONE);
		
		// seznam skicenter
		ListView listView = (ListView) mRootView.findViewById(ID_LAYOUT_LISTING_LISTVIEW);
		ListingAdapter adapter = new ListingAdapter(this, mSkicentreList, mAreaList, mCountryList);
		try
		{
			listView.setAdapter(adapter);
			listView.requestLayout();
			adapter.notifyDataSetChanged();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		// nastaveni onclick
		listView.setItemsCanFocus(false);
		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) 
			{
				int id = mSkicentreList.get(position).getId();
				showDetail(id);
			}
		});
		// TODO
//		listView.setOnItemLongClickListener(new OnItemLongClickListener() 
//		{
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long viewId) 
//			{
//				
//				Toast.makeText(getActivity(), "LONG CLICK", Toast.LENGTH_SHORT).show();
//				return true;
//			}
//		});
		
		// nastaveni vyhledavaciho infoboxu
		TextView infobox = (TextView) mRootView.findViewById(ID_LAYOUT_LISTING_INFOBOX);
		if(keyword==null)
		{
			infobox.setVisibility(View.GONE);
		}
		else
		{
			StringBuilder builder = new StringBuilder();
			builder.append(getString(R.string.search_result));
			builder.append(" '");
			builder.append(keyword);
			builder.append("': ");
			if(mSkicentreList==null) return;
			builder.append(mSkicentreList.size());
			builder.append(" ");
			if(mSkicentreList.size()==1) builder.append(getString(R.string.search_result_skicentre_1));
			else if(mSkicentreList.size()>0 && mSkicentreList.size()<5) builder.append(getString(R.string.search_result_skicentre_234));
			else builder.append(getString(R.string.search_result_skicentre_5));
			builder.append("\n");
			builder.append(getString(R.string.search_result_cancel));
			infobox.setVisibility(View.VISIBLE);
			infobox.setText(builder.toString());
			infobox.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					mSearchQuery = null;
					refreshData();
				}
			});
		}
		
		// nastaveni oznaceni polozky v listu
		if (mDualView) 
        {
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
		
		// TODO: odstranit modry obrazek v listview - kdyz pretahnu za okraj
	}
}
