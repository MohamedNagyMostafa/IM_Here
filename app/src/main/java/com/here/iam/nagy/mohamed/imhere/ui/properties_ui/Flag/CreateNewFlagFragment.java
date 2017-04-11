package com.here.iam.nagy.mohamed.imhere.ui.properties_ui.Flag;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebaseCreateFlag;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.CurrentLocation;

/**
 * A placeholder fragment containing a simple view.
 */
public class CreateNewFlagFragment extends Fragment
        implements GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private UserDataFirebaseCreateFlag userDataFirebaseCreateFlag;
    private  ViewAppHolder.FlagCreateViewHolder flagCreateViewHolder;
    private Uri flagImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_create_new_flag, container, false);

        flagCreateViewHolder =
                new ViewAppHolder.FlagCreateViewHolder(rootView);

        final String USER_LINK_FIREBASE = getActivity().getIntent().getExtras().getString(Constants.USER_EXTRA);

        userDataFirebaseCreateFlag = new UserDataFirebaseCreateFlag(USER_LINK_FIREBASE);

        flagCreateViewHolder.FLAG_CREATE_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.networkIsConnected(getContext())) {
                    if (createFlagValidation()) {

                        String flagTitle =
                                flagCreateViewHolder.FLAG_TITLE_EDIT_TEXT.getText().toString();
                        String flagDetails =
                                flagCreateViewHolder.FLAG_DETAILS_TEXT_VIEW.getText().toString();
                        RadioButton flagTypeRadioButton =
                                (RadioButton) rootView.findViewById(
                                        flagCreateViewHolder.FLAG_REDIO_GROUP.getCheckedRadioButtonId());
                        String flagType =
                                flagTypeRadioButton.getText().toString();

                        CurrentLocation flagLocation =
                                new CurrentLocation(
                                        Double.parseDouble(
                                                flagCreateViewHolder.FLAG_LONGITUDE_EDIT_TEXT.getText().toString()
                                        ),
                                        Double.parseDouble(
                                                flagCreateViewHolder.FLAG_LATITUDE_EDIT_TEXT.getText().toString()
                                        ),
                                        " "
                                );

                        userDataFirebaseCreateFlag.addUserFlag(
                                flagTitle, flagDetails,
                                flagType, flagLocation,
                                (flagImageUri != null));

                        getActivity().finish();
                    }
                }else{
                    Toast.makeText(getContext(),
                            "please check your network connection"
                            ,Toast.LENGTH_SHORT).show();
                }


            }
        });

        flagCreateViewHolder.FLAG_CANCEL_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        flagCreateViewHolder.FLAG_IMAGE_VIEW_CONTENT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.networkIsConnected(getContext())) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                    startActivityForResult(Intent.createChooser(
                            intent, "load flag photo"),
                            Constants.RC_PHOTO);
                }else{
                    Toast.makeText(getContext(),
                            "please check your network connection"
                            ,Toast.LENGTH_SHORT).show();
                }
            }
        });

        flagCreateViewHolder.TRANSPARENT_IMAGE_VIEW.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        // stop scroll touch
                        flagCreateViewHolder.FLAG_SCROLL_VIEW.requestDisallowInterceptTouchEvent(true);
                        // stop image touch
                        return false;

                    case MotionEvent.ACTION_UP:
                        flagCreateViewHolder.FLAG_SCROLL_VIEW.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        flagCreateViewHolder.FLAG_SCROLL_VIEW.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;

                }
            }
        });

        // set map
        MapFragment mapFragment =
                (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.flag_map_frame);
        mapFragment.getMapAsync(this);

        return rootView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.RC_PHOTO){
            if(resultCode == getActivity().RESULT_OK){
                flagImageUri = data.getData();
                userDataFirebaseCreateFlag
                        .setFlagImageToFirebaseStorage(
                                flagImageUri,
                                getContext(),
                                flagCreateViewHolder.FLAG_IMAGE_VIEW);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        flagCreateViewHolder.FLAG_LONGITUDE_EDIT_TEXT.setText(String.valueOf(latLng.longitude));
        flagCreateViewHolder.FLAG_LATITUDE_EDIT_TEXT.setText(String.valueOf(latLng.latitude));
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        userDataFirebaseCreateFlag.setupMap(googleMap);
        googleMap.setOnMapClickListener(this);

        flagCreateViewHolder.CURRENT_USER_LOCATION_IMAGE_VIEW
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        userDataFirebaseCreateFlag.setupMap(googleMap);
                    }
                });
    }

    // Validation.
    boolean createFlagValidation(){
        // Check text fields empty or not
        if(flagCreateViewHolder.FLAG_TITLE_EDIT_TEXT.getText().toString().isEmpty()){
            flagCreateViewHolder.FLAG_TITLE_EDIT_TEXT.setError("This field must not be empty.");
            return false;
        }
        if(flagCreateViewHolder.FLAG_DETAILS_TEXT_VIEW.getText().toString().isEmpty()){
            flagCreateViewHolder.FLAG_DETAILS_TEXT_VIEW.setError("This field must not be empty.");
            return false;
        }
        if(flagCreateViewHolder.FLAG_LATITUDE_EDIT_TEXT.getText().toString().isEmpty()){
            flagCreateViewHolder.FLAG_LATITUDE_EDIT_TEXT.setError("This field must not be empty.");
            return false;
        }
        if(flagCreateViewHolder.FLAG_LONGITUDE_EDIT_TEXT.getText().toString().isEmpty()){
            flagCreateViewHolder.FLAG_LONGITUDE_EDIT_TEXT.setError("This field must not be empty.");
            return false;
        }
        if(!flagCreateViewHolder.FLAG_PUBLIC_RADIO_BUTTON.isChecked() &&
                !flagCreateViewHolder.FLAG_FRIENDS_RADIO_BUTTON.isChecked()){
            Toast.makeText(getContext(),"You must choose flag type.",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

    }


}
