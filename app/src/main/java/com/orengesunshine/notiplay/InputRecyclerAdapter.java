package com.orengesunshine.notiplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;

import java.util.List;

import static com.orengesunshine.notiplay.InputCardActivity.TAG;

/**
 * Created by hayatomoritani on 8/2/17.
 */

public class InputRecyclerAdapter extends RecyclerView.Adapter<InputRecyclerAdapter.Holder> {

    private Context context;
    private List<Card> cards;
    private RecyclerActionListener listener;

    public InputRecyclerAdapter(Context context, List<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.input_list_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        holder.checkBox.setChecked(cards.get(position).isCheck());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onAction("check","",holder.getAdapterPosition());
                }
            }
        });
        holder.frontText.setText(cards.get(position).getFront());
        holder.frontText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (listener!=null){
                    listener.onAction("text_change_front",s.toString(),holder.getAdapterPosition());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        holder.backText.setText(cards.get(position).getBack());
        holder.backText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (listener!=null){
                    listener.onAction("text_change_back",s.toString(),holder.getAdapterPosition());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onAction("delete","",holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class Holder extends RecyclerView.ViewHolder{

        final View view;
        final CheckBox checkBox;
        final EditText frontText;
        final EditText backText;
        final ImageView deleteButton;

        Holder(View itemView) {
            super(itemView);
            view = itemView;
            checkBox = (CheckBox)itemView.findViewById(R.id.input_checkbox);
            frontText = (EditText)itemView.findViewById(R.id.input_front_text);
            backText = (EditText)itemView.findViewById(R.id.input_back_text);
            deleteButton = (ImageView) itemView.findViewById(R.id.input_delete_button);
        }
    }

    void setRecyclerActionListener(RecyclerActionListener listener){
        this.listener = listener;
    }

    interface RecyclerActionListener{
        void onAction(String action,String change,int position);
    }
}
