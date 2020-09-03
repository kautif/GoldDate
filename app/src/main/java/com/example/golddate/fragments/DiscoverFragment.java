package com.example.golddate.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import org.w3c.dom.Document;

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

    private Boolean userHasNoLikes;

    private ImageButton rejectBtn;
    private ImageButton acceptBtn;

    private TextView noMatchWarning;
    private ImageView brokenHeart;

    private List<String> likesList;
    private List<String> currentLikesList;
    private String currentLike;
    private int userIndex;
    private int i;

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
        acceptBtn = view.findViewById(R.id.acceptBtn);
        rejectBtn = view.findViewById(R.id.rejectBtn);
        brokenHeart = view.findViewById(R.id.broken_heart_imageView);
        noMatchWarning = view.findViewById(R.id.no_matches_textView);
        userHasNoLikes = false;
        currentLike = "";
        final List<Profile> mList = new ArrayList<>();
        likesList = new ArrayList<>();
        currentLikesList = new ArrayList<>();
        userIndex = 0;
        i = 0;

        mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> userLikesTask) {
//                            Log.i("current user likes", "onComplete: " + userLikesTask.getResult().get("likes"));
                if (userLikesTask.isSuccessful()) {
                    currentLikesList = (List<String>) userLikesTask.getResult().get("likes");
                }
            }
        });

        mStore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    while (userIndex < task.getResult().getDocuments().size()) {
                        if (task.getResult().getDocuments().get(userIndex).getId().equals(mAuth.getCurrentUser().getUid())) {
                            currentLikesList = (List<String>) task.getResult().getDocuments().get(userIndex).get("likes");
                        }
                        userIndex += 1;
                    }

                    for (final DocumentSnapshot documentSnapshot : task.getResult()) {
                        final String docID = documentSnapshot.getId();
                        if (i < currentLikesList.size()) {
                            currentLike = currentLikesList.get(i);
                            Log.i("currentLike", "onComplete: " + currentLike);
                            i += 1;
                        }


                        if (!docID.equals(mAuth.getCurrentUser().getUid()) && !docID.equals(currentLike)) {
                            Profile profile = documentSnapshot.toObject(Profile.class).withId(docID);
                            mProfileList.add(profile);
                        }
                    }
                    if (mProfileList.size() == 0) {
                        noMatchWarning.setText("No Matches Available Right Now");
                        brokenHeart.setVisibility(View.VISIBLE);
                        noMatchWarning.setVisibility(View.VISIBLE);
                        acceptBtn.setVisibility(View.GONE);
                        rejectBtn.setVisibility(View.GONE);
                    } else {
                        brokenHeart.setVisibility(View.GONE);
                        noMatchWarning.setVisibility(View.GONE);
                        acceptBtn.setVisibility(View.VISIBLE);
                        rejectBtn.setVisibility(View.VISIBLE);
                    }

                    for (Profile profile : mProfileList) {
                        mSwipeView.addView(new SwipeCard(mContext, profile, mSwipeView, selectedID));
                        Log.i("DiscoverCard", "onComplete: " + profile);
                    }
                }
            }
        });

        view.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeView.doSwipe(false);
            }
        });

        view.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeView.doSwipe(true);
            }
        });

        selectedID = new SwipeCard.SelectedID() {
            @Override
            public void setSwipedCardID(final String cardID, final String cardName) {
                Log.i("cardID", "setSwipedCardID: " + cardID);

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
                                        likesList.add(cardID);
                                        map.put("likes", likesList);
                                        mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
