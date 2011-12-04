package net.skimap.network;

import net.skimap.activities.SkimapApplication;
import net.skimap.parser.JsonParser;
import android.os.Handler;
import android.os.Message;

public class Synchronization 
{
	private final int MESSAGE_SYNCHRO_SHORT = 0;
	private final int MESSAGE_SYNCHRO_LONG = 1;
	
	private Handler mHandler;
	private SkimapApplication mApplication;
	
	
	// TODO: osetrit soubeznou synchronizaci short a long
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
		boolean synchro = mApplication.getSynchro();
		if(!synchro)
		{
			// start synchronizace
			mApplication.setSynchro(true);
			mApplication.startSynchro();
			
			// spusteni vlakna
			synchronizeShortData();
		}
	}
	
	
	public void trySynchronizeLongData()
	{
		boolean synchro = mApplication.getSynchro();
		if(!synchro)
		{
			// start synchronizace
			mApplication.setSynchro(true);
			mApplication.startSynchro();
			
			// spusteni vlakna
			synchronizeLongData();
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
	
	
	private void synchronizeLongData()
	{
	    // vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		synchronizeSkicentreLongThread();
        		Message message = new Message();
        		message.what = MESSAGE_SYNCHRO_LONG;
        		mHandler.sendMessage(message);
		    }
        }.start();
	}
	
	
	private boolean synchronizeSkicentreLongThread()
	{
		boolean state = false;
		
		// nacteni JSON dat z url
		String skicentreJson = null;
		try
		{
			// TODO
			skicentreJson = HttpCommunication.executeHttpGet("http://data.jestrab.net/skimap/skicentre_detail.txt");
			//skicentresJson = HttpCommunication.executeHttpGet("http://ski-map.net/skimapnet/php/common.php?fce=skicentre_detail&id=10&lang=cs");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		// parsovani JSON dat a ukladani do databaze
		if(skicentreJson!=null)
		{
			JsonParser parser = new JsonParser(mApplication.getApplicationContext());
			state = parser.storeSkicentreLong(skicentreJson);
		}

		return state;
	}
	
	
	private int synchronizeSkicentresShortThread()
	{
		int count = 0;
		
		// nacteni JSON dat z url
		String skicentresJson = null;
		try
		{
			// TODO
			skicentresJson = HttpCommunication.executeHttpGet("http://data.jestrab.net/skimap/skicentres_list.txt");
			//skicentresJson = HttpCommunication.executeHttpGet("http://ski-map.net/skimapnet/php/common.php?fce=skicentres_list&extended=1");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return count;
		}
		
		// parsovani JSON dat a ukladani do databaze
		if(skicentresJson!=null)
		{
			JsonParser parser = new JsonParser(mApplication.getApplicationContext());
			count = parser.storeSkicentresShort(skicentresJson);
		}

		return count;
	}
	
	
	private int synchronizeAreasThread()
	{
		int count = 0;
		
		// nacteni JSON dat z url
		String areasJson = null;
		try
		{		
			// TODO
			areasJson = HttpCommunication.executeHttpGet("http://data.jestrab.net/skimap/skicentres_list_areas.txt");
			//areasJson = HttpCommunication.executeHttpGet("http://ski-map.net/skimapnet/php/common.php?fce=skicentres_list_areas");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return count;
		}
		
		// parsovani JSON dat a ukladani do databaze		
		if(areasJson!=null)
		{
			JsonParser parser = new JsonParser(mApplication.getApplicationContext());
			count = parser.storeAreas(areasJson);
		}
		
		return count;
	}
	
	
	private int synchronizeCountriesThread()
	{
		int count = 0;
		
		// nacteni JSON dat z url
		String countriesJson = null;
		try
		{		
			// TODO
			countriesJson = HttpCommunication.executeHttpGet("http://data.jestrab.net/skimap/countries_list.txt");
			//countriesJson = HttpCommunication.executeHttpGet("http://ski-map.net/skimapnet/php/common.php?fce=countries_list");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return count;
		}
		
		// parsovani JSON dat a ukladani do databaze		
		if(countriesJson!=null)
		{
			JsonParser parser = new JsonParser(mApplication.getApplicationContext());
			count = parser.storeCountries(countriesJson);
		}
		
		return count;
	}
}
