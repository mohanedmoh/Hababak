package com.example.mohaned.hababak.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mohaned.hababak.Models.traveller_info;
import com.example.mohaned.hababak.R;

import java.util.ArrayList;

public class travellers_adapter  extends ArrayAdapter<traveller_info> implements View.OnClickListener  {
    private Context mContext;




    private static class ViewHolder {
        TextView traveller_name;
        TextView passport_no;
        TextView age;

    }



    public travellers_adapter(ArrayList<traveller_info> data, Context context) {
        super(context, R.layout.traveller_info, data);
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;
    private travellers_adapter.ViewHolder viewHolder;
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        traveller_info dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new travellers_adapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.traveller_info, parent, false);

            viewHolder.traveller_name = convertView.findViewById(R.id.traveller_name_info);
            viewHolder.passport_no =  convertView.findViewById(R.id.traveller_passport_info);
            viewHolder.age =  convertView.findViewById(R.id.traveller_age_info);

            assert dataModel != null;
            viewHolder.traveller_name.setText(dataModel.getTraveller_name());
            viewHolder.passport_no.setText(dataModel.getPassport_no());
            viewHolder.age.setText(dataModel.getAge());

            result=convertView;



            System.out.println("Position="+position);
            convertView.setTag("companion"+position);


        } else {
            // viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.top_from_down : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        assert dataModel != null;
          /*  viewHolder.txtLocation.setText(dataModel.getArea());
            viewHolder.txtPrice.setText(dataModel.getPrice());
            viewHolder.txtType.setText(dataModel.getType());
          */
        // new LoadImageTask(this).execute(dataModel.getImage());
        return convertView;
    }
}
