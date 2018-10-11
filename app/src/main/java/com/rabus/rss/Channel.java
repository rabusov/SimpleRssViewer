package com.rabus.rss;

import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;

import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import com.rabus.simplerssviewer.BuildConfig;

public class Channel extends DefaultHandler
{
	public String title;
	public String link;
	public String description;
	public String language;
	public String copyright;
	public String webMaster;
	public String pubdate;
	public String lastBuildDate;
	public Category category;
	public String generator;
	public String docs;
	public String cloud;
	public String ttl;
	public String rating;
	public String textInput;
	public String skipHours;
	public String skipDays;
	public Image image;
	public ArrayList<Item> items;
	//
	public String feed_url = null;
	public String filename = null;
	//
	private Item item = null;
	private Boolean inImage, inItem, ready;
	private StringBuffer buf;
	//
	private String[] channelfields = {
			 "title", "link", "description", "language", "copyright"
			, "webMaster", "pubdate", "lastBuildDate", "category", "generator", "docs"
			, "cloud", "ttl", "rating", "textInput", "skipHours", "skipDays"
			};
	Context context=null;
	@SuppressLint("SimpleDateFormat")
	//
	public static final String FEED_ACTION = "com.rabus.rss.FEED";
	public static final String FEED_ACTION_READY = "com.rabus.rss.FEEDREADY";
	public static final String RESPONSE_TITLE = "Response";
	public static final String RESPONSE_ERROR_TITLE = "Error";
	public static final String RESPONSE_OK = "OK";
	public static final String RESPONSE_BAD = "BAD";
	public static final String RESPONSE_NODATA = "No Data";
	public static final String RESPONSE_BADPARSE = "Parser is null";
	public static final String XMLTEMPFILE = "xmltempfile.temp";
	public static File FULLXMLTEMPFILEPATH;
	public static File m_cacheDir;
	//
	static String Tag = "CHANNEL";
	//
	public boolean isReady()
	{
		return ready;
	}
	public Channel() {
		// TODO Auto-generated constructor stub
		inImage = inItem = ready = false;
	}
	public Channel(Context context) {
		// TODO Auto-generated constructor stub
		inImage = inItem = ready = false;
		this.context = context;
		m_cacheDir = new File(context.getDir("Cache", Context.MODE_PRIVATE), "Cache");
		File FULLXMLTEMPFILEPATH = new File(m_cacheDir.getPath() + File.separator + XMLTEMPFILE);

	}
	//Called at the head of each new element
	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		if (BuildConfig.DEBUG)
		{
			Log.d(Tag, "startElement");
			Log.d(Tag, "uri = " + uri);
			Log.d(Tag, "name = " + name);
			Log.d(Tag, "qName = " + qName);
			Log.d(Tag, "atts.getLength() = " + atts.getLength());
		}
		try {

			if("channel".equalsIgnoreCase(name)) {
				items = new ArrayList<Item>();
			} else if("image".equalsIgnoreCase(name)) {
				image = new Image();
				inImage = true;
			} else if("item".equalsIgnoreCase(name)) {
				item = new Item();
				inItem = true;
				inImage = false;
			}
			if (inImage)
			{
				if (BuildConfig.DEBUG) Log.d(Tag, "In image tag");
				///////////////////////////////////////// image
				if("title".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("url".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("link".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("width".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("height".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("description".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				}
			} else if (inItem)
			{
				if (BuildConfig.DEBUG) Log.d(Tag, "In item tag");
				///////////////////////////////////////// item
				if("title".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("link".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("description".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("author".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("category".equalsIgnoreCase(name)) {
					if (item != null)
					{
						item.category = new Category();
						if ((atts != null) && (atts.getLength()>0))
						{
							if (atts.getValue("domain") != null)
								item.category.domain = atts.getValue("domain").toString();
						}
					}
					buf = new StringBuffer();
				} else if("comments".equalsIgnoreCase(name)) {
					buf = new StringBuffer();
				} else if("guid".equalsIgnoreCase(name) ) {
					if (item != null)
						item.guid = new Guid();
					buf = new StringBuffer();
				} else if("pubdate".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("source".equalsIgnoreCase(name) ) {
					buf = new StringBuffer();
				} else if("enclosure".equalsIgnoreCase(name) ) {
					if (BuildConfig.DEBUG) Log.d(Tag, "In enclosure tag");
					if (item != null)
					{
						Enclosure en = new Enclosure();
						if ((atts != null) && (atts.getLength()>0))
						{
							if (atts.getValue("url") != null)
								en.url = atts.getValue("url").toString();
							if (atts.getValue("type") != null)
								en.mime = atts.getValue("type").toString();
							if (atts.getValue("length") != null)
								en.length = Integer.parseInt(atts.getValue("length").toString());
							if (item.Encl == null)
								item.Encl = new ArrayList<>();
							item.Encl.add(en);
						}
					}
					buf = new StringBuffer();
				}
			}
			///////////////////////////////////////////////
			else if (!inItem && !inImage)
			{
				for (String fieldname : channelfields) {
					if (fieldname.equalsIgnoreCase(name))
					{
						buf = new StringBuffer();
						if ("category".equalsIgnoreCase(name)){
							category = new Category();
						}
					}
				}
			}
		} catch (Exception e)
		{
			Log.d(Tag, e.getLocalizedMessage());
		}
	}
	//Called with character data inside elements
	@Override
	public void characters(char ch[], int start, int length) {
		//Don't bother if buffer isn't initialized
		if (BuildConfig.DEBUG){
			Log.d(Tag, "characters: " + ch.length + " start: " + start + " length: " + length);
			Log.d(Tag, ch.toString());
		}
		try {
			if (buf != null) {
				for (int i = start; i < start + length; i++) {
					buf.append(ch[i]);
				}
				if (BuildConfig.DEBUG) Log.d(Tag, "buf: " + buf.toString());
			} else {
				Log.d(Tag, "buf is null");
			}
		} catch (Exception e)
		{
			Log.d(Tag, e.getLocalizedMessage());
		}
	}
	//Called at the tail of each element end
	@Override
	public void endElement(String uri, String name, String qName) {
		if (BuildConfig.DEBUG)
		{
			Log.d(Tag, "endElement");
			Log.d(Tag, "uri = " + uri);
			Log.d(Tag, "name = " + name);
			Log.d(Tag, "qName = " + qName);
		}
		try {
		if("item".equalsIgnoreCase(name)) {
			items.add(item);
			inItem = false;
		} else if ("image".equalsIgnoreCase(name))
		{
			inImage = false;
		}
		if(inImage)
		{
			//////////////////////////////////////////
			if("title".equalsIgnoreCase(name) ) {
				image.title = buf.toString();
			} else if("url".equalsIgnoreCase(name) ) {
				image.url = buf.toString();
			} else if("link".equalsIgnoreCase(name) ) {
				image.link = buf.toString();
			} else if("width".equalsIgnoreCase(name) ) {
				image.width = buf.toString();
			} else if("height".equalsIgnoreCase(name) ) {
				image.height = buf.toString();
			} else if("description".equalsIgnoreCase(name) ) {
				image.description = buf.toString();
			}
		} else if (inItem)
		{
			//////////////////////////////////////////// 
			if("title".equalsIgnoreCase(name) ) {
				item.title = buf.toString();
			} else if("link".equalsIgnoreCase(name) ) {
				item.link = buf.toString();
			} else if( "description".equalsIgnoreCase(name) ) {
				item.description = buf.toString();
			}  else if("author".equalsIgnoreCase(name) ) {
				item.author = buf.toString();
			} else if("category".equalsIgnoreCase(name) ) {
				item.category.Value = buf.toString();
			} else if("comments".equalsIgnoreCase(name) ) {
				item.comments = buf.toString();
			} else if("quid".equalsIgnoreCase(name) ) {
				item.guid.Value = buf.toString();
			} else if("pubdate".equalsIgnoreCase(name) ) {
				item.pubdate = buf.toString();
			} else if("source".equalsIgnoreCase(name) ) {
				item.source.Value = buf.toString();
			} else if("enclosure".equalsIgnoreCase(name)){
				Log.d(Tag, "end of enclosure: " + buf.toString());
			}
		}
		////////////////////////////////////////////
		else if (!inImage && !inItem)
		{
			if ("title".equalsIgnoreCase(name) ) {
				title = buf.toString();
			} else if("link".equalsIgnoreCase(name)) {
				link = buf.toString();
			} else if("description".equalsIgnoreCase(name)) {
				description = buf.toString();
			} else if("language".equalsIgnoreCase(name)) {
				language = buf.toString();
			} else if("copyright".equalsIgnoreCase(name)) {
				copyright = buf.toString();
			} else if("webMaster".equalsIgnoreCase(name)) {
				webMaster = buf.toString();
			} else if("pubdate".equalsIgnoreCase(name)) {
				pubdate = buf.toString();
			} else if("lastBuildDate".equalsIgnoreCase(name)) {
				lastBuildDate = buf.toString();
			} else if("category".equalsIgnoreCase(name)) {
				category.Value = buf.toString();
			} else if("generator".equalsIgnoreCase(name)) {
				generator = buf.toString();
			} else if("docs".equalsIgnoreCase(name)) {
				docs = buf.toString();
			} else if("cloud".equalsIgnoreCase(name)) {
				cloud = buf.toString();
			} else if("ttl".equalsIgnoreCase(name)) {
				ttl = buf.toString();
			} else if("rating".equalsIgnoreCase(name)) {
				rating = buf.toString();
			} else if("textInput".equalsIgnoreCase(name)) {
				textInput = buf.toString();
			} else if("skipHours".equalsIgnoreCase(name)) {
				skipHours = buf.toString();
			} else if("skipDays".equalsIgnoreCase(name)) {
				skipDays = buf.toString();
			}
		}
		} catch (Exception e)
		{
			Log.d(Tag, e.getLocalizedMessage());
		}
		buf = null;
	}
	
	public int size()
	{
		return (items != null) ? items.size() : 0; 
	}
	public void getFromFeed(String url, Context context)
	{
		ready = false;
		feed_url = url;
		this.context = context;
		context.registerReceiver(receiver, new IntentFilter(FEED_ACTION));
		//
		// Retrieve the RSS feed
		//
		try{
			//HttpGet feedRequest = new HttpGet( new URI(feed_url) );
			DownloadRssTask task = new DownloadRssTask(context, FEED_ACTION);
			//task.execute((Runnable) feedRequest);
			task.execute(feed_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public int getFromLocal(String filename)
	{
		int rc = 0;
		ready = false;
		this.filename = filename;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
		    SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(filename, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (items != null)
			rc = items.size();
		ready = true;
		return rc;
	}*/
	public ArrayList<Item> getParsedItems() {
		ArrayList<Item> items_plus = items;
		if ((image != null) && (image.url != null)) {
			// добавим для скачивания ссылку на логотип канала
			Item itm = new Item();
			itm.Encl = new ArrayList<>();
			Enclosure enclosure = new Enclosure();
			enclosure.mime = "image/gif";
			enclosure.url = image.url;
			itm.Encl.add(enclosure);
			items_plus.add(itm);
		}
		return items_plus;
	}
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String responsefilename = intent.getStringExtra(DownloadRssTask.HTTP_RESPONSE);
			String contentType = intent.getStringExtra(DownloadRssTask.HTTP_RESPONSE_CONTENTTYPE);
			Intent intentReady = new Intent(FEED_ACTION_READY);
			if (responsefilename!= null && responsefilename.length()>4) {
				try {
					//String t = URLEncoder.encode(response, "Windows-1251");
					//Parse the response data using SAX
					if (BuildConfig.DEBUG){
						final int buffer_size = 2048;
						InputStream response = new FileInputStream(responsefilename);
						InputStreamReader isr = new InputStreamReader(response, Charset.defaultCharset());
						char[] chars = new char[buffer_size];
						while (isr.read(chars, 0, buffer_size) > 0) {
							String s = new String(chars);
							Log.d(Tag, s);
						}
					}
					File rf = new File(responsefilename);
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser saxParser = factory.newSAXParser();
					Channel parser = new Channel(context);
					try {
						//Run the parsing operation
						//saxParser.parse(new InputSource(new StringReader(response)), parser);
						saxParser.parse(rf, parser);
						title = parser.title;
						link = parser.link;
						description = parser.description;
						language = parser.language;
						copyright = parser.copyright;
						webMaster = parser.webMaster;
						pubdate = parser.pubdate;
						lastBuildDate = parser.lastBuildDate;
						category = parser.category;
						generator = parser.generator;
						docs = parser.docs;
						cloud = parser.cloud;
						ttl = parser.ttl;
						rating = parser.rating;
						textInput = parser.textInput;
						skipHours = parser.skipHours;
						skipDays = parser.skipDays;
						image = parser.image;
						items = parser.items;
						feed_url = parser.feed_url;
						filename = parser.filename;
						ready = true;
						//
						intentReady.putExtra(RESPONSE_TITLE, RESPONSE_OK);
					}
					catch (Exception e)
					{
						//org.apache.harmony.xml.ExpatParser$ParseException: At line 5, column 30: not well-formed (invalid token)
						Log.d(Tag, e.getLocalizedMessage());
						intentReady.putExtra(RESPONSE_ERROR_TITLE,e.getLocalizedMessage());
						intentReady.putExtra(RESPONSE_TITLE, RESPONSE_BADPARSE);
					}
					//
				} catch (Exception e) {
					e.printStackTrace();
					Log.d(Tag, e.getLocalizedMessage());
					intentReady.putExtra(RESPONSE_ERROR_TITLE,e.getLocalizedMessage());
					intentReady.putExtra(RESPONSE_TITLE, RESPONSE_BAD);
				}
			} else {
				Log.d(Tag, "No response");
				intentReady.putExtra(RESPONSE_TITLE, RESPONSE_NODATA);
				intentReady.putExtra(RESPONSE_ERROR_TITLE,RESPONSE_NODATA);
			}
			//Broadcast the completion
			context.sendBroadcast(intentReady);
		}
	};
	/*public void fillAdapter(ArrayAdapter<Item> adapter)
	{
		if (ready)
			if (adapter != null)
			{
				//Clear all current items from the list
				adapter.clear();
				//Add all items from the parsed XML
				for(Item item : getParsedItems()) {
					adapter.add(item);
				}
				//Tell adapter to update the view
				adapter.notifyDataSetChanged();
			}
	}
	public void fillAdapter(ArrayAdapter<Item> adapter, int limit)
	{
		if (ready)
			if (adapter != null)
			{
				//Clear all current items from the list
				adapter.clear();
				//Add all items from the parsed XML
				for(Item item : getParsedItems()) {
					adapter.add(item);
					if (limit-- <= 0)
						break;
				}
				//Tell adapter to update the view
				adapter.notifyDataSetChanged();
			}
	}*/
	public void onDestroy() {
		if (context != null)
		{
			context.unregisterReceiver(receiver);
		}
	}
}
