package com.example.contactapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactapp.databinding.ContactListItemBinding;
import com.example.contactapp.databinding.FragmentContactsBinding;
import com.example.contactapp.models.Contact;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ContactsFragment extends Fragment {

    private final List<Contact> contactList = new ArrayList<>();
    private FragmentContactsBinding binding;
    private GotoAddContact gotoAddContact;

    public ContactsFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        binding.buttonAddContact.setOnClickListener(v -> gotoAddContact.goToAddContact());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchContacts();
        setupRecyclerView();
    }

    private void fetchContacts() {
        contactList.clear();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/contacts/json")
                .get()
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Log.d("Error", Objects.requireNonNull(e.getMessage()));
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray contacts = jsonObject.getJSONArray("contacts");
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject contact = contacts.getJSONObject(i);
                            Contact c = new Contact(contact.getInt("Cid"), contact.getString("Name"), contact.getString("Email"), contact.getString("Phone"), contact.getString("PhoneType"));
                            contactList.add(c);
                        }
                        requireActivity().runOnUiThread(() -> Objects.requireNonNull(binding.recyclerView.getAdapter()).notifyDataSetChanged());

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(new ContactAdapter(contactList));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GotoAddContact)
            gotoAddContact = (GotoAddContact) context;
        else
            throw new RuntimeException("Must implement GotoAddContact interface");
    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

        private final List<Contact> contactList;

        public ContactAdapter(List<Contact> contactList) {
            this.contactList = contactList;
        }

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ContactListItemBinding binding = ContactListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ContactViewHolder(binding);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            Contact c = contactList.get(position);
            holder.bind(c);
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {
            private final ContactListItemBinding binding;

            public ContactViewHolder(ContactListItemBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
            }

            @SuppressLint("NotifyDataSetChanged")
            public void bind(Contact contact) {
                binding.textViewName.setText(contact.getName());
                binding.textViewEmail.setText(contact.getEmail());
                binding.textViewPhone.setText(contact.getPhone());
                binding.textViewPhoneType.setText(contact.getPhoneType());

                binding.getRoot().setOnClickListener(v -> gotoAddContact.gotoDetails(contact.getName(), contact.getEmail(), contact.getPhone(), contact.getPhoneType()));

                binding.imageViewDelete.setOnClickListener(v -> {
                    contactList.remove(contact);
                    deleteContactFromServer(String.valueOf(contact.getCid()));
                    notifyDataSetChanged();
                });
            }
        }
    }

    private void deleteContactFromServer(String id) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("id", id)
                .build();

        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/contact/json/delete")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                Toast.makeText(requireActivity(), "Network call failed", Toast.LENGTH_SHORT).show();
                Log.d("Delete", "Network call failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                final String responseBody = response.body() != null ? response.body().string() : null;
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful() && responseBody != null) {
                        Toast.makeText(requireActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireActivity(), "Failed to delete contact" + responseBody, Toast.LENGTH_SHORT).show();
                        Log.d("Delete", "Failed to delete contact: " + responseBody);
                    }
                });
            }
        });
    }

     public interface GotoAddContact {
        void goToAddContact();

        void gotoDetails(String name, String email, String phone, String phoneType);
    }
}
