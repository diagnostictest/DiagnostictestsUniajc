package com.tecnologiajo.diagnostictestsuniajc;


import com.tecnologiajo.diagnostictestsuniajc.modelos.Result;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {

    @POST("send")
    Call<Result> sednAnswer(@Body Result body);
}
