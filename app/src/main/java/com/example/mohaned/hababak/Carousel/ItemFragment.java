package com.example.mohaned.hababak.Carousel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mohaned.hababak.R;
import com.example.mohaned.hababak.current_req_status;
import com.example.mohaned.hababak.home;
import com.example.mohaned.hababak.map_main;
import com.example.mohaned.hababak.other_service;
import com.example.mohaned.hababak.other_service_request;
import com.example.mohaned.hababak.traveller_main;

public class ItemFragment extends Fragment {

    private static final String POSITON = "position";
    private static final String SCALE = "scale";
    private static final String DRAWABLE_RESOURE = "resource";
    private static String[] text;

    private int screenWidth;
    private int screenHeight;
    private static int typea;

    private int[] imageArray = new int[]{R.drawable.takeoff, R.drawable.arrival,
            R.drawable.patient,R.drawable.services, R.drawable.route,
            R.drawable.process};
    private int[] imageArray2 = new int[]{R.drawable.services, R.drawable.services,
            R.drawable.services};

    public static Fragment newInstance(home context, other_service context2, int pos, float scale, int type) {
        typea = type;
        Bundle b = new Bundle();
        b.putInt(POSITON, pos);
        b.putFloat(SCALE, scale);
        if (type == 1) {

            text = new String[]{context.getString(R.string.departure),
                    context.getString(R.string.arrival),
                    context.getString(R.string.sick_travel),
                    context.getString(R.string.services),
                    context.getString(R.string.route),
                    context.getString(R.string.req_process)
            };
            return Fragment.instantiate(context.getActivity().getApplicationContext(), ItemFragment.class.getName(), b);

        } else {
            text = new String[]{context2.getString(R.string.exit_visa),
                    context2.getString(R.string.yellow_fever),
                    context2.getString(R.string.national_service)
            };
            return Fragment.instantiate(context2.getApplicationContext(), ItemFragment.class.getName(), b);

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidthAndHeight();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }


        final int postion = this.getArguments().getInt(POSITON);
        float scale = this.getArguments().getFloat(SCALE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth / 2, screenHeight / 2);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_image, container, false);
        try {


            TextView textView = (TextView) linearLayout.findViewById(R.id.text);
            CarouselLinearLayout root = (CarouselLinearLayout) linearLayout.findViewById(R.id.root_container);
            RelativeLayout relative = (RelativeLayout) linearLayout.findViewById(R.id.root_relative);
            ImageView imageView = (ImageView) linearLayout.findViewById(R.id.pagerImg);
            textView.setText(text[postion]);
            //relative.setLayoutParams(layoutParams);
            if (typea == 1) {
                imageView.setImageResource(imageArray[postion]);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click(postion);
                    }
                });
            } else {
                imageView.setImageResource(imageArray2[postion]);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OtherClick(postion);
                    }
                });
            }

            root.setScaleBoth(scale);
        } catch (Exception e) {
        }

        return linearLayout;
    }

    private void click(int postion) {
        switch (postion) {
            case 0: {
                openNextActivity(1);
            }
            break;
            case 1: {
                openNextActivity(2);
            }
            break;
            case 2: {
                openNextActivity(3);
            }
            break;
            case 3: {
                openOtherService();
            }
            break;
            case 4: {
                openMap();
            }
            break;
            case 5: {
                openCurrentProccess();
            }
            break;
        }
    }

    private void OtherClick(int postion) {
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
        Intent intent = new Intent(getActivity().getApplicationContext(), other_service_request.class);
        if (id == 1) {
            intent.putExtra("type", 1);
        } else if (id == 2) {
            intent.putExtra("type", 2);
        } else if (id == 3) {
            intent.putExtra("type", 3);
        }
        startActivity(intent);
    }

    private void openMap() {
        Intent intent = new Intent(getActivity().getApplicationContext(), map_main.class);
        startActivity(intent);
    }

    private void openCurrentProccess() {
        Intent intent = new Intent(getActivity().getApplicationContext(), current_req_status.class);
        startActivity(intent);
    }

    private void openOtherService() {
        Intent intent = new Intent(getActivity().getApplicationContext(), other_service.class);
        startActivity(intent);
    }

    public void openNextActivity(int id) {
        Intent intent = new Intent(getActivity().getApplicationContext(), traveller_main.class);
        if (id == 1) {
            intent.putExtra("type", 1);
        } else if (id == 2) {
            intent.putExtra("type", 2);
        } else if (id == 3) {
            intent.putExtra("type", 3);

        }
        startActivity(intent);
    }

    /**
     * Get device screen width and height
     */
    private void getWidthAndHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }
}