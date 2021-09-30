package com.changanford.home.base.response;

public class UpdateUiState<T> {

    public T data;
    public boolean isSuccess;
    public String message; // 请求返回的信息
    public boolean isLoadMore=true;
    public int isTomorrow=0;

    public UpdateUiState(T data, boolean isSuccess, String message) {
        this.data = data;
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public UpdateUiState(T data, boolean isSuccess, boolean isNeedPost, String message) {
        this.data = data;
        this.isSuccess = isSuccess;
        this.isLoadMore = isNeedPost;
        this.message=message;
    }


    public UpdateUiState(T data, boolean isSuccess, int isTomorrow, String message) {
        this.data = data;
        this.isSuccess = isSuccess;
        this.isTomorrow = isTomorrow;
        this.message=message;
    }

    public UpdateUiState(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
    public UpdateUiState(boolean isSuccess, String message, boolean isLoadMore) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.isLoadMore=isLoadMore;
    }
}
