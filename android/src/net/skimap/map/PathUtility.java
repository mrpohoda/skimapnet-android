package net.skimap.map;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.skimap.data.Placemark;
import net.skimap.parser.KmlParser;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class PathUtility 
{
	public static ArrayList<Placemark> getPlacemarks(String url)
	{
		ArrayList<Placemark> placemarks = null;
	    try
	    {
	    	// vytvoreni spojeni
	        final URL urlObject = new URL(url);
	        final URLConnection connection = urlObject.openConnection();
	        connection.setReadTimeout(10 * 1000);  // timeout po 10 sekundach
	        connection.connect();

	        // vytvoreni SAX parseru
	        SAXParserFactory parserFactory = SAXParserFactory.newInstance(); 
	        SAXParser saxParser = parserFactory.newSAXParser(); 

	        // vytvoreni KML parseru
	        XMLReader reader = saxParser.getXMLReader();
	        KmlParser kmlParser = new KmlParser(); 
	        reader.setContentHandler(kmlParser); 

	        // parsovani KML dat
	        reader.parse(new InputSource(urlObject.openStream()));

	        // ulozeni KML dat do objektu
	        placemarks = kmlParser.getParsedData(); 
	    }
	    catch (Exception e)
	    {
	    	Log.e("SKIMAP", "Cannot parse KML data.");
	    	e.printStackTrace();
	    	placemarks = null;
	    }   

	    return placemarks;
	}
	
	
	public static void drawPath(Placemark placemark, MapView mapView)
	{
		int color = placemark.getColor();
	    String path = placemark.getCoordinates();

	    if (path!=null && path.trim().length() > 0)
	    {
	        try
	        {
	        	// jednotlive souradnice
		        String[] coordinates = path.trim().split(" ");

		        // rozdeleni prvni souradnice na lng,lat,alt
		        String[] lngLat = coordinates[0].split(",");
		        
	        	// prvni souradnice
	            GeoPoint gpStart = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6));
	            GeoPoint gp1;
	            GeoPoint gp2 = gpStart;

	            // pridavani jednotlivych car cesty do mapy
	            for (int i = 1; i < coordinates.length; i++)
	            {
	            	lngLat = coordinates[i].split(",");
	                gp1 = gp2;
	                if (lngLat.length >= 2 && gp1.getLatitudeE6() > 0 && gp1.getLongitudeE6() > 0 && gp2.getLatitudeE6() > 0 && gp2.getLongitudeE6() > 0)
	                {
	                    gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6));
	                    mapView.getOverlays().add(new PathOverlay(gp1, gp2, color));
	                }
	            }
	        }
	        catch (NumberFormatException e)
	        {
	        	e.printStackTrace();
	        }
	    }
	}
}
