package net.skimap.map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PathOverlay extends Overlay
{ 
	private final int LINE_STROKE = 3;
	private final boolean ANTIALIAS = true;
	
	private GeoPoint mGeoPoint1;
	private GeoPoint mGeoPoint2;
	private int mColor;

	public PathOverlay(GeoPoint gp1, GeoPoint gp2, int color)
	{
	    this.mGeoPoint1 = gp1;
	    this.mGeoPoint2 = gp2;
	    this.mColor = color;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when)
	{
	    Projection projection = mapView.getProjection();
	    if (shadow == false)
	    {
	    	// kreslici nastroj
	        Paint paint = new Paint();
	        paint.setColor(mColor);
	        paint.setStrokeWidth(LINE_STROKE);
	        paint.setAntiAlias(ANTIALIAS);
            //paint.setAlpha(128);
	        
            // body
	        Point point1 = new Point();
	        projection.toPixels(mGeoPoint1, point1);
            Point point2 = new Point();
            projection.toPixels(mGeoPoint2, point2);
            
            // vykresleni
            canvas.drawLine(point1.x, point1.y, point2.x,point2.y, paint);
	    }
	    return super.draw(canvas, mapView, shadow, when);
	}
}
