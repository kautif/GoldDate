package com.example.golddate.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.golddate.R;
import com.example.golddate.adapter.MatchRecyclerAdapter;
import com.example.golddate.util.Match;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView mChatRecyclerView;
    private List<Match> mMatchList;
    private MatchRecyclerAdapter matchRecyclerAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    public ChatFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();


        mChatRecyclerView = view.findViewById(R.id.chat_recyclerView);
        mChatRecyclerView.setHasFixedSize(true);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMatchList = new ArrayList<>();
        matchRecyclerAdapter = new MatchRecyclerAdapter(getContext(), mMatchList);
        mChatRecyclerView.setAdapter(matchRecyclerAdapter);

        mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .collection("Match").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Match match = documentSnapshot.toObject(Match.class);
                        mMatchList.add(match);
                        matchRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }
}
