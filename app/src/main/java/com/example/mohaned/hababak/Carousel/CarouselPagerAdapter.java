package com.example.mohaned.hababak.Carousel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.mohaned.hababak.R;
import com.example.mohaned.hababak.home;
import com.example.mohaned.hababak.other_service;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private home context;
    int type;

    private FragmentManager fragmentManager;
    private float scale;
    private other_service context2;

    public CarouselPagerAdapter(home context, other_service context2, FragmentManager fm, int type) {
        super(fm);
        this.type = type;
        this.fragmentManager = fm;
        this.context = context;
        this.context2 = context2;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        if (type == 1) {
            try {
                if (position == home.FIRST_PAGE)
                    scale = BIG_SCALE;
                else
                    scale = SMALL_SCALE;

                position = position % home.count;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ItemFragment.newInstance(context, null, position, scale, type);

        } else {
            try {
                if (position == other_service.FIRST_PAGE)
                    scale = BIG_SCALE;
                else
                    scale = SMALL_SCALE;

                position = position % other_service.count;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return ItemFragment.newInstance(null, context2, position, scale, type);

        }
    }

    @Override
    public int getCount() {
        int count = 0;
        if (type == 1) {
            try {
                count = home.count * home.LOOPS;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        } else {
            try {
                count = other_service.count * other_service.LOOPS;
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        return count;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        try {
            if (positionOffset >= 0f && positionOffset <= 1f) {
                CarouselLinearLayout cur = getRootView(position);
                CarouselLinearLayout next = getRootView(position + 1);
                cur.setAlpha(1);
                next.setAlpha(0.5f);
                cur.setScaleBoth(BIG_SCALE - DIFF_SCALE * positionOffset);
                next.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @SuppressWarnings("ConstantConditions")
    private CarouselLinearLayout getRootView(int position) {
        return (CarouselLinearLayout) fragmentManager.findFragmentByTag(this.getFragmentTag(position))
                .getView().findViewById(R.id.root_container);
    }

    private String getFragmentTag(int position) {
        if (type == 1) return "android:switcher:" + context.pager.getId() + ":" + position;
        else return "android:switcher:" + context2.pager.getId() + ":" + position;

    }
}