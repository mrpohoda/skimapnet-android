package net.skimap.network;

import net.skimap.activities.SkimapApplication;
import net.skimap.parser.JsonParser;
import android.os.Handler;
import android.os.Message;

public class Synchronization 
{
	private final int MESSAGE_SYNCHRO_SHORT = 0;
	
	private Handler mHandler;
	private SkimapApplication mApplication;
	
	
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
            }
	    };
	}
	
	
	public void trySynchronizeShortData()
	{
		boolean synchro = mApplication.getSynchro();
		if(!synchro)
		{
			// TODO: progressbar viditelny ze vsech fragmentu
			
			// start synchronizace
			mApplication.setSynchro(true);
			mApplication.startSynchro();
			
			// spusteni vlakna
			synchronizeShortData();
		}
	}
	
	
	private void synchronizeShortData()
	{
	    // vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		synchronizeCountriesThread();
        		synchronizeSkicentresThread();
        		Message message = new Message();
        		message.what = MESSAGE_SYNCHRO_SHORT;
        		mHandler.sendMessage(message);
		    }
        }.start();
	}
	
	
	private int synchronizeSkicentresThread()
	{
		int count = 0;
		
		// nacteni JSON dat z url
		String skicentresJson = null;
		try
		{
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
	
	
	private int synchronizeCountriesThread()
	{
		int count = 0;
		
		// nacteni JSON dat z url
		String countriesJson = null;
		try
		{		
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
