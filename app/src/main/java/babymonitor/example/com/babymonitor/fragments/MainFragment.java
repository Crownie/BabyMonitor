package babymonitor.example.com.babymonitor.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import babymonitor.example.com.babymonitor.MainActivity;
import babymonitor.example.com.babymonitor.R;


public class MainFragment extends Fragment {

    String url = "http://192.168.43.192:8080";
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

        wv.loadUrl(url);

        return rootView;
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
            tv.setText("Temperature: " + temperature);
        }
    }
}
