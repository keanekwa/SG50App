package com.example.user.sg50app;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 4;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open FragmentTab1.java
            case 0:
                TopPhotosFragment fragmenttab1 = new TopPhotosFragment();
                return fragmenttab1;

            // Open FragmentTab2.java
            case 1:
                BestOfPastFragment fragmenttab2 = new BestOfPastFragment();
                return fragmenttab2;
            case 2:
                DayAsSingaporeanFragment fragmenttab3 = new DayAsSingaporeanFragment();
                return fragmenttab3;
            case 3:
               FutureHopesFragment fragmenttab4 = new FutureHopesFragment();
                return fragmenttab4;
        }
        return null;
    }

    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "Top Photos";
            case 1:
                return "Best of Past";
            case 2:
                return "Day as a Singaporean";
            case 3:
                return "Future Hopes";
        }
        return null;
    }


    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}