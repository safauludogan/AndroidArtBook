package com.bsf.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentGallery extends Fragment {

    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<String> artNameList;
    ArrayList<Integer> idList;
    SQLiteDatabase sqLiteDatabase;
    RecyclerView recyclerView;
    public FragmentGallery() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artNameList = new ArrayList<>();
        idList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(artNameList,idList);
        recyclerView.setAdapter(recyclerViewAdapter);
        getData();
    }
    public void getData(){
        try{
            sqLiteDatabase = getActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE,null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM arts",null);
            int artNameIx = cursor.getColumnIndex("artname");
            int idIx = cursor.getColumnIndex("id");
            while (cursor.moveToNext()){
                artNameList.add(cursor.getString(artNameIx));
                idList.add(cursor.getInt(idIx));
            }
            cursor.close();
            recyclerViewAdapter.notifyDataSetChanged();
        }catch (Exception ex){
            Toast.makeText(getActivity(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}