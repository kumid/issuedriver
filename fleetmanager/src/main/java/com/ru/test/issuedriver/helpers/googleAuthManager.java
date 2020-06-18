package com.ru.test.issuedriver.helpers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ru.test.issuedriver.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Client ID
// 240827898081-l01k6j5n1g68crq6rk7a3qnf7bt57roj.apps.googleusercontent.com
// Client Secret
// l5q9WryjenB6605czhmUvusW

public class googleAuthManager {

    private static AppCompatActivity activity;
    private static GoogleSignInOptions gso;

    // [START declare_auth]
    private static FirebaseAuth mAuth;
    // [END declare_auth]

    private static FirebaseUser currentUser;

    private static GoogleSignInClient mGoogleSignInClient;

    public static boolean isSigned() {
        mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser() != null;
    }




        public static void init(AppCompatActivity _activity){
        activity = _activity;

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
     }


    // [START on_start_check_user]
    public static void onStart() {
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }
    // [END on_start_check_user]


    // [START onactivityresult]

    private static String TAG = "myLogs";
    private static final int RC_SIGN_IN = 9001;

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null)
                    firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private static void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressBar();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //MainActivity.getInstance().setRefs();
                            //fbStorageUploads.setUserID(googleAuthManager.getUid());
                            //MainActivity.getInstance().loadMarkersFromFB();
                            mysettings.SetEmail(user.getEmail());
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(activity.findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if(callback4Auth != null)
                            callback4Auth.callback(task.isSuccessful());
                        // [START_EXCLUDE]
                        //hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    public static void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    public static void signOut() {
        if(mGoogleSignInClient != null) {
            // Google sign out
            mGoogleSignInClient.signOut().addOnCompleteListener(activity,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Firebase sign out
                            if (callback4signout != null)
                                callback4signout.callback();
//                        updateUI(null);
                        }
                    });
        }

        if(mAuth == null) {
            if(callback4signout != null)
                callback4signout.callback();
            return;
        }

        mAuth.signOut();

    }

    public static void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(callback4signout != null)
                            callback4signout.callback();
                        //updateUI(null);
                    }
                });
    }

    public static void updateUI(FirebaseUser user) {
        /*
        hideProgressBar();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.signInButton).setVisibility(View.GONE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.signInButton).setVisibility(View.VISIBLE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.GONE);
        }
         */
    }

    public static String getEmail() {
        String res;
        try {
            res = mAuth.getCurrentUser().getEmail();
        }
        catch (NullPointerException ex){
            res = "";
        }

        return res;
    }


    public static String getUid() {

       return mAuth.getUid();
    }

    public static AuthCompleate callback4Auth;
    public interface  AuthCompleate {
        void callback(boolean isCompleate);
    }


    public static signoutComplete callback4signout;
    public interface  signoutComplete {
        void callback();
    }
}
