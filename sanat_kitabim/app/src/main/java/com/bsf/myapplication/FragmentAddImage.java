package com.bsf.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Permission;

public class FragmentAddImage extends Fragment {

    ImageView imageView;
    Bitmap selectedImage;
    Button addImageBtn;
    EditText artNameEditText, painterNameEditText, yearEditText;
    SQLiteDatabase sqLiteDatabase;

    public FragmentAddImage() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        artNameEditText = view.findViewById(R.id.artNameEditText);
        painterNameEditText = view.findViewById(R.id.painterNameEditText);
        yearEditText = view.findViewById(R.id.yearEditText);

        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGallery(view);
            }
        });

        addImageBtn = view.findViewById(R.id.addImageBtn);
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveArtToSql(view);
            }
        });


        sqLiteDatabase = getActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null);
       // try{
            if (getArguments() != null) {
              //  addImageBtn.setVisibility(View.INVISIBLE);
                int id = FragmentAddImageArgs.fromBundle(getArguments()).getİd();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM arts WHERE id=?", new String[] {String.valueOf(id)});
                int artNameIx = cursor.getColumnIndex("artname");
                int painterNameIx = cursor.getColumnIndex("paintername");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");
                while (cursor.moveToNext()) {
                    artNameEditText.setText(cursor.getString(artNameIx));
                    painterNameEditText.setText(cursor.getString(painterNameIx));
                    yearEditText.setText(cursor.getString(yearIx));

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageView.setImageBitmap(bitmap);
                    System.out.println(bitmap);
                }
                cursor.close();
            }
       // }catch (Exception ex){}

    }

    public void goToGallery(View view) {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == MainActivity.RESULT_OK && data != null) {
            Uri imageData = data.getData();

            try {
                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageData);
                    imageView.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap makeSmaller(Bitmap image, int maximumSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public void saveArtToSql(View view) {
        String getArtName = artNameEditText.getText().toString();
        String getPainterName = painterNameEditText.getText().toString();
        String getYear = yearEditText.getText().toString();

        Bitmap smallImage = makeSmaller(selectedImage, 300);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50, output);
        byte[] byteArray = output.toByteArray();

        try {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR,paintername VARCHAR,year VARCHAR,image BLOB)");
            String sqlString = "INSERT INTO arts(artname,paintername,year,image) VALUES(?,?,?,?)";
            SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sqlString);
            sqLiteStatement.bindString(1, getArtName);
            sqLiteStatement.bindString(2, getPainterName);
            sqLiteStatement.bindString(3, getYear);
            sqLiteStatement.bindBlob(4, byteArray);
            sqLiteStatement.execute();
            Toast.makeText(getActivity(), "Kayıt gerçekleşti", Toast.LENGTH_SHORT).show();
            NavDirections action = FragmentAddImageDirections.actionFragmentAddPostToFragmentFeed();
            Navigation.findNavController(view).navigate(action);
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Kayıt gerçekleşemedi", Toast.LENGTH_SHORT).show();
        }


    }
}