package me.freetymekiyan.smartring.views;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import me.freetymekiyan.smartring.R;
import me.freetymekiyan.smartring.controllers.MeasureEvent;
import me.freetymekiyan.smartring.controllers.OnFragmentInteractionListener;
import me.freetymekiyan.smartring.controllers.RecyclerItemClickListener;
import me.freetymekiyan.smartring.models.DrawListAdapter;

public class MainActivity extends ActionBarActivity implements OnFragmentInteractionListener,
        MeasureOneFragment.OnMeasureListener {

    public static final String KEY_TITLE = "title";

    public static final int MEASURE_FRAGMENT = 1;

    public static final int HISTORY_FRAGMENT = 2;

    public static final int SETTINGS_FRAGMENT = 3;

    private final String name = "Yang Liu";

    private final String email = "freetymesunkiyan@gmail.com";

    private final int profile = R.drawable.photo;

    private final int[] icons = {R.drawable.ic_measure, R.drawable.ic_history,
            R.drawable.ic_settings};

    private Toolbar toolbar;

    RecyclerView mRcView;

    RecyclerView.Adapter mDrawerAdapter;

    RecyclerView.LayoutManager mLayoutManager;

    DrawerLayout drawer;

    ActionBarDrawerToggle mDrawerToggle;

    private int page = MEASURE_FRAGMENT;

    private NfcAdapter mAdapter;

    private PendingIntent mPendingIntent;

    private IntentFilter[] mIntentFilters;

    private int count = 0;

    public static final int START_INDEX = 3;

    private float sum;

    private float result;

    private boolean enabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.measure);

        mRcView = (RecyclerView) findViewById(R.id.recycle_view);
        mRcView.setHasFixedSize(true);
        mDrawerAdapter = new DrawListAdapter(getResources().getStringArray(R.array.titles), icons,
                name,
                profile, email);
        mRcView.setAdapter(mDrawerAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRcView.setLayoutManager(mLayoutManager);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer,
                R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                toolbar.setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                switch (page) {
                    case MEASURE_FRAGMENT:
                        toolbar.setTitle(R.string.measure);
                        break;
                    case HISTORY_FRAGMENT:
                        toolbar.setTitle(R.string.history);
                        break;
                    case SETTINGS_FRAGMENT:
                        toolbar.setTitle(R.string.settings);
                        break;
                    default:
                        break;
                }
            }
        };
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mRcView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        switch (position) {
                            case MEASURE_FRAGMENT:
                                if (page != MEASURE_FRAGMENT) {
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.content_frame,
                                                    MeasureFragment.getInstance()).commit();
                                    page = MEASURE_FRAGMENT;
                                    toolbar.setTitle(R.string.measure);
                                }
                                break;
                            case HISTORY_FRAGMENT:
                                if (page != HISTORY_FRAGMENT) {
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.content_frame,
                                                    HistoryFragment.getInstance()).commit();
                                    page = HISTORY_FRAGMENT;
                                    toolbar.setTitle(R.string.history);
                                }
                                break;
                            case SETTINGS_FRAGMENT:
                                if (page != SETTINGS_FRAGMENT) {
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.content_frame,
                                                    new MyPreferenceFragment()).commit();
                                    page = SETTINGS_FRAGMENT;
                                    toolbar.setTitle(R.string.settings);
                                }
                                break;
                            default:
                                break;
                        }
                        drawer.closeDrawer(Gravity.LEFT);
                    }
                }));
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                MeasureFragment.getInstance()).commit();
        // NFC Adapter
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            Toast.makeText(this, R.string.toast_nfc_not_available, Toast.LENGTH_LONG).show();
            // TODO deal with not available situation
        } else {
            mPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                ndef.addDataType("text/plain");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            mIntentFilters = new IntentFilter[]{ndef,};
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d("DEBUG", "onNewIntent");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_db) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new TestDbFragment()).commit();
            page = 0;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
                    if (count <= 10 && enabled) {
                        float rate = Float.valueOf(msg.substring(START_INDEX, START_INDEX + 4))
                                * 8192 / 8000000;
                        sum += 60 / rate;
                        count++;
                    } else {
//                      TODO stopUpdateNfc() in measure one fragment;
                        EventBus.getDefault().post(new MeasureEvent("Stop measurement"));
                        result = sum / count;
                        // TODO show result, choose state, and insert to db
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onMeasureStateChanged(boolean enabled) {
        this.enabled = enabled;
    }
}
