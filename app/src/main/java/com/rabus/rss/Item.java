package com.rabus.rss;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;

public class Item {
	public String title;
	public String link;
	public String description;
	public String author;
	public Category category;
	public String comments;
	public Guid guid;
	public String pubdate;
	public Source source;
	public ArrayList<Enclosure> Encl;
	//
	public String id;
	public String aDate;
	public String numbers;
	public String stmt;
	static private final String TAG = Item.class.getSimpleName();
	public Item() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Decodes the Image file to bitmap.
	 *
	 * @param p_file
	 *            Image file object
	 * @return decoded bitmap
	 */
	public static void decodeFile(File p_file, ImageButton image) throws ConnectException
	{
		Bitmap m_retBmp = null;
		try
		{
			if (p_file.exists())
			{
				// decode with inSampleSize
				// decode image size
				System.gc();
				int m_scale = 1;
				if (p_file.length() > 400000)
				{
					m_scale = 4;
				}
				else if (p_file.length() > 100000 && p_file.length() < 400000)
				{
					m_scale = 3;
				}
				BitmapFactory.Options m_o2 = new BitmapFactory.Options();
				m_o2.inSampleSize = m_scale;
				InputStream stream = new FileInputStream((p_file.getPath()));

				if (stream != null) Log.d(TAG, "stream is null");
				m_retBmp = BitmapFactory.decodeStream(stream, null, m_o2);
				if (m_retBmp != null) {
					Log.d(TAG, "m_retBmp.getWidth() = " + m_retBmp.getWidth());
					image.setImageBitmap(m_retBmp);
				} else {
					Log.d(TAG, "bitmap is null");
				}
				if (stream != null) stream.close();
			} else Log.d(TAG, "file not exists - " + p_file.getPath());
		}
		catch (Throwable t)
		{
			Log.d(TAG, "Error in decodeFile(File p_file) of ImageLoader", t);
		}
		return;
	}
	public static void decodeFile(File p_file, ImageView image) throws ConnectException
	{
		Bitmap m_retBmp = null;
		try
		{
			if (p_file.exists())
			{
				// decode with inSampleSize
				// decode image size
				System.gc();
				int m_scale = 1;
				if (p_file.length() > 400000)
				{
					m_scale = 4;
				}
				else if (p_file.length() > 100000 && p_file.length() < 400000)
				{
					m_scale = 3;
				}
				BitmapFactory.Options m_o2 = new BitmapFactory.Options();
				m_o2.inSampleSize = m_scale;
				InputStream stream = new FileInputStream((p_file.getPath()));

				if (stream != null) Log.d(TAG, "stream is null");
				m_retBmp = BitmapFactory.decodeStream(stream, null, m_o2);
				if (m_retBmp != null) {
					Log.d(TAG, "m_retBmp.getWidth() = " + m_retBmp.getWidth());
					image.setImageBitmap(m_retBmp);
				} else {
					Log.d(TAG, "bitmap is null");
				}
				if (stream != null) stream.close();
			} else Log.d(TAG, "file not exists - " + p_file.getPath());
		}
		catch (Throwable t)
		{
			Log.d(TAG, "Error in decodeFile(File p_file) of ImageLoader", t);
		}
		return;
	}
}
