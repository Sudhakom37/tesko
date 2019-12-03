package com.pactera.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pactera.fragment.FirstFragment;
import com.pactera.fragment.SecondFragment;
import com.pactera.fragment.ThirdFragment;

/**
 * Created by P0147845 on 11-11-2019.
 */

public class QueueAnalysisAdapter extends FragmentPagerAdapter {

    public QueueAnalysisAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos) {

            case 0:
                return FirstFragment.newInstance("FirstFragment, Instance 1");
            case 1:
                return SecondFragment.newInstance("SecondFragment, Instance 1");
            case 2:
                return ThirdFragment.newInstance("ThirdFragment, Instance 1");
            default:
                return FirstFragment.newInstance("ThirdFragment, Default");
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}