package com.example.mohaned.hababak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mohaned.hababak.Carousel.CarouselPagerAdapter;

public class other_service extends AppCompatActivity {
    LinearLayout exit_linear,yellow_linear,national_linear;
    public final static int LOOPS = 1000;
    public static int count = 3;
    public static int FIRST_PAGE = 3;
    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_service);
        //exit_linear=findViewById(R.id.exit_linear);
        //yellow_linear=findViewById(R.id.yellow_linear);
        //national_linear=findViewById(R.id.national_linear);
        init();
    }

    public void init() {
        pager = findViewById(R.id.myviewpager);
        //set page margin between pages for viewpager
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int pageMargin = ((metrics.widthPixels / 4) * 2);
        pager.setPageMargin(-pageMargin);

        adapter = new CarouselPagerAdapter(null, this, getSupportFragmentManager(), 2);
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        pager.addOnPageChangeListener(adapter);
        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);
        pager.setOffscreenPageLimit(4);
        findViewById(R.id.booknow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(pager.getCurrentItem() % 3);
            }
        });
    }

    private void click(int postion) {
        switch (postion) {
            case 0: {
                openForm(1);
            }
            break;
            case 1: {
                openForm(2);
            }
            break;
            case 2: {
                openForm(3);
            }
            break;
        }
    }

    private void openForm(int id) {
        Intent intent = new Intent(getApplicationContext(), other_service_request.class);
        if (id == 1) {
            intent.putExtra("type",1);
        } else if (id == 2) {
            intent.putExtra("type",2);
        } else if (id == 3) {
            intent.putExtra("type",3);
        }
        startActivity(intent);
    }
}
