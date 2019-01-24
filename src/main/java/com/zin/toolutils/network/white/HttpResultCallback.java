package com.zin.toolutils.network.white;

public interface HttpResultCallback<T> {


    void onSuccess(T result);

    void onFailure(int code, String message);

}
