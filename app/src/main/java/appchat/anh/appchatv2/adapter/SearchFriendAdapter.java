package appchat.anh.appchatv2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import appchat.anh.appchatv2.R;
import appchat.anh.appchatv2.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.SearchFriendHolder> {

    private ArrayList<User> mArrUser;
    private Context mContext;
    private SearchFriendAdapterInterface mSearchFriendInterface;

    public interface SearchFriendAdapterInterface{
        void onInviteFriendClick(User user);
    }

    public SearchFriendAdapter(ArrayList<User> arrUser, SearchFriendAdapterInterface searchFriendAdapterInterface) {
        mArrUser = arrUser;
        mSearchFriendInterface = searchFriendAdapterInterface;
    }

    @NonNull
    @Override
    public SearchFriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_friend, parent, false);
        return new SearchFriendHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFriendHolder holder, int position) {
        holder.setDataView(mArrUser.get(position));
    }

    @Override
    public int getItemCount() {
        return mArrUser.size();
    }

    public void addUser(ArrayList<User> arrUser){
        mArrUser.clear();
        mArrUser.addAll(arrUser);
        notifyDataSetChanged();
    }

    class SearchFriendHolder extends RecyclerView.ViewHolder {

        private CircleImageView mImgAvatar;
        private TextView mTextNameFriend;
        private ImageButton mBtnAddFriend;

        private View.OnClickListener mBtnAddFriendClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchFriendInterface.onInviteFriendClick(mArrUser.get(getAdapterPosition()));
                mBtnAddFriend.setVisibility(View.INVISIBLE);
            }
        };

        SearchFriendHolder(@NonNull View itemView) {
            super(itemView);
            mImgAvatar = itemView.findViewById(R.id.image_friend);
            mTextNameFriend = itemView.findViewById(R.id.text_view_name_user);
            mBtnAddFriend = itemView.findViewById(R.id.btn_add_friend);
            mBtnAddFriend.setOnClickListener(mBtnAddFriendClick);

        }

        void setDataView(User user){
            Glide.with(mContext).load(user.getProfilePic()).into(mImgAvatar);
            mTextNameFriend.setText(user.getFullName());
        }
    }

}
