package com.example.huy.chiaseanh;

public class MyData {

    private String content;
    private String time;
    private String location;
    private String name;
    private String status;
    private  String url;
    private String urlAvatar;
    private String countComment;

    public MyData()
    {

    }

    public String getCountComment() {
        return countComment;
    }

    public void setCountComment(String countComment) {
        this.countComment = countComment;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public void setTime(String time)
    {
        this.time = time;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }



    public String getLocation()
    {
        return location;
    }


    public String getContent()
    {
        return content;
    }

    public String getTime()
    {
        return time;
    }


}
