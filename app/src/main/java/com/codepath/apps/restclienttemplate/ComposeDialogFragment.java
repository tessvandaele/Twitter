package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import okhttp3.Headers;

public class ComposeDialogFragment extends DialogFragment {

    public static final int MAX_TWEET_LENGTH = 140;
    public static final String TAG = "ComposeFragment";

    //activity_compose xml views
    EditText etCompose;
    Button btnTweet;
    ImageButton btnClose;

    //Twitter client to post tweet to Twitter API
    TwitterClient client;

    //interface to pass tweet back to timeline activity
    public interface EditNameDialogListener {
        void onFinishEditDialog(Tweet tweet);
    }

    // Empty constructor required for DialogFragment
    public ComposeDialogFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //assigning each view in the fragment
        etCompose = view.findViewById(R.id.etCompose);
        btnTweet = view.findViewById(R.id.btnTweet);
        btnClose = view.findViewById(R.id.btnClose);

        client = TwitterApplication.getRestClient(getContext());

        //add a click listener on tweet button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieving text from the edit text view
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()) { //attempt to post an empty tweet
                    Toast.makeText(getContext(), "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if(tweetContent.length() > MAX_TWEET_LENGTH) { //attempt to exceed the character limit
                    Toast.makeText(getContext(), "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }

                //make an api call to Twitter to publish tweet
                Toast.makeText(getContext(), tweetContent, Toast.LENGTH_LONG).show();
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            //publish tweet
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            EditNameDialogListener listener = (EditNameDialogListener) getActivity();
                            //pass tweet back to timeline activity
                            listener.onFinishEditDialog(tweet);
                            //close modal
                            getDialog().dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });


        //close modal when clicking close button
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        // request focus to field
        etCompose.requestFocus();
    }
}
