package net.skimap.fragments;

import java.util.ArrayList;

import net.skimap.R;
import net.skimap.adapters.ListingAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ListingFragment extends Fragment
{
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.layout_listing, container, false);
		setView(view);
		return view;
	}
	
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.menu_listing, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	private void setView(View view)
	{
		// seznam skicenter
		ListView listView = (ListView) view.findViewById(R.id.layout_listing_listview);
		
		// vyprazdneni seznamu
		listView.setAdapter(null);
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("Bormio");
		list.add("Pec pod Snìžkou");
		list.add("Špindlerùv Mlýn");
		list.add("Kvilda");
		list.add("Kouty nad Desnou");
		list.add("Monínec");
		list.add("Èerná hora");
		list.add("Javorina");
		list.add("Bormio");
		list.add("Pec pod Snìžkou");
		list.add("Špindlerùv Mlýn");
		list.add("Kvilda");
		list.add("Kouty nad Desnou");
		list.add("Monínec");
		list.add("Èerná hora");
		list.add("Javorina");
		list.add("Bormio");
		list.add("Pec pod Snìžkou");
		list.add("Špindlerùv Mlýn");
		list.add("Kvilda");
		list.add("Kouty nad Desnou");
		list.add("Monínec");
		list.add("Èerná hora");
		list.add("Javorina");
		
		// nastaveni view seznamu skicenter
		ListingAdapter adapter =  new ListingAdapter(getActivity(), list);
		listView.setAdapter(adapter);
	}
}
