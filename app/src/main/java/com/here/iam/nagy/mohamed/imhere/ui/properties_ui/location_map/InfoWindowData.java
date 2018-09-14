package com.here.iam.nagy.mohamed.imhere.ui.properties_ui.location_map;

import com.here.iam.nagy.mohamed.imhere.user_account.account_property.objects.UserAccount;

/**
 * Created by Mohamed Nagy on 9/14/2018 .
 * Project IM_Here
 * Time    6:59 PM
 */
public class InfoWindowData {
    private UserAccount mUserAccount;
    private Long mStartDate;
    private Long mEndDate;

    public InfoWindowData(UserAccount userAccount, Long startDate, Long endDate){
        mUserAccount = userAccount;
        mStartDate = startDate;
        mEndDate = endDate;
    }

    public void setEndDate(Long mEndDate) {
        this.mEndDate = mEndDate;
    }

    public void setStartDate(Long mStartDate) {
        this.mStartDate = mStartDate;
    }

    public void setUserAccount(UserAccount mUserAccount) {
        this.mUserAccount = mUserAccount;
    }

    public Long getEndDate() {
        return mEndDate;
    }

    public Long getStartDate() {
        return mStartDate;
    }

    public UserAccount getUserAccount() {
        return mUserAccount;
    }
}
