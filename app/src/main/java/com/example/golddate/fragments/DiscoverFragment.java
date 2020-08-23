package com.example.golddate.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.golddate.R;
import com.example.golddate.util.Profile;
import com.example.golddate.util.SwipeCard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoverFragment extends Fragment {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private String swiperName;
    private String swipedName;
    private String swiperImage;
    private String swipedImage;
    List<Profile> mProfileList;
    SwipeCard.SelectedID selectedID;

    public DiscoverFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        mSwipeView = view.findViewById(R.id.swipeView);
        mContext = getContext();
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mProfileList = new ArrayList<>();

//        mSwipeView.getBuilder()
//                .setDisplayViewCount(3)
//                .setSwipeDecor(new SwipeDecor()
//                        .setPaddingTop(20)
//                        .setRelativeScale(0.01f)
//                        .setSwipeInMsgLayoutId(R.layout.swipe_accept_view)
//                        .setSwipeOutMsgLayoutId(R.layout.swipe_reject_view));

        final List<Profile> mList = new ArrayList<>();
        mStore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Log.i("DiscoverFragment", "onComplete: " + documentSnapshot.getId());
                        String docID = documentSnapshot.getId();

                        if (!docID.equals(mAuth.getCurrentUser().getUid())) {
                            Profile profile = documentSnapshot.toObject(Profile.class).withId(docID);
                            mProfileList.add(profile);
                        }
                    }

                    for (Profile profile : mProfileList) {
                        mSwipeView.addView(new SwipeCard(mContext, profile, mSwipeView, selectedID));
                    }
                }
            }
        });
//        mList.add(new Profile(  "Sofia",
//                                "https://pbs.twimg.com/profile_images/572905100960485376/GK09QnNG.jpeg",
//                                "20",
//                                "New York"));
//
//        mList.add(new Profile(  "Roma",
//                                "https://i.imgur.com/N6SaAlZ.jpg",
//                                "22",
//                                "Irvine"));
//
//        mList.add(new Profile(  "Zoya",
//                                "https://i.imgur.com/wqsvWT4.jpg",
//                                "28",
//                                "Atlantic City"));

        view.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeView.doSwipe(true);
            }
        });

        view.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeView.doSwipe(false);
            }
        });

        selectedID = new SwipeCard.SelectedID() {
            @Override
            public void setSwipedCardID(final String cardID, final String cardName) {

                mStore.collection("Users").document(cardID).collection("Likes")
                        .document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        mStore.collection("Users").document(cardID).collection("Likes")
                                .document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null && task.getResult().getData() != null) {
                                        mStore.collection("Users").document(cardID).collection("Likes")
                                                .document(mAuth.getCurrentUser().getUid()).delete();
                                        Toast.makeText(getContext(), "Match Found", Toast.LENGTH_SHORT).show();
                                        saveMatchToDB(cardID, cardName);
                                    } else {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("like", true);
                                        mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                                                .collection("Likes").document(cardID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(mContext, "Like Added", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("like", true);
                                    mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                                            .collection("Likes").document(cardID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(mContext, "Like Added", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        };

        return view;
    }

    private void saveMatchToDB(final String matchID, String matchName) {
        final Map<String, Object> map = new HashMap<>();
        map.put("user_id", matchID);

        mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && task.getResult().getData() != null) {
                        swiperName = task.getResult().getString("name");
                        swiperImage = task.getResult().getString("img_url");
                        mStore.collection("Users").document(matchID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult() != null && task.getResult().getData() != null) {
                                        swipedName = task.getResult().getString("name");
                                        swipedImage = task.getResult().getString("img_url");
                                        map.put("name", swipedName);
                                        map.put("img_url", swipedImage);
                                        mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                                                .collection("Match").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {
                                                    map.put("user_id", mAuth.getCurrentUser().getUid());
                                                    map.put("name", swiperName);
                                                    map.put("img_url", swiperImage);
                                                    mStore.collection("Users").document(matchID)
                                                            .collection("Match").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getContext(), "Match Added", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
