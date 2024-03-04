package com.leestream.artgallery.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Adapters.UserAdapter;
import com.leestream.artgallery.MainActivity;
import com.leestream.artgallery.Models.User;
import com.leestream.artgallery.R;
import com.paulrybitskyi.persistentsearchview.PersistentSearchView;
import com.paulrybitskyi.persistentsearchview.adapters.model.SuggestionItem;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchConfirmedListener;
import com.paulrybitskyi.persistentsearchview.listeners.OnSearchQueryChangeListener;
import com.paulrybitskyi.persistentsearchview.listeners.OnSuggestionChangeListener;
import com.paulrybitskyi.persistentsearchview.utils.SuggestionCreationUtil;
import com.paulrybitskyi.persistentsearchview.utils.VoiceRecognitionDelegate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFragment extends Fragment {
    private PersistentSearchView persistentSearchView;
    private RecyclerView RCusers;
    private ArrayList<User> mUsers;
    private UserAdapter userAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_search, container, false);

        persistentSearchView = view.findViewById(R.id.persistentSearchView);

        mUsers=new ArrayList<>();
        RCusers=view.findViewById(R.id.RCusers);
        userAdapter=new UserAdapter(getContext(),mUsers,true);
        RCusers.setHasFixedSize(true);
        RCusers.setLayoutManager(new LinearLayoutManager(getContext()));
        RCusers.setAdapter(userAdapter);
        readUsers();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SearchHistory", Context.MODE_PRIVATE);
        Set<String> searchQueriesSet = sharedPreferences.getStringSet("searchQueries", new HashSet<>());
        List<String> searchQueriesList = new ArrayList<>(searchQueriesSet);

        persistentSearchView.setSuggestions(SuggestionCreationUtil.asRecentSearchSuggestions(searchQueriesList), false);


        persistentSearchView.setOnLeftBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });


        persistentSearchView.setOnSearchQueryChangeListener((searchView, oldQuery, newQuery) -> {
            List<String> filteredSearchQueries = filterSearchQueries(searchQueriesList, newQuery);
            searchView.setSuggestions(SuggestionCreationUtil.asRecentSearchSuggestions(filteredSearchQueries), false);
            searchUsers(newQuery);
        });
        persistentSearchView.setOnSearchConfirmedListener(new OnSearchConfirmedListener() {
            @Override
            public void onSearchConfirmed(PersistentSearchView searchView, String query) {
                searchQueriesSet.add(query);
                sharedPreferences.edit().putStringSet("searchQueries", searchQueriesSet).apply();

                searchUsers(query);
                searchView.collapse();
            }
        });

        persistentSearchView.setOnSuggestionChangeListener(new OnSuggestionChangeListener() {
            @Override
            public void onSuggestionPicked(SuggestionItem suggestion) {
//                String query = suggestion.getTitle();
//                persistentSearchView.populateEditText(query);
                searchUsers(suggestion.toString());
            }

            @Override
            public void onSuggestionRemoved(SuggestionItem suggestion) {
                // Remove the search query from SharedPreferences
//                String query = suggestion.getTitle();
                searchQueriesSet.remove(suggestion);
                sharedPreferences.edit().putStringSet("searchQueries", searchQueriesSet).apply();
            }
        });


        persistentSearchView.setSuggestionsDisabled(false);
        persistentSearchView.setVoiceRecognitionDelegate(new VoiceRecognitionDelegate(this));
        persistentSearchView.setOnSearchConfirmedListener(new OnSearchConfirmedListener() {

            @Override
            public void onSearchConfirmed(PersistentSearchView searchView, String query) {
                searchUsers(query);
                searchView.collapse();
            }

        });

        return view;
    }
    private List<String> filterSearchQueries(List<String> searchQueries, String query) {
        List<String> filteredQueries = new ArrayList<>();
        for (String searchQuery : searchQueries) {
            if (searchQuery.toLowerCase().contains(query.toLowerCase())) {
                filteredQueries.add(searchQuery);
            }
        }
        return filteredQueries;
    }

    private void readUsers() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("USERS");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user =dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUsers(String s) {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("USERS").orderByChild("UserName")
                .startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }

                if (mUsers.isEmpty()) {
                    Toast.makeText(getContext(), "No Data Found..", Toast.LENGTH_SHORT).show();
                }

                // Notify the adapter of data changes to update the UI
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VoiceRecognitionDelegate.handleResult(persistentSearchView, requestCode, resultCode, data);
    }
}