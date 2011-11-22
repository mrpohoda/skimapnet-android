package net.skimap.network;

import net.skimap.parser.JsonParser;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.SupportActivity;

public class Synchronization 
{
	private final int MESSAGE_SYNCHRO_SKICENTRES = 0;
	
	private Context mContext;
	
	
	public Synchronization(Context context)
	{
		mContext = context;
	}
	
	
	public void synchronizeSkicentres(final SupportActivity activity)
	{
		// TODO
		activity.setProgressBarIndeterminateVisibility(Boolean.TRUE);
		activity.setProgress(0);
		
		// odchyceni zpravy
		final Handler handler = new Handler() 
	    {
            @Override
            public void handleMessage(Message message) 
            {        
            	if(message.what == MESSAGE_SYNCHRO_SKICENTRES)
            	{
            		activity.setProgressBarIndeterminateVisibility(Boolean.FALSE);
            	}
            }
	    };
	    
	    // vlakno
	    new Thread()
        {
        	public void run() 
		    {
        		synchronizeSkicentresThread();
        		Message message = new Message();
        		message.what = MESSAGE_SYNCHRO_SKICENTRES;
    			handler.sendMessage(message);
		    }
        }.start();
	}
	
	
	private int synchronizeSkicentresThread()
	{
		int count = 0;
		
		// nacteni JSON dat z url
		String json = null;
		try
		{
			json = HttpCommunication.executeHttpGet("http://data.jestrab.net/skimap/skimap.txt");
			//json = HttpCommunication.executeHttpGet("http://ski-map.net/skimapnet/php/common.php?fce=skicentres_list");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return count;
		}
		
		// parsovani JSON dat a ukladani do databaze
		if(json!=null)
		{
			JsonParser parser = new JsonParser(mContext);
			count = parser.storeSkicentresShort(json);
		}
		
		return count;
	}
}
