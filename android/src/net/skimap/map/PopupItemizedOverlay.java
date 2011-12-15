package net.skimap.map;

import java.util.ArrayList;

import net.skimap.activities.DetailActivity;
import net.skimap.fragments.DetailFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballons.BalloonItemizedOverlay;

public class PopupItemizedOverlay extends BalloonItemizedOverlay<OverlayItem>
{
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private ArrayList<Integer> mIds = new ArrayList<Integer>();
	private Context mMapViewContext;
	
	
	public PopupItemizedOverlay(Drawable defaultMarker, MapView mapView)
	{
		super(boundCenter(defaultMarker), mapView);
		mMapViewContext = mapView.getContext();
		populate();
	}

	
	public void addOverlay(OverlayItem overlay, int id)
	{
		mOverlays.add(overlay);
		mIds.add(id);
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
		// nova aktivita detail
		Intent intent = new Intent();
		intent.putExtra(DetailFragment.ITEM_ID, mIds.get(index));
		intent.putExtra(DetailFragment.DUAL_VIEW, false);
	    intent.setClass(mMapViewContext, DetailActivity.class);
	    mMapViewContext.startActivity(intent);
		return true;
	}
}
