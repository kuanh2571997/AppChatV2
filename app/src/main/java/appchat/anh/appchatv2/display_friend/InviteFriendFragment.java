package appchat.anh.appchatv2.display_friend;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import appchat.anh.appchatv2.R;
import appchat.anh.appchatv2.adapter.InviteFriendAdapter;
import appchat.anh.appchatv2.common.Contacts;
import appchat.anh.appchatv2.model.Message;
import appchat.anh.appchatv2.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFriendFragment extends Fragment {

    private RecyclerView mRecyclerViewInviteFriend;

    private DatabaseReference mDatabaseReference;
    private String mCurrentUserId;
    private ArrayList<User> mArrUserInvite = new ArrayList<>();
    private InviteFriendAdapter mInviteAdapter;

    private InviteFriendAdapter.InviteFriendInterface mInviteFriendInterface = new InviteFriendAdapter.InviteFriendInterface() {
        @Override
        public void onBtnAcceptClick(User user) {
            mDatabaseReference.child("FriendGroups").child(mCurrentUserId).child("Friends").child(user.getId()).child("userId").setValue(user.getId());
            mDatabaseReference.child("FriendGroups").child(user.getId()).child("Friends").child(mCurrentUserId).child("userId").setValue(mCurrentUserId);
            deleteInviteFriend(user);
            addChat(user);
        }

        @Override
        public void onBtnRefuseClick(User user) {
            deleteInviteFriend(user);
        }
    };

    public InviteFriendFragment() {
        // Required empty public constructor
    }

    static InviteFriendFragment newInstance(String currentUserId) {
        InviteFriendFragment inviteFriendFragment = new InviteFriendFragment();
        Bundle args = new Bundle();
        args.putString(Contacts.KEY_CURRENT_ID, currentUserId);
        inviteFriendFragment.setArguments(args);
        return inviteFriendFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurrentUserId = bundle.getString(Contacts.KEY_CURRENT_ID);
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_friend, container, false);
        initView(view);
        initRecyclerView();
        updateRecyclerView();
        return view;
    }

    private void initView(View view) {
        mRecyclerViewInviteFriend = view.findViewById(R.id.recycler_view_accept_friend);
    }

    private void initRecyclerView() {
        mInviteAdapter = new InviteFriendAdapter(mArrUserInvite, mInviteFriendInterface);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewInviteFriend.setAdapter(mInviteAdapter);
        mRecyclerViewInviteFriend.setLayoutManager(layoutManager);
    }

    private void updateRecyclerView() {
        mDatabaseReference.child("FriendGroups")
                .child(mCurrentUserId).child("AddFriends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mInviteAdapter.resetArray();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            addFriend(snapshot.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void addFriend(String userId) {
        mDatabaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mInviteAdapter.addInviteFriend(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteInviteFriend(User user) {
        mDatabaseReference.child("FriendGroups").child(mCurrentUserId).child("AddFriends").child(user.getId()).removeValue();
    }

    private void addChat(User user){
        String key;
        Message message = new Message("text", mCurrentUserId, "", "Vẫy tay!!!", System.currentTimeMillis()/1000);
        key = mDatabaseReference.child("Chats").push().getKey();
        mDatabaseReference.child("Chats").child(key).push().setValue(message);
        mDatabaseReference.child("FriendGroups").child(mCurrentUserId).child("Chats").child(user.getId()).setValue(key);
        mDatabaseReference.child("FriendGroups").child(user.getId()).child("Chats").child(mCurrentUserId).setValue(key);
        mDatabaseReference.child("RecentMessage").child(key).setValue(message);
    }

}
