package com.Turpster.Messenger.Server.Connection;

import com.Turpster.Messenger.Server.Server;
import com.Turpster.Messenger.net.MessagePacket;
import com.Turpster.Messenger.net.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;

public class Connector implements Runnable
{
    private volatile ConnectionHandler connectionHandler;

    private Thread playerThread;
    protected Socket connection;
    protected String name;
    protected int ID;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    public boolean canSend = false;

    public Connector(ConnectionHandler connectionHandler, Socket connection, String name)
    {
        this.connection = connection;
        this.name = name;

        this.connectionHandler = connectionHandler;

        try
        {
            start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Object obj = input.readObject();

                Packet packet = (Packet) obj;

                connectionHandler.recievePacket(this, packet);
            }
            catch (IOException e)
            {
                stop();

                e.printStackTrace();
            }
            catch (ClassCastException e)
            {
                Server.getLogger().log(Level.WARNING, "Connector " + name + " with the address of " + connection.getInetAddress().getHostName() + " has sent something suspicious.");
            }
            catch (ClassNotFoundException e)
            {
                Server.getLogger().log(Level.WARNING, "Connector " + name + " with the address of " + connection.getInetAddress().getHostName() + " has sent something suspicious.");
            }
        }
    }

    public void sendPacket(Packet packet)
    {
        try
        {
            output.writeObject(packet);
            output.flush();
        }
        catch (SocketException e)
        {
            Server.getLogger().log(Level.WARNING, this.name + "/" + this.connection.getInetAddress().toString() + " has disconnected.");
            stop();
        }
        catch (IOException e)
        {
            Server.getLogger().log(Level.WARNING, "Something went wrong sending a packet to " + connection.getInetAddress().getHostName() + " (" + name + ").");
            e.printStackTrace();
        }

    }

    public void sendUserMessage(String message)
    {
        this.sendPacket(new MessagePacket(message));
    }

    private void setupSockets() throws IOException
    {
        output = new ObjectOutputStream(connection.getOutputStream());
        input = new ObjectInputStream(connection.getInputStream());

        output.flush();
    }

    public synchronized void stop()
    {
        this.connectionHandler.connections.remove(this);

        try
        {
            playerThread.join();
        }
        catch (InterruptedException e)
        {
            stop();
            e.printStackTrace();
        }
    }

    public String getName()
    {
        return name;
    }


    public synchronized void start() throws IOException
    {
        setupSockets();

        playerThread = new Thread(this, name);
        playerThread.start();
    }
}
