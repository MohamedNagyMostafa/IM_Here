package com.here.iam.nagy.mohamed.imhere.ui.properties_adapter_ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by mohamednagy on 12/25/2016.
 */
public class UserPropertiesAdapter extends ArrayAdapter {

    private UserPropertiesAdapterUi userPropertiesAdapterUi;

    public UserPropertiesAdapter(Context context,
                                 UserPropertiesAdapterUi userPropertiesAdapterUi) {
        super(context, 0);
        this.userPropertiesAdapterUi = userPropertiesAdapterUi;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return userPropertiesAdapterUi.getView(position,parent);
    }

    @Override
    public int getCount() {
        return userPropertiesAdapterUi.getCount();
    }
}
