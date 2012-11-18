package net.skimap.parser;

import java.util.ArrayList;

import net.skimap.data.Placemark;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings("unused")
public class KmlParser extends DefaultHandler
{ 
	private boolean mKmlTag = false; 
	private boolean mPlacemarkTag = false; 
	private boolean mNameTag = false;
	private boolean mDescriptionTag = false;
	private boolean mGeometryCollectionTag = false;
	private boolean mLineStringTag = false;
	private boolean mPointTag = false;
	private boolean mCoordinatesTag = false;
	private boolean mStyleUrlTag = false;
	
	private ArrayList<Placemark> mPlacemarks;
	private StringBuffer mBufferCoordinates;
	private StringBuffer mBufferStyle;
	

	public ArrayList<Placemark> getParsedData()
	{
		return mPlacemarks; 
	} 


	@Override 
	public void startDocument() throws SAXException
	{
		mPlacemarks = new ArrayList<Placemark>();
		mBufferCoordinates = new StringBuffer();
		mBufferStyle = new StringBuffer();
	} 

	
	@Override 
	public void endDocument() throws SAXException
	{ 
	} 
 

	@Override 
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
	{ 
		if (localName.equals("kml"))
		{ 
			mKmlTag = true;
		} 
		else if (localName.equals("Placemark"))
		{ 
			mPlacemarkTag = true;
			// vynulovani bufferu
			mBufferCoordinates.delete(0, mBufferCoordinates.length());
			mBufferStyle.delete(0, mBufferStyle.length());
		} 
		else if (localName.equals("name"))
		{ 
			mNameTag = true;
		} 
		else if (localName.equals("description"))
		{ 
			mDescriptionTag = true;
		} 
		else if (localName.equals("GeometryCollection"))
		{ 
			mGeometryCollectionTag = true;
		} 
		else if (localName.equals("LineString"))
		{ 
			mLineStringTag = true;
		} 
		else if (localName.equals("point"))
		{ 
			mPointTag = true;
		} 
		else if (localName.equals("coordinates"))
		{
			mCoordinatesTag = true;
		}
		else if (localName.equals("styleUrl"))
		{ 
			mStyleUrlTag = true;
		}
	}

 
	@Override 
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{ 
		if (localName.equals("kml"))
		{
			mKmlTag = false; 
		}
		else if (localName.equals("Placemark"))
		{ 
			mPlacemarkTag = false;
			Placemark placemark = new Placemark(new String(mBufferCoordinates.toString().trim()), new String(mBufferStyle.toString().trim()));
			mPlacemarks.add(placemark);
		}
		else if (localName.equals("name"))
		{ 
			mNameTag = false;           
		}
		else if (localName.equals("description"))
		{ 
			mDescriptionTag = false;
		}
		else if (localName.equals("GeometryCollection"))
		{ 
			mGeometryCollectionTag = false;
		}
		else if (localName.equals("LineString"))
		{ 
			mLineStringTag = false;              
		}
		else if (localName.equals("point"))
		{ 
			mPointTag = false;          
		}
		else if (localName.equals("coordinates"))
		{ 
			mCoordinatesTag = false;
		}
		else if (localName.equals("styleUrl"))
		{ 
			mStyleUrlTag = false;
		}
	} 
 
 
	@Override 
	public void characters(char ch[], int start, int length)
	{ 
		// ulozeni obsahu do bufferu
		if(mCoordinatesTag)
		{
			mBufferCoordinates.append(ch, start, length);
		}
		else if(mStyleUrlTag)
		{        
			mBufferStyle.append(ch, start, length);
		}
	} 
}
