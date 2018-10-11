package com.rabus.simplerssviewer;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

public class ActivityUrlAdd extends Activity {
    private EditText channelName;
    private EditText channelLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.url_add);

        channelName = (EditText)findViewById(R.id.new_name);

        channelLink = (EditText)findViewById(R.id.new_link);

    }
    public void SaveNameAndLink(View view)
    {
        Intent intent = new Intent(SQLConstants.CHANNEL_NAME_LINK_ADD);

        intent.putExtra("newlink", channelLink.getText().toString());
        intent.putExtra("newname", channelName.getText().toString());

        getApplicationContext().sendBroadcast(intent);
        finish();
    }
}
