package com.example.mohaned.hababak.Adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mohaned.hababak.Models.companion_info;
import com.example.mohaned.hababak.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class companion_adapter extends ArrayAdapter<companion_info> implements View.OnClickListener {

        private Context mContext;
        public DatePickerDialog dpd;
        public Calendar now;
        String date;
        View view;
    public FragmentManager fm;

    private void appendToText(int id[], View view) {
        for (int i = 0; i < id.length; i++) {
            TextView textView = view.findViewById(id[i]);
            textView.append(" :");
        }
    }
        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            // Get the data item for this position
            companion_info dataModel = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            // view lookup cache stored in tag

            final View result;

            if (convertView == null) {


                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.companions_form, parent, false);
                viewHolder.companion_title = (TextView) convertView.findViewById(R.id.companion_title);
                viewHolder.companion_title.append(" " + (position + 1));
                convertView.findViewById(R.id.companion_name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  showKeyboard(view);
                    }
                });
                convertView.findViewById(R.id.companion_passport).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                viewHolder.txtLocation = (TextView) convertView.findViewById(R.id.companion_age);
                viewHolder.age_button = (Button) convertView.findViewById(R.id.companion_age_button);
                viewHolder.txtLocation.setTag(String.valueOf(position)+"age");
                viewHolder.age_button.setTag(String.valueOf(position) + "button");

                viewHolder.txtLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view1) {
                        dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                date= year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                                ((TextView)view1).setText(date);
                            }
                        },now);
                        dpd.show(fm, view.getTag().toString());

                    }
                });
                final View finalConvertView = convertView;
                viewHolder.age_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view1) {
                        dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                ((TextView) (finalConvertView.findViewWithTag(view1.getTag().toString().replace("button", "age")))).setText(date);
                            }
                        }, now);
                        dpd.show(fm, view.getTag().toString());
                    }
                });
                int i[] = {R.id.nameT, R.id.birthT, R.id.numberT};
//                appendToText(i, convertView);
                /*viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.price);
                viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
                viewHolder.SourceImage = (ImageView) convertView.findViewById(R.id.asset_image);*/

                result=convertView;

                Spinner nationality = (Spinner) result.findViewById(R.id.companion_nationality);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                        R.array.nationality_list, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                nationality.setAdapter(adapter);

                System.out.println("Position="+position);
                convertView.setTag("companion"+position);


            } else {
               // viewHolder = (ViewHolder) convertView.getTag();
                result=convertView;
            }
            view=convertView;



            lastPosition = position;

            assert dataModel != null;
          /*  viewHolder.txtLocation.setText(dataModel.getArea());
            viewHolder.txtPrice.setText(dataModel.getPrice());
            viewHolder.txtType.setText(dataModel.getType());
          */
            // new LoadImageTask(this).execute(dataModel.getImage());
            return convertView;
        }


    public companion_adapter(ArrayList<companion_info> data, Context context, FragmentManager fm) {
        super(context, R.layout.companions_form, data);
        this.mContext = context;
        this.fm = fm;
        now = Calendar.getInstance();

    }


    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;
    private ViewHolder viewHolder;

    private static class ViewHolder {
        TextView txtLocation;
        TextView companion_title;
        Button age_button;
        TextView txtPrice;
        ImageView SourceImage;

    }

    protected void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    protected void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    }
