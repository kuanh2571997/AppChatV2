package appchat.anh.appchatv2.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

import appchat.anh.appchatv2.R;
import appchat.anh.appchatv2.model.Message;
import de.hdodenhof.circleimageview.CircleImageView;

public class DetailChatAdapter extends RecyclerView.Adapter<DetailChatAdapter.ViewHolder> {

    private ArrayList<Message> mArrMess;
    private String mIdCurrentUser;
    private DatabaseReference mDatabaseReference;
    private Context mContext;

    public DetailChatAdapter(ArrayList<Message> arrMess, String idCurrentUser) {
        mArrMess = arrMess;
        mIdCurrentUser = idCurrentUser;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public int getItemViewType(int position) {
        if (mArrMess.get(position).getFromId().equals(mIdCurrentUser)) {
            return 1;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        switch (viewType) {
            case 0:
                View viewReceive = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receive_message, parent, false);
                return new ViewHolderReceivedMessage(viewReceive);

            case 1:
                View viewSend = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send_message, parent, false);
                return new ViewHolderSendMessage(viewSend);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindViewHolder(mArrMess.get(position));
    }

    @Override
    public int getItemCount() {
        return mArrMess.size();
    }

    class ViewHolderReceivedMessage extends ViewHolder {
        private TextView mTxtReceiveMessage;
        private CircleImageView mImgAvatar;
        private ImageView mImgReceive;

        public void bindViewHolder(Message message) {
            mDatabaseReference.child(message.getFromId()).child("profilePic").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Glide.with(mContext).load(dataSnapshot.getValue()).into(mImgAvatar);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if(message.getContentType().equals("image")){
                mImgReceive.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(message.getLinkImg()).into(mImgReceive);
                mTxtReceiveMessage.setVisibility(View.GONE);
            }
            else{
                mTxtReceiveMessage.setText(message.getMessage());
                mImgReceive.setVisibility(View.GONE);
            }

        }

        ViewHolderReceivedMessage(@NonNull View itemView) {
            super(itemView);
            mTxtReceiveMessage = itemView.findViewById(R.id.text_view_receive_message);
            mImgAvatar = itemView.findViewById(R.id.image_avatar_receive_message);
            mImgReceive = itemView.findViewById(R.id.image_view_receive_image);
        }
    }

    class ViewHolderSendMessage extends ViewHolder {
        private TextView mTxtSendMessage;
        private ImageView mImgSend;


        public void bindViewHolder(Message message) {

            if(message.getContentType().equals("image")){
                mImgSend.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(message.getLinkImg()).into(mImgSend);
                mTxtSendMessage.setVisibility(View.GONE);
            }
            else{
                mTxtSendMessage.setText(message.getMessage());
                mImgSend.setVisibility(View.GONE);
            }
        }

        ViewHolderSendMessage(@NonNull View itemView) {
            super(itemView);
            mTxtSendMessage = itemView.findViewById(R.id.text_view_send_message);
            mImgSend = itemView.findViewById(R.id.image_view_send_image);
        }

    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {
        abstract void bindViewHolder(Message message);
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void clearMess(){
        mArrMess.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addMessage(Message message) {
        mArrMess.add(message);
        mArrMess.sort(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                if(o1.getTime()>o2.getTime()){
                    return 1;
                }
                return 0;
            }
        });
        notifyDataSetChanged();
    }

}
