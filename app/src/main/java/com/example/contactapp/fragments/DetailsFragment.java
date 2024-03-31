package com.example.contactapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.contactapp.R;
import com.example.contactapp.databinding.FragmentDetailsBinding;


public class DetailsFragment extends Fragment {

    private static final String ARG_PARAM_NAME = "name";
    private static final String ARG_PARAM_EMAIL = "email";
    private static final String ARG_PARAM_PHONE = "phone";
    private static final String ARG_PARAM_PHENOTYPE = "phoneType";

    private String mName;
    private String mEmail;
    private String mPhone;
    private String mPhoneType;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String name, String email, String phone, String phoneType) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_NAME, name);
        args.putString(ARG_PARAM_EMAIL, email);
        args.putString(ARG_PARAM_PHONE, phone);
        args.putString(ARG_PARAM_PHENOTYPE, phoneType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ARG_PARAM_NAME);
            mEmail = getArguments().getString(ARG_PARAM_EMAIL);
            mPhone = getArguments().getString(ARG_PARAM_PHONE);
            mPhoneType = getArguments().getString(ARG_PARAM_PHENOTYPE);
        }
    }

    FragmentDetailsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textViewName.setText(mName);
        binding.textViewEmail.setText(mEmail);
        binding.textViewPhone.setText(mPhone);
        binding.textViewPhoneType.setText(mPhoneType);
    }
}