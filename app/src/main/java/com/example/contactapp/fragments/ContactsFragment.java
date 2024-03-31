package com.example.contactapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.contactapp.databinding.ContactListItemBinding;
import com.example.contactapp.databinding.FragmentContactsBinding;
import com.example.contactapp.models.Contact;

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


public class ContactsFragment extends Fragment {

    List<Contact> contactList = new ArrayList<>();

    public ContactsFragment() {
    }


    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    FragmentContactsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);

        binding.buttonAddContact.setOnClickListener(v -> {
            goToAddContact.goToAddContact();
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactList.clear();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/contacts/json")
                .get()
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.d("Error", e.getMessage());
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray contacts = jsonObject.getJSONArray("contacts");
                        for(int i = 0; i < contacts.length(); i++){
                            JSONObject contact = contacts.getJSONObject(i);
                            Contact c = new Contact(contact.getInt("Cid"),contact.getString("Name"), contact.getString("Email"), contact.getString("Phone"), contact.getString("PhoneType"));
                            contactList.add(c);
                        }
                        requireActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void run() {
                                Objects.requireNonNull(binding.recyclerView.getAdapter()).notifyDataSetChanged();
                            }
                        });


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        binding.recyclerView.setAdapter(new ContactAdapter(contactList));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setHasFixedSize(true);

    }

    gotoAddContact goToAddContact;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof gotoAddContact)
        goToAddContact = (gotoAddContact) context;
        else throw new RuntimeException("Must implement gotoAddContact interface");
    }


  class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

        List<Contact> contactList;

        public ContactAdapter(List<Contact> contactList) {
            this.contactList = contactList;
        }


      @NonNull
      @Override
      public ContactAdapter.ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ContactListItemBinding binding = ContactListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ContactViewHolder(binding);
      }

      @SuppressLint("NotifyDataSetChanged")
      @Override
      public void onBindViewHolder(@NonNull ContactAdapter.ContactViewHolder holder, int position) {
            Contact c = contactList.get(position);
            holder.binding.textViewName.setText(c.getName());
            holder.binding.textViewEmail.setText(c.getEmail());
            holder.binding.textViewPhone.setText(c.getPhone());
            holder.binding.getRoot().setOnClickListener(v -> {
                goToAddContact.gotoDetails(c.getName(), c.getEmail(), c.getPhone(), c.getPhoneType());
            });
            holder.binding.textViewPhoneType.setText(c.getPhoneType());
            holder.binding.imageViewDelete.setOnClickListener(v -> {
                contactList.remove(c);
                deleteContactFromServer(String.valueOf(c.getCid()));
                notifyDataSetChanged();

            });
      }

      private void deleteContactFromServer(String id) {
          OkHttpClient client = new OkHttpClient();

          RequestBody body = new FormBody.Builder()
                  .add("id",id)
                  .build();

          Request request = new Request.Builder()
                  .url("https://www.theappsdr.com/contact/json/delete")
                  .post(body)
                  .build();

          client.newCall(request).enqueue(new okhttp3.Callback() {
              @Override
              public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                  e.printStackTrace();
                  Log.d("Delete", "Network call failed: " + e.getMessage());
              }

              @Override
              public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                  final String responseBody = response.body() != null ? response.body().string() : null;
                  requireActivity().runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          if (response.isSuccessful() && responseBody != null) {
                              Toast.makeText(requireActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                          } else {
                              Toast.makeText(requireActivity(), "Failed to delete contact" + responseBody, Toast.LENGTH_SHORT).show();
                              Log.d("Delete", "Failed to delete contact: " + responseBody);
                          }
                      }
                  });
              }
          });
      }


      @Override
      public int getItemCount() {
          return contactList.size();
      }

      public  class ContactViewHolder extends RecyclerView.ViewHolder {
          ContactListItemBinding binding;
          public ContactViewHolder(ContactListItemBinding itemView) {
              super(itemView.getRoot());
                binding = itemView;
          }
      }
  }

    public interface gotoAddContact{
        void goToAddContact();
        void gotoDetails(String name, String email, String phone, String phoneType);
    }
}