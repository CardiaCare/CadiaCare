package ru.cardiacare.cardiacare;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import uk.me.lewisdeane.ldialogs.CustomDialog;

/**
* created by Yamushev Igor on 21.12.14
* PetrSU, 2014. 22305 group
*/
public class About {

    static void aboutDialog(Context context){

        // Create the builder with required paramaters - Context, Title, Positive Text
        String title = context.getString(R.string.aboutActivity);
        String positiveText =context.getString(R.string.aboutOK);
        CustomDialog.Builder builder = new CustomDialog.Builder(context, title, positiveText);

// Now we can any of the following methods.
        String content = context.getString(R.string.aboutText);
        builder.content( content);
        //builder.negativeText(String negativeText);
        //builder.darkTheme(boolean isDark);
        //builder.typeface(Typeface typeface);
        //builder.titleTextSize(int size);
        //builder.contentTextSize(int size);
        //builder.buttonTextSize(int size);
        //builder.titleAlignment(Layout.Alignment alignment); // Use either Alignment.LEFT, Alignment.CENTER or Alignment.RIGHT
        // builder.titleColor(String hex); // int res, or int colorRes parameter versions available as well.
        //builder.contentColor(String hex); // int res, or int colorRes parameter versions available as well.
        //builder.positiveColor(String hex); // int res, or int colorRes parameter versions available as well.
        //builder.negativeColor(String hex); // int res, or int colorRes parameter versions available as well.
        //builder.positiveBackground(Drawable drawable); // int res parameter version also available.
        //builder.rightToLeft(boolean rightToLeft); // Enables right to left positioning for languages that may require so.

// Now we can build the dialog.
        CustomDialog customDialog = builder.build();

// Show the dialog.
        customDialog.show();

        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {

            }

            @Override
            public void onCancelClick() {

            }
        });
    }


}
