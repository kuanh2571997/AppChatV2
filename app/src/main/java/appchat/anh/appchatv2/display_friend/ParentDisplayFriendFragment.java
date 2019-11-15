package appchat.anh.appchatv2.display_friend;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import appchat.anh.appchatv2.R;
import appchat.anh.appchatv2.adapter.ViewPagerFriendAdapter;
import appchat.anh.appchatv2.common.Contacts;
import appchat.anh.appchatv2.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentDisplayFriendFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private User mCurrentUser;

    public ParentDisplayFriendFragment() {
        // Required empty public constructor
    }

    public static ParentDisplayFriendFragment newInstance(User currentUser) {
        ParentDisplayFriendFragment parentDisplayFriendFragment = new ParentDisplayFriendFragment();
        Bundle args = new Bundle();
        args.putParcelable(Contacts.KEY_CURRENT_USER, currentUser);
        parentDisplayFriendFragment.setArguments(args);
        return parentDisplayFriendFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = getArguments().getParcelable(Contacts.KEY_CURRENT_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_display_friend, container, false);
        initView(view);
        initViewPager();
        return view;
    }

    private void initView(View view) {
        mViewPager = view.findViewById(R.id.viewPager);
        mTabLayout = view.findViewById(R.id.tabLayout);
    }

    private void initViewPager() {
        ViewPagerFriendAdapter viewPagerFriendAdapter = new ViewPagerFriendAdapter(getFragmentManager());

        AllChatFragment allChatFragment = AllChatFragment.newInstance(mCurrentUser.getId());
        viewPagerFriendAdapter.addFragment(allChatFragment);

        SearchFriendFragment searchFriendFragment = SearchFriendFragment.newInstance(mCurrentUser.getId());
        viewPagerFriendAdapter.addFragment(searchFriendFragment);

        InviteFriendFragment inviteFriendFragment = InviteFriendFragment.newInstance(mCurrentUser.getId());
        viewPagerFriendAdapter.addFragment(inviteFriendFragment);
        mViewPager.setAdapter(viewPagerFriendAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.group);
        mTabLayout.getTabAt(1).setIcon(R.drawable.search_bottom);
        mTabLayout.getTabAt(2).setIcon(R.drawable.user);
    }

}
