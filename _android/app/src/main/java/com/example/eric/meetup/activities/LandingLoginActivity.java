package com.example.eric.meetup.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.eric.meetup.R;
import com.example.eric.meetup.applications.MeetUpApplication;
import com.example.eric.meetup.errorhandling.RequestFailedException;
import com.example.eric.meetup.errorhandling.UserNotFoundException;
import com.example.eric.meetup.helpers.ToastHelper;
import com.example.eric.meetup.networking.MeetUpAuthConnection;
import com.example.eric.meetup.networking.MeetUpConnection;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LandingLoginActivity extends MeetUpActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Password Validation strings.
     */
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LEGAL_CHARS       = ".!?,-_";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRequestTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    public void onResume() {
        super.onResume();
        setApplicationActivity(this);
        MeetUpApplication app = setApplicationActivity(this);
        app.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MeetUpApplication app = setApplicationActivity(null);
        app.onPause();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, this);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        String[] values = email.split("@");

        if (values.length != 2)
            return false;
        else if (values[0].length() == 0 || values[1].length() == 0)
            return false;

        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 8)
            return false;
        if (!containsCharInSequence(password, LOWERCASE_LETTERS))
            return false;
        if (!containsCharInSequence(password, UPPERCASE_LETTERS))
            return false;
        if (!containsCharInSequence(password, LEGAL_CHARS))
            return false;

        return true;
    }

    private boolean containsCharInSequence(final String str, final CharSequence sequence) {
        for (int i = 0; i < sequence.length(); ++i) {
            if (str.contains(Character.toString(sequence.charAt(i)))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LandingLoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private abstract class UserRequestTask extends AsyncTask<Void, Void, Integer> {
        private final String mEmail;
        private final String mPassword;
        private final MeetUpActivity mActivity;

        public UserRequestTask(String email, String password, MeetUpActivity activity) {
            mEmail = email;
            mPassword = password;
            mActivity = activity;
        }

        protected String getEmail() {
            return mEmail;
        }

        protected String getPassword() {
            return mPassword;
        }

        protected MeetUpActivity getActivity() {
            return mActivity;
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends UserRequestTask {

        public UserLoginTask(String email, String password, MeetUpActivity activity) {
            super(email, password, activity);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String response = null;

            // Attempt to login.
            try {
                MeetUpAuthConnection connection = new MeetUpAuthConnection();
                response = connection.login(getEmail(), getPassword());
            } catch (UserNotFoundException e) {
                // User does not exist.
                return HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (RequestFailedException e) {
                // Something on the backend went wrong.
                ToastHelper errorToast = new ToastHelper(getActivity());
                errorToast.display(getString(R.string.request_failed));

                return HttpURLConnection.HTTP_INTERNAL_ERROR;
            }

            // Login was successful.
            getActivity().setJwt(response);

            return HttpURLConnection.HTTP_OK;
        }

        @Override
        protected void onPostExecute(final Integer statusCode) {
            mAuthTask = null;
            showProgress(false);

            ToastHelper resultToast = new ToastHelper(getActivity());

            // As the user if they would like to register, because the email was not found.
            if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = setUpAlertDialogBuilder(
                                getString(R.string.cancel),
                                getString(R.string.register),
                                getEmail(),
                                getPassword());
                        AlertDialog alert = builder.create();

                        alert.show();
                    }
                });
            } else if (statusCode == HttpURLConnection.HTTP_OK) {
                resultToast.display(getString(R.string.welcome_to_meetup));
                // TODO: Take user to UserLandingActivity.
            } else {
                resultToast.display(String.format(getString(R.string.login_failed), getEmail()));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        private AlertDialog.Builder setUpAlertDialogBuilder(CharSequence negMsg, CharSequence posMsg,
                                                            final String email, final String password) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

            dialogBuilder.setTitle(String.format(getString(R.string.registration_message), email));

            dialogBuilder.setNegativeButton(negMsg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialogBuilder.setPositiveButton(posMsg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserRegistrationTask registrationTask = new UserRegistrationTask(getEmail(), getPassword(), getActivity());
                    registrationTask.execute();
                }
            });

            return dialogBuilder;
        }
    }

    public class UserRegistrationTask extends UserRequestTask {
        public UserRegistrationTask(String email, String password, MeetUpActivity activity) {
            super(email, password, activity);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String response = null;

            int responseCode;
            try {
                MeetUpAuthConnection registrationConnection = new MeetUpAuthConnection();

                response = registrationConnection.register(getEmail(), getPassword());
                responseCode = registrationConnection.getResponseCode();
            } catch (Exception e) {
                // Something while registering the user went wrong.
                e.printStackTrace();
                responseCode = 500;
            }

            // Stored the JWT we get back from the response.
            if (response != null && responseCode == HttpURLConnection.HTTP_OK) {
                getActivity().setJwt(response);
            }

            return responseCode;
        }

        @Override
        public void onPostExecute(final Integer statusCode) {
            ToastHelper resultToast = new ToastHelper(getActivity());

            if (statusCode == HttpURLConnection.HTTP_OK) {
                resultToast.display(String.format(getString(R.string.registration_successful), getEmail()));
                // TODO: Take user to UserLandingActivity.
            }
            else {
                resultToast.display(String.format(getString(R.string.registration_failed), getEmail()));
            }
        }
    }
}

