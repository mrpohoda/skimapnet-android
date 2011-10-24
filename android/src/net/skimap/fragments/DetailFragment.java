package net.skimap.fragments;

import net.skimap.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.Menu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment
{
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
//		if (container == null) 
//		{
//            // We have different layouts, and in one of them this
//            // fragment's containing frame doesn't exist.  The fragment
//            // may still be created from its saved state, but there is
//            // no reason to try to create its view hierarchy because it
//            // won't be displayed.  Note this is not needed -- we could
//            // just run the code below, where we would create and return
//            // the view hierarchy; it would just never be used.
//            return null;
//        }
		
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.layout_detail, container, false);
		//TODO: rusit predchozi view - visi tam v action baru ikony navic
		
		try
		{
			int index = getArguments().getInt("index", 0);		
			TextView textView = (TextView) view.findViewById(R.id.layout_detail_textview);
			textView.setText("Detail: " + index);
		}
		catch(Exception e)
		{
			
		}
		
		return view;
	}
	
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.menu_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	
	public static DetailFragment newInstance(int index) 
	{
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
