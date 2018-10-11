package com.rabus.simplerssviewer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by rabusov on 11.07.2017.
 */

public class ActivityBase extends Activity {
    @Override
    public void onCreate(Bundle savedInstances)
    {
        super.onCreate(savedInstances);
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    public void ToastAMessage(int res)
    {
        ToastAMessage(getResources().getString(res));
    }

    public void ToastAMessage(String msg)
    {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }
}
