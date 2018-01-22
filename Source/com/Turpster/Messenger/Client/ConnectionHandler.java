package com.Turpster.Messenger.Client;

import com.Turpster.Messenger.Server.Server;
import com.Turpster.Messenger.net.MessagePacket;
import com.Turpster.Messenger.net.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;

public class ConnectionHandler implements Runnable
{
    private Thread thread;


    private Socket connection;
    String ipAddress;
    private int port;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    private Client client;

    public ConnectionHandler(Client client, String ipAddress, int port)
    {
        this.client = client;
        this.ipAddress = ipAddress;

        try
        {
            connection = new Socket(ipAddress, port);
            client.recieveMessage("Connected!");
            this.client.setSendable(true);
            setupStreams();
        }
        catch (UnknownHostException | ConnectException e)
        {
            client.recieveMessage("Failed to connect to: " + ipAddress + ".");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        start();
    }

    public void sendPacket(Packet packet) throws IOException
    {
        if (connection != null) {

            output.writeObject(packet);
            output.flush();
        }
        else
        {
            Client.getLogger().log(Level.SEVERE, "Could not connect to " + this.getIpAddress() + ". Exiting the program.");
            client.stop();
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (connection != null) {
                if (!connection.isClosed()) {
                    try {
                        Object obj = input.readObject();

                        Packet packet = (Packet) obj;

                        this.recievedPacket(packet);
                    } catch (SocketException e) {
                        Client.getLogger().log(Level.FINE, "Disconnecting...");
                        this.stop();
                    } catch (ClassCastException e) {
                        Client.getLogger().log(Level.FINE, "Server did not send a Packet");
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        Client.getLogger().log(Level.WARNING, "Server sent unknown object. (Are we updated to the latest version?) :P");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Client.getLogger().log(Level.SEVERE, "There has been an error recieving data from the connection.");
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                stop();
            }
        }
    }

    public void recievedPacket(Packet packet)
    {
        if (packet instanceof MessagePacket)
        {
            MessagePacket messagePacket = (MessagePacket) packet;
            client.recieveMessage(messagePacket.getMessage());
        }
    }

    public void setupStreams() throws IOException
    {
        output = new ObjectOutputStream(connection.getOutputStream());
        input = new ObjectInputStream(connection.getInputStream());
    }


    public synchronized void start()
    {
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop()
    {
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String newIp)
    {
        ipAddress = newIp;
    }
}
