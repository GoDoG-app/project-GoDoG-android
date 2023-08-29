package com.blue.walking.model;

import okhttp3.MultipartBody;

public class Post {

    public int post_userid;
    public int post_id;
    public String user_nickname;
    public String user_profile_image;
    public String user_region;
    public String postContent;
    public String postImgUrl;
    public int isLike;
    public String category;
    public int post_likes_count;
    public String createdAt;
    public String updatedAt;

    public Post(){}

    public Post(String postContent, String category){
        this.postContent = postContent;
        this.category = category;
    }

}
