package be.tjs.vubrestaurant;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.joda.time.LocalDate;

import java.util.Locale;

import be.tjs.vubrestaurant.core.Constants;
import be.tjs.vubrestaurant.core.RestaurantContainer;


public class MainActivity extends ActionBarActivity {
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    private static final String PREFS_FILENAME = "PreferencesVUBResto";
    private static final String PREFS_ACTIVE_RESTAURANT = "ActiveRestaurant";
    private static final String PREFS_LANGUAGE = "Language";

    RestaurantContainer restaurantContainer;
    private DatesAdapter datesAdapter;
    private ViewPager viewPager;

    private int activeRestaurant;

    private LoadContentTask loadContentTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Teal);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Restore preferences
        SharedPreferences preferences = getSharedPreferences(PREFS_FILENAME, MODE_PRIVATE);
        activeRestaurant = preferences.getInt(PREFS_ACTIVE_RESTAURANT, Constants.RESTO_ETTERBEEK);

        // Setup actionbar
        // TODO: use Toolbar from Appcompatv7
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setLogo(R.drawable.ic_vubrestaurant);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(
                new DropdownNavigationAdapter(this),
                new ActionBar.OnNavigationListener() {
                    @Override
                    public boolean onNavigationItemSelected(int position, long itemId) {
                        switchActiveRestaurant(position);
                        return true;
                    }
                });
        actionBar.setSelectedNavigationItem(activeRestaurant);

        // Select the correct language. If its set, take from settings, else derrive from locale
        int language = preferences.getInt(PREFS_LANGUAGE, getLanguageFromLocale());
        restaurantContainer = new RestaurantContainer(this.activeRestaurant, language);
        datesAdapter = new DatesAdapter(getSupportFragmentManager(), restaurantContainer.getDates());

        // Customize pagerTabStrip
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setTextColor(getResources().getColor(R.color.l_white));
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.l_dark_gray));

        // Set up the viewPager for first load (toggle visibility to hide the scroll to Today)
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(datesAdapter);
        viewPager.setVisibility(View.GONE);
        viewPager.setCurrentItem(0); // Today is always the first date (since 1.3.2)
        viewPager.setVisibility(View.VISIBLE);

        // Avoid having the loadContentTask working with an IllegalState
        Object retained = getLastCustomNonConfigurationInstance();
        if (retained instanceof LoadContentTask) {
            loadContentTask = (LoadContentTask) retained;
            loadContentTask.setActivity(this);
        }
    }

    private int getLanguageFromLocale(){
        if (Locale.getDefault().getLanguage().equals("nl")) {
            return Constants.LANG_NL;
        }
        return Constants.LANG_EN;
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Avoid having the loadContentTask working with an IllegalState
        if(loadContentTask != null) {
            loadContentTask.setActivity(this);
        }
        datesAdapter.notifyDataSetChanged();
        if (restaurantContainer.isEmpty()) {
            loadContent();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // We have the manually ensure the language state is correct
        menu.findItem(R.id.action_language).setChecked(
                restaurantContainer.getLanguage()==Constants.LANG_EN);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu selection
        switch (item.getItemId()) {
            case R.id.action_today:
                scrollToToday();
                return true;
            case R.id.action_language:
                // Toggle the visual state
                item.setChecked(!item.isChecked());
                switchLanguage();
                return true;
            case R.id.action_refresh:
                loadContent();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onPause() {
        // Avoid having the loadContentTask working with an IllegalState
        if(loadContentTask != null) {
            loadContentTask.setActivity(null);
        }
        // Make sure we save the current active restaurant for next time
        SharedPreferences preferences = getSharedPreferences(PREFS_FILENAME, MODE_PRIVATE);
        preferences.edit().putInt(PREFS_ACTIVE_RESTAURANT, this.activeRestaurant).apply();
        preferences.edit().putInt(PREFS_LANGUAGE, this.restaurantContainer.getLanguage()).apply();
        super.onPause();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        // Avoid having the loadContentTask working with an IllegalState
        if(loadContentTask != null) {
            loadContentTask.setActivity(null);
            return loadContentTask;
        }
        return null;
    }

    private void switchLanguage(){
        if(restaurantContainer.getLanguage() == Constants.LANG_EN){
            restaurantContainer.setLanguage(Constants.LANG_NL);
        } else {
            restaurantContainer.setLanguage(Constants.LANG_EN);
        }
        restaurantContainer.clear();
        datesAdapter.notifyDataSetChanged();
        loadContent();
    }

    private void switchActiveRestaurant(int restaurant) {
        this.activeRestaurant = restaurant;
        restaurantContainer.setActiveRestaurant(restaurant);
        if (restaurantContainer.isEmpty()) {
            loadContent();
        } else {
            datesAdapter.notifyDataSetChanged();
        }
    }

    private void scrollToToday() {
        // Today is always the first date (since 1.3.2)
        viewPager.setCurrentItem(0, true);
    }

    public void onContentLoaded(){
        datesAdapter.notifyDataSetChanged();
    }

    void loadContent() {
        try{
            if (loadContentTask != null && loadContentTask.isLoading()){
                loadContentTask.cancel(true);
            }
            loadContentTask = new LoadContentTask(this);
            loadContentTask.execute(this.activeRestaurant);
            datesAdapter.notifyDataSetChanged();
        } catch(Exception ex){
            Log.d(TAG, ex.getMessage());
        }

    }

    boolean isLoading(){
        return loadContentTask != null && loadContentTask.isLoading();
    }

    private class LoadContentTask extends AsyncTask<Object, Object, Boolean> {

        private MainActivity activity;
        private boolean isCompleted = false;

        public LoadContentTask(MainActivity activity) {
            this.activity = activity;
        }

        public void setActivity(MainActivity pActivity) {
            this.activity = pActivity;
        }

        private void notifiyActivityTaskCompleted() {
            if(activity != null) {
                activity.onContentLoaded();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.isCompleted = false;
        }

        @Override
        protected Boolean doInBackground(Object... args) {
            try {
                restaurantContainer.retrieveMenuItemsAndFill();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            this.isCompleted = true;
            notifiyActivityTaskCompleted();
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            this.isCompleted = true;
        }

        private boolean isLoading(){
            return !this.isCompleted;
        }
    }

    private class DatesAdapter extends FragmentStatePagerAdapter {
        private final LocalDate[] dates;

        public DatesAdapter(FragmentManager fragmentManager, LocalDate[] dates) {
            super(fragmentManager);
            this.dates = dates;
        }

        @Override
        public int getCount() {
            return dates.length;
        }

        @Override
        public Fragment getItem(int position) {
            return (DayFragment.newInstance(position, dates[position]));
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public String getPageTitle(int position) {
            return dates[position].toString("E d MMM");
        }
    }
}
