package net.skimap.network;

import net.skimap.activities.SkimapApplication;
import net.skimap.parser.JsonParser;
import android.os.Handler;
import android.os.Message;

public class Synchronization 
{
	private final String URL_AREAS = "http://ski-map.net/skimapnet/php/common.php?fce=skicentres_list_areas"; // http://data.jestrab.net/skimap/skicentres_list_areas.txt
	private final String URL_COUNTRIES = "http://ski-map.net/skimapnet/php/common.php?fce=countries_list"; // http://data.jestrab.net/skimap/countries_list.txt
	private final String URL_SKICENTRES_SHORT = "http://ski-map.net/skimapnet/php/common.php?fce=skicentres_list&extended=1"; // http://data.jestrab.net/skimap/skicentres_list.txt
	private final String URL_SKICENTRE_LONG = "http://ski-map.net/skimapnet/php/common.php?fce=skicentre_detail&lang=cs&id="; // http://data.jestrab.net/skimap/skicentre_detail.txt
	
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
        			mApplication.stopSynchro();
        			mApplication.setSynchro(false);
            	}
            	else if(message.what == MESSAGE_SYNCHRO_LONG)
            	{
            		// zastaveni synchronizace
        			mApplication.stopSynchro();
        			mApplication.setSynchro(false);
            	}
            }
	    };
	}
	
	
	public void trySynchronizeShortData()
	{
		boolean synchro = mApplication.isSynchro();
		if(!synchro)
		{
			// start synchronizace
			mApplication.setSynchro(true);
			mApplication.startSynchro();
			
			// spusteni vlakna
			synchronizeShortData();
		}
	}
	
	
	public void trySynchronizeLongData(int id)
	{
		boolean synchro = mApplication.isSynchro();
		if(!synchro)
		{
			// start synchronizace
			mApplication.setSynchro(true);
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
        		synchronizeAreasThread();
        		synchronizeCountriesThread();
        		synchronizeSkicentresShortThread();
        		Message message = new Message();
        		message.what = MESSAGE_SYNCHRO_SHORT;
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
        		synchronizeSkicentreLongThread(id);
        		Message message = new Message();
        		message.what = MESSAGE_SYNCHRO_LONG;
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
		parser.open();
		try
		{
			count = parser.storeSkicentreLong(url);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		parser.open();
		try
		{
			count = parser.storeSkicentresShort(url);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		parser.open();
		try
		{
			count = parser.storeAreas(url);
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		parser.open();
		try
		{
			count = parser.storeCountries(url);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		parser.close();
		
		return count;
	}
}
