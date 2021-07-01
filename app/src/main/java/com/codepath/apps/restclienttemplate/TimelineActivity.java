package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;
    long last_id;

    //all views from activity timeline xml
    RecyclerView rvTweets;
    Button btnLogout;
    SwipeRefreshLayout swipeContainer;
    Toolbar toolbar;

    TwitterClient client;
    TweetsAdapter adapter;
    List<Tweet> tweets;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApplication.getRestClient(this);

        //find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        btnLogout = findViewById(R.id.btnLogout);
        swipeContainer = findViewById(R.id.swipeContainer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setup refresh listener to trigger new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });

        //refresh color configurations
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        //recycler view setup (layout manager and adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        //layout manager for endless scroll
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore");
                //called when new data needs to be added to the list
                loadNextData();

            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        populateHomeTimeline();
    }

    //inflates the menu and adds items to the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //functionality for the compose tweet button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.compose) {
            //compose icon was clicked; navigate to compose activity
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
        if(item.getItemId() == R.id.logout) {
            //logout button clicked
            client.clearAccessToken();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //returns the data from a child activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //get data from the intent
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            //update recycler view with new tweet
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            //reset to position 0 of recycler view
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //creates json array of timeline data and populates the timeline recycler view
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {
                    //add all tweets in json array to tweets list
                    Log.d(TAG, "Successful load");
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    //notify adapter of new tweet data
                    adapter.notifyDataSetChanged();
                    last_id = tweets.get(tweets.size() - 1).id;
                } catch (JSONException e) {
                    Log.d(TAG, "Json Exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure!", throwable);
            }
        });
    }

    //sends the Twitter API a request to fetch updated data
    public void fetchTimelineAsync(int page) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                adapter.clear();
                try {
                    Log.i(TAG, "onSuccess!!");
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                    last_id = tweets.get(tweets.size() - 1).id;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Fetch timeline error", throwable);
            }
        });
    }

    //loads the next 25 tweets from the API
    private void loadNextData() {
        client.homeTimelineRefresh(last_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                    adapter.notifyDataSetChanged();
                    last_id = tweets.get(tweets.size() - 1).id;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }
}