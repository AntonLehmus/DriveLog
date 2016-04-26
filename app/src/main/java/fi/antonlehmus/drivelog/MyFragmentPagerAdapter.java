package fi.antonlehmus.drivelog;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {


    private Context context;
    // Holds tab titles
    private String tabTitles[] = new String[] {"Log","List" };

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        String tabTitles[] = new String[] { context.getString(R.string.log_fragment_title), context.getString(R.string.list_fragment_title)};
    }


    @Override
    public int getCount() {
        return tabTitles.length;
    }

    // Return the correct Fragment based on index
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new LogFragment();
        } else if(position == 1) {
            return new ListFragment();
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Return the tab title to SlidingTabLayout
        return tabTitles[position];
    }
}