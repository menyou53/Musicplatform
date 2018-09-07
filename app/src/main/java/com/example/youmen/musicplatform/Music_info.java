package com.example.youmen.musicplatform;

/**
 * Created by youmen on 2017/10/16.
 */

public class Music_info {
    private  String music_name;
    private String user;
    private int download_times;
    private String dl_url;
    private String type;
    public Music_info(){}



    public Music_info(int download_times, String music_name, String user,String dl_url,String type){
        this.download_times=download_times;
        this.music_name=music_name;
        this.user=user;
        this.dl_url=dl_url;
        this.type=type;
    }


    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type=type;
    }

    public String getDl_url() {
        return dl_url;
    }

    public void setDl_url(String music) {
        this.dl_url = dl_url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getDownload_times() {
        return download_times;
    }

    public void setDownload_times(int download_times) {
        this.download_times = download_times;
    }

    public String getMusic_name() {

        return music_name;
    }

    public void setMusic_name(String music_name) {
        this.music_name = music_name;
    }
}


