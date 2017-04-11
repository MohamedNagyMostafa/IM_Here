package com.here.iam.nagy.mohamed.imhere.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.here.iam.nagy.mohamed.imhere.R;
import com.here.iam.nagy.mohamed.imhere.ui.ViewAppHolder;

/**
 * Created by mohamednagy on 4/7/2017.
 */
public class Animation{

    public static class LoginActivityAnimation{

        private static final int ALPHA = 1;
        private static final int DURATION = 2000;
        private static final int DURATION_INCREASING = 500;
        private static final int FOCUS_COLOR = R.color.loginEditTextFocusColor;
        private static final int LEAVE_COLOR = R.color.loginEditTextInFocusColor;

        public static void editTextFocusAnimation(Context context, EditText... editTexts){
            for(EditText editText : editTexts){
                editText.setOnFocusChangeListener(editTextFocusChange(editText, context));
            }
        }

        private static View.OnFocusChangeListener editTextFocusChange(final EditText editText, final Context context){
            return new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if(hasFocus || !editText.getText().toString().equals("")){
                        editText.setBackgroundTintList(
                                ColorStateList.valueOf(
                                        context.getResources()
                                                .getColor(FOCUS_COLOR)));
                    }else{
                        editText.setBackgroundTintList(
                                ColorStateList.valueOf(
                                        context.getResources()
                                                .getColor(LEAVE_COLOR)));

                    }
                }
            };
        }

        public static void startingAnimation(Object viewAppHolder) {
            ViewAppHolder.LoginViewHolder loginViewHolder =
                    (ViewAppHolder.LoginViewHolder)viewAppHolder;

            loginViewHolder.EMAIL_EDIT_TEXT
                    .animate().alpha(ALPHA).setDuration(DURATION);

            loginViewHolder.PASSWORD_EDIT_TEXT
                    .animate().alpha(ALPHA).setDuration(DURATION + DURATION_INCREASING);

            loginViewHolder.SIGN_IN_BUTTON
                    .animate().alpha(ALPHA).setDuration(DURATION + (DURATION_INCREASING *2));

            loginViewHolder.SIGN_UP_TEXT_VIEW
                    .animate().alpha(ALPHA).setDuration(DURATION + (DURATION_INCREASING * 3));
        }

    }

    public static class CreateAccountAnimation{

        private static final int ALPHA = 1;
        private static final int DURATION = 2000;
        private static final int DURATION_INCREASING = 500;
        private static final int FOCUS_COLOR = R.color.loginEditTextFocusColor;
        private static final int LEAVE_COLOR = R.color.loginEditTextInFocusColor;

        public static void editTextFocusAnimation(Context context, EditText... editTexts){
            for(EditText editText : editTexts){
                editText.setOnFocusChangeListener(editTextFocusChange(editText, context));
            }
        }

        private static View.OnFocusChangeListener editTextFocusChange(final EditText editText, final Context context){
            return new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if(hasFocus || !editText.getText().toString().equals("")){
                        editText.setBackgroundTintList(
                                ColorStateList.valueOf(
                                        context.getResources()
                                                .getColor(FOCUS_COLOR)));
                    }else{
                        editText.setBackgroundTintList(
                                ColorStateList.valueOf(
                                        context.getResources()
                                                .getColor(LEAVE_COLOR)));

                    }
                }
            };
        }

        public static void startingAnimation(Object viewAppHolder) {
            ViewAppHolder.CreateAccountViewHolder loginViewHolder =
                    (ViewAppHolder.CreateAccountViewHolder)viewAppHolder;

            loginViewHolder.USER_NAME_EDIT_TEXT
                    .animate().alpha(ALPHA).setDuration(DURATION);

            loginViewHolder.USER_EMAIL_EDIT_TEXT
                    .animate().alpha(ALPHA).setDuration(DURATION + DURATION_INCREASING);

            loginViewHolder.USER_PASSWORD_EDIT_TEXT
                    .animate().alpha(ALPHA).setDuration(DURATION + (DURATION_INCREASING *2));

            loginViewHolder.SING_UP_BUTTON
                    .animate().alpha(ALPHA).setDuration(DURATION + (DURATION_INCREASING * 3));

            loginViewHolder.SIGN_IN_TEXT_VIEW
                    .animate().alpha(ALPHA).setDuration(DURATION + (DURATION_INCREASING * 4));
        }

    }

    public static class UserProfileAnimation{

        private static final int ALPHA = 1;
        private static final int LINE1 = 0;
        private static final int LINE2 = 1;
        private static final int LINE3 = 2;
        private static final int LINE4 = 3;
        private static final int DURATION = 1000;
        private static final int DURATION_INCREASING = 500;

        public static void startingAnimation(ViewAppHolder.UserProfileViewHolder profileViewHolder){
            setIconsAnimation(
                    profileViewHolder,
                    profileViewHolder.CREATE_FLAG_ICON,
                    profileViewHolder.HELP_ME_ICON,
                    profileViewHolder.DETECTION_ICON,
                    profileViewHolder.SIGN_OUT_ICON
            );

            setLinesAnimation(
                    profileViewHolder.LINE_FIVE,
                    profileViewHolder.LINE_SIX

            );

        }

        private static void setIconsAnimation(
                final ViewAppHolder.UserProfileViewHolder usersWindowViewHolder,
                ImageView... icons){
            for(int i = 0 ; i < icons.length ; i++){
                final int line = i;
                icons[i].animate().alpha(ALPHA).setDuration(DURATION + DURATION_INCREASING * i).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        switch (line){
                            case LINE1:
                                usersWindowViewHolder.LINE_ONE.setAlpha(ALPHA);
                                break;
                            case LINE2:
                                usersWindowViewHolder.LINE_TWO.setAlpha(ALPHA);
                                break;
                            case LINE3:
                                usersWindowViewHolder.LINE_THREE.setAlpha(ALPHA);
                                break;
                            case LINE4:
                                usersWindowViewHolder.LINE_FOUR.setAlpha(ALPHA);
                        }
                    }
                });
            }
        }

        private static void setLinesAnimation(View... lines){
            for (View line: lines){
                line.animate().alpha(ALPHA).setDuration(DURATION + DURATION_INCREASING);
            }
        }
    }

}
