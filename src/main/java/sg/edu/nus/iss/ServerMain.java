package sg.edu.nus.iss;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class ServerMain {
    public static void main(String[] args) throws IOException {

        int port = 3000;
        String dirPath = "download";

        if (args.length > 0) {
            switch (args.length) {
                case 2: 
                    dirPath = args[1];
                case 1:
                    port = Integer.parseInt(args[0]);
                    break;
                default:
                    break;
            }
            
        }

        ServerSocket server = new ServerSocket(port);

        System.out.println("Starting file transfer on port 3000");

        Socket socket = server.accept();
        
        try {
            InputStream is = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);

            String fileName = dis.readUTF();
            long fileSize = dis.readLong();

            System.out.printf("Transferring file %s (%d)\n", fileName, fileSize);

            
            String dirPathFileName = dirPath + File.separator + fileName;

            File newDir = new File(dirPath);

            if (!newDir.exists()) {
                newDir.mkdir();
            }

            File newFile = new File(dirPathFileName);

            int size = 0;
            byte[] buffer = new byte[4096];
            long sizeTransferred = 0;
            OutputStream os = new FileOutputStream(newFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);

            while ((sizeTransferred < fileSize) && ((size = dis.read(buffer)) > 0)) {
                bos.write(buffer, 0, size);
                sizeTransferred += size;
            }

            bos.flush();
            bos.close();
            os.close();

            OutputStream outputStatus = socket.getOutputStream();
            BufferedOutputStream bufferedStatus = new BufferedOutputStream(outputStatus);
            DataOutputStream dataStatus = new DataOutputStream(bufferedStatus);

            if (newFile.length() == fileSize) {
                dataStatus.writeUTF("ok");
            } else {
                dataStatus.writeUTF("error");
            }

            dataStatus.flush();

            String acknowledgement = dis.readUTF();

            if ("close".equalsIgnoreCase(acknowledgement)) {
                System.out.println("File received successfully");
            } else {
                System.out.println("An error occured during the file transfer.");
            }

            is.close();
            outputStatus.close();

        } finally {
            socket.close();
        }
        server.close();
    }
}
