package com.vic.hackernew;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TopStoryWebView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TopStoryWebView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopStoryWebView extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private WebView webView;
    ProgressBar progressBar;
    boolean mTwoPane = false;
    Handler mIncomingHandler = new Handler(new IncomingHandlerCallback());


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TopStoryWebView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopStoryWebView.
     */
    // TODO: Rename and change types and number of parameters
    public static TopStoryWebView newInstance(String param1, String param2) {
        TopStoryWebView fragment = new TopStoryWebView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_top_story_web_view, container, false);
        if (view.findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        Bundle bundle = this.getArguments();

        String topStoryUrl = bundle.getString("topStoryUrl");

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        webView = (WebView) view.findViewById(R.id.webView);

        // Reload the old WebView content
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }else // Create the WebView
        {

            //Webview Setting
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setSupportZoom(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setAppCachePath(getContext().getCacheDir().getPath());
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(false);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                }
            });

            webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    progressBar.setProgress(progress);

                    if (progress == 100) {
                        progressBar.setVisibility(View.GONE);

                    } else {
                        progressBar.setVisibility(View.VISIBLE);

                    }
                }
            });

            webView.setOnKeyListener(new View.OnKeyListener() {

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (webView.canGoBack()) {
                            mIncomingHandler.sendEmptyMessage(1);
                        }else {
                            if (view.findViewById(R.id.comment_detail_container)!=null){
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container,new ItemDetailFragment()).commit();
                            }else {
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new ItemDetailFragment()).commit();
                            }

                        }
                        return true;
                    }
                    return false;
                }

            });
        }


        webView.loadUrl(topStoryUrl);


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class IncomingHandlerCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message message) {

            switch (message.what) {
                case 1: {
                    webView.goBack();
                }
                break;
            }

            return true;
        }
    }
}
