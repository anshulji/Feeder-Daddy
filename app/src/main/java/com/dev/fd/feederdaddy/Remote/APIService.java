package com.dev.fd.feederdaddy.Remote;


import com.dev.fd.feederdaddy.model.MyResponse;
import com.dev.fd.feederdaddy.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAvsfoR7w:APA91bHbLseeMWeM1mQ20BlkaBhf5Z3_0BF1FxQSBLZl4e24whq321plpE24s67pGefRwKyKCLuJoiUrFXHIXE1M7DwCNKpL7uVCXsslD1dtwUULHAoj6I7mL8z8aalMp3La1y6cMfvY"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
