package net.skimap.utililty;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

//http://stackoverflow.com/questions/541966/android-how-do-i-do-a-lazy-load-of-images-in-listview
public class DrawableManager 
{
    private final Map<String, Drawable> mDrawableMap;

    
    public DrawableManager() 
    {
    	mDrawableMap = new HashMap<String, Drawable>();
    }

    
    public Drawable fetchDrawable(String urlString) 
    {
        if (mDrawableMap.containsKey(urlString)) 
        {
            return mDrawableMap.get(urlString);
        }

        try 
        {
            InputStream is = fetch(urlString);
            Drawable drawable = Drawable.createFromStream(is, "src");


            if (drawable != null) 
            {
            	mDrawableMap.put(urlString, drawable);
            } 
            else 
            {

            }

            return drawable;
        } 
        catch (MalformedURLException e) 
        {
            return null;
        } 
        catch (IOException e) 
        {
            return null;
        }
        catch (Exception e) 
        {
        	e.printStackTrace();
            return null;
        }
    }

    
    public void fetchDrawableOnThread(final String urlString, final ImageView imageView, final ArrayList<View> viewsToVisible) 
    {
        if (mDrawableMap.containsKey(urlString)) 
        {
            imageView.setImageDrawable(mDrawableMap.get(urlString));
        }

        final Handler handler = new Handler() 
        {
            @Override
            public void handleMessage(Message message) 
            {
            	Drawable drawable = (Drawable) message.obj;
                imageView.setImageDrawable(drawable);
                if(drawable!=null) showViews(viewsToVisible);
            }
        };

        Thread thread = new Thread() 
        {
            @Override
            public void run() 
            {
                Drawable drawable = fetchDrawable(urlString);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    
    private InputStream fetch(String urlString) throws MalformedURLException, IOException 
    {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }
    
    
    private void showViews(final ArrayList<View> viewsToVisible)
    {
    	Iterator<View> iterator = viewsToVisible.iterator();
    	while(iterator.hasNext())
		{
			View view = iterator.next();
			view.setVisibility(View.VISIBLE);
		}
    }
}
