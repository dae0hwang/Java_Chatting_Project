import com.fasterxml.jackson.databind.ObjectMapper;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class ServerHandler implements Runnable {
    static Socket sock;

    public ServerHandler(Socket sock) {
        this.sock = sock;
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    public void run() {
        InputStream fromServer = null;
        DataInputStream dataInputStream;
        try {
            while (true) {
                fromServer = sock.getInputStream();
                dataInputStream = new DataInputStream(fromServer);

                HeaderInformation headerInformation = recieveMessageHeader(dataInputStream);
                int messageBodyLength = headerInformation.messageBodyLength;
                int messageBodyType = headerInformation.messageBodyType;

                MessageBody messageBody = receiveMessageBody(dataInputStream, messageBodyLength);

                if (messageBodyType == Type.MESSAGETOCLIENT.getValue()) {
                    printNameAndMessage(messageBody);
                }
                else if (messageBodyType == Type.CLIENTCLOSEMESSAGE.getValue()) {
                    printCloseMessage(messageBody);
                }
                else if (messageBodyType == Type.IMAGETOCLIENT.getValue()) {
                    byteArrayConvertToImageFile(messageBody);
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

    private static HeaderInformation recieveMessageHeader(DataInputStream dataInputStream) throws IOException {
        int messageHeaderSize = 8;
        byte[] inputMessageHeader = new byte[messageHeaderSize];
        dataInputStream.readFully(inputMessageHeader,0, messageHeaderSize);
        Header makeHeader = new Header();
        makeHeader.decodeHeader(inputMessageHeader);
        HeaderInformation headerInformation = new HeaderInformation(makeHeader.messageLength, makeHeader.messageType);
        return headerInformation;
    }

    static class HeaderInformation {
        int messageBodyLength;
        int messageBodyType;
        HeaderInformation(int length, int type) {
            this.messageBodyLength = length;
            this.messageBodyType = type;
        }
    }

    private static MessageBody receiveMessageBody(DataInputStream dataInputStream, int messageBodyLength)
        throws IOException {
        byte[] messageBodyBytes = new byte[messageBodyLength];
        dataInputStream.readFully(messageBodyBytes, 0, messageBodyLength);
        MessageBody messageBody = objectMapper.readValue(messageBodyBytes, MessageBody.class);
        return messageBody;
    }

    private static void printNameAndMessage(MessageBody messageBody) {
        String name = messageBody.getName();
        String receiveMessage = new String(messageBody.getBytes());
        System.out.print(name + ": " + receiveMessage+ "\n");
    }

    private static void printCloseMessage(MessageBody messageBody) {
        String name = messageBody.getName();
        int sendNum = messageBody.getSendNum();
        int receiveNum = messageBody.getRecieveNum();
        System.out.print(name + " is out || Number of sendMessageNum: " + sendNum + ", Number of recieveMessageNum : "+ receiveNum+ "\n");
    }

    private static void byteArrayConvertToImageFile(MessageBody messageBody) throws IOException {
        byte[] imageBytes = messageBody.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        String directory = Integer.toString(sock.getLocalPort());
        Path path = Paths.get("C:\\Users\\geung\\Downloads" + "\\" + directory);
        Files.createDirectories(path);
        String fileName = path.toString() + "\\copy.jpg";
        ImageIO.write(bufferedImage, "jpg", new File(fileName));
        //open the download image by mspaint.
        ProcessBuilder processBuilder2 = new ProcessBuilder(
            "C:\\Windows\\System32\\mspaint.exe"
            , fileName);
        processBuilder2.start();
        //print image nmae and download success message
        System.out.println("image download success. filename is " +fileName);
    }
}