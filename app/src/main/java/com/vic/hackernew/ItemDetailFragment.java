package com.vic.hackernew;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vic.hackernew.Adapter.CommentAdapter;
import com.vic.hackernew.Model.Comment;
import com.vic.hackernew.Utils.Constant;
import com.vic.hackernew.Utils.CustomJsonObjectRequest;
import com.vic.hackernew.Utils.CustomVolleyRequest;
import com.vic.hackernew.Utils.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The Model content this fragment is presenting.
     */
//    private DummyContent.DummyItem mItem;
    String stringTransfer;
    JSONArray commentsId;
    List<Comment> comments;
    ProgressBar progressBar;
    RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CommentAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        comments = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comment_list, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        comments = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.comment_list);
        assert recyclerView != null;

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(getContext(), comments);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), null));


        requestQueue = CustomVolleyRequest.getInstance(getContext()).getRequestQueue();


        if (getArguments().containsKey(ARG_ITEM_ID)) {

            Bundle bundle = this.getArguments();
            stringTransfer = bundle.getString(ARG_ITEM_ID);
            try {
                JSONArray kidArray = new JSONArray(stringTransfer);
                for (int i = 0; i < kidArray.length(); i++) {
                    getCommentsDetail(requestQueue, Constant.TAG_BASE_URL + "item/" + kidArray.getString(i) + ".json?print=pretty");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(stringTransfer);
            }
        }

        return rootView;
    }

    private void getCommentsDetail(RequestQueue requestQueue, String commentUrl) {

        progressBar.setVisibility(View.VISIBLE);
        CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(Request.Method.GET, commentUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject respond) {
                        progressBar.setVisibility(View.GONE);

                        Comment comment = Comment.fromJson(respond);
                        comments.add(comment);
                        int index = Collections.binarySearch(comments, comment);
                        if (index < 0) index = -index - 1;
                        comments.add(index, comment);
                        adapter.notifyItemInserted(index);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
        jsonObjectRequest.setPriority(Request.Priority.HIGH);
        requestQueue.add(jsonObjectRequest);
    }

}


