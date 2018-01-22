package com.Turpster.Messenger.net;

public class LoginPacket extends Packet
{
    private String username;
    private int id;

    public LoginPacket(String username, int id)
    {
        this.username = username;
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public int getId()
    {
        return id;
    }

}
