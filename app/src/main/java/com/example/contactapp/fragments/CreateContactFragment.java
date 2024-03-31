package com.example.contactapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.contactapp.databinding.FragmentCreateContactBinding;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateContactFragment extends Fragment {

    private FragmentCreateContactBinding binding;
    private OnFragmentInteractionListener mListener;

    public CreateContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateContactBinding.inflate(inflater, container, false);

        binding.buttonSubmit.setOnClickListener(v -> onSubmitClicked());
        binding.buttonCancel.setOnClickListener(v -> mListener.cancelAddContact());

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnFragmentInteractionListener");
        }
    }

    private void onSubmitClicked() {
        String name = binding.editTextName.getText().toString();
        String email = binding.editTextEmail.getText().toString();
        String phone = binding.editTextPhone.getText().toString();
        String phoneType = "";
        if (binding.radioButtonCell.isChecked())
            phoneType = "CELL";
        else if (binding.radioButtonHome.isChecked())
            phoneType = "HOME";
        else if (binding.radioButtonOffice.isChecked())
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

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showToast("Failed to add contact: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        showToast("Contact added successfully");
                        mListener.cancelAddContact();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        showToast("Failed to add contact: " + response.body());
                        Log.d("Error11", String.valueOf(response));
                    });
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void cancelAddContact();
    }
}
