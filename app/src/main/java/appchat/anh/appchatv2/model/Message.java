package appchat.anh.appchatv2.model;

public class Message {
    private String linkImg;
    private long time;
    private String message;
    private String contentType;
    private String fromId;


    public Message() {
    }

    public Message(String contentType, String fromId, String linkImg, String message, long time) {
        this.contentType = contentType;
        this.fromId = fromId;
        this.linkImg = linkImg;
        this.message = message;
        this.time = time;
    }

    public Message(String linkImg, long time, String message, String contentType, String fromId) {
        this.linkImg = linkImg;
        this.time = time;
        this.message = message;
        this.contentType = contentType;
        this.fromId = fromId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getLinkImg() {
        return linkImg;
    }

    public void setLinkImg(String linkImg) {
        this.linkImg = linkImg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
