package com.xaqinren.healthyelders.apiserver;

import com.xaqinren.healthyelders.http.RetrofitClient;

/**
 * =====================================================
 * 描    述: 消息通知网络请求仓库
 * =====================================================
 */
public class MsgRepository {

    private static MsgRepository instance = new MsgRepository();

    private MsgRepository() {
    }
    public static MsgRepository getInstance() {
        if (instance == null) {
            instance = new MsgRepository();
        }
        return instance;
    }

    private ApiServer userApi = RetrofitClient.getInstance().create(ApiServer.class);


}
