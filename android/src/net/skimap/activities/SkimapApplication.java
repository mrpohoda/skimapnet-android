package net.skimap.activities;

import android.app.Application;
import android.support.v4.app.SupportActivity;

public class SkimapApplication extends Application
{
	private boolean mSynchro = false;
	private OnSynchroListener mSynchroListener;
	
	
	public boolean getSynchro() { return mSynchro; }
	public void setSynchro(boolean synchro) { mSynchro = synchro; }
	
	
	// kazda aktivita, ktera chce odchytavat stav synchronizace musi implementovat rozhrani OnSynchroListener
	// pri spusteni nove aktivity je vzdy treba volat metodu setSynchroListener
	public void setSynchroListener(SupportActivity activity)
	{
		try
        {
			mSynchroListener = (OnSynchroListener) activity;
        } 
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnSynchroListener");
        }
	}
	
	
	public void startSynchro() 
	{
		mSynchroListener.onSynchroStart();
	}
	
	
	public void stopSynchro()
	{
		mSynchroListener.onSynchroStop();
	}
	
	
	public interface OnSynchroListener 
	{
        public void onSynchroStart();
        public void onSynchroStop();
    }
}
