package com.rabus.simplerssviewer;

import java.io.File;
import java.net.ConnectException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rabus.rss.Item;

public class AboutRssActivity extends Activity {
	DItem ditem = null; 
	private ArrayList<DItem> DItems;
	DItemAdapter adapter = null;
	private final String Tag = AboutRssActivity.class.getSimpleName();
	File m_cacheDir;
	//TextView tvLink = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_rss);
		m_cacheDir = new File(getApplicationContext().getDir("Cache", Context.MODE_PRIVATE), "Cache");
		/*
		tvLink = (TextView) findViewById(R.id.Channel_Link);
		tvLink.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 GoToLink(v);
			}
		});
		*/
		DItems = new ArrayList<DItem>();
		FillForm();
	}

	private void FillDItems()
	{
		if (DItems != null)
		{
			if (DItems.size()>0) DItems.clear();
			
			if (MainActivity.rss_channel.cloud != null) {
				ditem = new DItem();
				ditem.Name = "cloud";
				ditem.Value = MainActivity.rss_channel.cloud;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.copyright != null) {
				ditem = new DItem();
				ditem.Name = "copyright";
				ditem.Value = MainActivity.rss_channel.copyright;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.description != null) {
				ditem = new DItem();
				ditem.Name = "description";
				ditem.Value = MainActivity.rss_channel.description;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.docs != null) {
				ditem = new DItem();
				ditem.Name = "docs";
				ditem.Value = MainActivity.rss_channel.docs;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.generator != null) {
				ditem = new DItem();
				ditem.Name = "generator";
				ditem.Value = MainActivity.rss_channel.generator;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.language != null) {
				ditem = new DItem();
				ditem.Name = "language";
				ditem.Value = MainActivity.rss_channel.language;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.lastBuildDate != null) {
				ditem = new DItem();
				ditem.Name = "lastBuildDate";
				ditem.Value = MainActivity.rss_channel.lastBuildDate;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.link != null) {
				ditem = new DItem();
				ditem.Name = "link";
				ditem.Value = MainActivity.rss_channel.link;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.pubdate != null) {
				ditem = new DItem();
				ditem.Name = "pubdate";
				ditem.Value = MainActivity.rss_channel.pubdate;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.rating != null) {
				ditem = new DItem();
				ditem.Name = "rating";
				ditem.Value = MainActivity.rss_channel.rating;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.skipDays != null) {
				ditem = new DItem();
				ditem.Name = "skipDays";
				ditem.Value = MainActivity.rss_channel.skipDays;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.skipHours != null) {
				ditem = new DItem();
				ditem.Name = "skipHours";
				ditem.Value = MainActivity.rss_channel.skipHours;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.textInput != null) {
				ditem = new DItem();
				ditem.Name = "textInput";
				ditem.Value = MainActivity.rss_channel.textInput;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.title != null) {
				ditem = new DItem();
				ditem.Name = "title";
				ditem.Value = MainActivity.rss_channel.title;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.ttl != null) {
				ditem = new DItem();
				ditem.Name = "ttl";
				ditem.Value = MainActivity.rss_channel.ttl;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.webMaster != null) {
				ditem = new DItem();
				ditem.Name = "webMaster";
				ditem.Value = MainActivity.rss_channel.webMaster;
				DItems.add(ditem);
			}
			if (MainActivity.rss_channel.image != null)
			{
				if (MainActivity.rss_channel.image.description != null) {
					ditem = new DItem();
					ditem.Name = "image.description";
					ditem.Value = MainActivity.rss_channel.image.description;
					DItems.add(ditem);
				}
				if (MainActivity.rss_channel.image.height != null) {
					ditem = new DItem();
					ditem.Name = "image.height";
					ditem.Value = MainActivity.rss_channel.image.height;
					DItems.add(ditem);
				}
				if (MainActivity.rss_channel.image.link != null) {
					ditem = new DItem();
					ditem.Name = "image.link";
					ditem.Value = MainActivity.rss_channel.image.link;
					DItems.add(ditem);
				}
				if (MainActivity.rss_channel.image.title != null) {
					ditem = new DItem();
					ditem.Name = "image.title";
					ditem.Value = MainActivity.rss_channel.image.title;
					DItems.add(ditem);
				}
				if (MainActivity.rss_channel.image.url != null) {
					ditem = new DItem();
					ditem.Name = "image.url";
					ditem.Value = MainActivity.rss_channel.image.url;
					DItems.add(ditem);
				}
				if (MainActivity.rss_channel.image.width != null) {
					ditem = new DItem();
					ditem.Name = "image.width";
					ditem.Value = MainActivity.rss_channel.image.width;
					DItems.add(ditem);
				}
			}

			ditem = new DItem();
			ditem.Name = "Items";
			ditem.Value  = String.valueOf( MainActivity.rss_channel.size() );
			DItems.add(ditem);
		}
	}
	private void FillForm()
	{
		if (MainActivity.rss_channel != null)
		{
			if (MainActivity.rss_channel.size() > 0)
			{
				TextView tv = (TextView) findViewById(R.id.Channel_Title);
				tv.setText(MainActivity.rss_channel.title);
	
				tv = (TextView) findViewById(R.id.Channel_Link);
				tv.setText(Html.fromHtml( "<u>" + MainActivity.rss_channel.link + "</u>") );
	
				tv = (TextView) findViewById(R.id.Channel_Description);
				tv.setText(MainActivity.rss_channel.description);

				tv = (TextView) findViewById(R.id.Channel_Copyright);
				tv.setText(MainActivity.rss_channel.copyright);
				ImageView iv = (ImageView) findViewById(R.id.Channel_Image);
				if (MainActivity.rss_channel.image != null)
					if (MainActivity.rss_channel.image.bmp != null)
					{
						iv.setImageBitmap(MainActivity.rss_channel.image.bmp);
					} else {

						File p_file = null;
						String fName = String.valueOf(MainActivity.rss_channel.image.url.hashCode());
						// if not loaded today

						p_file = new File(m_cacheDir, fName);

						// file exists on device
						if (p_file != null && p_file.exists()) {
							Log.i(Tag, "p_file.getName() = " + p_file.getName());
							try {
								Item.decodeFile(p_file, iv);
							} catch (ConnectException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							// file doesn't exists on device
							Log.i(Tag, "p_file is null or not exists");
							Log.i(Tag, "url: " + MainActivity.rss_channel.image.url);
							Log.i(Tag, "Need filename: " + fName);
							Log.i(Tag, "url.parse: " + Uri.parse(MainActivity.rss_channel.image.url));
						}

					}

				ListView LV = (ListView) findViewById(R.id.Channel_Properties);
				FillDItems();
				if (DItems != null)
				{
					adapter = new DItemAdapter(this, R.layout.list_item, DItems);
					LV.setAdapter(adapter);
				}
			}
		} else finish();
	}
	public void GoToLink(View view)
	{
		if (view != null)
		{
			String link = ((TextView )view).getText().toString();
			if ( (link != null) && (link.length()>10))
			{
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), WebViewActivity.class);
				intent.putExtra("link", link );
				startActivity(intent);
			}
		}
	}
}
