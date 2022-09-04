import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandlerService {
    ObjectMapper objectMapper = new ObjectMapper();

//   public HeaderInformation recieveMessageHeader(DataInputStream dataInputStream) throws IOException {
//        int messageHeaderSize = 8;
//        byte[] inputMessageHeader = new byte[messageHeaderSize];
//        dataInputStream.readFully(inputMessageHeader,0, messageHeaderSize);
//        HeaderConverter headerConverter = new HeaderConverter();
//        headerConverter.decodeHeader(inputMessageHeader);
//        Type type;
//        if (headerConverter.messageType == Type.MESSAGETOCLIENT.getValue()) {
//            type = Type.MESSAGETOCLIENT;
//        } else if (headerConverter.messageType == Type.IMAGETOCLIENT.getValue()) {
//            type = Type.IMAGETOCLIENT;
//        } else {
//            type = Type.CLIENTCLOSEMESSAGE;
//        }
//        HeaderInformation headerInformation = new HeaderInformation(headerConverter.messageLength, type);
//        return headerInformation;
//    }

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

    public void saveAndOpenImageFile(byte[] messageBodyBytes) throws IOException {
        ImageMessageBodyDto imageMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, ImageMessageBodyDto.class);
        byte[] imageBytes = imageMessageBodyDto.getImageMessageBytes();
        String directory = Integer.toString(ServerHandler.sock.getLocalPort());
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
