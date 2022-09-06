import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandlerService {
    ObjectMapper objectMapper = new ObjectMapper();

    public byte[] recieveMessageHeader(DataInputStream dataInputStream) throws IOException {
        byte[] header = new byte[8];
        dataInputStream.readFully(header, 0, header.length);
        return header;
    }


    public HeaderInformation implementHeaderInformation(byte[] headerBytes) {
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.decodeHeader(headerBytes);
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

    public byte[] recieveMessageBodyBytes(DataInputStream dataInputStream, int messageBodyLength) throws IOException {
        byte[] messageBodyBytes = new byte[messageBodyLength];
        dataInputStream.readFully(messageBodyBytes, 0, messageBodyLength);
        return messageBodyBytes;
    }

    public void printNameAndMessage(byte[] messageBodybytes) throws IOException {
        StringMessageBodyDto stringMessageBodyDto =
            objectMapper.readValue(messageBodybytes, StringMessageBodyDto.class);
        String name = stringMessageBodyDto.getName();
        String receiveMessage = new String(stringMessageBodyDto.getStringMessageBytes());
        System.out.print(name + ": " + receiveMessage+ "\n");
    }

    public void printCloseMessage(byte[] messageBodyBytes) throws IOException {
        CloseMessageBodyDto closeMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, CloseMessageBodyDto.class);
        String name = closeMessageBodyDto.getName();
        int sendNum = closeMessageBodyDto.getSendNum();
        int receiveNum = closeMessageBodyDto.getRecieveNum();
        System.out.print(name + " is out || Number of sendMessageNum: "
            + sendNum + ", Number of recieveMessageNum : "+ receiveNum+ "\n");
    }

    public ImageBytesAndDirectory saveImageInformation(Socket sockets, byte[] messageBodyBytes) throws IOException {
        ImageMessageBodyDto imageMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, ImageMessageBodyDto.class);
        byte[] imageBytes = imageMessageBodyDto.getImageMessageBytes();
        String directory = Integer.toString(sockets.getLocalPort());
        ImageBytesAndDirectory imageBytesAndDirectory = new ImageBytesAndDirectory();
        imageBytesAndDirectory.setImageBytes(imageBytes);
        imageBytesAndDirectory.setDirectory(directory);
        return imageBytesAndDirectory;
    }

    public void makeImageFile(ImageBytesAndDirectory imageBytesAndDirectory) throws IOException {
        String directory = imageBytesAndDirectory.getDirectory();
        byte[] imageBytes = imageBytesAndDirectory.getImageBytes();
        Path path = Paths.get("C:\\Users\\geung\\Downloads" + "\\" + directory);
        Files.createDirectories(path);
        String fileName = path.toString() + "\\copy.jpg";
        Files.write(Path.of(fileName), imageBytes);
    }

    public String returnFileName(ImageBytesAndDirectory imageBytesAndDirectory) throws IOException {
        String directory = imageBytesAndDirectory.getDirectory();
        Path path = Paths.get("C:\\Users\\geung\\Downloads" + "\\" + directory);
        String fileName = path.toString() + "\\copy.jpg";
        return fileName;
    }


    public void openImageFile(String fileName) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "C:\\Windows\\System32\\mspaint.exe"
            , fileName);
        processBuilder.start();
    }
}
