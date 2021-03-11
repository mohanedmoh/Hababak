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

import com.example.mohaned.hababak.Models.req_info;
import com.example.mohaned.hababak.R;

import java.util.ArrayList;

public class req_history_adapter extends ArrayAdapter<req_info> implements View.OnClickListener {
    private Context mContext;
    private int lastPosition = -1;
    private req_history_adapter.ViewHolder viewHolder;


    public req_history_adapter(ArrayList<req_info> data, Context context) {
        super(context, R.layout.req_info, data);
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        req_info dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new req_history_adapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.req_info, parent, false);

            viewHolder.orderId = convertView.findViewById(R.id.req_id);
            viewHolder.type = convertView.findViewById(R.id.req_type);
            viewHolder.destination = convertView.findViewById(R.id.req_destination_info);
            viewHolder.date = convertView.findViewById(R.id.req_date_info);

            assert dataModel != null;
            viewHolder.orderId.setText(dataModel.getTraveller_name());
            viewHolder.type.setText(dataModel.getAirline());
            viewHolder.destination.setText(dataModel.getDestination());
            viewHolder.date.setText(dataModel.getDate());

            result = convertView;


            System.out.println("Position=" + position);
            convertView.setTag("companion" + position);


        } else {
            // viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
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

    private static class ViewHolder {
        TextView orderId;
        TextView type;
        TextView destination;
        TextView date;

    }

}
