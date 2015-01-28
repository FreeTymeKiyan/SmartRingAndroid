package me.freetymekiyan.smartring;

import com.skyfishjy.library.RippleBackground;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeasureOneFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeasureOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeasureOneFragment extends Fragment {

    public String getTitle() {
        return title;
    }

    private String title;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @return A new instance of fragment MeasureOneFragment.
     */
    public static MeasureOneFragment newInstance(String title) {
        MeasureOneFragment fragment = new MeasureOneFragment();
        Bundle args = new Bundle();
        args.putString(MainActivity.KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public MeasureOneFragment() {
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
        View view = inflater.inflate(R.layout.fragment_measure_one, container, false);
        final RippleBackground rippleBackground = (RippleBackground) view.findViewById(
                R.id.rbkg_measure);
        ImageView imageView = (ImageView) view.findViewById(R.id.centerImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rippleBackground.isRippleAnimationRunning()) {
                    rippleBackground.stopRippleAnimation();
                } else {
                    rippleBackground.startRippleAnimation();
                }

            }
        });
        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);
    }

}
