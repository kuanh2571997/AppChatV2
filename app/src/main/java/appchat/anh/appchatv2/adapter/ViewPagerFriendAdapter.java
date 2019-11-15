package appchat.anh.appchatv2.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ViewPagerFriendAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mArrFragment = new ArrayList<>();

    public ViewPagerFriendAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mArrFragment.get(position);
    }

    @Override
    public int getCount() {
        return mArrFragment.size();
    }

    public void addFragment(Fragment fragment){
        if(fragment!=null){
            mArrFragment.add(fragment);
        }
    }

}
