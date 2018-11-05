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
                    "Authorization:key=AAAAywlN1wQ:APA91bG2SeC95RIw1s5svy2DF0z20rwzL-TdLB-Xv4p3HPtEgn0fD37cMd14y9NuNOTDtnXqZCo9anYwRGnnVdz4BBVzlYA-MdrNJi-ZEDG_O1E_BUD10Dpmrchfl7SjPmYfnDWMEpiRhj13Ua8PR_DbAFLzRoIO4A"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
