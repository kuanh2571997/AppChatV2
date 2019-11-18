package appchat.anh.appchatv2.chat;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import appchat.anh.appchatv2.R;
import appchat.anh.appchatv2.adapter.DetailChatAdapter;
import appchat.anh.appchatv2.common.Contacts;
import appchat.anh.appchatv2.model.Group;
import appchat.anh.appchatv2.model.Message;
import appchat.anh.appchatv2.notifications.APIService;
import appchat.anh.appchatv2.notifications.Client;
import appchat.anh.appchatv2.notifications.Data;
import appchat.anh.appchatv2.notifications.Response;
import appchat.anh.appchatv2.notifications.Sender;
import appchat.anh.appchatv2.notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailChatFragment extends Fragment {

    private final int CAMERA_CODE_IMG = 1, GALLERY_CODE_IMG = 2;
    private RecyclerView mRecyclerViewDetailChat;
    private DatabaseReference mData;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;
    private ImageButton mBtnSendMess, mBtnCallCamera, mBtnCallLibrary;
    private EditText mEditMess;
    private DetailChatAdapter mDetailChatAdapter;
    private ArrayList<Message> mArrMess = new ArrayList<>();
    private Group mGroup;
    private StorageReference mStoreReference;
    private FirebaseStorage mFireBaseStorage;
    private String mSendTo;

    APIService apiService;
    boolean notify = false;

    private View.OnClickListener mBtnSendClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            notify=true;
            String mess = mEditMess.getText().toString();
            mEditMess.setText("");
            Message inputMess = new Message("text", "", "", mess, System.currentTimeMillis() / 1000);
            if(!mess.trim().equals("")){
                upLoadMessage(inputMess);
            }
            mRecyclerViewDetailChat.smoothScrollToPosition(mDetailChatAdapter.getItemCount() - 1);
        }
    };

    private View.OnClickListener mBtnCallCameraClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, CAMERA_CODE_IMG);
        }
    };

    private View.OnClickListener mBtnCallLibraryClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, GALLERY_CODE_IMG);
        }
    };

    public DetailChatFragment() {
        // Required empty public constructor
    }

    static DetailChatFragment newInstance(Group group) {
        DetailChatFragment detailChatFragment = new DetailChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Contacts.KEY_GROUP, group);
        detailChatFragment.setArguments(bundle);
        return detailChatFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_chat, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mGroup = bundle.getParcelable(Contacts.KEY_GROUP);
        }

        initView(view);
        initAction();
        initFirebase();
        initRecyclerView();
        getDataFirebase();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CODE_IMG && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            upLoadImage(bitmap, mUser.getUid() + System.currentTimeMillis() / 1000);
        } else if (requestCode == GALLERY_CODE_IMG && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), uri);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                upLoadImage(bitmap, mUser.getUid() + System.currentTimeMillis() / 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "No Image", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initRecyclerView() {
        mDetailChatAdapter = new DetailChatAdapter(mArrMess, mUser.getUid());
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewDetailChat.setLayoutManager(layoutManager);
        mRecyclerViewDetailChat.setAdapter(mDetailChatAdapter);

        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

    }


    private void initView(View view) {
        mRecyclerViewDetailChat = view.findViewById(R.id.recycler_view_detail_chat);
        mBtnSendMess = view.findViewById(R.id.image_button_send);
        mEditMess = view.findViewById(R.id.edit_text_input_message);
        mBtnCallCamera = view.findViewById(R.id.image_button_call_camera);
        mBtnCallLibrary = view.findViewById(R.id.image_button_call_library);
    }

    private void initFirebase() {
        mData = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();
        mFireBaseStorage = FirebaseStorage.getInstance();
        mStoreReference = mFireBaseStorage.getReference();

    }

    private void getDataFirebase() {
        mData.child("Chats").child(mGroup.getId()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDetailChatAdapter.clearMess();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    mDetailChatAdapter.addMessage(message);
                }
                mRecyclerViewDetailChat.smoothScrollToPosition(mDetailChatAdapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("Notification").child(mGroup.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String id = (String) ds.getValue();
                    if(id!=null && !id.equals(mUser.getUid())){
                        mSendTo=id;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initAction() {
        mBtnSendMess.setOnClickListener(mBtnSendClick);
        mBtnCallLibrary.setOnClickListener(mBtnCallLibraryClick);
        mBtnCallCamera.setOnClickListener(mBtnCallCameraClick);
    }

    private void upLoadMessage(String message) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Message mess = new Message("text", user.getUid(), "", message, System.currentTimeMillis() / 1000);
        databaseReference.child("Chats").child(mGroup.getId()).push().setValue(mess);
        databaseReference.child("RecentMessage").child(mGroup.getId()).setValue(mess);
    }

    private void upLoadMessage(Message message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        message.setFromId(mUser.getUid());
        databaseReference.child("Chats").child(mGroup.getId()).push().setValue(message);
        databaseReference.child("RecentMessage").child(mGroup.getId()).setValue(message);

        if(notify){
            sendNotification(mUser.getUid(), mUser.getDisplayName(), message.getMessage());
        }
        notify=false;
    }

    private void sendNotification(final String uid, final String displayName, final String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(mSendTo);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(uid, displayName+":"+message,"Tin nhắn mới", uid, mGroup.getId(), R.drawable.newmess);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(getActivity(), ""+response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void upLoadImage(Bitmap bitmap, String imgName) {
        final StorageReference storageReference = mStoreReference.child(imgName + ".png");
//        mImgAvatar.setDrawingCacheEnabled(true);
//        mImgAvatar.buildDrawingCache();
        //Bitmap bitmap = ((BitmapDrawable)mImgAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        final byte[] data = outputStream.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Message mess = new Message("image", "", uri.toString(), "", System.currentTimeMillis() / 1000);
                        upLoadMessage(mess);
                    }
                });
            }
        });
    }


}
