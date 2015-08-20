package com.varwise.funnyjokes;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.varwise.funnyjokes.db.Category;
import com.varwise.funnyjokes.db.JokesDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hotchemi.android.rate.AppRate;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private JokesDB db;
    private ListView categoriesView;
    private List<Category> categories;
    public static int totalAdsThisRun = 0;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    public static boolean shouldShowAds = true;
    public static boolean shouldShowAppRate = false;
    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupGoogleAnalytics();

        categories = new ArrayList<>();
        categoriesView = (ListView)findViewById(R.id.categoryList);
        button = (Button)findViewById(R.id.buttonRandom);
        button.setOnClickListener(this);

        categoriesView.setOnItemClickListener(this);

        db = new JokesDB(getApplicationContext());
        fillCategoriesList();
        maybeShowAppRate();
        tracker.setScreenName(getLocalClassName());
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    private void setupGoogleAnalytics(){
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(getResources().getString(R.string.googleAnalytics));
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    private void maybeShowAppRate(){
        if(shouldShowAppRate) {
            AppRate.with(this)
                    .setInstallDays(1)
                    .setLaunchTimes(3)
                    .setRemindInterval(1)
                    .setShowNeutralButton(true)
                    .setDebug(false)
                    .monitor();

            AppRate.showRateDialogIfMeetsConditions(this);
        }
    }


    private void fillCategoriesList(){
        Cursor c = db.getAllCategories();
        if(c.moveToFirst()){
            do{
                Category category = new Category(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("name")));
                categories.add(category);
            } while(c.moveToNext());
        }

        ArrayAdapter<Category> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, categories);
        categoriesView.setAdapter(arrayAdapter);
        db.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startJokeActivity(position, false);
    }

    private void startJokeActivity(int categoryPosition, boolean randomize){
        Intent i = new Intent(getApplicationContext(), JokeActivity.class);
        Category categoryClicked = categories.get(categoryPosition);
        i.putExtra("categoryId", categoryClicked._id);
        i.putExtra("categoryName", categoryClicked.name);
        i.putExtra("randomize", randomize);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonRandom){
            int categoryPosition = new Random().nextInt(categories.size());
            startJokeActivity(categoryPosition, true);
        }
    }
}
