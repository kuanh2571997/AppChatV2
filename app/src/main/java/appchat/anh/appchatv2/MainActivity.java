package appchat.anh.appchatv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import appchat.anh.appchatv2.display_friend.ParentDisplayFriendFragment;
import appchat.anh.appchatv2.login_and_register.LoginFragment;
import appchat.anh.appchatv2.login_and_register.RegisterFragment;
import appchat.anh.appchatv2.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FragmentTransaction mFragmentTransaction;
    private FirebaseAuth mFireBaseAuth;
    private Toolbar mToolbar;
    private TextView mTxtCurrentUserName;
    private AppBarLayout mAppBarLayout;
    private CircleImageView mImgAvatar;
    private User mCurrentUser;
    private DatabaseReference mDatabaseReference;

    private LoginFragment.LoginFragmentInterface mLoginFragmentInterface = new LoginFragment.LoginFragmentInterface() {
        @Override
        public void onBtnRegisterClick() {
            initRegisterFragment();
        }

        @Override
        public void onLoginSuccess(User user) {
            checkUser();
            initParentDisplayFriendFragment(user);
        }

        @Override
        public void onLoginFall() {
            Toast.makeText(MainActivity.this, "Email or password is Wrong!", Toast.LENGTH_SHORT).show();
        }
    };

    private RegisterFragment.RegisterFragmentInterface mRegisterFragmentInterface = new RegisterFragment.RegisterFragmentInterface() {
        @Override
        public void registerSuccess() {
            checkUser();
            User user = getUser();
            if(user!=null){
                initParentDisplayFriendFragment(user);
            }
        }

        @Override
        public void actionCancel() {

        }

        @Override
        public void uploadImageFall() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initView();
        initFirebase();
        checkUser();
    }

    private void initView(){
        mToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mImgAvatar = findViewById(R.id.img_avatar);
        mAppBarLayout = findViewById(R.id.appbar_layout);
        mTxtCurrentUserName = findViewById(R.id.txt_current_user_name);
    }

    private void initFirebase(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void initLoginFragment(){
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setInterface(mLoginFragmentInterface);
        mFragmentTransaction.replace(R.id.frame, loginFragment);
        mFragmentTransaction.commit();
    }

    private void initRegisterFragment(){
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        RegisterFragment registerFragment = new RegisterFragment();
        registerFragment.setInterface(mRegisterFragmentInterface);
        mFragmentTransaction.replace(R.id.frame, registerFragment);
        mFragmentTransaction.commit();
    }

    private void initParentDisplayFriendFragment(User user){
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        ParentDisplayFriendFragment friendFragment = ParentDisplayFriendFragment.newInstance(user);
        mFragmentTransaction.replace(R.id.frame, friendFragment);
        mFragmentTransaction.commit();
    }

    private void checkUser(){
        mCurrentUser = getUser();
        if(mCurrentUser!=null){
            setToolbar(mCurrentUser);
            initParentDisplayFriendFragment(mCurrentUser);
            mDatabaseReference.child("Users").child(mCurrentUser.getId()).child("status").setValue("online");
        }
        else{
            initLoginFragment();
        }
    }

    private User getUser(){
        User user = new User();
        mFireBaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mFireBaseAuth.getCurrentUser();
        if(currentUser!=null){
            user.setId(currentUser.getUid());
            user.setFullName(currentUser.getDisplayName());
            user.setProfilePic(String.valueOf(currentUser.getPhotoUrl()));
            user.setStatus("online");
            return user;
        }

        return null;
    }

    private void setToolbar(User user){
        mAppBarLayout.setVisibility(View.VISIBLE);
        mTxtCurrentUserName.setText(user.getFullName());
        Glide.with(getApplicationContext()).load(user.getProfilePic()).into(mImgAvatar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.item_logout){
            actionSignOut();
            mAppBarLayout.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private void actionSignOut() {
        User user = getUser();
        if(user!=null){
            mDatabaseReference.child("Users").child(user.getId()).child("status").setValue("offline");
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            initLoginFragment();
        }
    }
}
