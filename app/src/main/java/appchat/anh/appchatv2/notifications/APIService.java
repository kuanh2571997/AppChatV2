package appchat.anh.appchatv2.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAv3kLq8E:APA91bHWV5XOg0BeoThQSUOEK5So-o4jLXkPyGGeLYOMJeg5c7QTC2JOzHX15aWti7eotmCHhIwPo2MKllBAPn4B2h-EVtt2V7L85ucIxRh-LKt5NE6zBsUkA2iPgGKVnkoMPt8GZzTd"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}
