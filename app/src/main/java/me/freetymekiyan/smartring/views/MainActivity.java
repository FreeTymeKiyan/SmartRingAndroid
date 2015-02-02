package me.freetymekiyan.smartring.views;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.freetymekiyan.smartring.R;
import me.freetymekiyan.smartring.controllers.OnFragmentInteractionListener;
import me.freetymekiyan.smartring.controllers.RecyclerItemClickListener;
import me.freetymekiyan.smartring.models.DrawListAdapter;

public class MainActivity extends ActionBarActivity implements OnFragmentInteractionListener {

    public static final String KEY_TITLE = "title";

    private final String name = "Yang Liu";

    private final String email = "freetymesunkiyan@gmail.com";

    private final int profile = R.drawable.photo;

    private final int[] icons = {R.drawable.ic_measure, R.drawable.ic_history,
            R.drawable.ic_settings};

    private Toolbar toolbar;

    RecyclerView mRcView;

    RecyclerView.Adapter mAdapter;

    RecyclerView.LayoutManager mLayoutManager;

    DrawerLayout drawer;

    ActionBarDrawerToggle mDrawerToggle;

    private int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mRcView = (RecyclerView) findViewById(R.id.recycle_view);
        mRcView.setHasFixedSize(true);
        mAdapter = new DrawListAdapter(getResources().getStringArray(R.array.titles), icons, name,
                profile, email);
        mRcView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRcView.setLayoutManager(mLayoutManager);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer,
                R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mRcView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        switch (position) {
                            case 1:
                                if (page != 1) {
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.content_frame,
                                                    MeasureFragment.getInstance()).commit();
                                    page = 1;
                                }
                                break;
                            case 2:
                                if (page != 2) {
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.content_frame,
                                                    HistoryFragment.getInstance()).commit();
                                    page = 2;
                                }
                                break;
                            case 3:
                                if (page != 3) {
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.content_frame,
                                                    new MyPreferenceFragment()).commit();
                                    page = 3;
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
}
