package com.rabus.simplerssviewer;

import android.provider.BaseColumns;

public interface SQLConstants extends BaseColumns {
	public static final String TABLE_NAME = "feeds";
	// _id  from BaseColumns
	public static final String COL_url = "url";
	public static final String COL_name = "name";
	public static final String COL_urlhashcode = "urlhashcode";
	static final int URLMINLENGTH = 10;
	//
	public static final String CHANNEL_NAME_LINK_UPDATE = "com.rabus.rss.CHANNEL_NAME_LINK_UPDATE";
	public static final String CHANNEL_NAME_LINK_ADD = "com.rabus.rss.CHANNEL_NAME_LINK_ADD";
}
