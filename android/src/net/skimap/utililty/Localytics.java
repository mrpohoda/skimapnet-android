package net.skimap.utililty;


public class Localytics
{
	//public static String KEY = "3ed8848e93e296614cc7e45-6c78e428-2b0a-11e1-0c8e-001b3da7bd76";
	public static String KEY = "debug";
	
	public static String TAG_BUTTON = "button";
	public static String ATTR_BUTTON_MAP = "map";
	public static String ATTR_BUTTON_LIST = "list";
	public static String ATTR_BUTTON_REFRESH = "refresh";
	public static String ATTR_BUTTON_PREFERENCES = "preferences";
	public static String ATTR_BUTTON_LOCATION = "map location";
	public static String ATTR_BUTTON_LAYER = "map layer";
	public static String ATTR_BUTTON_SHARE = "share";
	public static String VALUE_BUTTON_FROM_MAP = "from map";
	public static String VALUE_BUTTON_FROM_LIST = "from list";
	public static String VALUE_BUTTON_FROM_DETAIL = "from detail";
	public static String VALUE_BUTTON_FROM_HELP = "from help";
	public static String VALUE_BUTTON_FROM_PREFERENCES = "from preferences";
	public static String VALUE_BUTTON_LOCATION_CURRENT = "current";
	public static String VALUE_BUTTON_LOCATION_LAST = "last";
	public static String VALUE_BUTTON_LAYER_NORMAL = "normal";
	public static String VALUE_BUTTON_LAYER_SATELLITE = "satellite";
	
	public static String TAG_PREFERENCE = "preference";
	public static String ATTR_PREFERENCE_HELP = "help";
	public static String ATTR_PREFERENCE_CACHE = "clear cache";
	public static String ATTR_PREFERENCE_FEEDBACK = "feedback";
	public static String VALUE_PREFERENCE_FROM_PREFERENCES = "from preferences";
	
	public static String TAG_MAP = "map";
	public static String ATTR_MAP_ZOOM = "zoom";
	
	public static String TAG_ACTIVITY = "activity";
	public static String ATTR_ACTIVITY_FRAGMENT = "fragment";
	public static String VALUE_ACTIVITY_FRAGMENT_MAP = "map";
	public static String VALUE_ACTIVITY_FRAGMENT_LIST = "list";
	public static String VALUE_ACTIVITY_FRAGMENT_DETAIL = "detail";
	public static String VALUE_ACTIVITY_FRAGMENT_HELP = "help";
	public static String VALUE_ACTIVITY_FRAGMENT_PREFERENCES = "preferences";
	
	public static String TAG_INSTALL = "install";
	public static String ATTR_INSTALL_INTRO = "intro dialog";
	
	public static String TAG_SYNCHRO = "synchro";
	public static String ATTR_SYNCHRO_SHORT_STATUS = "status short";
	public static String ATTR_SYNCHRO_LONG_STATUS = "status long";
	public static String ATTR_SYNCHRO_AUTO = "automatic";
	public static String VALUE_SYNCHRO_STATUS_ONLINE = "online";
	public static String VALUE_SYNCHRO_STATUS_OFFLINE = "offline";
	public static String VALUE_SYNCHRO_STATUS_CANCELED = "canceled";
	public static String VALUE_SYNCHRO_STATUS_ERROR = "error";
	public static String VALUE_SYNCHRO_FROM_MAP = "from map";
	public static String VALUE_SYNCHRO_FROM_LIST = "from list";
	
	public static String TAG_LINK = "link";
	public static String ATTR_LINK_IMAGE = "image";
	public static String ATTR_LINK_WEB = "web";
	public static String VALUE_LINK_IMAGE_MAP = "map";
	public static String VALUE_LINK_IMAGE_METEOGRAM = "meteogram";
	public static String VALUE_LINK_IMAGE_WEBCAM = "webcam";
	public static String VALUE_LINK_WEB_SKIMAP = "skimap";
	public static String VALUE_LINK_WEB_HOME = "homepage";
	public static String VALUE_LINK_WEB_SNOW = "snow report";
	public static String VALUE_LINK_WEB_WEATHER = "weather report";
	public static String VALUE_LINK_WEB_WEBCAMS = "webcams";
	
	public static String createValueInstallIntro(long duration)
	{
		String timeString;
		if(duration<=2000) timeString = "0-2 s";
		else if(duration<=4000) timeString = "2-4 s";
		else if(duration<=6000) timeString = "4-6 s";
		else if(duration<=8000) timeString = "6-8 s";
		else if(duration<=10000) timeString = "8-10 s";
		else if(duration<=15000) timeString = "10-15 s";
		else if(duration<=20000) timeString = "15-20 s";
		else if(duration<=30000) timeString = "20-30 s";
		else if(duration<=60000) timeString = "30-60 s";
		else timeString = ">60 s";
		return timeString;
	}
}
