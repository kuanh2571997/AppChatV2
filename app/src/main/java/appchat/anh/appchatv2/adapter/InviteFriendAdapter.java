package appchat.anh.appchatv2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import appchat.anh.appchatv2.R;
import appchat.anh.appchatv2.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class InviteFriendAdapter extends RecyclerView.Adapter<InviteFriendAdapter.InviteFriendHolder> {

    private ArrayList<User> mArrUser;
    private Context mContext;
    private InviteFriendInterface mInviteFriendInterface;

    public InviteFriendAdapter(ArrayList<User> arrUser, InviteFriendInterface inviteFriendInterface) {
        mArrUser = arrUser;
        mInviteFriendInterface = inviteFriendInterface;
    }

    public interface InviteFriendInterface {
        void onBtnAcceptClick(User user);

        void onBtnRefuseClick(User user);
    }

    @NonNull
    @Override
    public InviteFriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext != null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accept_friend, parent, false);
        return new InviteFriendHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteFriendHolder holder, int position) {
        User user = mArrUser.get(position);
        holder.setDataView(user);
    }

    @Override
    public int getItemCount() {
        return mArrUser.size();
    }

    public void addInviteFriend(User user){
        mArrUser.add(user);
        notifyDataSetChanged();
    }

    public void resetArray(){
        mArrUser.clear();
    }

    class InviteFriendHolder extends RecyclerView.ViewHolder {

        private CircleImageView mImageAvatar;
        private TextView mTextNameFriend;
        private ImageView mImageAccept, mImageRefuse;

        View.OnClickListener mBtnAcceptClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInviteFriendInterface.onBtnAcceptClick(mArrUser.get(getAdapterPosition()));
                mArrUser.remove(getAdapterPosition());
                notifyDataSetChanged();
            }
        };

        View.OnClickListener mBtnRefuseClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInviteFriendInterface.onBtnRefuseClick(mArrUser.get(getAdapterPosition()));
                mArrUser.remove(getAdapterPosition());
                notifyDataSetChanged();
            }
        };

        InviteFriendHolder(@NonNull View itemView) {
            super(itemView);
            mImageAvatar = itemView.findViewById(R.id.image_friend);
            mTextNameFriend = itemView.findViewById(R.id.text_view_name_user);
            mImageAccept = itemView.findViewById(R.id.btn_accept_friend);
            mImageRefuse = itemView.findViewById(R.id.btn_refuse_friend);

            mImageAccept.setOnClickListener(mBtnAcceptClick);
            mImageRefuse.setOnClickListener(mBtnRefuseClick);
        }

        void setDataView(User user) {
            if (mContext != null) {
                Glide.with(mContext).load(user.getProfilePic()).into(mImageAvatar);
            }
            mTextNameFriend.setText(user.getFullName());
        }

    }

}
