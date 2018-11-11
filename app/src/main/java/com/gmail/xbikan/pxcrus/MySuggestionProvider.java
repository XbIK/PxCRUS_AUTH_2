package com.gmail.xbikan.pxcrus;

import android.content.SearchRecentSuggestionsProvider;

import static android.content.SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES;

/**
 * Created by xbika on 13-Dec-17.
 */

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.gmail.xbikan.pxcrus.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}