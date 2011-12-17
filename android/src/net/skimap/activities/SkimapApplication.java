package net.skimap.activities;

import android.app.Application;
import android.support.v4.app.Fragment;

public class SkimapApplication extends Application
{
	private boolean mSynchronizing = false;
	private OnSynchroListener mSynchroListener;
	
	
	public boolean isSynchronizing() { return mSynchronizing; }
	public void setSynchronizing(boolean synchronizing) { mSynchronizing = synchronizing; }
	
	
	// kazdy fragment, ktery chce odchytavat stav synchronizace musi implementovat rozhrani OnSynchroListener
	// pri spusteni noveho fragmentu je vzdy treba volat metodu setSynchroListener
	public void setSynchroListener(Fragment fragment)
	{
		try
        {
			mSynchroListener = (OnSynchroListener) fragment;
        } 
        catch (ClassCastException e)
        {
            throw new ClassCastException(fragment.toString() + " must implement OnSynchroListener.");
        }
	}
	
	
	public void startSynchro() 
	{
		mSynchroListener.onSynchroStart();
	}
	
	
	public void stopSynchro(int result)
	{
		mSynchroListener.onSynchroStop(result);
	}
	
	
	public interface OnSynchroListener 
	{
        public void onSynchroStart();
        public void onSynchroStop(int result);
    }
}
