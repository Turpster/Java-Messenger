package com.Turpster.Messenger.Server.Connection;

import com.Turpster.Messenger.Server.Server;
import com.Turpster.Messenger.net.ChangeUsernamePacket;
import com.Turpster.Messenger.net.LoginPacket;
import com.Turpster.Messenger.net.MessagePacket;
import com.Turpster.Messenger.net.Packet;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;

public class ConnectionHandler implements Runnable
{
    boolean acceptConnection = false;

    Thread connectionThread = new Thread();

    Server server;

    ServerSocket serverSocket;
    public ArrayList<Connector> connections = new ArrayList<Connector>();

    public ConnectionHandler(Server server, int port)
    {
        this.server = server;

        try
        {
            serverSocket = new ServerSocket(port);

        }
        catch (BindException e)
        {
            Server.getLogger().log(Level.SEVERE, "COULD NOT BIND TO " + port + ", PRAHAPS SERVER IS ALREADY RUNNING?");
            e.printStackTrace();
            stop();
            System.exit(0);
        }
        catch (IOException e)
        {
            Server.getLogger().log(Level.SEVERE, "Could not make server socket");
            e.printStackTrace();
        }

        server.recieveMessage("Waiting for connections...");
        start();
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Socket connector = serverSocket.accept();

                connections.add(new Connector(this, connector, "Client"));
                Server.getLogger().log(Level.FINE, connector.getInetAddress().toString() + " has connected.");
            }
            catch (IOException e)
            {
                Server.getLogger().log(Level.FINE, "Something went wrong accepting a connection");
                e.printStackTrace();
            }
        }
    }

    public void recievePacket(Connector connector, Packet packet)
    {
        if (packet instanceof MessagePacket)
        {
            MessagePacket messagePacket = (MessagePacket) packet;

            if (connector.canSend)
            {
                if (Server.serverOperatorId == connector.ID)
                {
                    server.sendMessage("[Operator] " + connector.getName() + ": " + messagePacket.getMessage());
                }
                else server.sendMessage("" + connector.getName() + ": " + messagePacket.getMessage());
            }
            else
            {
                connector.sendPacket(new MessagePacket("Invalid User, you can't send this message."));
            }
        }
        else if (packet instanceof ChangeUsernamePacket)
        {
            ChangeUsernamePacket changeUsername = (ChangeUsernamePacket) packet;
            connector.name = changeUsername.getUsername();
        }
        else if (packet instanceof LoginPacket)
        {
            LoginPacket loginPacket = (LoginPacket) packet;

            connector.name = loginPacket.getUsername();
            connector.ID = loginPacket.getId();
            connector.canSend = true;
        }
    }

    public synchronized void start()
    {
        connectionThread = new Thread(this, "Connection-Manager");    

        connectionThread.start();
        acceptConnection = true;
    }

    public synchronized void stop()
    {
        try
        {
            connectionThread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
