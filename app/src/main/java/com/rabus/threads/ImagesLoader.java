package com.rabus.threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.rabus.rss.Constants;
import com.rabus.rss.Enclosure;
import com.rabus.rss.Item;
import com.rabus.simplerssviewer.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by rabusov on 13.09.2016.
 *
 * В папку m_cacheDir
 * загрузить картинки по URLs, заданным в списке entries
 */
public class ImagesLoader  extends Thread
{
    private static final String TAG = ImagesLoader.class.getSimpleName();
    // as a list of image's url
    private ArrayList<Item> entries = null;
    // as a list of list of image's url
    private ArrayList<ArrayList<Item>> listOfEntries = null;
    // where to save images
    private File m_cacheDir = null;
    private String fullPath = "";
    // keeping a working process
    public boolean need2go = true;
    //
    // Methods
    //
    public ImagesLoader(String cacheDir)
    {
        fullPath = cacheDir;
        m_cacheDir = new File(fullPath);
    }
    public void setEntries(ArrayList<com.rabus.rss.Item> objects)
    {
        if (listOfEntries == null)
            listOfEntries = new ArrayList<>();
        if (!listOfEntries.contains(objects))
            listOfEntries.add(objects);
    }
    private Bitmap getBitmap(String p_url) throws ConnectException
    {
        if (m_cacheDir==null) return null;
        System.gc();
        // filename to save bitmap on device
        String m_fileName = String.valueOf(p_url.hashCode());
        File m_file = new File(m_cacheDir, m_fileName);
        Log.d(TAG, m_file.getAbsolutePath());
        // from SD cache .. if already loaded
        Bitmap m_bitmap = decodeFile(m_file);
        if (m_bitmap != null)
            return m_bitmap;
        // if not loaded still
        // from web
        Log.d(TAG, "Try to get from web...");
        try
        {
            URL a = new URL(p_url);
            URLConnection conn=a.openConnection();
            m_bitmap = null;
            int m_connectionCode = 0;
            m_connectionCode = ((HttpURLConnection)conn).getResponseCode();
            switch(m_connectionCode)
            {
                case 301:
                case 302:
                case 307:
                    p_url = conn.getHeaderField("Location");
                    m_connectionCode = HttpURLConnection.HTTP_OK;
            }

            if (m_connectionCode == HttpURLConnection.HTTP_OK)
            {
                InputStream m_is = new URL(p_url).openStream();
                OutputStream m_os = new FileOutputStream(m_file);
                CopyStream(m_is, m_os);
                m_os.close();
                m_os = null;
                m_bitmap = decodeFile(m_file);
                m_is.close();
                m_is = null;
                Log.d(TAG, "Save to " + m_file.getAbsolutePath());
                //HttpConnection.getHttpUrlConnection(p_url).disconnect();
                ((HttpURLConnection)conn).disconnect();
            }
        }
        catch (ConnectException c)
        {
            throw c;
        }
        catch (Throwable t)
        {
            Log.d(TAG, "Error in getBitmap(String p_url) of ImageLoader", t);
        }
        return m_bitmap;
    }
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
    private Bitmap decodeFile(File p_file) throws ConnectException
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
                m_retBmp = BitmapFactory.decodeFile(p_file.getPath(), m_o2);
            } else Log.d(TAG, "file not exists - " + p_file.getPath());
        }
        catch (Throwable t)
        {
            Log.d(TAG, "Error in decodeFile(File p_file) of ImageLoader", t);
        }
        if (m_retBmp == null)
            Log.d(TAG, "m_retBmp is null");
        return m_retBmp;
    }
    public void quit()
    {
        need2go = false;
    }
    public void run()
    {
        if (m_cacheDir==null) return;
        need2go = true;
        try
        {
            while (need2go)
            {
                entries = null;
                if (listOfEntries != null && listOfEntries.size()>0 )
                {
                    // first in = first out
                    // lets working with list with index == 0
                    entries = listOfEntries.get(0);
                }
                if (entries != null)
                {
                    for (int i = 0; i < entries.size(); i++ )
                    {
                        // check on every step of work
                        if (!need2go) return;
                        Item currentItem = entries.get(i);
                        if (currentItem.Encl != null)
                        {
                            Bitmap m_bmp = null;
                            Enclosure encl = null;
                            for (int j=0; j < currentItem.Encl.size(); j++)
                            {
                                // check on every step of work
                                if (!need2go) return;
                                encl = currentItem.Encl.get(j);
                                if (encl != null)
                                {
                                    if ( (encl.url != null) )
                                    {
                                        if ( (encl.mime.startsWith(Constants.MIME_IMAGE_START)) && (encl.Local_filename == null))
                                        {
                                            m_bmp = getBitmap(encl.url);
                                            if (m_bmp != null)
                                            {
                                                j += currentItem.Encl.size();
                                            }
                                        } else Log.d(TAG, "encl.bit_map is not null");
                                    } else Log.d(TAG, "encl.url is null");
                                } else Log.d(TAG, "encl is null");
                            }
                        } else Log.d(TAG, "currentItem.Encl is null");
                    }
                    // Delete list with index 0 from stack (listOfEntries)
                    listOfEntries.remove(0);
                } else sleep(500);
            }
        }
        catch (Throwable t)
        {
            Log.d(TAG, t.toString());
        }
    }
}
