package com.blue.walking.config;

public class Config {

    public static final String PREFERENCE_NAME = "walking";

    public static final String ACCESS_TOKEN = "accessToken";

    public static final String HOST = "https://vjh2nj92xk.execute-api.ap-northeast-2.amazonaws.com";

    // static final을 사용하여 상수로 지정.
    // 상수인 경우엔 변수 전체를 대문자로 지정 하는 것이 관례
    private static final String TMAP_APP_KEY = "liqQZ984u82Ezz0H5CfzT2TLEQe1gOIN5fjhEQco";
    // AppKey가 private이므로 gettter 생성
    public static String getAppKey(){
        return TMAP_APP_KEY;
    }
}
