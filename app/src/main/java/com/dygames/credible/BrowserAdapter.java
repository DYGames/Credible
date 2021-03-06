package com.dygames.credible;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import soup.neumorphism.NeumorphCardView;

public class BrowserAdapter extends RecyclerView.Adapter<BrowserAdapter.ViewHolder> {

    public BrowserAdapter(Data[] data) {
        this.data = data;
    }

    public static class Data {
        public String title;
        public Bitmap thumbnailID;

        public Data(String title, Bitmap thumbnailID) {
            this.title = title;
            this.thumbnailID = thumbnailID;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public ViewHolder(@NonNull View v) {
            super(v);

            this.title = v.findViewById(R.id.browser_text);
            this.thumbnail = v.findViewById(R.id.browser_thumbnail);
        }
    }

    Data[] data;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_browser, parent, false);
        ViewHolder h = new ViewHolder(holderView);
        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(this.data[position].title);
        holder.thumbnail.setImageBitmap(this.data[position].thumbnailID);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

}