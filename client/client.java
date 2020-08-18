/**
 Author: Nandini Patel
 Student ID: 105148289
 Date: 05/04/2020
 Instructor: Jessica Chen
 Description: Client File
 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class client extends JFrame{
    JPanel a, top, bot;
    JLayeredPane l;
    JLabel string;
    JTextField classname;
    boolean resolve;
    private static String name, name_non_extension, serverName;
    Object c;
    //============================================================================
    public static void main (String[] args ) throws IOException {
        client frame = new client();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600,800);
        frame.setVisible(true);
    }
    //============================================================================
    public client() throws IOException{
        super("simplest loader");
            
        a = new JPanel();
        l = new JLayeredPane();
        top = new JPanel();
        bot = new JPanel();
        string = new JLabel("Server IP: ");
        classname = new JTextField();
           
        setLayout(new BorderLayout());
        getContentPane().add(l, BorderLayout.CENTER);
        l.setBounds(0, 0, 600, 800);
        top.setBounds(0, 0, 600, 25);
        bot.setBackground(Color.white);
        bot.setBounds(0,30,600,775);
        l.add(top, 0, 0);
        l.add(bot, 1, 0);

        classname.setColumns(20);
        classname.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                serverName = classname.getText();
                System.out.println(serverName);

                try{
                    serverConnect(serverName);
                }
                catch(Exception e1){
                    System.out.println(e1);
                }

                try {
                    c = Class.forName(name_non_extension).newInstance();
                    a.removeAll();
                    a = (JPanel) c;
                    a.setBounds(0,30,600,600);
                    a.revalidate(); 
                    a.repaint();      
                    l.add(a,1,0);
                } 
                catch (Exception e1) {
                    System.out.println(e1);
                }
            }
        });
        a.setVisible(true);
        top.add(string);
        top.add(classname);
    }
    //============================================================================
    public void serverConnect(String serverName) throws IOException{
        Socket socket = new Socket(serverName,55588);
        System.out.println(socket);
    
        DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                 
        //read the number of files from the client
        int number = dis.readInt();
        ArrayList<File>files = new ArrayList<File>(number);
        System.out.println("Number of Files to be received: " +number);
                          
        //read file names, add files to arraylist
        for(int i = 0; i< number;i++){
            File file = new File(dis.readUTF());
            files.add(file);
        }

        name = files.get((files.size() - 1)).getName() ;
        name_non_extension = name.replaceFirst("[.][^.]+$", "");
        System.out.println("Without Class: " + name_non_extension);
        int n = 0;
        byte[]buf = new byte[4092];

        //outer loop, executes one for each file
        for(int k = 0; k < files.size();k++){    
            System.out.println("Receiving file: " + files.get(k).getName());
            //create a new fileoutputstream for each new file
            FileOutputStream fos = new FileOutputStream(files.get(k).getName());
            //read file
            int x = dis.readInt();
            while((n = dis.read(buf)) != -1){
                fos.write(buf,0,n);
                fos.flush();
                System.out.println("file size: " + n);
            }
            dis.close();
            fos.close();
            socket.close();
        }
    }
    //============================================================================
}
