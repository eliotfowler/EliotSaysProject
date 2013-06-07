package com.eliotfowler.eliotsays;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView tweetDisplay;
    private final String URL = "https://api.twitter.com/1/statuses/user_timeline.json?include_entities=true&include_rts=true&trim_user=true&screen_name=eliotfowler&page=0&count=800";
    private ArrayList<String> tweets = new ArrayList<String>();
    private boolean initDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tweetDisplay = (TextView)findViewById(R.id.tweetDisplay);
        new GetTweets().execute(URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onTouchEvent(MotionEvent event){
        int action = MotionEventCompat.getActionMasked(event);
        if(action == MotionEvent.ACTION_DOWN) {
            System.out.println("Action was down");
            int randomIndex = (int) (Math.random() * tweets.size());
            System.out.println("Random index is " + randomIndex);
            tweetDisplay.setText(tweets.get(randomIndex));
            System.out.println("Tweet is " + tweets.get(randomIndex));
            return true;
        }
        return super.onTouchEvent(event);
    }

    private class GetTweets extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... twitterURL) {
            StringBuilder tweetFeedBuilder = new StringBuilder();
            for (String searchURL : twitterURL) {
                HttpClient tweetClient = new DefaultHttpClient();
                try {
                    HttpGet tweetGet = new HttpGet(searchURL);
                    HttpResponse tweetResponse = tweetClient.execute(tweetGet);
                    StatusLine searchStatus = tweetResponse.getStatusLine();
                    if (searchStatus.getStatusCode() == 200) {
                        HttpEntity tweetEntity = tweetResponse.getEntity();
                        InputStream tweetContent = tweetEntity.getContent();
                        InputStreamReader tweetInput = new InputStreamReader(tweetContent);
                        BufferedReader tweetReader = new BufferedReader(tweetInput);
                        String lineIn;
                        while ((lineIn = tweetReader.readLine()) != null) {
                            tweetFeedBuilder.append(lineIn);
                        }
                    }
                    else
                        tweetDisplay.setText("Whoops - something went wrong!");
                }
                catch(Exception e) {
                    tweetDisplay.setText("Whoops - something went wrong!");
                    e.printStackTrace();
                }
            }
            return tweetFeedBuilder.toString();
        }

        protected void onPostExecute(String result) {
            StringBuilder tweetResultBuilder = new StringBuilder();
            try {
                JSONArray tweetArray =  new JSONArray(result);
                for(int t=0; t<tweetArray.length(); t++) {
                    JSONObject tweet = tweetArray.getJSONObject(t);
                    tweets.add(tweet.getString("text"));
                }
            }
            catch (Exception e) {
                tweetDisplay.setText("Whoops - something went wrong!");
                e.printStackTrace();
            }
            initDone = true;
        }
    }
    
}
