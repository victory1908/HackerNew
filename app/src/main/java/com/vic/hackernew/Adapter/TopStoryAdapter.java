package com.vic.hackernew.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vic.hackernew.ItemDetailActivity;
import com.vic.hackernew.ItemDetailFragment;
import com.vic.hackernew.ItemListActivity;
import com.vic.hackernew.Model.TopStory;
import com.vic.hackernew.R;
import com.vic.hackernew.TopStoryWebView;
import com.vic.hackernew.Utils.DateTimeFunction;

import java.util.List;

/**
 * Created by vic on 22-Apr-16.
 */
public class TopStoryAdapter extends RecyclerView.Adapter<TopStoryAdapter.ViewHolder> {

    Context context;
    List<TopStory> topStories;


    public TopStoryAdapter(Context context,List<TopStory> topStories) {
        this.context = context;
        this.topStories = topStories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TopStory topStory = topStories.get(position);
        holder.author.setText(topStory.getAuthor());
        holder.title.setText(topStory.getTitle());
        holder.score.setText(String.valueOf(topStory.getScore()));
        holder.time.setText(DateTimeFunction.formatDateTime(topStory.getTime()));
    }

    @Override
    public int getItemCount() {
        return topStories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,author,score,time;
        Button url;
        View topStory_View;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            author = (TextView) view.findViewById(R.id.author);
            score = (TextView) view.findViewById(R.id.score);
            time = (TextView) view.findViewById(R.id.time);
            url = (Button) view.findViewById(R.id.url);

            topStory_View = view.findViewById(R.id.Topstory_View);


            url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("topStoryUrl", topStories.get(getLayoutPosition()).getUrl());

                    TopStoryWebView topStoryWebViewFragment = new TopStoryWebView();
                    topStoryWebViewFragment.setArguments(bundle);
                    if (ItemListActivity.mTwoPane) {

                        ((FragmentActivity) context).getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.item_detail_container, topStoryWebViewFragment)
                                .commit();
                    } else {
                        topStoryWebViewFragment.setArguments(bundle);
                        ((FragmentActivity) context).getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.frameLayout, topStoryWebViewFragment).addToBackStack(null)
                                .commit();
                    }
                }
            });

            topStory_View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TopStory topStory = topStories.get(getLayoutPosition());

                    if (topStory.getKids() != null) {

                        if (ItemListActivity.mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, topStory.getKids().toString());

                            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, topStory.getKids().toString());

                            ItemDetailFragment fragment = new ItemDetailFragment();
                            fragment.setArguments(arguments);

                            ((FragmentActivity) context).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        } else {
                            Intent intent = new Intent(context, ItemDetailActivity.class);
                            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, topStory.getKids().toString());
                            context.startActivity(intent);
                        }
                    } else {
                        Toast.makeText(context, "No comment for this article", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + author.getText() + "'";
        }
    }
}

