package babymonitor.example.com.babymonitor.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import babymonitor.example.com.babymonitor.MainActivity;
import babymonitor.example.com.babymonitor.R;


public class MainFragment extends Fragment implements View.OnClickListener {

    public static final String url = "http://192.168.43.66:8080/video";
    boolean attached = false;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        WebView wv = (WebView) rootView.findViewById(R.id.stream_webview);

        ImageButton chartBtn = (ImageButton) rootView.findViewById(R.id.btnChart);
        chartBtn.setOnClickListener(this);

        ImageButton settingsBtn = (ImageButton) rootView.findViewById(R.id.btnSettings);
        settingsBtn.setOnClickListener(this);

        initButtons();

        wv.loadUrl(url);

        return rootView;
    }

    private void initButtons() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
        attached = true;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        attached = false;
    }

    public void setTemperature(long temperature) {
        if(attached){
            TextView tv = (TextView) getView().findViewById(R.id.tvTemperature);
            tv.setText(Long.toString(temperature));
        }
    }

    @Override
    public void onClick(View v) {
        MainActivity mainActivity = (MainActivity) getActivity();
        switch(v.getId()){
            case R.id.btnChart:
                mainActivity.onNavigationDrawerItemSelected(1);
                break;
            case R.id.btnSettings:
                mainActivity.onNavigationDrawerItemSelected(2);
                break;
        }
    }
}
