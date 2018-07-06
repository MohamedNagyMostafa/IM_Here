package com.here.iam.nagy.mohamed.imhere.firebase_data;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.CurrentLocation;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.FlagsMarkers;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by mohamednagy on 1/7/2017.
 */
public class UserDataFirebaseCreateFlag extends UserDataFirebase {

    public UserDataFirebaseCreateFlag(String userLinkFirebase) {
        super(userLinkFirebase);
    }

    public void setupMap(final GoogleMap googleMap){
        FirebaseHelper.getUserLocation(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CurrentLocation currentLocation =
                                dataSnapshot.getValue(CurrentLocation.class);

                        if(currentLocation != null) {

                            final LatLng USER_POSITION =
                                    new LatLng(currentLocation.getLocationLatitude(),
                                            currentLocation.getLocationLongitude());

                            CameraPosition cameraPosition = CameraPosition.builder()
                                    .tilt(15)
                                    .target(USER_POSITION)
                                    .zoom(16)
                                    .build();

                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void addUserFlag(final String FLAG_TITLE, final String FLAG_DETAILS,
                            final String FLAG_TYPE, final CurrentLocation FLAG_LOCATION,
                            final boolean FLAG_IMAGE_IS_FOUNDED){
        // Load image first
        FirebaseHelper.getUserDatabaseReference(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                        if(FLAG_IMAGE_IS_FOUNDED) {

                            FirebaseHelper.getUserFlagImageStorageReference(USER_LINK_FIREBASE)
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String image = uri.toString();
                                    // set flag marker.
                                    FlagsMarkers flagsMarkers =
                                            new FlagsMarkers(
                                                    userAccount.getUserName(),
                                                    FLAG_TITLE,
                                                    image,
                                                    FLAG_DETAILS,
                                                    Constants.NO_LIKE,
                                                    FLAG_LOCATION
                                            );
                                    // set flag to user.
                                    FirebaseHelper.getUserFlag(USER_LINK_FIREBASE).child(USER_LINK_FIREBASE)
                                            .setValue(flagsMarkers);
                                    // set flag for public/friends.
                                    setFlag(flagsMarkers, FLAG_TYPE);
                                }
                            });

                        }else{

                            // set flag marker.
                            FlagsMarkers flagsMarkers =
                                    new FlagsMarkers(
                                            userAccount.getUserName(),
                                            FLAG_TITLE,
                                            null,
                                            FLAG_DETAILS,
                                            Constants.NO_LIKE,
                                            FLAG_LOCATION
                                    );
                            // set flag to user.
                            FirebaseHelper.getUserFlag(USER_LINK_FIREBASE).child(USER_LINK_FIREBASE)
                                    .setValue(flagsMarkers);
                            // set flag for public/friends.
                            setFlag(flagsMarkers, FLAG_TYPE);

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void setFlag(FlagsMarkers flagsMarkers, String flagType){
        // set flag depend on its type.
        switch (flagType.toLowerCase()){
            case Constants.PUBLIC_FLAGS:
                setFlagAsPublic(flagsMarkers);
                break;
            case Constants.FRIENDS_FLAGS:
                setFlagAsFriends(flagsMarkers);
                break;
        }
    }

    private void setFlagAsPublic(final FlagsMarkers flagsMarkers){

        HashMap<String,Object> hashMap = new HashMap<String, Object>();
        hashMap.put(USER_LINK_FIREBASE,flagsMarkers);

        FirebaseHelper.getPublicFlags().updateChildren(hashMap);

    }

    private void setFlagAsFriends(final FlagsMarkers flagsMarkers){
        FirebaseHelper.getUserFriendList(USER_LINK_FIREBASE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        HashMap<String,Object> hashMap = new HashMap<String, Object>();
                        hashMap.put(USER_LINK_FIREBASE,flagsMarkers);

                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            FirebaseHelper.getUserFriendsFlags(
                                    Utility.encodeUserEmail(dataSnapshot1.getValue(String.class)))
                                    .updateChildren(hashMap);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void setFlagImageToFirebaseStorage(Uri uri, final Context context, final ImageView imageView){
        FirebaseHelper.getUserFlagImageStorageReference(USER_LINK_FIREBASE)
                .putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Picasso.with(context).load(Objects.requireNonNull(taskSnapshot.getUploadSessionUri()).getPath())
                        .into(imageView);
            }
        });
    }


}
