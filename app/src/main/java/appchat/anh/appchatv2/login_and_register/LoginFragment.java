package appchat.anh.appchatv2.login_and_register;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import java.util.Objects;

import appchat.anh.appchatv2.model.User;
import appchat.anh.appchatv2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private final int RC_SIGN_IN = 1;
    private EditText mEdtEmail, mEdtPassword;
    private Button mBtnLogin, mBtnRegister;
    private SignInButton mGoogleSignInButton;
    private LoginButton mBtnFacebookLogin;
    private ProgressBar mProgressBar;
    private DatabaseReference mDatabaseReference;
    private LoginFragmentInterface mLoginFragmentInterface;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    private static final String TAG = "LoginFragment";

    private View.OnClickListener mBtnLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            login();
        }
    };

    private View.OnClickListener mBtnLoginWithGoogleClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initGoogleSignIn();
            signInGoogle();
        }
    };

    private View.OnClickListener mBtnLoginFaceBookClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonClickLoginFacebook();
        }
    };

    private View.OnClickListener mBtnRegisterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mLoginFragmentInterface.onBtnRegisterClick();
        }
    };

    public interface LoginFragmentInterface {

        void onBtnRegisterClick();

        void onLoginSuccess(User user);

        void onLoginFall();
    }

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        AppEventsLogger.activateApp(getContext());
        mCallbackManager = CallbackManager.Factory.create();
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initView(view);
        initAction();
        initFireBase();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                Log.d(TAG, "onActivityResult:"+e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = task.getResult().getUser();
                            User user1 = new User();
                            user1.setId(user.getUid());
                            user1.setStatus("online");
                            user1.setProfilePic(String.valueOf(user.getPhotoUrl()));
                            user1.setFullName(user.getDisplayName());
                            saveUser(user1);
                            mLoginFragmentInterface.onLoginSuccess(user1);
                        }
                        else{
                            Toast.makeText(getContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void initView(View view) {
        mEdtEmail = view.findViewById(R.id.edtEmail);
        mEdtPassword = view.findViewById(R.id.edtPass);
        mBtnLogin = view.findViewById(R.id.btnLogin);
        mBtnRegister = view.findViewById(R.id.btnRegister);
        mProgressBar = view.findViewById(R.id.progressbar_login);
        mGoogleSignInButton = view.findViewById(R.id.sign_in_button);
        mGoogleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mBtnFacebookLogin = view.findViewById(R.id.login_button);
        mBtnFacebookLogin.setReadPermissions("email");
        mBtnFacebookLogin.setFragment(this);
    }

    private void initAction() {
        mBtnLogin.setOnClickListener(mBtnLoginClick);
        mBtnRegister.setOnClickListener(mBtnRegisterClick);
        mGoogleSignInButton.setOnClickListener(mBtnLoginWithGoogleClick);
        mBtnFacebookLogin.setOnClickListener(mBtnLoginFaceBookClick);
    }

    public void setInterface(LoginFragmentInterface loginFragmentInterface) {
        mLoginFragmentInterface = loginFragmentInterface;
    }

    private void initGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    private void signInGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void login() {
        mProgressBar.setVisibility(View.VISIBLE);
        String email, password;
        email = mEdtEmail.getText().toString();
        password = mEdtPassword.getText().toString();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(getContext(), getString(R.string.fragment_login_message_empty_edit_text), Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = task.getResult().getUser();
                        mDatabaseReference.child("Users").child(currentUser.getUid()).child("status").setValue("online");
                        User user = new User(currentUser.getDisplayName(), currentUser.getUid(), String.valueOf(currentUser.getPhotoUrl()), "online");
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mLoginFragmentInterface.onLoginSuccess(user);
                    } else {
                        mLoginFragmentInterface.onLoginFall();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    private void buttonClickLoginFacebook(){
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "LoginFacebook Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getContext(),  error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            User user = new User();
                            user.setId(firebaseUser.getUid());
                            user.setStatus("online");
                            user.setProfilePic(String.valueOf(firebaseUser.getPhotoUrl()));
                            user.setFullName(firebaseUser.getDisplayName());
                            saveUser(user);
                            mLoginFragmentInterface.onLoginSuccess(user);
                        }
                        else{
                            Toast.makeText(getContext(), "Login Facebook Fall!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUser(User user){
        mDatabaseReference.child("Users").child(user.getId()).setValue(user);
    }

}
