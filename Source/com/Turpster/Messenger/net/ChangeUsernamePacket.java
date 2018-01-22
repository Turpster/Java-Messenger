package com.Turpster.Messenger.net;

public class ChangeUsernamePacket extends Packet
{
    private String username;

    public ChangeUsernamePacket(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }
}
