package appchat.anh.appchatv2.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import appchat.anh.appchatv2.R;
import appchat.anh.appchatv2.common.Contacts;
import appchat.anh.appchatv2.model.Group;
import appchat.anh.appchatv2.model.Message;
import de.hdodenhof.circleimageview.CircleImageView;

public class DetailChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView mTxtCurrentUserName;
    private AppBarLayout mAppBarLayout;
    private CircleImageView mImgAvatar;
    private Group mCurrentGroup;
    private FragmentTransaction mFragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_chat);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initView();
        getData();
        setToolbar(mCurrentGroup);
        initDetailChatFragment();
        //initFragmentInputMess();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            mCurrentGroup = intent.getParcelableExtra(Contacts.KEY_GROUP);
        }
    }

    private void initView() {
        mToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mImgAvatar = findViewById(R.id.img_avatar);
        mAppBarLayout = findViewById(R.id.appbar_layout);
        mTxtCurrentUserName = findViewById(R.id.txt_current_user_name);
    }

    private void setToolbar(Group group) {
        mAppBarLayout.setVisibility(View.VISIBLE);
        mTxtCurrentUserName.setText(group.getName());
        Glide.with(getApplicationContext()).load(group.getGroupIcon()).into(mImgAvatar);
    }

    private void initDetailChatFragment() {
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        DetailChatFragment detailChatFragment = DetailChatFragment.newInstance(mCurrentGroup);
        mFragmentTransaction.replace(R.id.frame, detailChatFragment);
        mFragmentTransaction.commit();
    }

    private void upLoadMessage(String message) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Message mess = new Message("text", user.getUid(), "", message, System.currentTimeMillis() / 1000);
        databaseReference.child("Chats").child(mCurrentGroup.getId()).push().setValue(mess);
        databaseReference.child("RecentMessage").child(mCurrentGroup.getId()).setValue(mess);
    }

}
