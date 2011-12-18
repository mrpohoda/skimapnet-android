package net.skimap.activities;

import android.app.Application;
import android.support.v4.app.Fragment;

public class SkimapApplication extends Application
{
	private boolean mSynchronizing = false;
	// TODO: 3 listenery, navic pro favourites
	private OnSynchroListener mSynchroListener1;
//	private OnSynchroListener mSynchroListener2;
	
	
	public boolean isSynchronizing() { return mSynchronizing; }
	public void setSynchronizing(boolean synchronizing) { mSynchronizing = synchronizing; }
	
	
	// kazdy fragment, ktery chce odchytavat stav synchronizace musi implementovat rozhrani OnSynchroListener
	// pri spusteni noveho fragmentu je vzdy treba volat metodu setSynchroListener
	public void setSynchroListener(Fragment fragment)
	{
		try
        {
			mSynchroListener1 = (OnSynchroListener) fragment;
//			mSynchroListener2 = null;
        } 
        catch (ClassCastException e)
        {
            throw new ClassCastException(fragment.toString() + " must implement OnSynchroListener.");
        }
	}
	
	
//	public void setSynchroListener1(Fragment fragment1)
//	{
//		try
//        {
//			mSynchroListener1 = (OnSynchroListener) fragment1;
//        } 
//        catch (ClassCastException e)
//        {
//            throw new ClassCastException(fragment1.toString() + " must implement OnSynchroListener.");
//        }
//	}
//	
//	
//	public void setSynchroListener2(Fragment fragment2)
//	{
//		try
//        {
//			mSynchroListener1 = (OnSynchroListener) fragment2;
//        } 
//        catch (ClassCastException e)
//        {
//            throw new ClassCastException(fragment2.toString() + " must implement OnSynchroListener.");
//        }
//	}
	
	
	public void startSynchro() 
	{
		if(mSynchroListener1!=null) mSynchroListener1.onSynchroStart();
//		if(mSynchroListener2!=null) mSynchroListener2.onSynchroStart();
	}
	
	
	public void stopSynchro(int result)
	{
		if(mSynchroListener1!=null) mSynchroListener1.onSynchroStop(result);
//		if(mSynchroListener2!=null) mSynchroListener2.onSynchroStop(result);
	}
	
	
	public interface OnSynchroListener 
	{
        public void onSynchroStart();
        public void onSynchroStop(int result);
    }
}
