package me.freetymekiyan.smartring;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MeasureActivity extends ActionBarActivity implements
        NfcAdapter.CreateNdefMessageCallback {

    private IntentFilter[] mIntentFilters;

    private String[][] techListsArray;

    private NfcAdapter mAdapter;

    private PendingIntent mPendingIntent;

    private TextView tvNfc;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        tvNfc = (TextView) findViewById(R.id.tv_textview);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mAdapter.setNdefPushMessageCallback(this, this);

//        mPendingIntent = PendingIntent.getActivity(
//                this, 0,
//                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT), 0);
//        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
//        try {
//            ndef.addDataType("http://me.freetymekiyan.smartring");
//        } catch (IntentFilter.MalformedMimeTypeException e) {
//            throw new RuntimeException("fail", e);
//        }
//        mIntentFilters = new IntentFilter[]{ndef,};
//        techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
//        mAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, techListsArray);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        tvNfc.setText(new String(msg.getRecords()[0].getPayload()) + count++);
//        if (rawMsgs != null) {
//            NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
//            for (int i = 0; i < rawMsgs.length; i++) {
//                msgs[i] = (NdefMessage) rawMsgs[i];
//                tvNfc.setText(new String(msgs[i].getRecords()[0].getPayload()) + " " + count++);
//            }
//            Log.d("DEBUG", msgs[0].toString());
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
//        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // TODO do something with tagFromIntent
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("Beam me up, Android!\n\n" + "Beam Time: " + System.currentTimeMillis());
        NdefMessage msg = new NdefMessage(new NdefRecord[]{NdefRecord.createMime(
                "application/vnd.com.example.android.beam", text.getBytes())});
        return msg;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_measure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
