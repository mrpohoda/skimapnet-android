package net.skimap.fragments;

import net.skimap.R;
import net.skimap.activities.MapActivity;
import net.skimap.activities.SkimapApplication;
import net.skimap.utililty.Version;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HelpFragment extends Fragment implements SkimapApplication.OnSynchroListener
{
	private View mRootView;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
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

        // naslouchani synchronizace
        ((SkimapApplication) getSupportActivity().getApplicationContext()).setSynchroListener(this);
        
        // aktualizace stavu progress baru
    	boolean synchro = ((SkimapApplication) getSupportActivity().getApplicationContext()).isSynchronizing();
    	getSupportActivity().setProgressBarIndeterminateVisibility(synchro ? Boolean.TRUE : Boolean.FALSE);
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
