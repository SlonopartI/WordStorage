package net.project.slounik.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.project.slounik.R;

import java.util.ArrayList;
import java.util.TreeMap;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final TreeMap<String, ArrayList<String>> map;

    public RecyclerViewAdapter(TreeMap<String, ArrayList<String>> map){
        this.map=map;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.text_block,parent,false))
        {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position<=getItemCount()){
            StringBuilder temp= new StringBuilder();
            String key;
            key= (String) map.keySet().toArray()[position];
            ((TextView)holder.itemView.findViewById(R.id.textView3)).setText(key);
            TextView textView= holder.itemView.findViewById(R.id.textView2);

            for(String text: map.get(key)){
                temp.append(text).append("\n");
            }
            textView.setText(temp.toString());
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(map.size(), 200);
    }
}
