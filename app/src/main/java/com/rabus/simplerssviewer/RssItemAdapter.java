package com.rabus.simplerssviewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rabus.rss.Constants;
//import com.rabus.rss.Enclosure;
import com.rabus.rss.Item;
//import com.rabus.rss.RestTask;

public class RssItemAdapter extends ArrayAdapter<Item> {
	private final ArrayList<Item> entries;
	private final Activity activity;
	//
	private InputStream m_is = null;
	private OutputStream m_os = null;
	public File m_cacheDir;
	private Context p_context = null;
	//
	private final String TAG = RssItemAdapter.class.getSimpleName();
	//
	public Bitmap ImageAdapter = null;
	public String url_image = null;


	public RssItemAdapter(final Activity a, int textViewResourceId, ArrayList<Item> objects) {
		super(a, textViewResourceId, objects);
		this.entries = objects;
	    activity = a;
	    p_context = activity.getApplicationContext();
		m_cacheDir = new File(p_context.getDir("Cache", Context.MODE_PRIVATE), "Cache");
	}
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		ViewItemHolder holder = null;
		if ( convertView != null )
        {
        	// an exists Entity
            holder = (ViewItemHolder) convertView.getTag();
        } else {        	
        	// a new Entity - we need to create it and bind it with controls
        	convertView = (activity.getLayoutInflater()).inflate(R.layout.listoflist_item, parent, false);
        	if (convertView != null)
        	{
	        	holder = new ViewItemHolder();
	        	if (holder != null) {
		        	holder.image = (ImageButton) convertView.findViewById(R.id.rssimage);
		        	holder.title = (TextView) convertView.findViewById(R.id.itemtitle);
		        	holder.date = (TextView) convertView.findViewById(R.id.itemdate);
		        	holder.description = (TextView) convertView.findViewById(R.id.itemdescription);
		        	holder.position = position;
		        	if (convertView.isSelected()) {}
			    	convertView.setTag(holder);
	        	}
        	}
        }
        // now holder is must be ready
        if (holder != null)
        {
			Log.i(TAG, "holder is ready for position = " + position);
        	// get an item
			Item pi = entries.get(position);
	        if (pi != null) {
	        	// if there is an enclosure and image on it
				holder.image.setImageResource(R.drawable.rsslogo);
	        	if ( pi.Encl != null )
	        	{
					Log.i(TAG, "Encl.size = " + pi.Encl.size());
	        		for (int j = 0; j < pi.Encl.size(); j++)
	        		{
	        			if ( (pi.Encl.get(j).mime.startsWith(Constants.MIME_IMAGE_START)) )
	        			{
	        				File p_file = null;
							String fName = String.valueOf(pi.Encl.get(j).url.hashCode());
	        				// if not loaded today
	        				if (pi.Encl.get(j).Local_filename == null) {
	        					// check if it loaded yesterday
								p_file = new File(m_cacheDir, fName );
	        				} else {
	        					p_file = new File(m_cacheDir, pi.Encl.get(j).Local_filename);
	        				}
							// file exists on device
							if (p_file != null && p_file.exists()) {
								Log.i(TAG, "p_file.getName() = " + p_file.getName());
								try {
									Item.decodeFile(p_file, holder.image);
									j += pi.Encl.size();
								} catch (ConnectException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								// file doesn't exists on device
								Log.i(TAG, "p_file is null or not exists");
								Log.i(TAG, "url: " + pi.Encl.get(j).url);
								Log.i(TAG, "Need filename: " + fName );
								Log.i(TAG, "url.parse: " + Uri.parse(pi.Encl.get(j).url));
							}
	        			}
	        		}
	        	}
	        	holder.date.setText( pi.pubdate );
	        	holder.title.setText( pi.title );
	        	holder.description.setText( pi.description );
	        	holder.position = position;
	        	holder.image.setTag(position);
	        	//
	        } //else Log.d(TAG, "pi is null");
        } //else Log.d(TAG, "holder is null");
		/*convertView.setOnLongClickListener(new View.OnLongClickListener() {
			// Called when the user long-clicks on someView
			public boolean onLongClick(View view) {
				view.setSelected(true);
				MainActivity.lvItemsLayout.setVisibility(View.GONE);
				MainActivity.lvChannales.setVisibility(View.VISIBLE);
				return true;
			}
		});*/
        return convertView;
    }
}
