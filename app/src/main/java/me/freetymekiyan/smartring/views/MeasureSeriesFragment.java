package me.freetymekiyan.smartring.views;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.freetymekiyan.smartring.R;
import me.freetymekiyan.smartring.controllers.OnFragmentInteractionListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeasureSeriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeasureSeriesFragment extends Fragment {

    public String getTitle() {
        return title;
    }

    // TODO: Rename and change types of parameters
    private String title;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MeasureSeriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeasureSeriesFragment newInstance(String param1) {
        MeasureSeriesFragment fragment = new MeasureSeriesFragment();
        Bundle args = new Bundle();
        args.putString(MainActivity.KEY_TITLE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public MeasureSeriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(MainActivity.KEY_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measure_series, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
