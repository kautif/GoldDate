package com.example.golddate.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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

import org.w3c.dom.Document;

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

        mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                Log.i("CHAT", "onComplete: " + task.getResult().get("matches"));
                ArrayList chatMatches = new ArrayList();
                chatMatches = (ArrayList) task.getResult().get("matches");
                Log.i("chat matches", "onComplete: " + chatMatches);
                for (int z = 0; z < chatMatches.size(); z++) {
                    mStore.collection("Users").document((String) chatMatches.get(z)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> matchTask) {
                            Match match = (Match) matchTask.getResult().toObject(Match.class);
                            mMatchList.add(match);
                            matchRecyclerAdapter.notifyDataSetChanged();
                            Log.i("Chat Match", "onComplete: " + match.getName());
                        }
                    });
                }
            }
        });

        return view;
    }
}
