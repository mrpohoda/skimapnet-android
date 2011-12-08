package net.skimap.map;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class CustomMapView extends MapView
{
    private int mOldZoomLevel = -1;
    private GeoPoint mOldCenterGeoPoint;
    private OnPanAndZoomListener mListener;
    private int mCoordinateDifferenceE6 = 10000; // 10000e6 = 0.01deg = 800m

    
    public CustomMapView(Context context, String apiKey)
    {
        super(context, apiKey);
    }

    public CustomMapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomMapView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    
    public void setPanListenerSensitivity(int coordinateDifferenceE6)
    {
    	mCoordinateDifferenceE6 = coordinateDifferenceE6;
    }

    
    public void setOnPanListener(Fragment fragment)
    {
    	try
        {
    		mListener = (OnPanAndZoomListener) fragment;
        } 
        catch (ClassCastException e)
        {
            throw new ClassCastException(fragment.toString() + " must implement OnPanAndZoomListener.");
        }
    }

    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            GeoPoint centerGeoPoint = this.getMapCenter();
            boolean bigMove = checkMoveStep(mOldCenterGeoPoint, centerGeoPoint, mCoordinateDifferenceE6);
            if (bigMove)
            {
                mListener.onPan();
            }
            mOldCenterGeoPoint = this.getMapCenter();
        }
        return super.onTouchEvent(event);
    }

    
    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        if (getZoomLevel() != mOldZoomLevel)
        {
            mListener.onZoom();
            mOldZoomLevel = getZoomLevel();
        }
    }
    
    
    private boolean checkMoveStep(GeoPoint oldCenterGeoPoint, GeoPoint newCenterGeoPoint, int coordinateDifferenceE6)
    {
    	if(oldCenterGeoPoint==null || newCenterGeoPoint==null)
    		return true;
    		
    	int diffLatitude = Math.abs(oldCenterGeoPoint.getLatitudeE6() - newCenterGeoPoint.getLatitudeE6());
    	int diffLongitude = Math.abs(oldCenterGeoPoint.getLongitudeE6() - newCenterGeoPoint.getLongitudeE6());
    	
    	// geobod se posunul o vetsi delku
    	if(diffLatitude>coordinateDifferenceE6 || diffLongitude>coordinateDifferenceE6)
    		return true;
    	
    	// geobod se posunul o mensi delku
    	else
    		return false;
    }

    
    public interface OnPanAndZoomListener
    {
        public void onPan();
        public void onZoom();
    }
}
