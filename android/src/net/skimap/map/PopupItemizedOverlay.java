package net.skimap.map;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballons.BalloonItemizedOverlay;

public class PopupItemizedOverlay extends BalloonItemizedOverlay<OverlayItem>
{
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	
	public PopupItemizedOverlay(Drawable defaultMarker, MapView mapView)
	{
		super(boundCenter(defaultMarker), mapView);
		mContext = mapView.getContext();
	}

	
	public void addOverlay(OverlayItem overlay)
	{
		mOverlays.add(overlay);
	    populate();
	}

	
	@Override
	protected OverlayItem createItem(int i)
	{
		return mOverlays.get(i);
	}

	
	@Override
	public int size()
	{
		return mOverlays.size();
	}

	
	@Override
	protected boolean onBalloonTap(int index, OverlayItem item)
	{
		Toast.makeText(mContext, "SHOW SKICENTRE " + index, Toast.LENGTH_SHORT).show();
		return true;
	}
}
