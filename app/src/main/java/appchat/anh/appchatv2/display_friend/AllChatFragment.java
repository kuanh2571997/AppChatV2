package appchat.anh.appchatv2.display_friend;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import appchat.anh.appchatv2.R;
import appchat.anh.appchatv2.adapter.AllChatAdapter;
import appchat.anh.appchatv2.chat.DetailChatActivity;
import appchat.anh.appchatv2.common.Contacts;
import appchat.anh.appchatv2.model.Group;
import appchat.anh.appchatv2.model.Message;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllChatFragment extends Fragment {

    private DatabaseReference mDatabaseReference;
    private String mCurrentUserId;
    private RecyclerView mRecyclerViewAllChat;

    private AllChatAdapter.AllChatAdapterInterface mAllChatAdapterInterface = new AllChatAdapter.AllChatAdapterInterface() {
        @Override
        public void onItemClick(Group group) {
            Intent intent = new Intent(getActivity(), DetailChatActivity.class);
            intent.putExtra(Contacts.KEY_GROUP, group);
            startActivity(intent);
        }
    };

    private AllChatAdapter mAllChatAdapter = new AllChatAdapter(new ArrayList<Group>(), new HashMap<String, Message>(), mAllChatAdapterInterface);

    public AllChatFragment() {
        // Required empty public constructor
    }

    public static AllChatFragment newInstance(String currentUserId) {
        AllChatFragment allChatFragment = new AllChatFragment();
        Bundle args = new Bundle();
        args.putString(Contacts.KEY_CURRENT_ID, currentUserId);
        allChatFragment.setArguments(args);
        return allChatFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mCurrentUserId = bundle.getString(Contacts.KEY_CURRENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_chat, container, false);
        initView(view);
        initRecyclerView();
        initData();
        return view;
    }

    private void initView(View view) {
        mRecyclerViewAllChat = view.findViewById(R.id.recycler_view_all_chat);
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewAllChat.setLayoutManager(layoutManager);
        mRecyclerViewAllChat.setAdapter(mAllChatAdapter);
    }

    private void initData() {
//        if(mCurrentUserId!=null){
//            mDatabaseReference.child("FriendGroups").child(mCurrentUserId).child("Chats").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        final String idChat = (String) snapshot.getValue();
//                        String idFriend = snapshot.getKey();
//                        if(idFriend!=null){
//                            mDatabaseReference.child("Users").child(idFriend).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    User user = dataSnapshot.getValue(User.class);
//                                    Group group = new Group();
//                                    group.setId(idChat);
//                                    group.setGroupIcon(user.getProfilePic());
//                                    group.setName(user.getFullName());
//                                    mAllChatAdapter.addGroupChat(group);
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//
//                        if(idChat!=null){
//                            mDatabaseReference.child("RecentMessage").child(idChat).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    Message message = dataSnapshot.getValue(Message.class);
//                                    mAllChatAdapter.updateRecentMessage(idChat, message);
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
        if (mCurrentUserId != null) {
            mDatabaseReference.child("FriendGroups").child(mCurrentUserId).child("Groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final Group group = snapshot.getValue(Group.class);
                        mAllChatAdapter.addGroupChat(group);
                        mDatabaseReference.child("RecentMessage").child(group.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Message message = dataSnapshot.getValue(Message.class);
                                mAllChatAdapter.updateRecentMessage(group.getId(), message);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
