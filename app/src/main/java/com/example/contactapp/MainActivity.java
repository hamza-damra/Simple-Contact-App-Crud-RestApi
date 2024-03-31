package com.example.contactapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.contactapp.fragments.ContactsFragment;
import com.example.contactapp.fragments.CreateContactFragment;
import com.example.contactapp.fragments.DetailsFragment;

public class MainActivity extends AppCompatActivity implements ContactsFragment.GotoAddContact, CreateContactFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportFragmentManager().beginTransaction().add(R.id.rootView, new ContactsFragment()).commit();




    }

    @Override
    public void goToAddContact() {
       getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new CreateContactFragment()).addToBackStack(null).commit();
    }

    @Override
    public void gotoDetails(String name, String email, String phone, String phoneType) {
        DetailsFragment detailsFragment = DetailsFragment.newInstance(name, email, phone, phoneType);
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, detailsFragment).addToBackStack(null).commit();
    }

    @Override
    public void cancelAddContact() {
        getSupportFragmentManager().popBackStack();
    }
}