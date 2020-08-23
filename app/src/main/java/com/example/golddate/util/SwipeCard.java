package com.example.golddate.util;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.golddate.R;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

@Layout(R.layout.swipe_card_view)
public class SwipeCard {

    @View(R.id.swipeImageView)
    public ImageView swipeImageView;

    @View(R.id.nameAgeTxt)
    public TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    public TextView locationNameTxt;

    public Profile mProfile;
    public Context mContext;
    public SwipePlaceHolderView mSwipeView;
    private SelectedID selectedID;

    public SwipeCard(Context context, Profile profile, SwipePlaceHolderView swipeView, SelectedID selectedID) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
        this.selectedID = selectedID;
    }

    @Resolve
    public void onResolved(){
//        Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        Glide.with(mContext).load(mProfile.getImg_url()).into(swipeImageView);
        nameAgeTxt.setText(mProfile.getName() + ", " + mProfile.getAge());
        locationNameTxt.setText(mProfile.getLocation());
    }

    @SwipeOut
    public void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
    }

    @SwipeCancelState
    public void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    public void onSwipeIn(){
        selectedID.setSwipedCardID(mProfile.profileID, mProfile.getName());
        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeInState
    public void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    public void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }

    public interface SelectedID {
        public void setSwipedCardID(String cardID, String cardName);
    }
}
