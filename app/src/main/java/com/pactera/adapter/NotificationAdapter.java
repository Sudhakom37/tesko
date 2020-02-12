package com.pactera.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pactera.tesko.R;

import java.util.List;

/**
 * Created by P0147845 on 22-10-2019.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<String> notificationsList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.title);
        }
    }

    public NotificationAdapter(List<String> notificationsList) {
        this.notificationsList = notificationsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list_item, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
       /* Notification notification = notificationsList.get(position);
        holder.title.setText(notification.getName());*/

        String notification = notificationsList.get(position);
        holder.title.setText(notification);


//        holder.genre.setText(movie.getGenre());
//        holder.year.setText(movie.getYear());
    }


    @Override
    public int getItemCount() {
        Log.e("Adapter", "List size : " + notificationsList.size());
        if(notificationsList.size()<5){
            return notificationsList.size();
        }else{
            return 5;
        }

    }
}
