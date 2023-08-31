package com.blue.walking.model;

import java.io.Serializable;

import okhttp3.MultipartBody;

public class Post implements Serializable {

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

    public Post(int post_userid, int post_id, String user_nickname, String user_profile_image,
                String user_region, String postContent, String postImgUrl, int isLike,
                String category, int post_likes_count, String createdAt, String updatedAt) {
        this.post_userid = post_userid;
        this.post_id = post_id;
        this.user_nickname = user_nickname;
        this.user_profile_image = user_profile_image;
        this.user_region = user_region;
        this.postContent = postContent;
        this.postImgUrl = postImgUrl;
        this.isLike = isLike;
        this.category = category;
        this.post_likes_count = post_likes_count;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
