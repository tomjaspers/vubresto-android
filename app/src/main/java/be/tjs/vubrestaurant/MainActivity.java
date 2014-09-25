package be.tjs.vubrestaurant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;

import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.List;

import be.tjs.vubrestaurant.core.RestaurantContainer;
import be.tjs.vubrestaurant.core.RestaurantParser;


public class MainActivity extends ActionBarActivity {
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";

    private RestaurantContainer restaurantContainer;
    private DatesAdapter datesAdapter;
    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Teal);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Setup actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setSubtitle("Etterbeek");
        actionBar.setLogo(R.drawable.ic_vubrestaurant);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (restaurantContainer.isCleared()) {
            datesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu selection
        switch (item.getItemId()) {
            case R.id.action_today:
                scrollToToday(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void scrollToToday(boolean animated) {
        viewPager.setCurrentItem(datesAdapter.getIndexToday(), animated);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Initialize elements
        restaurantContainer = RestaurantContainer.getInstance();
        datesAdapter = new DatesAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);

        // Customize pagerTabStrip
        pagerTabStrip.setTextColor(getResources().getColor(R.color.l_white));
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.0f);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.l_dark_gray));

        // Set up the viewPager for first load
        // Toggle visibility so the scroll to Today isn't noticeable
        viewPager.setAdapter(datesAdapter);
        viewPager.setVisibility(View.GONE);
        viewPager.setCurrentItem(datesAdapter.getIndexToday());
        viewPager.setVisibility(View.VISIBLE);

        // Analytics
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Analytics
        EasyTracker.getInstance().activityStop(this);
    }

    private class DatesAdapter extends FragmentStatePagerAdapter {
        private List<LocalDate> dates;

        public DatesAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            this.dates = Arrays.asList(RestaurantParser.getDates());
        }

        public int getIndexToday() {
            // Today is always the first date (since 1.3.2)
            return 0;
        }

        @Override
        public int getCount() {
            return dates.size();
        }

        @Override
        public Fragment getItem(int position) {
            return (DayFragment.newInstance(position, dates.get(position)));
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public String getPageTitle(int position) {
            return dates.get(position).toString("E d MMM");
        }
    }
}
