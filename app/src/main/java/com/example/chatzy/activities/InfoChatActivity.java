package com.example.chatzy.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.chatzy.R;
import com.example.chatzy.databinding.ActivityInfoChatBinding;
import com.example.chatzy.models.User;
import com.example.chatzy.utilities.PreferenceManager;


public class InfoChatActivity extends AppCompatActivity {

    private ActivityInfoChatBinding binding;
    private User user;
    private String myID, myName, myImage;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        user = (User) getIntent().getSerializableExtra("user");
        myID = getIntent().getStringExtra("myID");
        myName = getIntent().getStringExtra("myName");
        myImage = getIntent().getStringExtra("myImage");

        setListenner();
        setData();
        setStatusBarColor();
    }

    private void setStatusBarColor() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
    }

    private void setListenner() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.imageMore.setOnClickListener(v -> { });
    }


    private void setData() {
        binding.image.setImageBitmap(getBitmapFromEncodedString(user.image));
        binding.name.setText(user.name);
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

}