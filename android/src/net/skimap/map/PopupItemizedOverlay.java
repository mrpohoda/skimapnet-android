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
	private ArrayList<Integer> mIds = new ArrayList<Integer>();
	private Context mMapViewContext;
	private Context mContext;
	
	
	public PopupItemizedOverlay(Drawable defaultMarker, MapView mapView, Context context)
	{
		super(boundCenter(defaultMarker), mapView);
		mMapViewContext = mapView.getContext();
		mContext = context;
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
		// TODO: otevrit detail, nesplest index a id
		Toast.makeText(mContext, "SHOW SKICENTRE " + mIds.get(index), Toast.LENGTH_SHORT).show();
		
//		// nova aktivita detail
//		Intent intent = new Intent(mContext, DetailActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.putExtra(DetailFragment.ITEM_ID, mIds.get(index));
//		//intent.putExtra(DetailFragment.DUAL_VIEW, dualView);
//		mContext.startActivity(intent);
		
		return true;
	}
}
