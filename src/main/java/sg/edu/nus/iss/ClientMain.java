package sg.edu.nus.iss;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientMain {
    
    public static void main(String[] args) throws IOException{

        Socket socket = new Socket("localhost", 3000);

        // instantiate streams to write to server and receive message from server
        InputStream is = socket.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);

        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        DataOutputStream dos = new DataOutputStream(bos);


        boolean quit = false;


        // send file size to server
        while (!quit) {

            String msgIncoming = dis.readUTF();
            System.out.println(msgIncoming);
            
            Console cons = System.console();
            String msgOutGoing = cons.readLine();
            dos.writeUTF(msgOutGoing);
            dos.flush();
            
            // send file to server
            msgIncoming = dis.readUTF();
    
            System.out.println(msgIncoming);
            String fileName = "myFile.txt";
            File fileToSend = new File(fileName);
    
            int count;
            byte[] buffer = new byte[4096];
    
            FileInputStream fis = new FileInputStream(fileToSend);
    
            while ((count = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, count);
            }
    
            fis.close();
    
            if ((msgIncoming = dis.readUTF()) == "ok") {
                System.out.println("File transferred successfully");
                quit = true;
            } else {
                System.out.println("An error occurred during the file transfer. Please try again");
            }
        }
        
        
        // close 
        dos.close();
        bos.close();
        os.close();
        dis.close();
        bis.close();
        is.close();
        socket.close();
    }
}
