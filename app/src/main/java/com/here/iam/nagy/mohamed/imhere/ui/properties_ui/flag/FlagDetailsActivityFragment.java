package com.here.iam.nagy.mohamed.imhere.ui.properties_ui.flag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.firebase_data.UserDataFirebaseFlagDetails;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FlagDetailsActivityFragment extends Fragment{
    private UserDataFirebaseFlagDetails userDataFirebaseFlagDetails;
    private ViewAppHolder.FlagDetailsViewHolder flagDetailsViewHolder;
    private String USER_LINK_FIREBASE ;

    private View.OnClickListener flagLoveListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Utility.networkIsConnected(getContext())) {
                        userDataFirebaseFlagDetails.flagLoveClicked(
                                flagDetailsViewHolder.FLAG_LOVE_IMAGE_VIEW,
                                getContext(),
                                flagDetailsViewHolder.FLAG_LOVES_NUMBER_TEXT_VIEW);
                    }else{
                        Toast.makeText(getContext(),
                                getString(R.string.network_connection)
                                ,Toast.LENGTH_SHORT).show();
                    }
                }
            };

    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if(Utility.networkIsConnected(getContext())) {
                        userDataFirebaseFlagDetails.setFlagDetailsUi(flagDetailsViewHolder, getContext());
                        flagDetailsViewHolder.FLAG_SWIPE_REFRESH.setRefreshing(false);
                    }else{
                        Toast.makeText(getContext(),
                                getString(R.string.network_connection)
                                ,Toast.LENGTH_SHORT).show();
                        flagDetailsViewHolder.FLAG_SWIPE_REFRESH.setRefreshing(false);
                    }
                }
            };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_flag_details, container, false);

        USER_LINK_FIREBASE = getActivity().getIntent().getExtras().getString(Constants.USER_EXTRA);
        final ArrayList<String> FLAG_DATA = getActivity().getIntent().getExtras()
                .getStringArrayList(Constants.FLAG_EXTRA);

        if(FLAG_DATA != null && FLAG_DATA.size() > 1) {

            userDataFirebaseFlagDetails = new UserDataFirebaseFlagDetails(
                    USER_LINK_FIREBASE,
                    FLAG_DATA.get(0),
                    FLAG_DATA.get(1));

        }
        flagDetailsViewHolder = new ViewAppHolder.FlagDetailsViewHolder(rootView);
        flagDetailsViewHolder.FLAG_SWIPE_REFRESH.setColorSchemeResources(R.color.active_tab);
        flagDetailsViewHolder.FLAG_SWIPE_REFRESH.setOnRefreshListener(swipeRefreshListener);


        if(Utility.networkIsConnected(getContext())) {
            userDataFirebaseFlagDetails.setFlagDetailsUi(flagDetailsViewHolder, getContext());
        }else{
            Toast.makeText(getContext(),
                    getString(R.string.network_connection)
                    ,Toast.LENGTH_SHORT).show();
        }

        flagDetailsViewHolder.FLAG_LOVE_IMAGE_VIEW.setOnClickListener(flagLoveListener);

        return rootView;
    }



}
