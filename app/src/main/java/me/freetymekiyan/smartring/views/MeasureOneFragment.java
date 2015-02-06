package me.freetymekiyan.smartring.views;

import com.skyfishjy.library.RippleBackground;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import me.freetymekiyan.smartring.R;
import me.freetymekiyan.smartring.controllers.MeasureEvent;

public class MeasureOneFragment extends Fragment implements View.OnClickListener {

    private RippleBackground rippleBkg;

    public String getTitle() {
        return title;
    }

    private String title;

    private OnMeasureListener mListener;

    public interface OnMeasureListener {

        public void onMeasureStateChanged(boolean enabled);
    }

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
        rippleBkg = (RippleBackground) view.findViewById(R.id.rbkg_measure);
        ImageView imageView = (ImageView) view.findViewById(R.id.centerImage);
        imageView.setOnClickListener(this);
        return view;
    }

    public void onNFCStateChanged(boolean enabled) {
        if (mListener != null) {
            mListener.onMeasureStateChanged(enabled);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMeasureListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        EventBus.getDefault().register(this);
    }

    public void onEvent(MeasureEvent event){
        Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
        stopUpdateNfc();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        EventBus.getDefault().unregister(this);
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
        onNFCStateChanged(false);
    }

    private void startUpdateNfc() {
        rippleBkg.startRippleAnimation();
        onNFCStateChanged(true);
//        sum = 0;
//        count = 0;
    }

    private boolean isReceiving() {
        return rippleBkg.isRippleAnimationRunning();
    }
}
