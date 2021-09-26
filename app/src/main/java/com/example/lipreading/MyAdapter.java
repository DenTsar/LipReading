package com.example.lipreading;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Issue> data;
    private Context context;
    private int selectedPos = RecyclerView.NO_POSITION;
    private int clicked;
    public MyAdapter(List<Issue> list, Context context){
        data = list;
        this.context = context;
    }
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.list_layout,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    public void onBindViewHolder(final MyAdapter.ViewHolder holder, int position){
        holder.text.setText(data.get(position).getText());
        holder.title.setText(data.get(position).getTitle());
        //holder.itemView.setSelected(selectedPos == position);
        if(selectedPos==holder.getAdapterPosition()) {
            holder.text.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
            holder.title.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
        }
        else{
            holder.text.setTextColor(Color.WHITE);
            holder.title.setTextColor(Color.WHITE);
        }
        if(!data.get(holder.getAdapterPosition()).isExtra()) {
            holder.spinner.setVisibility(View.GONE);
        }
        else{
            String[] options = {"Ahead by a little (<.05 s)", "Ahead by a good amount (~.05 s)", "Ahead by a lot (~.1 s)", "Behind by a little (<.05 s)", "Behind by a good amount (~.05 s)", "Behind by a lot (~.1 s)"};
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, options);
            holder.spinner.setAdapter(arrayAdapter);
            holder.spinner.setVisibility(View.VISIBLE);
        }
    }
    public int getPos(){
        return selectedPos;
    }
    public int getItemCount(){
        return data.size();
    }public int getClicked(){
        return clicked;
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title;
        private TextView text;
        private Spinner spinner;
        public ViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.c_title);
            text = itemView.findViewById(R.id.c_text);
            spinner = itemView.findViewById(R.id.c_spinner);
            clicked = 0;
            itemView.setOnClickListener(this);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    clicked = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        public void onClick(View view){
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }
}
