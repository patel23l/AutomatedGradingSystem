/**
 Author: Nandini Patel
 Student ID: 105148289
 Date: 05/04/2020
 Instructor: Jessica Chen
 Description: Server File
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class server {
    private static ArrayList<File> files = new ArrayList<>();
    static String name, name_non_extension;
    static String strexec, strtxt, studentname;
    public static void main (String[] args ) throws IOException{
        while(true){
            strtxt = args[0];
            files.add(new File("page.class"));
            System.out.println(files.toString());
            ServerSocket serverSocket = new ServerSocket(55588);
            Socket sock = serverSocket.accept();
            System.out.println("Accepted connection : " + sock);

            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            System.out.println(files.size());

            //write the number of files to the server
            dos.writeInt(1);
            dos.writeUTF(files.get(0).getName());
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
            fis.close();
            serverSocket.close();
            sock.close();
            System.out.println("File transfer complete");
            
            //studentname = "studentA"; //hard coded for now
            serverConnect();
            strexec = name;
            compile(strexec, strtxt);
            sendReport();
        }
    }
    //=========================================================================================
    public static void serverConnect() throws IOException {
        System.out.println("waiting...");
        ServerSocket serverSocket1 = new ServerSocket(5000);
        Socket sock1 = serverSocket1.accept();
        System.out.println("accepted...");
        DataInputStream dis = new DataInputStream(new BufferedInputStream(sock1.getInputStream()));
                 
        //read the number of files from the client
        int number = dis.readInt();
        ArrayList<File>files = new ArrayList<File>(number);
        System.out.println("Number of Files to be received: " +number);
                          
        //read file names, add files to arraylist
        for(int i = 0; i< number;i++){
            File file = new File(dis.readUTF());
            studentname = dis.readUTF();
            System.out.println(studentname);
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
            //store the file inside this directory
            File fd = new File(studentname);
            fd.mkdir();

            String path = studentname+"/"+files.get(k).getName();
            //create a new fileoutputstream for each new file
            FileOutputStream fos = new FileOutputStream(path);
            //read file
            int x = dis.readInt();
            while((n = dis.read(buf)) != -1){
                fos.write(buf,0,n);
                fos.flush();
                System.out.println("file size: " + n);
            }
            fos.close();
            sock1.close();
            serverSocket1.close();
        }
    }
    //=========================================================================================
    public static void compile (String fd, String fd1) {
        BufferedReader in = null;
        BufferedWriter out = null;

        String fd1in = ""; //input of the text file
        String fd1out = ""; //output of the text file
        String fdout = ""; //output of the exec file
        String line = ""; //to store each line of the text file

        int num = 1; //to count number of cases in a file
        FileWriter writer = null;
        try {
            String path = studentname+"/" + "report.txt";
            File fd3 = new File(path);
            fd3.createNewFile();    
            writer = new FileWriter(fd3);

            Scanner scan = new Scanner(new FileInputStream(fd1)); //create a scanner for the text file
            Process p = Runtime.getRuntime().exec("cc -o homework " + studentname + "/" + fd); //execute the process file
            p.waitFor();
            //p.destroyForcibly(); //is this even needed?
            InputStream error = p.getErrorStream();
            System.out.println(error.read());

            if(error.read()==-1) {
                //need to account for nocompile.c file here or files that don't compile
                Process p2 = Runtime.getRuntime().exec("./homework");

                in = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(p2.getOutputStream()));

                while (scan.hasNext()) {
                    line = scan.nextLine();
                    if (line.equals("#")) {
                        num++;
                        //gcc needs to be changed to cc
                        p = Runtime.getRuntime().exec("cc -o homework " + fd);
                        p2 = Runtime.getRuntime().exec("./homework");
                        in = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                        out = new BufferedWriter(new OutputStreamWriter(p2.getOutputStream()));
                    }
                    else if (line.equals("*")) {
                        fd1out = scan.nextLine(); //get the answer of the file
                        fdout = in.readLine(); //get output of the exec file and store it in fdout
                        
                        if (fd1out.equals(fdout)) {
                            writer.write("test case " + num + ": pass"); 
                            writer.write(System.lineSeparator());
                            writer.flush();
                        }
                        else {
                            writer.write("test case " + num + ": fail");
                            writer.write(System.lineSeparator());
                            writer.flush();
                        }
                    }
                    else {
                        fd1in = line;
                        out.write(fd1in);
                        out.newLine();              
                        out.flush(); //*is optional*
                    }
                }
            }   
            else {
                writer.write("compilation failed");
                writer.flush();
            }
            in.close(); //close input stream
            out.close(); //close output stream
            writer.close();
            p.destroy(); //destroy the process
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    //=========================================================================================
    public static void sendReport() {
        String text = "";
        String st = "";
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(studentname+"/report.txt"));
            while((st = br.readLine()) != null) {
                text += st;
                text += System.lineSeparator();
            }
            Socket socketN = new Socket("127.0.0.1", 6000);

            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socketN.getOutputStream()));
            dos.writeUTF(text);
            dos.flush();
            dos.close();
            socketN.close();
        } 
        catch (Exception e) {
            System.out.println(e);
        }
        files.remove(0);
    }
}
