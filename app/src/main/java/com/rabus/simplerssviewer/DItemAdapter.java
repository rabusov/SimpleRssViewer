package com.rabus.simplerssviewer;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class DItemAdapter extends ArrayAdapter<DItem> {
	private final Activity activity;
	private final ArrayList<DItem> entries;

	private class ViewHolder {
        public TextView nameView;
        public TextView valueView;
		//public ImageButton imageButton;
    }

	public DItemAdapter(final Activity a, final int textViewResourceId, final ArrayList<DItem> entries) {
        super(a, textViewResourceId, entries);
        this.entries = entries;
        activity = a;
        // i'd like to has sorted list of files
    }
    @SuppressLint("ResourceAsColor")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
        if ( convertView != null )
        {
            holder = (ViewHolder) convertView.getTag();
        } else {        	
        	convertView = (activity.getLayoutInflater()).inflate(R.layout.list_item, parent, false);
        	holder = new ViewHolder();
        	holder.nameView = (TextView) convertView.findViewById(R.id.named);
        	holder.valueView = (TextView) convertView.findViewById(R.id.value);
			//holder.imageButton = (ImageButton) convertView.findViewById(R.id.img);
	    	convertView.setTag(holder);
        }
		//android:textAppearance="?attr/textAppearanceListItem"
		//android:textAppearance="?android:attr/textAppearanceSmallInverse"
        if (holder != null)
        {
        	DItem Item = entries.get(position);
	        if (Item != null) {
	        	holder.nameView.setText( Item.Name );
	        	holder.valueView.setText( Item.Value);
	        } else {
	        	holder.nameView.setText("unknown");
	        	holder.valueView.setText("empty");
	        }
        }
        
        return convertView;
    }
    // list only names of files 
    public String[] getNames()
    {
    	String[] rc = new String[entries.size()];
    	int i = 0;
    	for (DItem fi : entries)
    	{
    		rc[i] = fi.Name;
    		i++;
    	}
    	return rc;
    }

}
