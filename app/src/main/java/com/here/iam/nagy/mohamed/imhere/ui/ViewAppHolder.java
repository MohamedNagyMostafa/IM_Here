package com.here.iam.nagy.mohamed.imhere.ui;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.here.iam.nagy.mohamed.imhere.R;

import org.w3c.dom.Text;

/**
 * Created by mohamednagy on 12/25/2016.
 */
public class ViewAppHolder {

    public static class CreateAccountViewHolder{

        public final EditText USER_NAME_EDIT_TEXT;
        public final EditText USER_EMAIL_EDIT_TEXT;
        public final EditText USER_PASSWORD_EDIT_TEXT;

        public final Button SING_UP_BUTTON;

        public final TextView SIGN_IN_TEXT_VIEW;

        public CreateAccountViewHolder(View createAccountView){

            USER_NAME_EDIT_TEXT     =  createAccountView.findViewById(R.id.username_account);
            USER_EMAIL_EDIT_TEXT    =  createAccountView.findViewById(R.id.user_email_account);
            USER_PASSWORD_EDIT_TEXT =  createAccountView.findViewById(R.id.user_password_account);

            SIGN_IN_TEXT_VIEW =  createAccountView.findViewById(R.id.sign_in_text_view);

            SING_UP_BUTTON =  createAccountView.findViewById(R.id.create_account_button);

        }
    }

    public static class LoginViewHolder{

        public final EditText EMAIL_EDIT_TEXT;
        public final EditText PASSWORD_EDIT_TEXT;

        public final Button SIGN_IN_BUTTON;

        public final TextView SIGN_UP_TEXT_VIEW;
        public LoginViewHolder(View loginView){

            EMAIL_EDIT_TEXT    =  loginView.findViewById(R.id.login_email_edit_text);
            PASSWORD_EDIT_TEXT =  loginView.findViewById(R.id.login_password_edit_text);

            SIGN_IN_BUTTON  =  loginView.findViewById(R.id.sign_in_button);

            SIGN_UP_TEXT_VIEW =  loginView.findViewById(R.id.login_sign_up_text_view);
        }
    }

    public static class UserProfileViewHolder{

        public final TextView USER_NAME_TEXT_VIEW;
        public final TextView USER_EMAIL_TEXT_VIEW;
        final TextView USER_LOCATION_TEXT_VIEW;

        public final ImageView USER_IMAGE_VIEW_CONTENT;

        public final LinearLayout USER_HELP_ME_LAYOUT;
        public final LinearLayout CREATE_FLAG_LAYOUT;
        public final LinearLayout SIGN_OUT_LAYOUT;
        public final LinearLayout DETECTION_LAYOUT;

        public final Switch LOCATION_FRIEND_VISIBILITY;
        public final Switch RECEIVE_PUBLIC_FLAGS;
        public final Switch RECEIVE_FRIEND_FLAGS;

        public final FloatingActionButton MAP_BUTTON;

        public final ImageView USER_IMAGE_VIEW;
        public final ImageView HELP_ME_ICON;
        public final ImageView CREATE_FLAG_ICON;
        public final ImageView SIGN_OUT_ICON;
        public final ImageView DETECTION_ICON;

        public final View LINE_ONE;
        public final View LINE_TWO;
        public final View LINE_THREE;
        public final View LINE_FIVE;
        public final View LINE_SIX ;
        public final View LINE_FOUR;

        public final SwipeRefreshLayout USER_PROFILE_SWIPE_REFRESH;

        public UserProfileViewHolder(View userProfileView){

            USER_NAME_TEXT_VIEW        =  userProfileView.findViewById(R.id.user_name_idt);
            USER_EMAIL_TEXT_VIEW       =  userProfileView.findViewById(R.id.user_email_idt);
            USER_LOCATION_TEXT_VIEW    =  userProfileView.findViewById(R.id.user_location_idt);

            LOCATION_FRIEND_VISIBILITY =  userProfileView.findViewById(R.id.location_visible_friend_switch);
            RECEIVE_PUBLIC_FLAGS       =  userProfileView.findViewById(R.id.show_public_flags_switch);
            RECEIVE_FRIEND_FLAGS       =  userProfileView.findViewById(R.id.show_friend_flags_switch);

            USER_IMAGE_VIEW_CONTENT    = userProfileView.findViewById(R.id.user_image_profile_idt_content);

            CREATE_FLAG_LAYOUT  =  userProfileView.findViewById(R.id.create_new_flag_layout);
            SIGN_OUT_LAYOUT     =  userProfileView.findViewById(R.id.sign_out_layout);
            USER_HELP_ME_LAYOUT =  userProfileView.findViewById(R.id.help_me_layout);
            DETECTION_LAYOUT    =  userProfileView.findViewById(R.id.detection_layout);

            USER_IMAGE_VIEW  =  userProfileView.findViewById(R.id.user_image_profile_idt);
            HELP_ME_ICON     =  userProfileView.findViewById(R.id.help_me_icon);
            CREATE_FLAG_ICON =  userProfileView.findViewById(R.id.create_flag_icon);
            SIGN_OUT_ICON    =  userProfileView.findViewById(R.id.sign_out_icon);
            DETECTION_ICON   =  userProfileView.findViewById(R.id.detection_icon);

            LINE_ONE   = userProfileView.findViewById(R.id.line1);
            LINE_TWO   = userProfileView.findViewById(R.id.line2);
            LINE_THREE = userProfileView.findViewById(R.id.line3);
            LINE_FOUR  = userProfileView.findViewById(R.id.line4);
            LINE_FIVE  = userProfileView.findViewById(R.id.line5);
            LINE_SIX   = userProfileView.findViewById(R.id.line6);

            USER_PROFILE_SWIPE_REFRESH =  userProfileView.findViewById(R.id.user_profile_swipe_refresh);

            MAP_BUTTON                 =  userProfileView.findViewById(R.id.map_button);

        }
    }

    public static class FriendsListViewHolder{

        public final ImageView FRIEND_IMAGE_VIEW;
        public final ImageView FRIEND_LOCATION_IMAGE_VIEW;

        public final TextView FRIEND_NAME_TEXT_VIEW;
        public final TextView FRIEND_STATE_TEXT_VIEW;

        public final TextView REMOVE_FRIEND_TEXT_VIEW;

        public FriendsListViewHolder(View friendsListView){

            FRIEND_IMAGE_VIEW          =  friendsListView.findViewById(R.id.friends_list_user_image);
            FRIEND_LOCATION_IMAGE_VIEW =  friendsListView.findViewById(R.id.friends_list_friend_location_image_view);

            FRIEND_NAME_TEXT_VIEW      =  friendsListView.findViewById(R.id.friends_list_user_name);
            FRIEND_STATE_TEXT_VIEW     =  friendsListView.findViewById(R.id.user_state_text_view);

            REMOVE_FRIEND_TEXT_VIEW    =    friendsListView.findViewById(R.id.friends_list_remove_friend_button);
        }
    }

    public static class NotificationsViewHolder{

        public final ImageView NOTIFICATION_IMAGE_VIEW;

        public final TextView NOTIFICATION_FIRST_TEXT_VIEW;
        public final TextView NOTIFICATION_SECOND_TEXT_VIEW;
        public final TextView NOTIFICATION_DATE_TEXT_VIEW;

        public final TextView NOTIFICATION_ACCEPT_TEXT_VIEW;
        public final TextView NOTIFICATION_IGNORE_TEXT_VIEW;

        public final ConstraintLayout NOTIFICATION_LAYOUT;


        public NotificationsViewHolder(View notificationsView){

            NOTIFICATION_IMAGE_VIEW       =  notificationsView.findViewById(R.id.notification_image_view);

            NOTIFICATION_FIRST_TEXT_VIEW  =  notificationsView.findViewById(R.id.notification_first_text_view);
            NOTIFICATION_SECOND_TEXT_VIEW =  notificationsView.findViewById(R.id.notification_second_text_view);
            NOTIFICATION_DATE_TEXT_VIEW   =  notificationsView.findViewById(R.id.notification_date_text_view);

            NOTIFICATION_ACCEPT_TEXT_VIEW =  notificationsView.findViewById(R.id.notification_accept_text_view);
            NOTIFICATION_IGNORE_TEXT_VIEW =  notificationsView.findViewById(R.id.notification_ignore_text_view);

            NOTIFICATION_LAYOUT           =  notificationsView.findViewById(R.id.notification_layout);
        }
    }

    public static class SearchViewHolder{

        public final ImageView SEARCH_IMAGE_VIEW;

        public final TextView SEARCH_FIRST_TEXT_VIEW;
        public final TextView SEARCH_SECOND_TEXT_VIEW;

        public final TextView SEARCH_ACCEPT_TEXT_VIEW;
        public final TextView SEARCH_IGNORE_TEXT_VIEW;

        public SearchViewHolder(View searchView){

            SEARCH_IMAGE_VIEW       =  searchView.findViewById(R.id.search_image_view);

            SEARCH_FIRST_TEXT_VIEW  =  searchView.findViewById(R.id.search_first_text_view);
            SEARCH_SECOND_TEXT_VIEW =  searchView.findViewById(R.id.search_second_text_view);

            SEARCH_ACCEPT_TEXT_VIEW =  searchView.findViewById(R.id.search_accept_text_view);
            SEARCH_IGNORE_TEXT_VIEW =  searchView.findViewById(R.id.search_ignore_text_view);

        }
    }

    public static class FlagCreateViewHolder{

        public final RelativeLayout FLAG_IMAGE_VIEW_CONTENT;

        public final EditText FLAG_TITLE_EDIT_TEXT;
        public final EditText FLAG_DETAILS_TEXT_VIEW;
        public final EditText FLAG_LATITUDE_EDIT_TEXT;
        public final EditText FLAG_LONGITUDE_EDIT_TEXT;

        public final RadioGroup FLAG_REDIO_GROUP;

        public final RadioButton FLAG_PUBLIC_RADIO_BUTTON;
        public final RadioButton FLAG_FRIENDS_RADIO_BUTTON;

        public final TextView FLAG_CREATE_BUTTON;
        public final TextView FLAG_CANCEL_BUTTON;

        public final ScrollView FLAG_SCROLL_VIEW;

        public final ImageView FLAG_IMAGE_VIEW;
        public final ImageView TRANSPARENT_IMAGE_VIEW;
        public final ImageView CURRENT_USER_LOCATION_IMAGE_VIEW;

        public FlagCreateViewHolder(View flagCreateView){

            FLAG_IMAGE_VIEW_CONTENT          =  flagCreateView.findViewById(R.id.flag_image_view_content);

            FLAG_IMAGE_VIEW                  =  flagCreateView.findViewById(R.id.flag_image_view);
            TRANSPARENT_IMAGE_VIEW           =  flagCreateView.findViewById(R.id.transparent_image_create_flag);
            CURRENT_USER_LOCATION_IMAGE_VIEW =  flagCreateView.findViewById(R.id.user_location_create_flag);

            FLAG_TITLE_EDIT_TEXT             =  flagCreateView.findViewById(R.id.flag_title_edit_text);
            FLAG_DETAILS_TEXT_VIEW           =  flagCreateView.findViewById(R.id.flag_details_edit_text);
            FLAG_LATITUDE_EDIT_TEXT          =  flagCreateView.findViewById(R.id.flag_latitude_edit_Text);
            FLAG_LONGITUDE_EDIT_TEXT         =  flagCreateView.findViewById(R.id.flag_longitude_edit_Text);

            FLAG_REDIO_GROUP                 =  flagCreateView.findViewById(R.id.flag_radio_group);

            FLAG_PUBLIC_RADIO_BUTTON         =  flagCreateView.findViewById(R.id.flag_public_radio);
            FLAG_FRIENDS_RADIO_BUTTON        =  flagCreateView.findViewById(R.id.flag_friends_radio);

            FLAG_CREATE_BUTTON               =  flagCreateView.findViewById(R.id.create_flag_button);
            FLAG_CANCEL_BUTTON               =  flagCreateView.findViewById(R.id.cancel_flag_button);

            FLAG_SCROLL_VIEW                 =  flagCreateView.findViewById(R.id.create_flag_scroll_bar);


        }
    }

    public static class FlagDetailsViewHolder{

        public final TextView FLAG_TITLE_TEXT_VIEW;
        public final TextView FLAG_DETAILS_TEXT_VIEW;
        public final TextView FLAG_USER_NAME_CREATED_TEXT_VIEW;
        public final TextView FLAG_LOVES_NUMBER_TEXT_VIEW;

        public final ImageView FLAG_IMAGE_VIEW;

        public final FloatingActionButton FLAG_LOVE_IMAGE_VIEW;

        public final SwipeRefreshLayout FLAG_SWIPE_REFRESH;

        public FlagDetailsViewHolder(View flagDetailsView){

            FLAG_TITLE_TEXT_VIEW             =  flagDetailsView.findViewById(R.id.flag_details_title_text_view);
            FLAG_DETAILS_TEXT_VIEW           =  flagDetailsView.findViewById(R.id.flag_details_details_text_view);
            FLAG_USER_NAME_CREATED_TEXT_VIEW =  flagDetailsView.findViewById(R.id.flag_details_user_created_name_text_view);
            FLAG_LOVES_NUMBER_TEXT_VIEW      =  flagDetailsView.findViewById(R.id.flag_details_loves_number_text_view);

            FLAG_IMAGE_VIEW      =  flagDetailsView.findViewById(R.id.flag_details_image_view);

            FLAG_LOVE_IMAGE_VIEW =  flagDetailsView.findViewById(R.id.love_flag_btn);

            FLAG_SWIPE_REFRESH   =  flagDetailsView.findViewById(R.id.flag_details_swipe_refresh);

        }
    }

    public static class FlagWindowViewHolder{

        public final TextView FLAG_WINDOW_TITLE;
        public final TextView FLAG_WINDOW_TYPE;
        public final TextView FLAG_WINDOW_CREATED_NAME;
        public final TextView FLAG_WINDOW_LIKES_NUMBER;
        public final TextView FLAG_WINDOW_DELETE_TEXT_VIEW;

        public FlagWindowViewHolder(View flagWindowView){

            FLAG_WINDOW_TITLE            =  flagWindowView.findViewById(R.id.flag_window_title);
            FLAG_WINDOW_TYPE             =  flagWindowView.findViewById(R.id.flag_window_type);
            FLAG_WINDOW_CREATED_NAME     =  flagWindowView.findViewById(R.id.flag_window_created_name);
            FLAG_WINDOW_LIKES_NUMBER     =  flagWindowView.findViewById(R.id.flag_window_like_number);
            FLAG_WINDOW_DELETE_TEXT_VIEW =  flagWindowView.findViewById(R.id.flag_window_delete);
        }
    }

    public static class UsersWindowViewHolder{

        public final TextView USER_NAME_WINDOW_TEXT_VIEW;
        public final TextView USER_STATE_WINDOW_TEXT_VIEW;
        public final TextView USER_DURATIION_WINDOW_TEXT_VIEW;
        public final ImageView USER_IMAGE_WINDOW_IMAGE_VIEW;

        public UsersWindowViewHolder(View usersWindowView){

            USER_NAME_WINDOW_TEXT_VIEW   =  usersWindowView.findViewById(R.id.user_name_window_users);
            USER_STATE_WINDOW_TEXT_VIEW  =  usersWindowView.findViewById(R.id.user_state_window_users);
            USER_DURATIION_WINDOW_TEXT_VIEW  =  usersWindowView.findViewById(R.id.user_track_duration);
            USER_IMAGE_WINDOW_IMAGE_VIEW =  usersWindowView.findViewById(R.id.user_image_window_users);

        }
    }

    public static class CustomTabViewHolder{


        public final ImageView TAB_IMAGE_VIEW;
        public final TextView TAB_TEXT_VIEW;

        public CustomTabViewHolder(View customTabView){

            TAB_IMAGE_VIEW = customTabView.findViewById(R.id.tab_image_view);

            TAB_TEXT_VIEW = customTabView.findViewById(R.id.tab_text_view);

        }
    }

}
