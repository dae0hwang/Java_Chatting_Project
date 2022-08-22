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
                Type messageBodyType = headerInformation.messageBodyType;
                byte[] receiveMessageBodyBytes = recieveMessageBodyBytes(dataInputStream, messageBodyLength);
                switch (messageBodyType) {
                    case MESSAGETOCLIENT:
                        printNameAndMessage(receiveMessageBodyBytes);
                        break;
                    case CLIENTCLOSEMESSAGE:
                        printCloseMessage(receiveMessageBodyBytes);
                        break;
                    case IMAGETOCLIENT:
                        saveAndOpenImageFile(receiveMessageBodyBytes);
                        break;
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
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.decodeHeader(inputMessageHeader);
        Type type;
        if (headerConverter.messageType == Type.MESSAGETOCLIENT.getValue()) {
            type = Type.MESSAGETOCLIENT;
        } else if (headerConverter.messageType == Type.IMAGETOCLIENT.getValue()) {
            type = Type.IMAGETOCLIENT;
        } else {
            type = Type.CLIENTCLOSEMESSAGE;
        }
        HeaderInformation headerInformation = new HeaderInformation(headerConverter.messageLength, type);
        return headerInformation;
    }

    static class HeaderInformation {
        int messageBodyLength;
        Type messageBodyType;
        HeaderInformation(int length, Type type) {
            this.messageBodyLength = length;
            this.messageBodyType = type;
        }
    }

    private static byte[] recieveMessageBodyBytes(DataInputStream dataInputStream, int messageBodyLength) throws IOException {
        byte[] messageBodyBytes = new byte[messageBodyLength];
        dataInputStream.readFully(messageBodyBytes, 0, messageBodyLength);
        return messageBodyBytes;
    }

    private static void printNameAndMessage(byte[] messageBodybytes) throws IOException {
        StringMessageBodyDto stringMessageBodyDto =
            objectMapper.readValue(messageBodybytes, StringMessageBodyDto.class);
        String name = stringMessageBodyDto.getName();
        String receiveMessage = new String(stringMessageBodyDto.getStringMessageBytes());
        System.out.print(name + ": " + receiveMessage+ "\n");
    }

    private static void printCloseMessage(byte[] messageBodyBytes) throws IOException {
        CloseMessageBodyDto closeMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, CloseMessageBodyDto.class);
        String name = closeMessageBodyDto.getName();
        int sendNum = closeMessageBodyDto.getSendNum();
        int receiveNum = closeMessageBodyDto.getRecieveNum();
        System.out.print(name + " is out || Number of sendMessageNum: " + sendNum + ", Number of recieveMessageNum : "+ receiveNum+ "\n");
    }

    private static void saveAndOpenImageFile(byte[] messageBodyBytes) throws IOException {
        ImageMessageBodyDto imageMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, ImageMessageBodyDto.class);
        byte[] imageBytes = imageMessageBodyDto.getImageMessageBytes();
        String directory = Integer.toString(sock.getLocalPort());
        Path path = Paths.get("C:\\Users\\geung\\Downloads" + "\\" + directory);
        Files.createDirectories(path);
        String fileName = path.toString() + "\\copy.jpg";
        Files.write(Path.of(fileName), imageBytes);
        //open the download image by mspaint.
        ProcessBuilder processBuilder2 = new ProcessBuilder(
            "C:\\Windows\\System32\\mspaint.exe"
            , fileName);
        processBuilder2.start();
        //print image nmae and download success message
        System.out.println("image download success. filename is " +fileName);
    }
}