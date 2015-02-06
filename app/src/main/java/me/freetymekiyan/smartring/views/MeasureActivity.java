package me.freetymekiyan.smartring.views;

import com.skyfishjy.library.RippleBackground;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.freetymekiyan.smartring.R;

public class MeasureActivity extends ActionBarActivity implements View.OnClickListener {

    public static final int START_INDEX = 3;

    private IntentFilter[] mIntentFilters;

    private NfcAdapter mAdapter;

    private PendingIntent mPendingIntent;

    private TextView tvNfc;

    private int count = 0;

    private float sum;

    private RippleBackground rippleBkg;

    private ImageView ivPhone;

    private TextView tvState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        tvNfc = (TextView) findViewById(R.id.tv_pulse);
        rippleBkg = (RippleBackground) findViewById(R.id.rbkg_measure);
        rippleBkg.startRippleAnimation();

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mIntentFilters = new IntentFilter[]{ndef,};

        ivPhone = (ImageView) findViewById(R.id.centerImage);
        ivPhone.setOnClickListener(this);
        tvState = (TextView) findViewById(R.id.tv_nfc_state);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, null);
    }

    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null) {
            NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
                String msg = new String(msgs[i].getRecords()[0].getPayload());
                Log.d("DEBUG", "msg: " + msg);
                if (!msg.equals("\u0003me.freetymekiyan.smartring")) {
                    if (count <= 10 && isReceiving()) {
                        float rate =
                                Float.valueOf(msg.substring(START_INDEX, START_INDEX + 4)) * 8192
                                        / 8000000;
                        tvNfc.setText("rate: " + (60 / rate) + " count: " + count++);
                        sum += 60 / rate;
                    } else {
                        stopUpdateNfc();
                        tvNfc.setText("Heart Rate: " + (sum / count));
                    }
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // TODO do something with tagFromIntent
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.centerImage:
                if (isReceiving()) {
                    stopUpdateNfc();
                } else {
                    startUpdateNfc();
                }
                break;
            default:
                break;
        }
    }

    private void stopUpdateNfc() {
        rippleBkg.stopRippleAnimation();
        tvState.setText(isReceiving() + "");
    }

    private void startUpdateNfc() {
        sum = 0;
        count = 0;
        rippleBkg.startRippleAnimation();
        tvState.setText(isReceiving() + "");
    }

    private boolean isReceiving() {
        return rippleBkg.isRippleAnimationRunning();
    }
}
