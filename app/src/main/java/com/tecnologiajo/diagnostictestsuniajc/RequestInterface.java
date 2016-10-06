package com.tecnologiajo.diagnostictestsuniajc;



import com.tecnologiajo.diagnostictestsuniajc.modelos.RequestResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {

    @POST("send")
    Call<RequestResult> sendAnswer(@Body RequestResult body);
}
