package com.example.contactapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.contactapp.R;
import com.example.contactapp.databinding.FragmentCreateContactBinding;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class CreateContactFragment extends Fragment {



    public CreateContactFragment() {
        // Required empty public constructor
    }


    public static CreateContactFragment newInstance(String param1, String param2) {
        CreateContactFragment fragment = new CreateContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentCreateContactBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateContactBinding.inflate(inflater, container, false);

       binding.buttonSubmit.setOnClickListener(v -> {
           String name = binding.editTextName.getText().toString();
           String email = binding.editTextEmail.getText().toString();
           String phone = binding.editTextPhone.getText().toString();
           String phoneType = "";
           if(binding.radioButtonCell.isChecked())
               phoneType = "CELL";
           else if(binding.radioButtonHome.isChecked())
               phoneType = "HOME";
           else if(binding.radioButtonOffice.isChecked())
               phoneType = "OFFICE";

           OkHttpClient client = new OkHttpClient();
           RequestBody body = new FormBody.Builder()
                   .add("name", name)
                   .add("email", email)
                   .add("phone", phone)
                   .add("type", phoneType)
                   .build();

           Request request = new Request.Builder()
                   .url("https://www.theappsdr.com/contact/json/create")
                   .post(body)
                   .build();

           client.newCall(request).enqueue(new okhttp3.Callback() {
               @Override
               public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                   e.printStackTrace();
               }

               @Override
               public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                   if(response.isSuccessful()){
                       requireActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(requireActivity(), "Contact Added Successfully", Toast.LENGTH_SHORT).show();
                                 mListener.cancelAddContact();
                           }
                       });

                   }else
                       requireActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(requireActivity(), "Failed to add contact"+  response.body(), Toast.LENGTH_SHORT).show();
                               assert response.body() != null;
                               Log.d("Error11", String.valueOf(response));
                           }
                       });
               }
           });




       });



        binding.buttonCancel.setOnClickListener(v -> {
            mListener.cancelAddContact();
        });
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentInteractionListener)
            mListener = (OnFragmentInteractionListener) context;
        else throw new RuntimeException("Must implement OnFragmentInteractionListener interface");
    }

    OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void cancelAddContact();
    }
}