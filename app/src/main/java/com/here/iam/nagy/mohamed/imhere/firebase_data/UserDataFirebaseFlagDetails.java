package com.here.iam.nagy.mohamed.imhere.firebase_data;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.FlagsMarkers;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by mohamednagy on 1/9/2017.
 */
public class UserDataFirebaseFlagDetails extends UserDataFirebase {

    private final String FLAG_KEY ;
    private final String FLAG_TYPE;

    public UserDataFirebaseFlagDetails(String userLinkFirebase, String flag_key, String flag_type) {
        super(userLinkFirebase);
        this.FLAG_KEY = flag_key;
        this.FLAG_TYPE = flag_type;
    }

    public void flagLoveClicked(ImageView flagLoveImage, Context context,
                                final TextView flagLoversTextView){
        // Check first like state.
        switch (FLAG_TYPE){

            case Constants.PUBLIC_FLAGS :
                publicFlagLoveClicked(flagLoveImage, context, flagLoversTextView);
                break;

            case Constants.FRIENDS_FLAGS :
                friendFlagLoveClicked(flagLoveImage, context, flagLoversTextView);
                break;
        }
    }

    private void publicFlagLoveClicked(final ImageView flagLoveImage, final Context context,
                                       final TextView flagLoversTextView){
        FirebaseHelper.getPublicFlags()
                .child(FLAG_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FlagsMarkers flagsMarkers = dataSnapshot.getValue(FlagsMarkers.class);
                setFlagPublicLoveProcess(flagsMarkers,flagLoversTextView, flagLoveImage, context);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void friendFlagLoveClicked(final ImageView flagLoveImage, final Context context,
                                       final TextView flagLoversTextView){

        FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                .child(FLAG_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FlagsMarkers flagsMarkers = dataSnapshot.getValue(FlagsMarkers.class);
                setFlagFriendLoveProcess(flagsMarkers,flagLoversTextView, flagLoveImage, context);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setFlagPublicLoveProcess(final FlagsMarkers flagsMarkers, final TextView flagLoversTextView,
                                          final ImageView flagLoveImage, final Context context){

        final int FLAG_LIKES = flagsMarkers.getLikesNumber();

        if(isFlagLiked(flagsMarkers)){

            // SetViews
            flagLoveImage.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_black_36dp));
            flagLoversTextView.setText(String.valueOf(FLAG_LIKES - 1));

            // delete like from flag for all users.
            int newFlagLikes = FLAG_LIKES - 1;
            HashMap<String,Object> likesHashMap = new HashMap<String, Object>();

            likesHashMap.put(Constants.FLAG_LIKES,newFlagLikes);

            // decrement flag likes for all users.
            FirebaseHelper.getPublicFlags()
                    .child(FLAG_KEY).updateChildren(likesHashMap);

            // remove user from lovers.
            FirebaseHelper.getPublicFlags()
                    .child(FLAG_KEY).child(Constants.FLAGS_LOVERS)
                    .child(USER_LINK_FIREBASE).removeValue();

            // delete like from flag owner
            FirebaseHelper.getUserFlag(FLAG_KEY).child(FLAG_KEY)
                    .child(Constants.FLAG_LIKES)
                    .setValue(FLAG_LIKES -1);

            FirebaseHelper.getUserFlag(FLAG_KEY).child(FLAG_KEY)
                    .child(Constants.FLAGS_LOVERS)
                    .child(USER_LINK_FIREBASE).removeValue();

            // set flag like is removed.
            FirebaseHelper.getPublicFlags()
                    .child(FLAG_KEY).child(Constants.FLAGS_LOVERS)
                    .setValue(setFlagLiked(false));


        }else{

            // SetViews
            flagLoveImage.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_black_36dp));
            flagLoversTextView.setText(String.valueOf(FLAG_LIKES + 1));
            // new like
            final HashMap<String,Object> likerHashMap = new HashMap<String, Object>();
            likerHashMap.put(USER_LINK_FIREBASE,true);

            // add like from flag for all users.

            int newFlagLikes = FLAG_LIKES + 1;

            HashMap<String,Object> likesHashMap = new HashMap<String, Object>();

            likesHashMap.put(Constants.FLAG_LIKES,newFlagLikes);
            // increment flag likes for all users.
            FirebaseHelper.getPublicFlags()
                    .child(FLAG_KEY).updateChildren(likesHashMap);
            // add user from lovers.
            FirebaseHelper.getPublicFlags()
                    .child(FLAG_KEY).child(Constants.FLAGS_LOVERS)
                    .updateChildren(likerHashMap);

            // add like from flag owener
            FirebaseHelper.getUserFlag(FLAG_KEY).child(FLAG_KEY)
                    .child(Constants.FLAG_LIKES)
                    .setValue(FLAG_LIKES + 1);
            FirebaseHelper.getUserFlag(FLAG_KEY).child(FLAG_KEY)
                    .child(Constants.FLAGS_LOVERS)
                    .updateChildren(likerHashMap);

            // set flag is liked.
            FirebaseHelper.getPublicFlags()
                    .child(FLAG_KEY).child(Constants.FLAGS_LOVERS)
                    .setValue(setFlagLiked(true));
        }
    }


    private void setFlagFriendLoveProcess(final FlagsMarkers flagsMarkers, final TextView flagLoversTextView,
                                          final ImageView flagLoveImage, final Context context){

        final int FLAG_LIKES = flagsMarkers.getLikesNumber();

        if(isFlagLiked(flagsMarkers)){

            // SetViews
            flagLoveImage.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_black_36dp));
            flagLoversTextView.setText(String.valueOf(FLAG_LIKES - 1));

            // delete like from flag for all user friends.
            FirebaseHelper.getUserFriendList(FLAG_KEY)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            int newFlagLikes = FLAG_LIKES - 1;
                            HashMap<String,Object> likesHashMap = new HashMap<String, Object>();

                            likesHashMap.put(Constants.FLAG_LIKES,newFlagLikes);

                            for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                // decrement flag likes for all users.
                                FirebaseHelper.getUserFriendsFlags(Utility.encodeUserEmail(dataSnapshot1.getValue(String.class)))
                                        .child(FLAG_KEY).updateChildren(likesHashMap);
                                // remove user from lovers.
                                FirebaseHelper.getUserFriendsFlags(Utility.encodeUserEmail(dataSnapshot1.getValue(String.class)))
                                        .child(FLAG_KEY).child(Constants.FLAGS_LOVERS)
                                        .child(USER_LINK_FIREBASE).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            // delete like from flag owner
            FirebaseHelper.getUserFlag(FLAG_KEY).child(FLAG_KEY)
                    .child(Constants.FLAG_LIKES)
                    .setValue(FLAG_LIKES -1);
            FirebaseHelper.getUserFlag(FLAG_KEY).child(FLAG_KEY)
                    .child(Constants.FLAGS_LOVERS)
                    .child(USER_LINK_FIREBASE).removeValue();

            // flag like is removed.
            FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                    .child(FLAG_KEY).child(Constants.FLAGS_LOVERS)
                    .setValue(setFlagLiked(false));

        }else{

            // SetViews
            flagLoveImage.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_black_36dp));
            flagLoversTextView.setText(String.valueOf(FLAG_LIKES + 1));

            final HashMap<String,Object> likerHashMap = new HashMap<String, Object>();
            likerHashMap.put(USER_LINK_FIREBASE,true);

            // add like from flag for all users.
            FirebaseHelper.getUserFriendList(FLAG_KEY)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            int newFlagLikes = FLAG_LIKES + 1;
                            HashMap<String,Object> likesHashMap = new HashMap<String, Object>();

                            likesHashMap.put(Constants.FLAG_LIKES,newFlagLikes);

                            for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                // increment flag likes for all users.
                                FirebaseHelper.getUserFriendsFlags(
                                        Utility.encodeUserEmail(dataSnapshot1.getValue(String.class)))
                                        .child(FLAG_KEY).updateChildren(likesHashMap);
                                // add user to lovers.
                                FirebaseHelper.getUserFriendsFlags(
                                        Utility.encodeUserEmail(dataSnapshot1.getValue(String.class)))
                                        .child(FLAG_KEY).child(Constants.FLAGS_LOVERS)
                                        .setValue(likerHashMap);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            // add like to owner flag
            FirebaseHelper.getUserFlag(FLAG_KEY).child(FLAG_KEY)
                    .child(Constants.FLAG_LIKES)
                    .setValue(FLAG_LIKES + 1);
            FirebaseHelper.getUserFlag(FLAG_KEY).child(FLAG_KEY)
                    .child(Constants.FLAGS_LOVERS)
                    .updateChildren(likerHashMap);

            // set flag is liked.
            FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                    .child(FLAG_KEY).child(Constants.FLAGS_LOVERS)
                    .setValue(setFlagLiked(true));
        }
    }

    public void setFlagDetailsUi(final ViewAppHolder.FlagDetailsViewHolder flagDetailsViewHolder,
                                 final Context context){

        switch (FLAG_TYPE){

            case Constants.PUBLIC_FLAGS :
                publicFlagUi(flagDetailsViewHolder, context);
                break;

            case Constants.FRIENDS_FLAGS :
                friendFlagUi(flagDetailsViewHolder, context);
                break;
        }

    }

    private void publicFlagUi(final ViewAppHolder.FlagDetailsViewHolder flagDetailsViewHolder,
                              final Context context){
        FirebaseHelper.getPublicFlags()
                .child(FLAG_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FlagsMarkers flagsMarkers = dataSnapshot.getValue(FlagsMarkers.class);

                setDetailsFlagUi(flagDetailsViewHolder, context, flagsMarkers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void friendFlagUi(final ViewAppHolder.FlagDetailsViewHolder flagDetailsViewHolder,
                              final Context context){
        FirebaseHelper.getUserFriendsFlags(USER_LINK_FIREBASE)
                .child(FLAG_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FlagsMarkers flagsMarkers = dataSnapshot.getValue(FlagsMarkers.class);

                setDetailsFlagUi(flagDetailsViewHolder, context, flagsMarkers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setDetailsFlagUi(ViewAppHolder.FlagDetailsViewHolder flagDetailsViewHolder,
                                  Context context, FlagsMarkers flagsMarkers){
        flagDetailsViewHolder.FLAG_TITLE_TEXT_VIEW.setText(flagsMarkers.getTitle());

        if(flagsMarkers.getImage() != null)
            Picasso.with(context).load(flagsMarkers.getImage())
                    .into(flagDetailsViewHolder.FLAG_IMAGE_VIEW);

        flagDetailsViewHolder.FLAG_DETAILS_TEXT_VIEW.setText(flagsMarkers.getDetails());
        flagDetailsViewHolder.FLAG_LOVES_NUMBER_TEXT_VIEW
                .setText(String.valueOf(flagsMarkers.getLikesNumber()));
        flagDetailsViewHolder.FLAG_USER_NAME_CREATED_TEXT_VIEW.setText(flagsMarkers.getUserCreatedName());

        if(isFlagLiked(flagsMarkers)){
            flagDetailsViewHolder.FLAG_LOVE_IMAGE_VIEW
                    .setImageDrawable(context.getDrawable(R.drawable.ic_favorite_black_36dp));
        }else{
            flagDetailsViewHolder.FLAG_LOVE_IMAGE_VIEW
                    .setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_black_36dp));
        }

    }

    private boolean isFlagLiked(FlagsMarkers flagsMarkers){
        if(flagsMarkers.getFlagLovers() != null &&
                flagsMarkers.getFlagLovers().containsKey(USER_LINK_FIREBASE)){
            return flagsMarkers.getFlagLovers().get(USER_LINK_FIREBASE);
        }
        return false;
    }

    private HashMap<String,Boolean> setFlagLiked(boolean isLiked){

        HashMap<String,Boolean> flagLikers = new HashMap<>();
        flagLikers.put(USER_LINK_FIREBASE,isLiked);

        return flagLikers;
    }
}
