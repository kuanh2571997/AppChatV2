package appchat.anh.appchatv2.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import appchat.anh.appchatv2.chat.DetailChatActivity;
import appchat.anh.appchatv2.common.Contacts;
import appchat.anh.appchatv2.model.Group;

public class FirebaseMessaging extends FirebaseMessagingService {

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser mUser;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //get current user from shared preferences
        SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
        String savedCurrentUser = sp.getString("Current_USERID", "None");

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        final String idGroup = remoteMessage.getData().get("idGroup");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null && !savedCurrentUser.equals(user)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendOAndAboveNotification(remoteMessage);
            } else {
                sendNormalNotification(remoteMessage);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNormalNotification(final RemoteMessage remoteMessage) {
        final String user = remoteMessage.getData().get("user");
        final String icon = remoteMessage.getData().get("icon");
        final String title = remoteMessage.getData().get("title");
        final String body = remoteMessage.getData().get("body");
        String idGroup = remoteMessage.getData().get("idGroup");
        mDatabaseReference.child("FriendGroups").child(mUser.getUid()).child("Groups").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                RemoteMessage.Notification notification = remoteMessage.getNotification();
                int i = Integer.parseInt(user.replaceAll("[\\D]", ""));

                //call Activity;
                Intent intent = new Intent(getApplicationContext(), DetailChatActivity.class);
                intent.putExtra(Contacts.KEY_GROUP, group);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), i, intent, PendingIntent.FLAG_ONE_SHOT);

                Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(Integer.parseInt(icon))
                        .setContentText(body)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setSound(defSoundUri)
                        .setContentIntent(pIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int j = 0;
                if (i > 0) {
                    j = i;
                }
                notificationManager.notify(j, builder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotification(final RemoteMessage remoteMessage) {

        final String user = remoteMessage.getData().get("user");
        final String icon = remoteMessage.getData().get("icon");
        final String title = remoteMessage.getData().get("title");
        final String body = remoteMessage.getData().get("body");
        String idGroup = remoteMessage.getData().get("idGroup");
        mDatabaseReference.child("FriendGroups").child(mUser.getUid()).child("Groups").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                RemoteMessage.Notification notification = remoteMessage.getNotification();
                int i = Integer.parseInt(user.replaceAll("[\\D]", ""));

                //call Activity;
                Intent intent = new Intent(getApplicationContext(), DetailChatActivity.class);
                intent.putExtra(Contacts.KEY_GROUP, group);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), i, intent, PendingIntent.FLAG_ONE_SHOT);

                Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                OreoAndAboveNotification notification1 = new OreoAndAboveNotification(getApplicationContext());
                Notification.Builder builder = notification1.getONotifications(title, body, pIntent, defSoundUri, icon);

                int j = 0;
                if (i > 0) {
                    j = i;
                }
                notification1.getManager().notify(j, builder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
