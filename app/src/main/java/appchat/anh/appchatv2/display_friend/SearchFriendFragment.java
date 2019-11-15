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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import appchat.anh.appchatv2.R;
import appchat.anh.appchatv2.adapter.SearchFriendAdapter;
import appchat.anh.appchatv2.common.Contacts;
import appchat.anh.appchatv2.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFriendFragment extends Fragment {

    private String mCurrentUserId;
    private EditText mEditNameFriend;
    private ImageButton mImgBtnSearch;
    private RecyclerView mRecyclerView;
    private ArrayList<User> mArrFriend = new ArrayList<>();
    private DatabaseReference mDatabaseReference;
    private SearchFriendAdapter mSearchFriendAdapter;

    private View.OnClickListener mImgBtnSearchClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String nameUser = mEditNameFriend.getText().toString().trim();
            searchFriend(nameUser);
        }
    };

    private SearchFriendAdapter.SearchFriendAdapterInterface mSearchFriendAdapterInterface = new SearchFriendAdapter.SearchFriendAdapterInterface() {
        @Override
        public void onInviteFriendClick(final User user) {
            final User friendUser = user;
            if(mCurrentUserId!=null){
                mDatabaseReference.child("FriendGroups")
                        .child(mCurrentUserId)
                        .child("Friends")
                        .orderByChild("userId")
                        .startAt(user.getId())
                        .endAt(friendUser.getId()+"\uf88f")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            Toast.makeText(getContext(), "invited", Toast.LENGTH_SHORT).show();
                            mDatabaseReference.child("FriendGroups")
                                    .child(friendUser.getId())
                                    .child("AddFriends")
                                    .child(mCurrentUserId)
                                    .child("userId")
                                    .setValue(mCurrentUserId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    };

    public SearchFriendFragment() {
        // Required empty public constructor
    }

    static SearchFriendFragment newInstance(String currentUserId){
        SearchFriendFragment searchFriendFragment = new SearchFriendFragment();
        Bundle args = new Bundle();
        args.putString(Contacts.KEY_CURRENT_ID, currentUserId);
        searchFriendFragment.setArguments(args);
        return searchFriendFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference();
        Bundle bundle = getArguments();
        if(bundle!=null){
            mCurrentUserId = bundle.getString(Contacts.KEY_CURRENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_friend, container, false);
        initView(view);
        initAction();
        initRecyclerView();
        return view;
    }

    private void initView(View view){
        mEditNameFriend = view.findViewById(R.id.edit_text_name_friend);
        mImgBtnSearch = view.findViewById(R.id.image_button_search);
        mRecyclerView = view.findViewById(R.id.recycler_view);
    }

    private void initAction(){
        mImgBtnSearch.setOnClickListener(mImgBtnSearchClick);
    }

    private void initRecyclerView(){
        mSearchFriendAdapter =  new SearchFriendAdapter(mArrFriend, mSearchFriendAdapterInterface);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mSearchFriendAdapter);
    }

    private void searchFriend(String userName){
        mDatabaseReference.child("Users")
                .orderByChild("fullName")
                .startAt(userName)
                .endAt(userName+"\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<User> arrUser = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    arrUser.add(user);
                }

                if(arrUser.size()>0){
                    mSearchFriendAdapter.addUser(arrUser);
                }
                else{
                    Toast.makeText(getContext(), "Không tìm thấy bạn bè", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
