package net.project.slounik.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.project.slounik.R;

import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleAdapter extends RecyclerView.Adapter {
    private CopyOnWriteArrayList<String> arrayList;

    public SimpleAdapter(CopyOnWriteArrayList<String> arrayList) {
        this.arrayList=arrayList;
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
        ((TextView)holder.itemView.findViewById(R.id.textView3)).setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
