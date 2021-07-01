package com.codepath.apps.restclienttemplate;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/* This is the object responsible for communicating with a REST API */

public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); //creating instance of Twitter API
	public static final String REST_URL = "https://api.twitter.com/1.1"; // base API url
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // consumer key
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // secret consumer key

	// backup url in case Chrome blocks navigation back to the app
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	/* all methods for twitter API access */

	//retrieves home timeline data
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// specifying query string params through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25); //number of tweets
		params.put("since_id", 1); //restriction for how recent tweet must be
		client.get(apiUrl, params, handler);
	}

	//updates the Twitter API with a new tweet
	public void publishTweet(String tweetContent, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		// specifying query string params through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", tweetContent);
		client.post(apiUrl, params, "",handler);
	}

	//retrieves home timeline data
	public void homeTimelineRefresh(long max_id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// specifying query string params through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25); //number of tweets
		params.put("max_id", max_id); //fetching the next 25 latest tweets
		client.get(apiUrl, params, handler);
	}

}
