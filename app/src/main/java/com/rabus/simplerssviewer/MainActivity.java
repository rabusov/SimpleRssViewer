package com.rabus.simplerssviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rabus.rss.Channel;
import com.rabus.rss.Constants;
import com.rabus.rss.DownloadRssTask;
import com.rabus.rss.Item;
import com.rabus.threads.DirCleaner;
import com.rabus.threads.ImagesLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ActivityBase {
    public static Channel rss_channel = null;
    //
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String POSITION_IN_LIST_OF_RSS = "POSITION_IN_LIST_OF_RSS";
    private static final String LVCHANNALES_VISIBILITY = "LVCHANNALES_VISIBILITY";
    private static int screenWidth, screenHeight;
    private static int mPosition = -1;
    private static String link_2_delete = "";
    //
    private Context context;
    private boolean _busy = false;
    private RssData UrlDB;
    private TextView channelName = null;
    private ListView lvChannales = null;
    private RelativeLayout lvItemsLayout = null;
    private RssItemAdapter adapter = null;
    private ListView LV = null;
    private ArrayList< HashMap<String, String> > myArrList;
    private DirCleaner m_DirCleaner = null;
    private File m_cacheDir;
    private ImagesLoader m_imageLoaderThread;
    private ProgressBar progressBar = null;
    private final long WEEKDAYS_MS = Constants.ALLDAYLONG_MS*7;
    private final int CONTEXTMENU_ABOUTCHANNEL_INDEX = 0;
    public static final String ACTION_URLUPD_ACTIVITY = "com.rabus.simplerssviewer.ActivityUrlUpd";
    public static final String ACTION_URLADD_ACTIVITY = "com.rabus.simplerssviewer.ActivityUrlAdd";

    // private LinearLayout.LayoutParams paramsChannels;
   // private RelativeLayout.LayoutParams paramsLayout;
    /*
    // для сохранения ссылок на выбранные каналы
    private class ChannelObj {
        Channel channel;
        int myArrayListIndex;
        Date lasttimeshow;
    }
    private ArrayList<ChannelObj> channelObjs = new ArrayList<ChannelObj>();
    */
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        lvChannales = (ListView)findViewById(R.id.listView);
        channelName = (TextView)findViewById(R.id.channelname);

        lvItemsLayout = (RelativeLayout)findViewById(R.id.ListOfRssItems);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        registerForContextMenu(lvChannales);
        //
        // Запускаем чистильщика кэш-директории
        //
        if (m_cacheDir == null){

            m_cacheDir = DownloadRssTask.getDataDirectoryName(context);
            if (!m_cacheDir.exists())
                m_cacheDir.mkdirs();
            //
            m_DirCleaner = new DirCleaner(m_cacheDir.getAbsolutePath(), WEEKDAYS_MS);
            m_DirCleaner.start();
        }
        // запустим загрузчика фотографий
        m_imageLoaderThread = new ImagesLoader(m_cacheDir.getAbsolutePath());
        m_imageLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
        m_imageLoaderThread.start();
        //
        //
        //myArrList = new ArrayList< HashMap<String, String> >();
        // make a registration of our receiver
        registerReceiver(receiverRssReady, new IntentFilter(Channel.FEED_ACTION_READY));
        registerReceiver(receiverUrlUpdate, new IntentFilter(SQLConstants.CHANNEL_NAME_LINK_UPDATE));
        registerReceiver(receiverUrlAdd, new IntentFilter(SQLConstants.CHANNEL_NAME_LINK_ADD));

        UrlDB = new RssData(context);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;
        Log.d(TAG, "screenWidth = " + screenWidth);
        Log.d(TAG, "screenHeight = " + screenHeight);
        if (lvChannales != null) {
            resetChannelsAdapter();

            lvChannales.isClickable();
            lvChannales.setOnItemClickListener(lvClickListener);
            //
            if (!isWideScreen()) {
                lvItemsLayout.setVisibility(View.GONE);
            }
        }
    }
    public void resetChannelsAdapter()
    {
        myArrList = UrlDB.getArrayList();
        SimpleAdapter adapter = new SimpleAdapter(this, myArrList, android.R.layout.simple_list_item_2,
                new String[] {"Name", "Link"},
                new int[] {android.R.id.text1, android.R.id.text2});

        lvChannales.setAdapter(adapter);
    }
    @Override
    public void onBackPressed()
    {
        if (lvChannales != null && lvItemsLayout != null && !isWideScreen()) {
            if (lvChannales.getVisibility() == View.VISIBLE)
            {
                super.onBackPressed();
            } else {
                lvChannales.setVisibility(View.VISIBLE);
                lvItemsLayout.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
    }
    private boolean isWideScreen()
    {
        if (screenWidth > 1440) return true;
        return false;
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
        mPosition = savedInstanceState.getInt(POSITION_IN_LIST_OF_RSS ,0);
        if (mPosition >= 0) {
            HashMap<String, String> map = myArrList.get(mPosition);
            String url = map.get("Link");
            rss_channel.getFromFeed(url, context);
            channelName.setText(map.get("Name"));
        }
        if (lvChannales != null && lvItemsLayout != null && !isWideScreen()) {
            if (savedInstanceState.getBoolean(LVCHANNALES_VISIBILITY, true) ) {
                lvChannales.setVisibility(View.VISIBLE);
                lvChannales.setMinimumWidth(screenWidth);
                lvItemsLayout.setVisibility(View.GONE);

            } else {
                lvChannales.setVisibility(View.GONE);
                lvItemsLayout.setVisibility(View.VISIBLE);
                lvItemsLayout.setMinimumWidth(screenWidth);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rss_test_, menu);

        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_aboutchannel)
        {
            if (rss_channel != null) {
                Intent intent = new Intent();
                intent.setClass(context, AboutRssActivity.class);
                startActivity(intent);
            } else ToastAMessage(getString(R.string.nochannel));
            return true;
        }
        if (id == R.id.action_addchannel) {
            startActivity(new Intent(ACTION_URLADD_ACTIVITY));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        if (mPosition == info.position)
        {
            menu.getItem(CONTEXTMENU_ABOUTCHANNEL_INDEX).setEnabled(true);
        } else {
            menu.getItem(CONTEXTMENU_ABOUTCHANNEL_INDEX).setEnabled(false);
        }
        menu.setHeaderTitle(R.string.rss_channel_menu_title);

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        HashMap<String, String> map = myArrList.get(info.position);
       /* if (BuildConfig.DEBUG) {

            String s = new String("position: " + info.position + " ");

            ShowMessageError(s + map.get("Link"));
        }*/
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_aboutchannel:
                if (rss_channel != null) {
                    intent = new Intent();
                    intent.setClass(context, AboutRssActivity.class);
                    startActivity(intent);
                } else ToastAMessage(getString(R.string.nochannel));
                return true;
            case R.id.action_addchannel:
                intent = new Intent(ACTION_URLADD_ACTIVITY);
                startActivity(intent);
                return true;
            case R.id.action_updchannel:
                intent = new Intent(ACTION_URLUPD_ACTIVITY);
                intent.putExtra("Link", map.get("Link"));
                intent.putExtra("Name", map.get("Name"));
                startActivity(intent);
                return true;
            case R.id.action_delchannel:
                {
                    String name = map.get("Name");
                    link_2_delete = map.get("Link");
                    AlertDialog.Builder dialog = null;
                    AlertDialog ad = null;
                    dialog = new AlertDialog.Builder(this);
                    dialog.setTitle(R.string.rss_delchannel);
                    dialog.setMessage("\"" + name + "\" (" +  link_2_delete + ")");
                    dialog.setNegativeButton(R.string.button_no, null);
                    dialog.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            UrlDB.DeleteUrl(link_2_delete);
                            resetChannelsAdapter();
                        }
                    });
                    ad = dialog.create();
                    ad.show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(POSITION_IN_LIST_OF_RSS, mPosition);
        if (lvChannales != null) {
            if (lvChannales.getVisibility() == View.GONE) {
                outState.putBoolean(LVCHANNALES_VISIBILITY, false);
            } else outState.putBoolean(LVCHANNALES_VISIBILITY, true);
        }
    }
    /*
    * By click on the name of channel
    * */
    private void fillFromListItems(int position)
    {
        HashMap<String, String> map = myArrList.get(position);
        mPosition = position;
        // На экран выводим наименование выбранного канала
        channelName.setText(map.get("Name"));
        if (!isWideScreen()) {
            lvItemsLayout.setVisibility(View.VISIBLE);
            // Убераем список каналов с экрана
            lvChannales.setVisibility(View.GONE);
        }
        if ( !isBusy())
        {
            if (rss_channel == null)
                rss_channel = new Channel(context);
            String url = map.get("Link");
            //
            // if there are valid URL and RSS Channel object
            if ( (rss_channel != null) && (url != null) && (url.length() > SQLConstants.URLMINLENGTH ))
            {
                // set a busy flag
                setBusy(true);

                rss_channel.getFromFeed(url, context);
            }
        } else {
            ToastAMessage(getString(R.string.busy));
        }
    }
    private final AdapterView.OnItemClickListener lvClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            fillFromListItems(position);
        }
    };
    @Override
    protected void onDestroy()
    {
        unregisterReceiver(receiverRssReady);
        unregisterReceiver(receiverUrlUpdate);
        unregisterReceiver(receiverUrlAdd);
        unregisterForContextMenu(lvChannales);
        m_imageLoaderThread.quit();
        m_imageLoaderThread.interrupt();
        super.onDestroy();
    }
    //
    // this is a reaction function, it will get reaction on an event
    // rising from background process from HTTP
    //
    //--------------------------------------------------------------------
    public BroadcastReceiver receiverRssReady = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // set off a busy flag
                setBusy(false);
                String answer = intent.getStringExtra(Channel.RESPONSE_TITLE);
                Log.d(TAG, "answer=" + answer);
                // OK, BAD, No Data
                // if answer==OK
                if ( (answer != null) && (answer.startsWith( Channel.RESPONSE_OK ) ) ) {
                    ShowListViewOfRss();
                } else {
                    // Error or no data situation
                    String error = intent.getStringExtra(Channel.RESPONSE_ERROR_TITLE);
                    ToastAMessage(error);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public BroadcastReceiver receiverUrlUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String currentlink = intent.getStringExtra("currentlink");
                String newlink = intent.getStringExtra("newlink");
                String newname = intent.getStringExtra("newname");
                Log.d(TAG, "currentlink=" + currentlink);
                Log.d(TAG, "newlink=" + newlink);
                Log.d(TAG, "newname=" + newname);
                UrlDB.UpdateNameAndUrl(currentlink, newname, newlink);
                resetChannelsAdapter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    public BroadcastReceiver receiverUrlAdd = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String newlink = intent.getStringExtra("newlink");
                String newname = intent.getStringExtra("newname");
                Log.d(TAG, "newlink=" + newlink);
                Log.d(TAG, "newname=" + newname);
                UrlDB.AddNameAndUrl( newname, newlink);
                resetChannelsAdapter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    //
    // normally calling from receiver
    // when all items loaded from server
    protected void ShowListViewOfRss()
    {
        if (rss_channel != null){
            if (rss_channel.isReady()) {
                // fill the adapter
                m_imageLoaderThread.setEntries(rss_channel.getParsedItems());
                adapter = new RssItemAdapter(this, R.layout.listoflist_item, rss_channel.getParsedItems());
                if (adapter != null)
                {
                    adapter.m_cacheDir = m_cacheDir;
                    if  (rss_channel.image != null )
                        if ((rss_channel.image.url != null)	&& (rss_channel.image.url.trim().length()>0))
                            adapter.url_image = rss_channel.image.url;
                    // Show list view from adapter
                    if (LV == null)
                        LV = (ListView) findViewById(R.id.ListViewOfRssItems);

                    LV.setAdapter(adapter);

                }  else Log.d(TAG,"adapter is null");
            } else Log.d(TAG, "(rss_channel.isReady()=" + rss_channel.isReady());
        }
    }
    //
    // Show a document when you tap ImageButton
    //
    public void OnClickImageButton(View view){
        Object o = view.getTag();
        if (o != null)
        {
            int item_current_index = o.hashCode();
            //Log.d(Tag,"item_current_index=" + item_current_index);
            if ((item_current_index >= 0) && (item_current_index < rss_channel.size() ))
            {
                Item anItem = (Item) rss_channel.items.get(item_current_index);
                if ( (anItem != null) && (anItem.link != null) && (anItem.link.length() > SQLConstants.URLMINLENGTH))
                {
                    Intent intent = new Intent();
                    intent.setClass(context, WebViewActivity.class);
                    intent.putExtra("link", anItem.link);
                    startActivity(intent);
                }
            }
        }
    }
    // Edit RSS LINK
    public void OnClickEditRSSUrl(View view)
    {
        Object o = view.getTag();
        if (o != null)
        {

        }
    }
    public boolean isBusy() {
        return _busy;
    }

    public void setBusy(boolean busy) {
        this._busy = busy;
        if (_busy)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            if (lvItemsLayout.getVisibility() != View.VISIBLE )
                lvItemsLayout.setVisibility(View.VISIBLE);
        }
    }
}
