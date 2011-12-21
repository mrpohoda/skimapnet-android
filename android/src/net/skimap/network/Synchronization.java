package net.skimap.network;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.localytics.android.LocalyticsSession;

import net.skimap.activities.SkimapApplication;
import net.skimap.parser.JsonParser;
import net.skimap.utililty.Localytics;
import net.skimap.utililty.Settings;
import android.os.Handler;
import android.os.Message;

public class Synchronization 
{
	private final String URL_AREAS = "http://ski-map.net/skimapnet/php/common.php?fce=areas_list";
	private final String URL_COUNTRIES = "http://ski-map.net/skimapnet/php/common.php?fce=countries_list";
	private final String URL_SKICENTRES_SHORT = "http://ski-map.net/skimapnet/php/common.php?fce=skicentres_list&extended=1";
	private final String URL_SKICENTRE_LONG = "http://ski-map.net/skimapnet/php/common.php?fce=skicentre_detail&lang=cs&id=";
	
	public static final int SYNCHRO_DELAY = 12 * 3600000; // 12 hodin
	
	public static final int STATUS_OFFLINE = -1;
	public static final int STATUS_UNKNOWN = -2;
	
	private final int MESSAGE_SYNCHRO_SHORT = 0;
	private final int MESSAGE_SYNCHRO_LONG = 1;
	
	private Handler mHandler;
	private SkimapApplication mApplication;
	
	
	// TODO: osetrit soubeznou synchronizaci short a long
	// TODO: vsude pri praci s databazi pouzivat transakce
	public Synchronization(SkimapApplication application)
	{
		mApplication = application;
		
		// odchyceni zpravy z vlakna
		mHandler = new Handler() 
	    {
            @Override
            public void handleMessage(Message message) 
            {        
            	if(message.what == MESSAGE_SYNCHRO_SHORT)
            	{
            		// zastaveni synchronizace
        			mApplication.stopSynchro(message.arg1);
        			mApplication.setSynchronizing(false);
            	}
            	else if(message.what == MESSAGE_SYNCHRO_LONG)
            	{
            		// zastaveni synchronizace
        			mApplication.stopSynchro(message.arg1);
        			mApplication.setSynchronizing(false);
            	}
            }
	    };
	}
	
	
	public void trySynchronizeShortDataAuto(LocalyticsSession localyticsSession, String localyticsValueFrom)
	{
		// porovnani casove prodlevy
		Settings settings = new Settings(mApplication.getApplicationContext());
		long lastSynchroTime = settings.getLastSynchro();
		long currentTime = System.currentTimeMillis();
		long diff = Math.abs(currentTime-lastSynchroTime);
		if(diff>SYNCHRO_DELAY)
		{
			settings.setLastSynchro(currentTime);
			trySynchronizeShortData();
			
			Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
	        localyticsValues.put(Localytics.ATTR_SYNCHRO_AUTO, localyticsValueFrom); // Localytics atribut
			localyticsSession.tagEvent(Localytics.TAG_SYNCHRO, localyticsValues); // Localytics
		}
	}
	
	
	public void trySynchronizeShortData()
	{
		boolean synchro = mApplication.isSynchronizing();
		if(!synchro)
		{
			// start synchronizace
			mApplication.setSynchronizing(true);
			mApplication.startSynchro();
			
			// spusteni vlakna
			synchronizeShortData();
		}
	}
	
	
	public void trySynchronizeLongData(int id)
	{
		boolean synchro = mApplication.isSynchronizing();
		if(!synchro)
		{
			// start synchronizace
			mApplication.setSynchronizing(true);
			mApplication.startSynchro();
			
			// spusteni vlakna
			synchronizeLongData(id);
		}
	}
	
	
	private void synchronizeShortData()
	{
	    // vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		int countAreas = synchronizeAreasThread();
        		int countCountries = synchronizeCountriesThread();
        		int countSkicentres = synchronizeSkicentresShortThread();
        		int count = countAreas + countCountries + countSkicentres;
        		if(countAreas==STATUS_OFFLINE || countCountries==STATUS_OFFLINE || countSkicentres==STATUS_OFFLINE) count = STATUS_OFFLINE;
        		else if(countAreas==STATUS_UNKNOWN || countCountries==STATUS_UNKNOWN || countSkicentres==STATUS_UNKNOWN) count = STATUS_UNKNOWN;
        		Message message = new Message();
        		message.what = MESSAGE_SYNCHRO_SHORT;
        		message.arg1 = count;
        		mHandler.sendMessage(message);
		    }
        }.start();
	}
	
	
	private void synchronizeLongData(final int id)
	{
	    // vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		int count = synchronizeSkicentreLongThread(id);
        		Message message = new Message();
        		message.what = MESSAGE_SYNCHRO_LONG;
        		message.arg1 = count;
        		mHandler.sendMessage(message);
		    }
        }.start();
	}
	
	
	private int synchronizeSkicentreLongThread(int id)
	{
		int count = 0;
		String url = URL_SKICENTRE_LONG + id;
		
		// parsovani JSON dat a ukladani do databaze		
		JsonParser parser = new JsonParser(mApplication.getApplicationContext());
		try
		{
			parser.open();
			count = parser.storeSkicentreLong(url);
		}
		catch (UnknownHostException e)
		{
			// uzivatel je offline
			e.printStackTrace();
			count = STATUS_OFFLINE;
		}
		catch (SocketException e)
		{
			// uzivatel je offline
			e.printStackTrace();
			count = STATUS_OFFLINE;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			count = STATUS_UNKNOWN;
		}
		parser.close();
		
		return count;
	}
	
	
	private int synchronizeSkicentresShortThread()
	{
		int count = 0;
		String url = URL_SKICENTRES_SHORT;
		
		// parsovani JSON dat a ukladani do databaze		
		JsonParser parser = new JsonParser(mApplication.getApplicationContext());
		try
		{
			parser.open();
			count = parser.storeSkicentresShort(url);
		}
		catch (UnknownHostException e)
		{
			// uzivatel je offline
			e.printStackTrace();
			count = STATUS_OFFLINE;
		}
		catch (SocketException e)
		{
			// uzivatel je offline
			e.printStackTrace();
			count = STATUS_OFFLINE;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			count = STATUS_UNKNOWN;
		}
		parser.close();
		
		return count;
	}
	
	
	private int synchronizeAreasThread()
	{
		int count = 0;
		String url = URL_AREAS;
		
		// parsovani JSON dat a ukladani do databaze		
		JsonParser parser = new JsonParser(mApplication.getApplicationContext());
		try
		{
			parser.open();
			count = parser.storeAreas(url);
		}
		catch (UnknownHostException e)
		{
			// uzivatel je offline
			e.printStackTrace();
			count = STATUS_OFFLINE;
		}
		catch (SocketException e)
		{
			// uzivatel je offline
			e.printStackTrace();
			count = STATUS_OFFLINE;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			count = STATUS_UNKNOWN;
		}
		parser.close();
		
		return count;
	}
	
	
	private int synchronizeCountriesThread()
	{
		int count = 0;
		String url = URL_COUNTRIES;
		
		// parsovani JSON dat a ukladani do databaze		
		JsonParser parser = new JsonParser(mApplication.getApplicationContext());
		try
		{
			parser.open();
			count = parser.storeCountries(url);
		}
		catch (UnknownHostException e)
		{
			// uzivatel je offline
			e.printStackTrace();
			count = STATUS_OFFLINE;
		}
		catch (SocketException e)
		{
			// uzivatel je offline
			e.printStackTrace();
			count = STATUS_OFFLINE;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			count = STATUS_UNKNOWN;
		}
		parser.close();
		
		return count;
	}
}
