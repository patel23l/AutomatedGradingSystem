/**
 Author: Nandini Patel
 Student ID: 105148289
 Date: 05/04/2020
 Instructor: Jessica Chen
 Description: Page File
 */
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class page extends JPanel implements ActionListener {
    JTextField s, f;
    JLabel studentname, filename;
    JButton upload;
    String sname, fname;
    String serverip = "127.0.0.1";
    
    public page() {
        setSize(600, 500);
        setVisible(true);
        setLayout(null);
        
        s = new JTextField();
        f = new JTextField();
        studentname = new JLabel("student name");
        filename = new JLabel("file name");
        upload = new JButton();
        upload.setText("upload");

        studentname.setBounds(30, 100, 80, 20);
        filename.setBounds(30, 130, 80, 20);
        s.setBounds(120, 100, 200, 20);
        f.setBounds(120, 130, 200, 20);
        upload.setBounds(200, 160, 80, 20);
        add(studentname);
        add(filename);
        add(s);
        add(f);
        add(upload);

        upload.addActionListener(this);
    }
    //============================================================================
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new page());
        frame.setSize(600, 500);
        frame.setVisible(true);
    }
    //============================================================================
    public void actionPerformed(ActionEvent e) {
        sname = s.getText();
        fname = f.getText();
        sendToServer();
    }
    //============================================================================
    public void sendToServer() {
        try {
            Socket socket = new Socket(serverip, 5000);
            ArrayList<File> files = new ArrayList<>();
            files.add(new File(fname));
            System.out.println(files.toString());

            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            System.out.println(files.size());

            dos.writeInt(1);
            dos.writeUTF(files.get(0).getName());
            dos.writeUTF(sname);
            dos.flush();
            dos.writeInt(1940);
            dos.flush();
            dos.flush();

            int n = 0;
            byte[] buf;

            buf = new byte[(int) files.get(0).length()];
            FileInputStream fis = new FileInputStream(files.get(0));
            while ((n = fis.read(buf)) != -1) {
                dos.write(buf, 0, n);
                System.out.println(n);
                dos.flush();
            }

            dos.flush();
            dos.close();
            socket.close();
        }
        catch (Exception e1) {
            System.out.println(e1);
        }

        //============================================================================
        //receive test report from server
        String str = "";
        try {
            ServerSocket serverSocket1 = new ServerSocket(6000);
            Socket sock1 = serverSocket1.accept();
            System.out.println("Accepted connection: " + sock1);
            DataInputStream dis = new DataInputStream(new BufferedInputStream(sock1.getInputStream()));
            
            str = dis.readUTF();

            //remove some GUI components
            remove(f);
            remove(studentname);
            remove(filename);
            remove(s);
            remove(upload);

            repaint();

            dis.close();
            sock1.close();
            serverSocket1.close();
            //add JTextArea to display test report
            JTextArea area = new JTextArea(str);
            area.setBounds(20, 100, 300, 300);
            add(area);
        }
        catch (Exception e1) {
            System.out.println(e1);
        }
    }
}
