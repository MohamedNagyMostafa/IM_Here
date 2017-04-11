package com.here.iam.nagy.mohamed.imhere.firebase_data;

import com.google.firebase.auth.FirebaseUser;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Constants;
import com.here.iam.nagy.mohamed.imhere.helper_classes.FirebaseHelper;
import com.here.iam.nagy.mohamed.imhere.helper_classes.Utility;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.AccountSettings;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;

/**
 * Created by mohamednagy on 12/27/2016.
 */
public class UserDataFirebaseCreateAccount extends UserDataFirebase {


    public UserDataFirebaseCreateAccount(final String USER_EMAIL){
        super(Utility.encodeUserEmail(USER_EMAIL));
    }

    public void createAccount(FirebaseUser user, String userName){

        UserAccount userAccount = new UserAccount(
                userName,
                user.getEmail(),
                null);

        FirebaseHelper.USERS_DATABASE_REFERENCE.child(
                Utility.encodeUserEmail(user.getEmail())).setValue(userAccount);

        setAccountSettings();

    }

    private void setAccountSettings(){

        AccountSettings accountSettings =
                new AccountSettings(false,false,false,false,
                        Constants.USER_DETECTION_INACTIVE, Constants.USER_ONLINE);

        FirebaseHelper.getUserAccountSettings(USER_LINK_FIREBASE)
                .setValue(accountSettings);

    }
}
