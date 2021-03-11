package com.example.mohaned.hababak;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohaned.hababak.Adapter.travellers_adapter;
import com.example.mohaned.hababak.Models.req_info;
import com.example.mohaned.hababak.Models.traveller_info;
import com.example.mohaned.hababak.Network.Iokihttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link history.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link history#newInstance} factory method to
 * create an instance of this fragment.
 */
public class history extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public View view;
    public ListView listView;
    public ArrayList<req_info> dataModels;
    public req_status_list_adapter adapter;
    public ListView travellerslistView;
    public ArrayList<traveller_info> travellersDataModels;
    public travellers_adapter travellerAdapter;
    public boolean main=true;
    private Iokihttp okhttp;
    private OnFragmentInteractionListener mListener;
    SharedPreferences shared;
    Handler handler;

    public history() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment history.
     */
    // TODO: Rename and change types and number of parameters
    public static history newInstance(String param1, String param2) {
        history fragment = new history();
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
        view = inflater.inflate(R.layout.fragment_history, container, false);
        shared = this.getActivity().getSharedPreferences("com.example.mohaned.hababak", Context.MODE_PRIVATE);
        okhttp=new Iokihttp();
        handler = new Handler(this.getActivity().getMainLooper());
        if(shared.getString("user_id","")!="") {
            getList(Integer.parseInt(shared.getString("user_id","")));
        }
        return view;
    }
    private void getList(int id){
        System.out.println("ID IS : "+id);
        JSONArray jsonArray=new JSONArray();
        JSONObject json=new JSONObject();
        final JSONObject subJSON = new JSONObject();
        try {

            json.put("method","getHistory");
            subJSON.put("id", id);
            json.put("data",subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getContext())) {
            showLoading();
            okhttp.post(getContext().getResources().getString(R.string.lara_url) + "getHistory", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    history.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                        }});
                    //  hideLoading();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    // hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                if (subresJSON.get("historyReq").toString().equals("[]")) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.no_complete_request), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    JSONArray req = new JSONArray(subresJSON.get("historyReq").toString());
                                    if (req.length() > 0) setList(req);
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.no_complete_request), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                try{
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                                    }
                                });}
                                catch (Exception e){}
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }

            });
        }
    }
    public void setList(JSONArray companionsA) throws JSONException {
        System.out.println("11");
        listView=view.findViewById(R.id.history_req_list);
        System.out.println("22");
        dataModels= new ArrayList<>();
        System.out.println("length=" + companionsA.length());
        if (companionsA.getJSONObject(0).length() == 0) {
            history.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.no_req), Toast.LENGTH_SHORT).show();
                    //  hideLoading();
                }
            });
        }
        else {
            // System.out.println("33"+y);
            for (int x = 0; x < companionsA.length(); x++) {
                req_info dataModel = new req_info();
                dataModel.setId(((JSONObject)companionsA.get(x)).getString("id"));
                dataModel.setFlight_date(((JSONObject)companionsA.get(x)).getString("flight_date"));
                dataModel.setFlight_time(((JSONObject)companionsA.get(x)).getString("flight_time"));
                dataModel.setDate(((JSONObject)companionsA.get(x)).getString("creation_date"));
                dataModel.setDestination(((JSONObject)companionsA.get(x)).getString("destination"));
                dataModel.setTraveller_name(((JSONObject)companionsA.get(x)).getString("name"));
                dataModel.setPassport(((JSONObject)companionsA.get(x)).getString("passport"));
                dataModel.setFlag(((JSONObject)companionsA.get(x)).getString("flag"));
                dataModel.setType(((JSONObject)companionsA.get(x)).getString("type"));
                dataModel.setPrice(((JSONObject)companionsA.get(x)).getString("price"));
                dataModel.setPhone(((JSONObject)companionsA.get(x)).getString("country_code")+" "+((JSONObject)companionsA.get(x)).getString("phone"));
                dataModel.setStatus(((JSONObject)companionsA.get(x)).getString("status"));
                if( ((JSONObject)companionsA.get(x)).getInt("flag")==1){
                    dataModel.setCompanion_no(((JSONObject)companionsA.get(x)).getString("companions_no"));
                    dataModel.setPackage_info(((JSONObject)companionsA.get(x)).getString("package"));
                    dataModel.setAirline(((JSONObject)companionsA.get(x)).getString("airline"));
                    dataModel.setFlight_no(((JSONObject)companionsA.get(x)).getString("flight_no"));
                    dataModel.setBirth_date(((JSONObject)companionsA.get(x)).getString("age"));
                    dataModel.setNationality(((JSONObject)companionsA.get(x)).getString("nationality"));
                }
                else{
                    dataModel.setHaveNat(((JSONObject)companionsA.get(x)).getString("has_ns"));
                    dataModel.setProfession(((JSONObject)companionsA.get(x)).getString("job"));
                    dataModel.setVisa(((JSONObject)companionsA.get(x)).getString("permit"));
                }
                if(((JSONObject)companionsA.get(x)).getInt("type")==2 && ((JSONObject)companionsA.get(x)).getInt("flag")==1){
                    dataModel.setLuggage_no(((JSONObject)companionsA.get(x)).getString("luggages_no"));
                }
                else if(((JSONObject)companionsA.get(x)).getInt("type")==3 && ((JSONObject)companionsA.get(x)).getInt("flag")==1){
                    dataModel.setLuggage_no(((JSONObject)companionsA.get(x)).getString("treatment_details"));
                }

                dataModels.add(dataModel);
            }

            adapter = new req_status_list_adapter(dataModels, getContext());

           /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    req_info dataModel = dataModels.get(position);
                    openDetailsLayout(dataModel.getId());
                    //   intent.putExtra();
                    // startActivity(intent);
                }
            });*/
            history.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    listView.setAdapter(adapter);
                }
            });
        }
    }

    public void getTravellerList(String id, String type) {
        JSONArray jsonArray=new JSONArray();
        JSONObject json=new JSONObject();
        JSONObject subJSON=new JSONObject();
        try {
            subJSON.put("id",id);
            subJSON.put("type", type);
            json.put("method","getRequest");
            json.put("data",subJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(okhttp.isNetworkConnected(getContext())) {
            showLoading();
            okhttp.post(getContext().getResources().getString(R.string.lara_url) + "getRequest", json.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hideLoading();
                    System.out.println("FAIL");
                    history.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
                            //  hideLoading();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    hideLoading();
                    // hideLoading();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        System.out.println(responseStr+"trrrrraaaaaaaaaaaveeeeeeeel");
                        try {
                            JSONObject resJSON = new JSONObject(responseStr);
                            JSONObject subresJSON = new JSONObject(resJSON.getString("data"));
                            if (Integer.parseInt(resJSON.get("error").toString()) == 1) {
                                JSONArray companions = new JSONArray(subresJSON.get("travellers").toString());
                                setTravellersList(companions);
                            } else {
                                history.this.getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getActivity().getApplicationContext(), getContext().getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response=" + responseStr);

                    } else {
                        System.out.println("Response=" + "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                }

            });
        }
    }
    public void setTravellersList(JSONArray travellersList) throws JSONException {
        System.out.println("11");
        travellerslistView=view.findViewById(R.id.main_profile_include).findViewById(R.id.req_travellers_list);
        System.out.println("22");
        JSONObject jsonObject;

        travellersDataModels= new ArrayList<>();
        if(travellersList.length()==0){}
        else {
            // System.out.println("33"+y);
            for (int x = 0; x < travellersList.length(); x++) {
                traveller_info dataModel = new traveller_info();

                dataModel.setTraveller_name(((JSONObject)travellersList.get(x)).getString("name"));
                dataModel.setAge(((JSONObject)travellersList.get(x)).getString("age"));
                dataModel.setPassport_no(((JSONObject)travellersList.get(x)).getString("passport"));

                travellersDataModels.add(dataModel);
            }

            travellerAdapter = new travellers_adapter(travellersDataModels, getContext());

            travellerslistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    traveller_info dataModel = travellersDataModels.get(position);

                    //   intent.putExtra();
                    // startActivity(intent);
                }
            });
            history.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    travellerslistView.setAdapter(travellerAdapter);
                }
            });
        }
    }
    private void setInfo(req_info model){

        if(Integer.parseInt(model.getFlag())==1){
            View view =getActivity().findViewById(R.id.main_profile_include);
            int[] i = {R.id.order_noT, R.id.dateT, R.id.statusT, R.id.typeT, R.id.nameT, R.id.passportT, R.id.nationalityT, R.id.bdateT, R.id.airlineT, R.id.fnoT, R.id.companionT, R.id.priceT, R.id.packageT, R.id.medicalT, R.id.destinationT, R.id.fdateT,
                    R.id.ftimeT};
            appendToText(i, view);
            ((TextView)view.findViewById(R.id.req_id)).setText(model.getId());
            ((TextView)view.findViewById(R.id.req_date)).setText(model.getDate());
            ((TextView)view.findViewById(R.id.req_type)).setText(getReqType(model.getFlag(),model.getType()));
            ((TextView)view.findViewById(R.id.req_status)).setText(getStatusString(Integer.parseInt(model.getStatus())));
            ((TextView)view.findViewById(R.id.req_name)).setText(model.getTraveller_name());
            ((TextView)view.findViewById(R.id.p_no)).setText(model.getPassport());
            System.out.println(model.getNationality()+"ggggggggggggggg");
            ((TextView)view.findViewById(R.id.req_nationality)).setText(getNationality(Integer.parseInt(model.getNationality())));
            ((TextView)view.findViewById(R.id.req_birthdate)).setText(model.getBirth_date());
            ((TextView)view.findViewById(R.id.req_destination)).setText(model.getDestination());
            ((TextView)view.findViewById(R.id.req_airline)).setText(model.getAirline());
            ((TextView)view.findViewById(R.id.req_flight_date)).setText(model.getFlight_date());
            ((TextView)view.findViewById(R.id.req_flight_time)).setText(model.getFlight_time());
            ((TextView)view.findViewById(R.id.req_phone)).setText(model.getPhone());
            ((TextView)view.findViewById(R.id.req_companion)).setText(model.getCompanion_no());
            ((TextView) view.findViewById(R.id.req_price)).setText(model.getPrice() == null ? "0" : model.getPrice() + " " + getActivity().getApplicationContext().getResources().getString(R.string.SDG));
            ((TextView)view.findViewById(R.id.req_package)).setText(getPackage(Integer.parseInt(model.getPackage_info())));

            if(Integer.parseInt(model.getType())==2){
                ((TextView)view.findViewById(R.id.req_luggage)).setText(model.getLuggage_no());
            }
            else if(Integer.parseInt(model.getType())==3){
                ((TextView)view.findViewById(R.id.req_medical_details)).setText(model.getMedical_details());
            }
        }
        else if(Integer.parseInt(model.getFlag())==2){
            View view =getActivity().findViewById(R.id.other_profile_include);
            int[] i = {R.id.order_noT, R.id.dateT, R.id.statusT, R.id.typeT, R.id.nameT, R.id.passportT, R.id.nationalityT, R.id.permitT, R.id.destinationT, R.id.jobT, R.id.fdateT,
                    R.id.ftimeT, R.id.priceT};
            appendToText(i, view);
            ((TextView)view.findViewById(R.id.req_id)).setText(model.getId());
            ((TextView)view.findViewById(R.id.req_date)).setText(model.getDate());
            ((TextView)view.findViewById(R.id.req_type)).setText(getReqType(model.getFlag(),model.getType()));
            ((TextView)view.findViewById(R.id.req_status)).setText(getStatusString(Integer.parseInt(model.getStatus())));
            ((TextView)view.findViewById(R.id.req_name)).setText(model.getTraveller_name());
            ((TextView)view.findViewById(R.id.p_no)).setText(model.getPassport());
           // ((TextView)view.findViewById(R.id.req_nationality)).setText(getNationality(Integer.parseInt(model.getNationality())));
            ((TextView)view.findViewById(R.id.req_permit)).setText(getPermit(Integer.parseInt(model.getVisa())));
            ((TextView)view.findViewById(R.id.req_destination)).setText(model.getDestination());
            ((TextView)view.findViewById(R.id.req_profession)).setText(model.getProfession());
            ((TextView)view.findViewById(R.id.req_flight_date)).setText(model.getFlight_date());
            ((TextView)view.findViewById(R.id.req_flight_time)).setText(model.getFlight_time());
            ((TextView)view.findViewById(R.id.req_phone)).setText(model.getPhone());
            ((TextView) view.findViewById(R.id.req_price)).setText(model.getPrice() == null ? "0" : model.getPrice() + " " + getActivity().getApplicationContext().getResources().getString(R.string.SDG));
        }
    }
    public void openDetailsLayout(req_info model){
         View req_details=null;
        final View history =  view.findViewById(R.id.history);
        if (Integer.parseInt(model.getFlag()) == 1) {
            req_details = view.findViewById(R.id.req_details);
            getTravellerList(model.getId(), model.getFlag());
        }
        else if(Integer.parseInt(model.getFlag())==2)
            req_details =  view.findViewById(R.id.service_details);
        else return;
        setInfo(model);
        final View finalReq_details = req_details;
        history.animate().alpha(0f).setDuration(700).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                history.setVisibility(View.GONE);
                finalReq_details.setVisibility(View.VISIBLE);
                finalReq_details.animate().alpha(1f).setDuration(700);
                main=false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    public void onBackPressed() {

        if(main){
        }
        else{
            final View history =  view.findViewById(R.id.history);
            final View req_details =  view.findViewById(R.id.req_details);



            history.setVisibility(View.VISIBLE);
            req_details.setVisibility(View.GONE);

            main=true;
            history.setAlpha(1f);
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void showLoading(){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("inside run");
                view.findViewById(R.id.layout).setVisibility(View.GONE);
                view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
                System.out.println("after inside run");

            }

        });
        System.out.println("after run");


    }
    public void hideLoading(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("inside run");
                view.findViewById(R.id.layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.loading).setVisibility(View.GONE);
                System.out.println("after inside run");

            }

        });
        System.out.println("after run");
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    private String getPermit(int permit){
        switch (permit){
            case 1:{
                return getActivity().getApplicationContext().getResources().getString(R.string.visitor);
            }
            case 2:{
                return getActivity().getApplicationContext().getResources().getString(R.string.resident);
            }
            default:{
                return "";
            }
        }
    }
    private String getStatusString(int status){
        System.out.println("STAAAAAAAAAAAATUS"+status);
        String statusS="";
        switch (status){
            case 1:{statusS=getContext().getResources().getString(R.string.status1);}break;
            case 2:{statusS=getContext().getResources().getString(R.string.status2);}break;
            case 3:{statusS=getContext().getResources().getString(R.string.status3);}break;
        }
        return statusS;
    }
    private String getReqType(String flag,String req_type){
        String req_type_s="";
        System.out.println(flag+"________________"+req_type);
        if (Integer.parseInt(flag)==1){
            switch (Integer.parseInt(req_type)){
                case 1:{req_type_s=getContext().getResources().getString(R.string.departure);}break;
                case 2:{req_type_s=getContext().getResources().getString(R.string.arrival);}break;
                case 3:{req_type_s=getContext().getResources().getString(R.string.medical_details);}break;
            }
        }
        else{
            switch (Integer.parseInt(req_type)){
                case 1:{req_type_s=getContext().getResources().getString(R.string.exit_visa);}break;
                case 2:{req_type_s=getContext().getResources().getString(R.string.yellow_fever);}break;
                case 3:{req_type_s=getContext().getResources().getString(R.string.national_service);}break;
            }
        }
        return req_type_s;
    }
    private String getPackage(int packageS){
        String package_s="";
        switch (packageS){
            case 1:{package_s=getContext().getResources().getString(R.string.classic);}break;
            case 2:{package_s=getContext().getResources().getString(R.string.vip);}break;
        }
        return package_s;
    }
    private String getNationality(int nationality){
        if(nationality==1){
            return getActivity().getApplicationContext().getResources().getString(R.string.sudanese);
        }
        else if(nationality==2){
            return getActivity().getApplicationContext().getResources().getString(R.string.foreigner);
        }
        else
            return "";
    }

    private void appendToText(int[] id, View view) {
        for (int i = 0; i < id.length; i++) {
            TextView textView = view.findViewById(id[i]);
            textView.append(" :");
        }
    }

    public class req_status_list_adapter extends ArrayAdapter<req_info> implements View.OnClickListener {
        private Context mContext;
        private int lastPosition = -1;
        private history.req_status_list_adapter.ViewHolder viewHolder;


        public req_status_list_adapter(ArrayList<req_info> data, Context context) {
            super(context, R.layout.req_status, data);
            System.out.println("DATAMODELS INSIDE ADAPTER=" + data.size());
            this.mContext = context;

        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final req_info dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            // view lookup cache stored in tag

            final View result;
            if (convertView == null) {


                viewHolder = new history.req_status_list_adapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.req_status, parent, false);

                viewHolder.req_id = convertView.findViewById(R.id.req_id);
                // viewHolder.datetime = convertView.findViewById(R.id.date_status);
                viewHolder.req_status = convertView.findViewById(R.id.req_status);
                viewHolder.req_type = convertView.findViewById(R.id.req_type);
                viewHolder.status = convertView.findViewById(R.id.rel_status);
                viewHolder.more = convertView.findViewById(R.id.rel_more);
                viewHolder.delete = convertView.findViewById(R.id.deleteBtn);


                result = convertView;


                System.out.println("Position=" + position);
                convertView.setTag(viewHolder);


            } else {
                viewHolder = (history.req_status_list_adapter.ViewHolder) convertView.getTag();
                result=convertView;
            }
            assert dataModel != null;

            viewHolder.req_id.setText(dataModel.getId());

            //   viewHolder.datetime.setText(dataModel.getDate());
            viewHolder.req_status.setText(getStatusString(Integer.parseInt(dataModel.getStatus())));
            viewHolder.req_status.setTextColor(getColor(Integer.parseInt(dataModel.getStatus())));
            System.out.println(dataModel.getFlag() + "FLAAAAAAAAAg" + dataModel.getType());
            viewHolder.req_type.setText(getReqType(dataModel.getFlag(), dataModel.getType()));
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    3f
            );
            if (Integer.parseInt(dataModel.getStatus()) != 1) {
                viewHolder.delete.setVisibility(View.GONE);
                viewHolder.status.setVisibility(View.GONE);
                viewHolder.more.setLayoutParams(param);

            }

            viewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDetailsLayout(dataModel);
                }
            });

            lastPosition = position;

          /*  viewHolder.txtLocation.setText(dataModel.getArea());
            viewHolder.txtPrice.setText(dataModel.getPrice());
            viewHolder.txtType.setText(dataModel.getType());
          */
            // new LoadImageTask(this).execute(dataModel.getImage());
            return convertView;
        }

        @Override
        public void onClick(View v) {

        }

        public int getColor(int status) {
            switch (status) {
                case 1: {
                    return getResources().getColor(R.color.started);
                }
                case 2: {
                    return getResources().getColor(R.color.under_process);
                }
                case 3: {
                    return getResources().getColor(R.color.success_green);
                }
                default:
                    return getResources().getColor(R.color.primary_text);
            }

        }

        private class ViewHolder {
            TextView req_id, datetime, req_status, req_type, order_noT, dateT, statusT, typeT;
            RelativeLayout status, more, delete;


        }


    }
}
