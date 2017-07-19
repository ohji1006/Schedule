package org.androidtown.schedule;
/**
 * Created by ohji1 on 2017-07-05.
 */

//this is DTO
public class ChatData
{
    private String userName;
    private String message;

    public ChatData(){}
    public ChatData(String userName, String message/*, int day*/)
    {
        this.userName = userName;
        this.message = message;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
