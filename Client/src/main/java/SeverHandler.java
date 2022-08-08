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
        try {
            while (true) {
                fromServer = sock.getInputStream();
                dis = new DataInputStream(fromServer);

                //First input Header
                byte[] header = new byte[8];
                dis.readFully(header, 0, 8);
                Header.decodeHeader(header);
                int length = Header.messageLength;
                int type = Header.messageType;
                byte[] receiveBytes = new byte[length];
                
                ///type = 3333이면 -> name and Message
                if (type == Type.MESSAGETOCLIENT.getValue()) {
                    String fromJson = dis.readUTF();
                    JsonNode jsonNode = objectMapper.readTree(fromJson);
                    String name = jsonNode.get("name").asText();
                    receiveBytes = jsonNode.get("bytes").binaryValue();
                    String receiveMessage = new String(receiveBytes);
                    System.out.println(name + ": " + receiveMessage);
                }
                //type = 4444 -> close Message
                else if (type == Type.CLIENTCLOSEMESSAGE.getValue()) {
                    String fromJson = dis.readUTF();
                    JsonNode jsonNode = objectMapper.readTree(fromJson);
                    String name = jsonNode.get("name").asText();
                    int sendNum = jsonNode.get("sendNum").asInt();
                    int receiveNum = jsonNode.get("recieveNum").asInt();
                    System.out.println(name + " is out || Number of sendMessageNum: " + sendNum + ", Number of recieveMessageNum :"+ receiveNum);
                }
                //type = 5555 -> image download
                else if (type == Type.IMAGETOCLIENT.getValue()) {
                    String fromJson = dis.readUTF();
                    JsonNode jsonNode = objectMapper.readTree(fromJson);
                    String name = jsonNode.get("name").asText();
                    receiveBytes = jsonNode.get("bytes").binaryValue();
                    byteArrayConvertToImageFile(receiveBytes);
                    System.out.println(name + "이 보낸 사진을 저장했습니다.");
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
    }
}