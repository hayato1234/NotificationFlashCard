package com.orengesunshine.notiplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Time;
import java.util.List;

/**
 * Created by hayatomoritani on 8/1/17.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Folder> folderList;
    private OnButtonClickedListener listener;

    MainRecyclerAdapter(Context context, List<Folder> folderList){
        this.context = context;
        this.folderList = folderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.title.setText(folderList.get(position).getTitle());
        Time time = folderList.get(position).getLastEdited();
        holder.lastEdit.setText(getTimeAgo(time));
        holder.count.setText(String.valueOf(folderList.get(position).getNumberOfCards()));
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonClicked(0,holder.getAdapterPosition());
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonClicked(1,holder.getAdapterPosition());
            }
        });
        holder.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonClicked(2,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        final View view;
        final TextView title;
        final TextView count;
        final TextView lastEdit;
        final ImageView deleteButton;
        final ImageView editButton;
        final ImageView startButton;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView)itemView.findViewById(R.id.folder_name);
            count = (TextView)itemView.findViewById(R.id.number_of_items);
            lastEdit = (TextView)itemView.findViewById(R.id.last_edit);
            deleteButton = (ImageView)itemView.findViewById(R.id.delete_button);
            editButton = (ImageView)itemView.findViewById(R.id.edit_button);
            startButton = (ImageView)itemView.findViewById(R.id.play_button);
        }
    }

    private String getTimeAgo(Time time) {
        long timeRepliedInMillis = time.getTime();
        long now = System.currentTimeMillis();
        long resolution = DateUtils.MINUTE_IN_MILLIS; // less than a minuet will be zero

        String diff;

        if (DateUtils.isToday(timeRepliedInMillis)){
            diff = DateUtils.getRelativeTimeSpanString(timeRepliedInMillis,now,resolution).toString();
        }else {
            long transitionRes = DateUtils.WEEK_IN_MILLIS; // show date if more than 7days
            diff = DateUtils.getRelativeDateTimeString(context,timeRepliedInMillis,resolution,transitionRes,0).toString();
        }
        return diff;
    }

    //actionNum: delete=0, edit=1, play=2
    interface OnButtonClickedListener{void onButtonClicked(int actionNum, int position);}
    void setOnButtonClickedListener(OnButtonClickedListener listener){this.listener = listener;}
}
