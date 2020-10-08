package com.bsf.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ArtHolder> {

    ArrayList<String> _arrayList;
    ArrayList<Integer> _idList;

    public RecyclerViewAdapter(ArrayList<String> arrayList,ArrayList<Integer> idList) {
        _arrayList = arrayList;
        _idList = idList;
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row, parent, false);
        return new ArtHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, final int position) {
        holder.textView.setText(_arrayList.get(position));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FragmentGalleryDirections.ActionFragmentFeedToFragmentAddPost action = FragmentGalleryDirections.actionFragmentFeedToFragmentAddPost();
               action.setİd(_idList.get(position));
               Navigation.findNavController(v).navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _arrayList.size();
    }

    public class ArtHolder extends RecyclerView.ViewHolder {
        public ArtHolder(@NonNull View itemView) {
            super(itemView);
        }

        TextView textView = itemView.findViewById(R.id.textView);
    }
}
