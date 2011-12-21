package net.skimap.utililty;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.skimap.R;
import net.skimap.activities.MapActivity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.text.Html;

import com.localytics.android.LocalyticsSession;

public class Version 
{
	public static String LANGUAGE_CS = "cs";
	public static String LANGUAGE_SK = "sk";
	
	
	@SuppressWarnings("rawtypes")
	public static String getApplicationVersion(Context context, Class cls)
	{
		try 
		{
			ComponentName comp = new ComponentName(context, cls);
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
			return pinfo.versionName;
		} 
		catch (android.content.pm.PackageManager.NameNotFoundException e) 
		{
			return "";
		}
	}
	
	
	public static void tryShowIntoDialog(final Context context, final LocalyticsSession localyticsSession)
	{
		Settings settings = new Settings(context);
		String settingsVersion = settings.getCurrentVersion();
		String currentVersion = getApplicationVersion(context, MapActivity.class);
		if(!currentVersion.contentEquals(settingsVersion))
		{
			// intro dialog
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setCancelable(false);
			alert.setIcon(R.drawable.icon);
			alert.setTitle(R.string.dialog_intro_title);
			alert.setMessage(Html.fromHtml(context.getString(R.string.dialog_intro_text)));
			alert.setPositiveButton(R.string.dialog_intro_ok, new DialogInterface.OnClickListener()
			{
				final long startTime = System.currentTimeMillis();
				
				public void onClick(DialogInterface dialog, int id)
				{
					long stopTime = System.currentTimeMillis();
					String value = Localytics.createValueInstallIntro(stopTime - startTime);
					
					Map<String,String> localyticsValues = new HashMap<String,String>(); // Localytics hodnoty
			        localyticsValues.put(Localytics.ATTR_INSTALL_INTRO, value); // Localytics atribut
			        localyticsSession.tagEvent(Localytics.TAG_INSTALL, localyticsValues); // Localytics
				}
			});
			// TODO: vyzva k ohodnoceni na marketu
//			alert.setNeutralButton(R.string.dialog_intro_rate, new DialogInterface.OnClickListener()
//			{
//				public void onClick(DialogInterface dialog, int id)
//				{
//					
//				}
//			});
			alert.show();
			
			// aktualizuje settings
			settings.setCurrentVersion(currentVersion);
		}
	}
	
	
	public static String getLanguage()
	{	
		// vrati ISO 639-1 jazykovy kod, napr. cs, en, sk
		return Locale.getDefault().getLanguage();
	}
}
