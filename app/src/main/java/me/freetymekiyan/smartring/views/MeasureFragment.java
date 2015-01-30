package me.freetymekiyan.smartring.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.freetymekiyan.smartring.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeasureFragment extends Fragment {

    private static MeasureFragment instance;

    public MeasureFragment() {
        // Required empty public constructor
    }

    public static MeasureFragment getInstance() {
        if (instance == null) {
            instance = new MeasureFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measure, container, false);
        MeasurePagerAdapter mPagerAdapter = new MeasurePagerAdapter(
                getChildFragmentManager());
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        return view;
    }

    public class MeasurePagerAdapter extends FragmentPagerAdapter {

        public MeasurePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return (Fragment) MeasureOneFragment
                        .newInstance(getString(R.string.title_fragment_measure_one));
            } else {
                return (Fragment) MeasureSeriesFragment.newInstance(
                        getString(R.string.title_fragment_measure_series));
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getItem(position).getArguments().getString(MainActivity.KEY_TITLE);
        }
    }
}
