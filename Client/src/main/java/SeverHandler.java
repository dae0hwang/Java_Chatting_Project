import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

// Thread for receive message from Server
class ServerHandler implements Runnable {
    static Socket sock;

    public ServerHandler(Socket sock) {
        this.sock = sock;
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    public void run() {
        InputStream fromServer = null;
        DataInputStream dis;
        Header makeHeader = new Header();
        try {
            while (true) {
                fromServer = sock.getInputStream();
                dis = new DataInputStream(fromServer);

                //First input messgae Header
                byte[] header = new byte[8];
                dis.readFully(header, 0, 8);
                makeHeader.decodeHeader(header);
                int length = makeHeader.messageLength;
                int type = makeHeader.messageType;
                byte[] receiveBytes =new byte[length];

                //Second input message body
                dis.readFully(receiveBytes,0,length);
                Body inputBody = objectMapper.readValue(receiveBytes, Body.class);

                ///type == 3333 -> name and Message
                if (type == Type.MESSAGETOCLIENT.getValue()) {
                    String name = inputBody.getName();
                    String receiveMessage = new String(inputBody.getBytes());
                    System.out.println(name + ": " + receiveMessage);
                }
                //type == 4444 -> close Message
                else if (type == Type.CLIENTCLOSEMESSAGE.getValue()) {
                    String name = inputBody.getName();
                    int sendNum = inputBody.getSendNum();
                    int receiveNum = inputBody.getRecieveNum();
                    System.out.println(name + " is out || Number of sendMessageNum: " + sendNum + ", Number of recieveMessageNum :"+ receiveNum);
                }
                //type == 6666 -> image download
                else if (type == Type.IMAGETOCLIENT.getValue()) {
                    byte[] imageBytes = inputBody.getBytes();
                    byteArrayConvertToImageFile(imageBytes);
                }
            }
        } catch (IOException ex) {
            System.out.println("Connection termination (" + ex + ")");
        } finally {
            try {
                if (fromServer != null)
                    fromServer.close();
                if (sock != null)
                    sock.close();
            } catch (IOException ex) {
            }
        }
    }
    public static void byteArrayConvertToImageFile(byte[] imageBytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        String directory = Integer.toString(sock.getLocalPort());
        Path path = Paths.get("C:\\Users\\geung\\Downloads" + "\\" + directory);
        Files.createDirectories(path);
        String fileName = path.toString() + "\\copy.jpg";
        ImageIO.write(bufferedImage, "jpg", new File(fileName));

        //open the download image by mspaint.
        ProcessBuilder processBuilder2 = new ProcessBuilder(
            "C:\\Windows\\System32\\mspaint.exe",
            fileName);
        processBuilder2.start();

        //print image nmae and download success message
        System.out.println("image download success. filename is " +fileName);
    }
}