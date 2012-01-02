package net.skimap.content;

import android.content.SearchRecentSuggestionsProvider;

public class CustomSuggestionProvider extends SearchRecentSuggestionsProvider
{
	public final static String AUTHORITY = "net.skimap.content.CustomSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public CustomSuggestionProvider()
    {
        setupSuggestions(AUTHORITY, MODE);
    }
}
