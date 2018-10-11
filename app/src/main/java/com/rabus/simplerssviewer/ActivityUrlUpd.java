package com.rabus.simplerssviewer;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

public class ActivityUrlUpd extends Activity {
    private EditText channelName;
    private EditText channelLink;
    private String currentLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.url_update);
        Intent intent = getIntent();
        channelName = (EditText)findViewById(R.id.new_name);
        channelName.setText(intent.getStringExtra("Name"));

        channelLink = (EditText)findViewById(R.id.new_link);
        currentLink = intent.getStringExtra("Link");
        channelLink.setText(currentLink);
    }
    public void SaveNameAndLink(View view)
    {
        Intent intent = new Intent(SQLConstants.CHANNEL_NAME_LINK_UPDATE);
        intent.putExtra("currentlink", currentLink);
        intent.putExtra("newlink", channelLink.getText().toString());
        intent.putExtra("newname", channelName.getText().toString());

        getApplicationContext().sendBroadcast(intent);
        finish();
    }
}
