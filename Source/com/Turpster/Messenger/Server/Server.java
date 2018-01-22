package com.Turpster.Messenger.Server;

import com.Turpster.Messenger.Logger;
import com.Turpster.Messenger.Server.Commands.CommandExecutor;
import com.Turpster.Messenger.Server.Commands.CommandHandler;
import com.Turpster.Messenger.Server.Connection.ConnectionHandler;
import com.Turpster.Messenger.Server.Connection.Connector;
import com.Turpster.Messenger.net.MessagePacket;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.logging.Level;

public class Server extends JFrame
{
    public static Random random = new Random();
    public static int serverOperatorId = random.nextInt(Integer.MAX_VALUE);

    private static Logger logger;

    ConnectionHandler connectionHandler;
    CommandHandler commandHandler;

    JTextArea text;

    public Server()
    {
        super("Server");

        text = new JTextArea("Setting up...");
        text.setEditable(false);

        commandHandler = new CommandHandler();

        super.setSize(new Dimension(700, 500));
        super.setLocationRelativeTo(null);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        text.setFont(new Font("Consolas", Font.PLAIN, 13));
        super.add(new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        super.setVisible(true);

        logger = new Logger();

        connectionHandler = new ConnectionHandler(this, 2345);
    }

    public void sendMessage(String message)
    {
        this.recieveMessage(message);

        for (Connector connector : connectionHandler.connections)
        {
            connector.sendPacket(new MessagePacket(message));
        }
    }

    public void recieveMessage(String message)
    {
        text.append("\n" + message);

        this.getLogger().log(Level.FINE, message);
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public String getPublicAddress()
    {
        return "null";
    }

    public static Logger getLogger()
    {
        return logger;
    }

    public String getLocalAddress()
    {
        return "127.0.0.1";
    }
}
