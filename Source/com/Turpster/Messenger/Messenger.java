package com.Turpster.Messenger;

import com.Turpster.Messenger.Client.Client;
import com.Turpster.Messenger.Server.Server;

public class Messenger
{
    /*
    PORT: 2345
     */

    public static void main(String[] args)
    {
        boolean noClient = false;
        boolean server = false;

        Server serverObj = null;
        String targetIp = null;
        int id = 0;

        int x = 0;
        for (String argument : args)
        {
            if (argument.equalsIgnoreCase("-server"))
            {
                server = true;
            }
            if (argument.equalsIgnoreCase("-noclient"))
            {
                noClient = true;
            }
            if (argument.equalsIgnoreCase("-ip"))
            {
                targetIp = args[x + 1];
            }
            if (argument.equalsIgnoreCase("-id"))
            {
                id = Integer.parseInt(args[x + 1]);
            }
            x++;
        }

        if (server)
        {
            serverObj = new Server();
            if (!noClient)
            {
                targetIp = serverObj.getLocalAddress();
                id = Server.serverOperatorId;
                new Client(targetIp, null, id);
            }
        }
        else
        {
            new Client(targetIp, null, id);
        }
    }
}
