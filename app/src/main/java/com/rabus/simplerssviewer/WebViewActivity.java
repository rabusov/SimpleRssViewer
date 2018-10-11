package com.rabus.simplerssviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

//
// Comments about functionality of this activity see at 
// http://developer.alexanderklimov.ru/android/mybrowser.php
//
public class WebViewActivity extends ActivityBase {
	private WebView WV = null;
	private boolean ready2print = false;
	private ArrayList<PrintJob> mPrintJobs = new ArrayList<PrintJob>();
	//static private final String TAG = WebViewActivity.class.getSimpleName();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		//Log.d(TAG, "WebViewActivity onCreate");
		WV = (WebView) findViewById(R.id.WebView);
		//
		WV.setWebViewClient(new EasyWebViewClient());
		//
		Intent intent = getIntent();
		String url = intent.getStringExtra("link");
		WV.loadUrl(url);
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if ((keyCode == KeyEvent.KEYCODE_BACK) && WV.canGoBack()) {
    		WV.goBack();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
	private class EasyWebViewClient extends WebViewClient 
	{
	   /* @Override
	    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
	    {
	        view.loadUrl(request.getUrl().toString());
	        return true;
	    }
	  */
	   @Override
	   public void onPageFinished(WebView view, String url) {
		   ready2print = true;
	   }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rss_print, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id==R.id.action_print)
		{
			if (ready2print) {
				// Получаем экземпляр менеджера печати - PrintManager
				PrintManager printManager = (PrintManager) this
						.getSystemService(getApplicationContext().PRINT_SERVICE);

				// Получаем экземпляр адаптера печати
				PrintDocumentAdapter printAdapter = WV.createPrintDocumentAdapter();

				// Создаем задание печати с адаптером и именем
				String jobName = getString(R.string.app_name) + " Document";
				PrintJob printJob = printManager.print(
						jobName
						, printAdapter
						, new PrintAttributes.Builder().build());
				// Сохраняем объект задания для проверки статуса в будущем
				mPrintJobs.add(printJob);
			} else {
				ToastAMessage(R.string.docnotloaded);
			}
		}

		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onDestroy()
	{
		for(int i=0; i<mPrintJobs.size(); i++)
		{
			PrintJob printJob = mPrintJobs.get(i);
			try {
				if (printJob.isBlocked() || printJob.isFailed() || printJob.isCompleted())
					printJob.cancel();
			} finally {
				mPrintJobs.clear();
			}
		}
		super.onDestroy();
	}
}
