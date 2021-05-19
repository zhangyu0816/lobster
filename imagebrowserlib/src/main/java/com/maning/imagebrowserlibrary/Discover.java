package com.maning.imagebrowserlibrary;

public class Discover  {

    private long friendDynId = 0L;
    private long otherUserId; // otherUserId
    private long userId;// otherUserId
    private String nick = "";// 昵称 艺名//1-16
    private String image = "";// 头像
    private String text = "";
    private String createTime = "2020-04-16 09:41:00";
    private int goodNum = 0;// 赞的数量
    private int reviews = 0;// 评论数

    public Discover() {
    }

    public Discover(long friendDynId, long otherUserId, long userId, String nick, String image, String text, String createTime, int goodNum, int reviews) {
        this.friendDynId = friendDynId;
        this.otherUserId = otherUserId;
        this.userId = userId;
        this.nick = nick;
        this.image = image;
        this.text = text;
        this.createTime = createTime;
        this.goodNum = goodNum;
        this.reviews = reviews;
    }

    public long getFriendDynId() {
        return friendDynId;
    }

    public void setFriendDynId(long friendDynId) {
        this.friendDynId = friendDynId;
    }

    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }
}
