package com.varwise.funnyjokes;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.varwise.funnyjokes.db.Joke;
import com.varwise.funnyjokes.db.JokesDB;

import java.util.ArrayList;
import java.util.Random;

public class JokeActivity extends Activity implements View.OnClickListener {
    private AdView mAdView;
    private TextView jokeTextView;
    private ArrayList<Joke> jokes;
    private int currentJoke = 0;
    private int totalJokes = 0;
    private InterstitialAd interstitial;
    private AdRequest adRequest;
    private Tracker t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t = MainActivity.tracker;
        t.setScreenName(getLocalClassName());
        t.send(new HitBuilders.AppViewBuilder().build());

        setContentView(R.layout.joke_activity);
        jokes = new ArrayList<>();
        if(MainActivity.shouldShowAds) {
            mAdView = (AdView) findViewById(R.id.adView);
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("0457F45F2F3B38D51216287AD98A2C3D").addTestDevice("3AC2DCEE575018317C028D0C93F19AD0").addTestDevice("2D7D6AE8606296EB97A2A9B3681B90F6").build();
            mAdView.loadAd(adRequest);
        }

        ImageView backButton = (ImageView) findViewById(R.id.buttonBack);
        ImageView nextButton = (ImageView) findViewById(R.id.buttonNext);
        ImageView shareButton = (ImageView) findViewById(R.id.buttonShare);

        jokeTextView = (TextView)findViewById(R.id.jokeTextView);

        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        Intent intent = getIntent();
        int categoryId = intent.getIntExtra("categoryId", 0);
        String categoryName = intent.getStringExtra("categoryName");



        String newTitle = getResources().getString(R.string.app_name)  + ": " + categoryName;
        ActionBar ab = getActionBar();
        if(ab != null){
            ab.setTitle(newTitle);
        }

        setupJokesForCategory(categoryId);

        if(intent.getBooleanExtra("randomize", false)){
            currentJoke = new Random().nextInt(jokes.size());
        }

        totalJokes = jokes.size();
        showJoke();
    }

    private void maybeShowInterstitial(){
        if(MainActivity.shouldShowAds && (MainActivity.totalAdsThisRun > 20 && MainActivity.totalAdsThisRun % 25 == 0)) {
            interstitial = new InterstitialAd(this);
            interstitial.setAdUnitId("ca-app-pub-5829945009169600/3045839569");
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    interstitial.show();
                }
            });

            interstitial.loadAd(adRequest);
        }
    }

    private void setupJokesForCategory(int categoryId){
        JokesDB db;
        db = new JokesDB(getApplicationContext());
        Cursor c = db.getAllJokesInCategoryById(categoryId);
        if(c.moveToFirst()){
            do{
                jokes.add(new Joke(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("content")), c.getInt(c.getColumnIndex("categoryId"))));
            } while(c.moveToNext());
        }
        db.close();
    }

    private void showJoke(){
        ++MainActivity.totalAdsThisRun;
        maybeShowInterstitial();
        String header = getResources().getString(R.string.jokeNumber) + " " + (currentJoke+1) + "/" + totalJokes + "\n\n";
        jokeTextView.setText(header + Html.fromHtml(jokes.get(currentJoke).content));
    }


    private void handleBackClick(){
        t.send(new HitBuilders.EventBuilder().setCategory("Jokes").setAction("click").setLabel("back").build());
        if(currentJoke > 0) {
            --currentJoke;
            showJoke();
        }
    }

    private void handleNextClick(){
        t.send(new HitBuilders.EventBuilder().setCategory("Jokes").setAction("click").setLabel("next").build());

        if(currentJoke < totalJokes-1) {
            ++currentJoke;
            showJoke();
        }
    }

    private void handleShareClick(){
        t.send(new HitBuilders.EventBuilder().setCategory("Jokes").setAction("click").setLabel("share").build());

        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", getResources().getString(R.string.app_name));
        intent.putExtra("android.intent.extra.TEXT", Html.fromHtml(jokes.get(currentJoke).content).toString());
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonBack:
                handleBackClick();
                break;
            case R.id.buttonNext:
                handleNextClick();
                break;
            case R.id.buttonShare:
                handleShareClick();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(MainActivity.shouldShowAds) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        if(MainActivity.shouldShowAds) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(MainActivity.shouldShowAds) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
