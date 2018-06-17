package com.example.forrest_hunter.p2pmessaging;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class TextMsg extends RecyclerView.Adapter<TextMsg.MyViewHolder> {

    private LayoutInflater inflater;
    List<Byte> message = Collections.emptyList();

    public  TextMsg (Context context, List<Byte> message){
        inflater = LayoutInflater.from(context);
        this.message = message;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.msg_layout, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        Byte current = message.get(position);
        holder.msgText.setText(current.msgText);
    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView msgText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            msgText = (TextView) itemView.findViewById(R.id.msgText);

        }
    }
}
