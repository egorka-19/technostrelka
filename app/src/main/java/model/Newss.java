package model;

public class Newss {

    int id;
    String Nickname, place, name_post, postText, avatar, image;

    public Newss(int id, String nickname, String name_post, String postText, String avatar, String image) {
        this.id = id;
        this.Nickname = nickname;
        this.name_post = name_post;
        this.postText = postText;
        this.avatar = avatar;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getName_post() {
        return name_post;
    }

    public void setName_post(String name_post) {
        this.name_post = name_post;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
