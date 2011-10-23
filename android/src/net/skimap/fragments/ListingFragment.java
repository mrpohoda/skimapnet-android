package net.skimap.fragments;

import net.skimap.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListingFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.layout_listing, container);
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.menu_listing, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
}
