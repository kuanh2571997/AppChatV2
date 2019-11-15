package appchat.anh.appchatv2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import appchat.anh.appchatv2.R;
import appchat.anh.appchatv2.model.Group;
import appchat.anh.appchatv2.model.Message;
import de.hdodenhof.circleimageview.CircleImageView;

public class AllChatAdapter extends RecyclerView.Adapter<AllChatAdapter.AllChatHolder> {

    private ArrayList<Group> mArrGroupChat = new ArrayList<>();
    private final HashMap<String, Message> mHashMapMessage = new HashMap<>();
    private Context mContext;
    private AllChatAdapterInterface mAllChatAdapterInterface;

    public interface AllChatAdapterInterface{
        void onItemClick(Group group);
    }

    public AllChatAdapter(ArrayList<Group> arrGroup, HashMap<String, Message> hashMapMessage , AllChatAdapterInterface allChatAdapterInterface) {
        mAllChatAdapterInterface = allChatAdapterInterface;
        if(arrGroup!=null){
            mArrGroupChat.addAll(arrGroup);
        }
        if(hashMapMessage!=null){
            mHashMapMessage.putAll(hashMapMessage);
        }
    }

    @NonNull
    @Override
    public AllChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_chat, parent, false);
        return new AllChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllChatHolder holder, int position) {
        holder.setData(mArrGroupChat.get(position));
    }

    @Override
    public int getItemCount() {
        return mArrGroupChat.size();

    }

    class AllChatHolder extends RecyclerView.ViewHolder {

        private CircleImageView mImageAvatar;
        private TextView mTxtNameGroup, mTxtContentMessage, mTxtTimeMessage;
        private View.OnClickListener mItemClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAllChatAdapterInterface.onItemClick(mArrGroupChat.get(getAdapterPosition()));
            }
        };

        AllChatHolder(@NonNull View itemView) {
            super(itemView);
            mImageAvatar = itemView.findViewById(R.id.imgAvatar);
            mTxtContentMessage = itemView.findViewById(R.id.txtContentMessage);
            mTxtTimeMessage = itemView.findViewById(R.id.txtTimeMessage);
            mTxtNameGroup = itemView.findViewById(R.id.txtGroupName);
            itemView.setOnClickListener(mItemClick);
        }

        private void setData(Group group) {
            if(mContext!=null){
                Glide.with(mContext).load(group.getGroupIcon()).placeholder(R.drawable.default_user).into(mImageAvatar);
            }
            mTxtNameGroup.setText(group.getName());
            if(mHashMapMessage.get(group.getId())!=null) {
                mTxtContentMessage.setText(mHashMapMessage.get(group.getId()).getMessage());
                mTxtTimeMessage.setText(convertTime((mHashMapMessage.get(group.getId()).getTime())));
            }
        }
    }

    public void addGroupChat(Group group) {
        boolean check = true;
        for(Group group1 : mArrGroupChat){
            if(group1.getId().equals(group.getId())){
                group1.setId(group.getId());
                check = false;
                break;
            }
        }
        if(check){
            mArrGroupChat.add(group);
        }
        notifyDataSetChanged();
    }

    public void updateRecentMessage(String idGroup, Message message) {
        if(message!=null) {
            mHashMapMessage.put(idGroup, message);
            mHashMapMessage.get(idGroup).setMessage(message.getMessage());
            mHashMapMessage.get(idGroup).setTime(message.getTime());
            if (message.getContentType().equals("image")) {
                mHashMapMessage.get(idGroup).setMessage("Hình ảnh");
            }
            notifyDataSetChanged();
        }
    }


    private String convertTime(long time){
        long differentTime = System.currentTimeMillis()/1000-time;
        if(differentTime>86400){
            Date date = new Date(time*1000);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM");
            String textTime = sdf.format(date);
            return textTime;
        }
        Date date = new Date(time*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.CHINA);
        String textTime = sdf.format(date);
        return textTime;
    }

}
