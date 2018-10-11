package com.rabus.rss;

/*import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
*/
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;

import com.rabus.simplerssviewer.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import static com.rabus.rss.Channel.XMLTEMPFILE;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class DownloadRssTask extends AsyncTask<String, Void, String> {
	public static final String HTTP_RESPONSE = "httpResponse";
	public static final String HTTP_RESPONSE_CONTENTTYPE = "httpResponseContentType";
	private static Context mContext;
	private String mAction;
	private String contentType = "utf-8";

	static final String Tag = DownloadRssTask.class.getSimpleName();
	@Override
	protected String doInBackground(String... urls) {
		try {
			return loadXmlFromNetwork(urls[0]);
		} catch (IOException e) {
			return mContext.getResources().getString(R.string.connection_error);
		} catch (XmlPullParserException e) {
			return mContext.getResources().getString(R.string.xml_error);
		}
	}
	public DownloadRssTask(Context context, String action) {
		mContext = context;
		mAction = action;
	}
	// Uploads XML from stackoverflow.com, parses it, and combines it with
	// HTML markup. Returns HTML string.
	private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
		InputStream stream = null;
		String rc="";
		try {
			stream = downloadUrl(urlString);
			//ByteArrayOutputStream oas = new ByteArrayOutputStream();
			File d = new File(mContext.getDir("Cache", Context.MODE_PRIVATE), "Cache");
			if (!d.exists()) d.mkdir();
			File aFile = new File(d.getPath() + File.separator + XMLTEMPFILE);
			try {
				FileOutputStream fos = new FileOutputStream(aFile);
				copyStream(stream, fos, contentType);
				fos.close();
				rc = aFile.getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return rc;
	}
	public static File getDataDirectoryName(Context aContext)
	{
		if (mContext==null) mContext = aContext;

		String m_imagePath = "";
		File rc = null;
		/**
		 * Check the Device SD card exits or not and assign path according this
		 * condition.
		 */
		/*if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			m_imagePath = Environment.getExternalStorageDirectory() + Constants.ANDROIDDATADIRECTORY + aContext.getPackageName();
			rc = new File(m_imagePath);
		}
		else
		{
			rc = new File(aContext.getDir("Cache", Context.MODE_PRIVATE), "Cache");
		}*/
		rc = new File(aContext.getDir("Cache", Context.MODE_PRIVATE), "Cache");
		if (!rc.exists()) rc.mkdir();
		return rc;
	}

	@Override
	protected void onPostExecute(String result) {
		Intent intent = new Intent(mAction);
		intent.putExtra(HTTP_RESPONSE, result);
		intent.putExtra(HTTP_RESPONSE_CONTENTTYPE, contentType);
		//Broadcast the completion
		mContext.sendBroadcast(intent);
	}
	/*
	* Copy from any CharSet of InputStream to file with Charset.defaultCharset()
	* */
	private void copyStream(InputStream is, OutputStream os, String ct)
	{
		final int buffer_size = 2048;
		boolean needConvert = false, replaced = false;
		InputStreamReader reader = null;
		int all = 0;
		try {
			reader = new InputStreamReader(is, ct);
			try
			{
				if (!ct.toLowerCase().equals(Charset.defaultCharset()))
				{
					needConvert = true;
				}
				char[] chars=new char[buffer_size];
				for(;;) {
					int count = reader.read(chars, 0, buffer_size);
					if (count == -1) break;
					all += count;
					Log.d(Tag, "all: " + all);
					String s = new String(chars);
					if (needConvert) {
						if (!replaced) {
							s = s.replace(ct, Charset.defaultCharset().displayName());
							replaced = true;
							int diflen = ct.length() - Charset.defaultCharset().displayName().length();
							count -= diflen;
						}
						String utf8String = new String(s.substring(0, count).getBytes(), Charset.defaultCharset());
						byte[] bytes2 = utf8String.getBytes();

						os.write(bytes2, 0, bytes2.length);
					} else {
						os.write(s.getBytes(), 0, s.getBytes().length);
					}
					//chars.
				}
			}
			catch(Exception e){
				Log.d(Tag, e.getLocalizedMessage());
			}
		} catch (UnsupportedEncodingException e)
		{
			Log.d(Tag, e.getLocalizedMessage());
		}

	}
	// Given a string representation of a URL, sets up a connection and gets
	// an input stream.
	private InputStream downloadUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		// Определяем тип контента - кодовую страницу текста
		String contentType_ = conn.getHeaderField("Content-Type");
		String[] ct = contentType_.split("=");
		if (ct.length>0)
		{
			contentType = ct[ct.length-1];
		}
		Log.d(Tag, contentType);
		return conn.getInputStream();
	}
}
