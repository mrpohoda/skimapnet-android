package net.skimap.utililty;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
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
    private final Map<String, SoftReference<Drawable>> mDrawableMap;

    
    public DrawableManager() 
    {
    	mDrawableMap = new HashMap<String, SoftReference<Drawable>>();
    }

    
    public Drawable fetchDrawable(String urlString) 
    {
    	SoftReference<Drawable> drawableRef = mDrawableMap.get(urlString);
    	if (drawableRef != null) 
    	{
            Drawable drawable = drawableRef.get();
            if (drawable != null) return drawable;
            // Reference has expired so remove the key from drawableMap
            mDrawableMap.remove(urlString);
        }

    	try
    	{
            InputStream is = fetch(urlString);
            Drawable drawable = Drawable.createFromStream(is, "src");
            drawableRef = new SoftReference<Drawable>(drawable);
            mDrawableMap.put(urlString, drawableRef);
            return drawableRef.get();
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

//        if (mDrawableMap.containsKey(urlString)) 
//        {
//            return mDrawableMap.get(urlString);
//        }
//
//        try 
//        {
//            InputStream is = fetch(urlString);
//            Drawable drawable = Drawable.createFromStream(is, "src");
//
//
//            if (drawable != null) 
//            {
//            	mDrawableMap.put(urlString, drawable);
//            } 
//            else 
//            {
//
//            }
//
//            return drawable;
//        } 
//        catch (MalformedURLException e) 
//        {
//            return null;
//        } 
//        catch (IOException e) 
//        {
//            return null;
//        }
//        catch (Exception e) 
//        {
//        	e.printStackTrace();
//            return null;
//        }
    }

    
    public void fetchDrawableOnThread(final String urlString, final ImageView imageView, final ArrayList<View> viewsToVisible) 
    {
    	SoftReference<Drawable> drawableRef = mDrawableMap.get(urlString);
    	
    	if (drawableRef != null)
    	{
            Drawable drawable = drawableRef.get();
            if (drawable != null)
            {
                imageView.setImageDrawable(drawableRef.get());
                return;
            }
            // Reference has expired so remove the key from drawableMap
            mDrawableMap.remove(urlString);
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

//    	if (mDrawableMap.containsKey(urlString)) 
//        {
//            imageView.setImageDrawable(mDrawableMap.get(urlString));
//        }
//
//        final Handler handler = new Handler() 
//        {
//            @Override
//            public void handleMessage(Message message) 
//            {
//            	Drawable drawable = (Drawable) message.obj;
//                imageView.setImageDrawable(drawable);
//                if(drawable!=null) showViews(viewsToVisible);
//            }
//        };
//
//        Thread thread = new Thread() 
//        {
//            @Override
//            public void run() 
//            {
//                Drawable drawable = fetchDrawable(urlString);
//                Message message = handler.obtainMessage(1, drawable);
//                handler.sendMessage(message);
//            }
//        };
//        thread.start();
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
