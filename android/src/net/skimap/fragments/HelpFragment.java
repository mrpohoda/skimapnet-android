package net.skimap.fragments;

import java.util.HashMap;
import java.util.Map;

import net.skimap.R;
import net.skimap.activities.MapActivity;
import net.skimap.activities.SkimapApplication;
import net.skimap.utililty.Localytics;
import net.skimap.utililty.Version;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.localytics.android.LocalyticsSession;

public class HelpFragment extends Fragment implements SkimapApplication.OnSynchroListener
{
	private View mRootView;
	
	private LocalyticsSession mLocalyticsSession;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        // Localytics
	    this.mLocalyticsSession = new LocalyticsSession(getActivity().getApplicationContext(), Localytics.KEY);
	    this.mLocalyticsSession.open(); // otevre session
	    this.mLocalyticsSession.upload(); // upload dat
	    // At this point, Localytics Initialization is done.  After uploads complete nothing
	    // more will happen due to Localytics until the next time you call it.
    }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance)
	{	
		// nastaveni view
		setHasOptionsMenu(true);
		mRootView = inflater.inflate(R.layout.layout_help, container, false);
		setView();
		return mRootView;
	}
	
	
	@Override
    public void onResume()
	{
        super.onResume();
        
        // Localytics
        this.mLocalyticsSession.open(); // otevre session pokud neni jiz otevrena

        // naslouchani synchronizace
        ((SkimapApplication) getSupportActivity().getApplicationContext()).setSynchroListener(this);
        
        // aktualizace stavu progress baru
    	boolean synchro = ((SkimapApplication) getSupportActivity().getApplicationContext()).isSynchronizing();
    	getSupportActivity().setProgressBarIndeterminateVisibility(synchro ? Boolean.TRUE : Boolean.FALSE);

        Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
        localyticsValues.put(Localytics.ATTR_ACTIVITY_FRAGMENT, Localytics.VALUE_ACTIVITY_FRAGMENT_HELP); // Localytics atribut
		mLocalyticsSession.tagEvent(Localytics.TAG_ACTIVITY, localyticsValues); // Localytics
    }
	
	
	@Override
	public void onPause()
	{
		// Localytics
	    this.mLocalyticsSession.close();
	    this.mLocalyticsSession.upload();
	    super.onPause();
	}

	
	@Override
	public void onSynchroStart()
	{
		// zapnuti progress baru
		getSupportActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);
	}


	@Override
	public void onSynchroStop(int result)
	{
		// vypnuti progress baru
		getSupportActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
	}
	
	
	private void setView()
	{
		TextView about = (TextView) mRootView.findViewById(R.id.layout_help_about_text);
		TextView info = (TextView) mRootView.findViewById(R.id.layout_help_info_text);
		TextView changes = (TextView) mRootView.findViewById(R.id.layout_help_changes_text);
		
		String version = Version.getApplicationVersion(getActivity(), MapActivity.class);
		String versionString = getString(R.string.layout_help_about_text_pre) + " " + version + getString(R.string.layout_help_about_text);
		about.setText(Html.fromHtml(versionString));
		about.setMovementMethod(LinkMovementMethod.getInstance());
		
		info.setText(Html.fromHtml(getString(R.string.layout_help_info_text)));
		changes.setText(Html.fromHtml(getString(R.string.layout_help_changes_text)));
	}
}
