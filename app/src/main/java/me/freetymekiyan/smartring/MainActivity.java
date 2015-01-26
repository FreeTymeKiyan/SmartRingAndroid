package me.freetymekiyan.smartring;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

public class MainActivity extends ActionBarActivity implements
        MeasureOneFragment.OnFragmentInteractionListener,
        MeasureSeriesFragment.OnFragmentInteractionListener {

    public static final String KEY_TITLE = "title";

    private Toolbar toolbar;

    String name = "Yang Liu";

    String email = "freetymesunkiyan@gmail.com";

    int profile = R.drawable.photo;

    int[] icons = {R.drawable.ic_measure, R.drawable.ic_history, R.drawable.ic_settings};

    RecyclerView mRcView;

    RecyclerView.Adapter mAdapter;

    RecyclerView.LayoutManager mLayoutManager;

    DrawerLayout drawer;

    ActionBarDrawerToggle mDrawerToggle;

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
                            case 3:
                                startActivity(
                                        new Intent(MainActivity.this, SettingsActivity.class));
                                break;
                            case 1:
                                drawer.closeDrawer(Gravity.LEFT);
                                break;
                            case 2:
                                break;
                            default:
                                break;
                        }
                    }
                }));

        MeasurePagerAdapter mPagerAdapter = new MeasurePagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class MeasurePagerAdapter extends FragmentPagerAdapter {

        public MeasurePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return (Fragment) MeasureOneFragment.newInstance(getApplicationContext().getString(
                        R.string.title_fragment_measure_one));
            } else {
                return (Fragment) MeasureSeriesFragment
                        .newInstance(getApplicationContext().getString(
                                R.string.title_fragment_measure_series));
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getItem(position).getArguments().getString("title");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
