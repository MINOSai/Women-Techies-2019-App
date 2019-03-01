package com.example.wtmapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SignInFragment extends Fragment {
    private SignInButton btnGoogleSignIn;
    private final static int RC_SIGN_IN = 1;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Main Activity";
    private View rootView;

    public SignInFragment() {
    }

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_sign_in, container, false);
        btnGoogleSignIn = (SignInButton) rootView.findViewById(R.id.btn_google_login);
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        mAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference();

//        if (Build.VERSION.SDK_INT >= 21) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.parseColor("#FF6961"));
//        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(final @NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                    mMessagesDatabaseReference.child("users").addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                                        if ((firebaseAuth.getCurrentUser().getDisplayName()).equals(data.child("user_name").getValue())) {
                                           // Toast.makeText(getActivity(), "Already logged in ", Toast.LENGTH_SHORT).show();
                                        } else {
                                            String key = firebaseAuth.getCurrentUser().getUid();

                                            final Users u = new Users();
                                            u.setUserName(firebaseAuth.getCurrentUser().getDisplayName());
                                            u.setUserEmail(firebaseAuth.getCurrentUser().getEmail());
                                            u.setUserPhotoUrl(firebaseAuth.getCurrentUser().getPhotoUrl().toString());

                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(key, u.toUsersFirebaseObject());
                                            mMessagesDatabaseReference.child("users").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError == null) {
                                                        Toast.makeText(getActivity(), "Authentication successful ", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );

//                    Intent intentToEventsActivity = new Intent(SignInFragment.this, MainActivity.class);
//                    intentToEventsActivity.putExtra("userName", firebaseAuth.getCurrentUser().getDisplayName());
//                    intentToEventsActivity.putExtra("userMail", firebaseAuth.getCurrentUser().getEmail());
//                    intentToEventsActivity.putExtra("userImage", firebaseAuth.getCurrentUser().getPhotoUrl().toString());
//                    startActivity(intentToEventsActivity);

                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return rootView;
    }



    /*
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign_in);

            btnGoogleSignIn = (SignInButton) findViewById(R.id.btn_google_login);
            btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
            mAuth = FirebaseAuth.getInstance();

            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mMessagesDatabaseReference = mFirebaseDatabase.getReference();

            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#FF6961"));
            }

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(final @NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() != null) {

                        mMessagesDatabaseReference.child("users").addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        for ( final DataSnapshot data : dataSnapshot.getChildren()) {
                                            if((firebaseAuth.getCurrentUser().getDisplayName()).equals(data.child("user_name").getValue())){
                                            }
                                            else {
                                                String key = firebaseAuth.getCurrentUser().getUid();
                                                //mMessagesDatabaseReference.child("users").push().getKey();

                                                final Users u = new Users();
                                                u.setUserName(firebaseAuth.getCurrentUser().getDisplayName());
                                                u.setUserEmail(firebaseAuth.getCurrentUser().getEmail());
                                                u.setUserPhotoUrl(firebaseAuth.getCurrentUser().getPhotoUrl().toString());

                                                Map<String, Object> childUpdates = new HashMap<>();
                                                childUpdates.put(key, u.toUsersFirebaseObject());
                                                mMessagesDatabaseReference.child("users").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        if (databaseError == null) {
                                                        }
                                                    }
                                                });

    //                                            String userKey = mMessagesDatabaseReference.push().getKey();
    //                                            mMessagesDatabaseReference.child(userKey).child("user_name").setValue(firebaseAuth.getCurrentUser().getDisplayName());
    //                                            mMessagesDatabaseReference.child(userKey).child("user_email").setValue(firebaseAuth.getCurrentUser().getEmail());
    //                                            mMessagesDatabaseReference.child(userKey).child("user_photo").setValue(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );
    //
    //
    //                    String userKey = mMessagesDatabaseReference.push().getKey();
    //                    mMessagesDatabaseReference.child(userKey).child("user_name").setValue(firebaseAuth.getCurrentUser().getDisplayName());
    //                    mMessagesDatabaseReference.child(userKey).child("user_email").setValue(firebaseAuth.getCurrentUser().getEmail());
    //                    mMessagesDatabaseReference.child(userKey).child("user_photo").setValue(firebaseAuth.getCurrentUser().getPhotoUrl().toString());


                        Intent intentToEventsActivity = new Intent(SignInFragment.this, MainActivity.class);
                        intentToEventsActivity.putExtra("userName", firebaseAuth.getCurrentUser().getDisplayName());
                        intentToEventsActivity.putExtra("userMail", firebaseAuth.getCurrentUser().getEmail());
                        intentToEventsActivity.putExtra("userImage", firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                        startActivity(intentToEventsActivity);

                    }
                }
            };

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Toast.makeText(SignInFragment.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

        }
    */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(getActivity(), "Authentication went wrong ", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            Log.d(TAG, "signInWithCredential:onComplete" + task.getException());
                            Toast.makeText(getActivity(), "Authentication wrong ", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
}
