package com.android.locationtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.android.locationtracker.databinding.ActivityDocumentationBinding;

public class Documentation extends AppCompatActivity {

    ActivityDocumentationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDocumentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.contactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber("+919675510243");
            }
        });

        binding.emailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail("yogi586400@gmail.com");
            }
        });

        binding.gitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openGithubProfile();
            }
        });

        binding.linkedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkedInProfile();
            }
        });


    }

    //method to handle the working of email button
    @SuppressLint("QueryPermissionsNeeded")
    private void composeEmail(String addresses) { Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);

        // Set the intent to show the chooser only for email apps
        Intent chooserIntent = Intent.createChooser(intent, "Select Email App");
        if (chooserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooserIntent);
        }
    }

    // Method to handle the working of call button
    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        // Set the intent to show the chooser only for phone call apps
        Intent chooserIntent = Intent.createChooser(intent, "Select Phone Call App");
        if (chooserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooserIntent);
        }
    }


    // Method to handle the working of the GitHub button
    @SuppressLint("QueryPermissionsNeeded")
    private void openGithubProfile() {
        String githubUrl = "https://github.com/yps09";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));

        // Check if there is an app capable of handling the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // Method to handle opening the LinkedIn profile in the default web browser
    @SuppressLint("QueryPermissionsNeeded")
    private void openLinkedInProfile() {
        String linkedinUrl = "https://www.linkedin.com/in/yogendra-pratap-singh-29123b21a/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinUrl));

        // Check if there's an app capable of handling the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}