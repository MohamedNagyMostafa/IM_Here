package com.here.iam.nagy.mohamed.imhere.firebase_data;

import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;
import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;

import java.util.ArrayList;

/**
 * Created by mohamednagy on 12/26/2016.
 */
public interface UserSearchUi {
    void setSearchData(ArrayList<UserAccount> usersAccounts);
    void setUserView(boolean userInFriendList, boolean userInFriendRequest,
                     boolean currentUserSendFriendRequest,
                     ViewAppHolder.SearchViewHolder searchViewHolder, final String USER_EMAIL);
}
