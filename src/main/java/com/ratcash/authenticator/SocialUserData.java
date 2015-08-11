/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ratcash.authenticator;

import javax.json.JsonObject;

/**
 * Facebook: {"id":"1549374151954091","first_name":"Ratcash","gender":"male","last_name":"Dev","link":"https:\/\/www.facebook.com\/app_scoped_user_id\/1549374151954091\/","locale":"en_US","name":"Ratcash Dev","timezone":1,"updated_time":"2014-06-16T12:11:11+0000","verified":true}
Info:   
 */
public class SocialUserData {
    public String token;
    
    public String id;
    
    public String first_name;
    
    public String last_name;
    
    public String name;
    
    public String link;
    
    public String type;

    public SocialUserData(String id, String first_name, String last_name, String name, String link, String type) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.name = name;
        this.link = link;
        this.type = type;
    }

    public SocialUserData() {
    }
    
    public static SocialUserData forFacebook(JsonObject jsonObject) {
        return new SocialUserData(
                jsonObject.getString("id"),
                jsonObject.getString("first_name"),
                jsonObject.getString("last_name"),
                jsonObject.getString("name"),
                jsonObject.getString("link"),
                "facebook");
    }
}
