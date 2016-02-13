package com.meetup.helpers;

import android.app.Activity;
import android.widget.Toast;

public class ToastHelper extends PresentationHelper {
    private Activity mActivity;

    public ToastHelper(Activity a) {
        mActivity = a;
    }

    @Override
    protected void doDisplay(CharSequence dialog) {
        doDisplay(dialog, Toast.LENGTH_LONG);
    }

    @Override
    protected void doDisplay(CharSequence dialog, int duration) {
        displayToast(dialog.toString(), duration);
    }

    private void displayToast(final CharSequence dialog, int length) {
        final int duration = (length == Toast.LENGTH_LONG || length == Toast.LENGTH_SHORT) ? length : Toast.LENGTH_SHORT;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mActivity.getApplicationContext(), dialog.toString(), duration);
                toast.show();
            }
        });
    }
}
