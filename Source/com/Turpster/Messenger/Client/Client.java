package com.Turpster.Messenger.Client;

import com.Turpster.Messenger.Logger;
import com.Turpster.Messenger.net.ChangeUsernamePacket;
import com.Turpster.Messenger.net.LoginPacket;
import com.Turpster.Messenger.net.MessagePacket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;

public class Client extends JFrame
{
    String userName;

    String serverAddress;

    Socket connection;

    JTextArea text;

    JPanel bottom;
    JTextField typeBox;
    JButton changeUsername;

    private static Logger logger;

    int id;

    private ConnectionHandler connectionHandler;

    public Client(String serverAddress, String userName, int id) {
        super("Turpster Messenger");

        this.id = id;

        logger = new Logger();

        if (userName == null || userName.equals("null")) {
            this.userName = JOptionPane.showInputDialog("Enter Username");
        } else {
            this.userName = userName;
        }

        if (serverAddress == null || serverAddress.equals("null")) {
            this.serverAddress = JOptionPane.showInputDialog("Enter Target Server Address");
        } else {
            this.serverAddress = serverAddress;
        }


        super.setSize(new Dimension(700, 700));

        text = new JTextArea("Setting up...");
        text.setEditable(false);
        super.add(new JScrollPane(text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        text.setFont(new Font("Consolas", Font.PLAIN, 13));

        bottom = new JPanel();
        changeUsername = new JButton("Change Username");
        typeBox = new JTextField();
        typeBox.setPreferredSize(new Dimension(super.getSize().width - 170, 25));
        typeBox.setEditable(false);

        typeBox.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage(typeBox.getText());
                    typeBox.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        JScrollPane typeBoxScrollPane = new JScrollPane(typeBox, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        bottom.add(typeBoxScrollPane, BorderLayout.CENTER);

        changeUsername.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeUsername();
            }
        });

        bottom.add(changeUsername, BorderLayout.LINE_END);

        super.add(bottom, BorderLayout.PAGE_END);

        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLocationRelativeTo(null);
        super.setVisible(true);

        typeBox.requestFocus();

        this.connectionHandler = new ConnectionHandler(this, this.serverAddress, 2345);

        try {
            this.connectionHandler.sendPacket(new LoginPacket(this.userName, this.id));
        } catch (IOException e) {
            this.getLogger().log(Level.WARNING, "Could not change username");
            e.printStackTrace();
        }

    }

    public void stop() {
        System.exit(0);
    }

    public void setSendable(boolean condition)
    {
        typeBox.setEditable(condition);
    }

    public static Logger getLogger()
    {
        return logger;
    }

    public void changeUsername(String username)
    {
        try
        {
            this.userName = username;
            this.connectionHandler.sendPacket(new ChangeUsernamePacket(username));
        }
        catch (IOException e)
        {
            Client.getLogger().log(Level.SEVERE, "Could not change username");
        }
    }

    public void changeUsername()
    {
        String username = JOptionPane.showInputDialog("New username");

        try
        {
            this.userName = username;
            this.connectionHandler.sendPacket(new ChangeUsernamePacket(username));
        }
        catch (IOException e)
        {
            Client.getLogger().log(Level.SEVERE, "Could not change username");
        }
    }

    public void recieveMessage(String message)
    {
        this.text.append("\n" + message);
    }

    public void sendMessage(String message)
    {
        try
        {
            connectionHandler.sendPacket(new MessagePacket(message));
        }
        catch (IOException e)
        {
            this.getLogger().log(Level.WARNING, "Error sending message to server.");
            e.printStackTrace();
        }
    }
}
