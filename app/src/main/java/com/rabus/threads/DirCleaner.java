package com.rabus.threads;

import android.util.Log;

import java.io.File;

/**
 * Created by rabusov on 13.09.2016.
 */
public class DirCleaner extends Thread
{
    private File dirname=null;
    private String fullPath;
    private long MaxAge = 1; // 0 = one day, 1 = two days
    private static final String TAG = DirCleaner.class.getSimpleName();
    public DirCleaner(String watchDirFiles, long ageOfFiles)
    {
        fullPath = watchDirFiles;
        MaxAge = ageOfFiles;
    }
    // keeping a working process
    public boolean need2go = true;
    public void quit()
    {
        need2go = false;
    }
    public void run()
    {
        need2go = true;
        try
        {
            // The current time in milliseconds since January 1, 1970 00:00:00.0 UTC.
            Long now = System.currentTimeMillis();
            dirname = new File(fullPath);
            for (File f : dirname.listFiles())
            {
                if (!need2go) return;
                if (f.isFile()){
                    // delete files older than MaxAge day
                    if ( (f.lastModified() + MaxAge) < now )
                        f.delete();
                }
            }
        }
        catch (Throwable t)
        {
            Log.d(TAG, t.toString());
        }
    }
}
