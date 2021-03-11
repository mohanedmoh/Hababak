package com.example.mohaned.hababak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mohaned.hababak.Carousel.CarouselPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout departure_linear,arrival_linear,patient_linear,service_linear,map_linear,current_proccess;
    private OnFragmentInteractionListener mListener;
    private Context context;
    SharedPreferences shared;

    public final static int LOOPS = 1000;
    /////////count to pages
    public static int count = 6;
    public static int FIRST_PAGE = 6;
    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    public home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static home newInstance(String param1, String param2) {
        home fragment = new home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        shared = this.getActivity().getSharedPreferences("com.example.mohaned.hababak", Context.MODE_PRIVATE);

        view= inflater.inflate(R.layout.fragment_home, container, false);
        System.out.println("GGGGGGGGGGGGGGG");
        check();
        init();
        return view;
    }

    public void init() {
        pager = view.findViewById(R.id.myviewpager);
        //set page margin between pages for viewpager
        DisplayMetrics metrics = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int pageMargin = ((metrics.widthPixels / 4) * 2);
        pager.setPageMargin(-pageMargin);
        System.out.println("BEFORE ADAPTER");
        adapter = new CarouselPagerAdapter(this, null, getFragmentManager(), 1);
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        pager.addOnPageChangeListener(adapter);
        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(shared.getInt("items", FIRST_PAGE * 6));
        pager.setOffscreenPageLimit(4);
        view.findViewById(R.id.booknow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(pager.getCurrentItem() % count);
            }
        });
    }
    /* public void init() {
         departure_linear= view.findViewById(R.id.departure_linear);
         arrival_linear= view.findViewById(R.id.arrival_linear);
         patient_linear=view.findViewById(R.id.patient_linear);
         service_linear=view.findViewById(R.id.service_linear);
         map_linear=view.findViewById(R.id.map_linear);
         current_proccess=view.findViewById(R.id.current_proccess);
         departure_linear.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 openNextActivity(view);
             }
         });
         arrival_linear.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 openNextActivity(view);
             }
         });
         patient_linear.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 openNextActivity(view);
             }
         });
         service_linear.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 openOtherService(view);
             }
         });
         map_linear.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 openMap(view);
             }
         });
         current_proccess.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 openCurrentProccess(view);
             }
         });
     }

     */
    public void check() {
        if (shared.getBoolean("login", false)) {

        } else {
            Intent i = new Intent(getActivity().getApplicationContext(), Login.class);
            startActivity(i);
            this.getActivity().finish();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        this.context=context;
        Bundle bundle = getArguments();

        // onCreateView(getLayoutInflater(), ((ViewGroup)this.getView()),bundle);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }

}
