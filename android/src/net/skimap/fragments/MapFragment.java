package net.skimap.fragments;

import net.skimap.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.maps.MapView;

public class MapFragment extends Fragment 
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		setHasOptionsMenu(true);
		
		View view = inflater.inflate(R.layout.layout_map, container, false);
		
		MapView mapView = (MapView)view.findViewById(R.id.layout_map_mapview);
		mapView.setBuiltInZoomControls(true);
		
		return view;
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.menu_map, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
}
