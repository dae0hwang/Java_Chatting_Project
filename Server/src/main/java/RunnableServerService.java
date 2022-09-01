import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RunnableServerService {
    ObjectMapper objectMapper = new ObjectMapper();

    public  byte[] recieveMessageHeaderFromClient(DataInputStream dis) throws IOException {
        byte[] header = new byte[8];
        dis.readFully(header, 0, header.length);
        return header;
    }

    public byte[] receiveMessageBodyFromClient(DataInputStream dis, byte[] header) throws IOException {
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.decodeHeader(header);
        int length = headerConverter.messageLength;
        byte[] receiveBytes = new byte[length];
        dis.readFully(receiveBytes,0,length);
        System.out.println("server message received message header and body");
        return receiveBytes;
    }

    public Type readType(byte[] header) {
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.decodeHeader(header);
        int intType = headerConverter.messageType;
        if (intType == Type.RESISTERNAME.getValue()) {
            return Type.RESISTERNAME;
        } else if (intType == Type.MESSAGETOSERVER.getValue()) {
            return Type.MESSAGETOSERVER;
        } else if (intType == Type.IMAGETOSERVER.getValue()) {
            return Type.IMAGETOSERVER;
        }
        return null;
    }

    public String resisterName(byte[] messageBodyBytes) throws IOException {
        ResisterNameMessageBodyDto resisterNameMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, ResisterNameMessageBodyDto.class);
        return resisterNameMessageBodyDto.getName();
    }

    public byte[] implementStringMessageJsonBytes(byte[] messageBodyBytes, String thisName) throws IOException {
        StringMessageBodyDto stringMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, StringMessageBodyDto.class);
        stringMessageBodyDto.setName(thisName);
        byte[] stringMessageJsonBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
        return stringMessageJsonBytes;
    }

    public byte[] implementStringMessageServerHeaderBytes(byte[] stringMessageJsonBytes) {
        HeaderConverter headerConverter = new HeaderConverter();
        int serverType = Type.MESSAGETOCLIENT.getValue();
        int serverLength = stringMessageJsonBytes.length;
        headerConverter.encodeHeader(serverLength, serverType);
        return headerConverter.bytesHeader;
    }

    public byte[] implementImageMessageJsonBytes(byte[] messageBodyBytes, String thisName) throws IOException {
        ImageMessageBodyDto imageMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, ImageMessageBodyDto.class);
        imageMessageBodyDto.setName(thisName);
        byte[] imageMessageJsonBytes = objectMapper.writeValueAsBytes(imageMessageBodyDto);
        return imageMessageJsonBytes;
    }

    public static byte[] implementImageMessageServerHeaderBytes(byte[] imageMessageJsonBytes) {
        HeaderConverter headerConverter = new HeaderConverter();
        int serverType = Type.IMAGETOCLIENT.getValue();
        int serverLength = imageMessageJsonBytes.length;
        headerConverter.encodeHeader(serverLength, serverType);
        return headerConverter.bytesHeader;
    }

    public byte[] implementCloseBody(String name, int sendNum, int recieveNum) throws JsonProcessingException {
        CloseMessageBodyDto closeMessageBodyDto = new CloseMessageBodyDto();
        closeMessageBodyDto.setName(name);
        closeMessageBodyDto.setSendNum(sendNum);
        closeMessageBodyDto.setRecieveNum(recieveNum);
        return objectMapper.writeValueAsBytes(closeMessageBodyDto);
    }

    public byte[] implementCloseHeader( byte[] sendJsonBytes) {
        HeaderConverter headerConverter = new HeaderConverter();
        int serverLength = sendJsonBytes.length;
        int serverType = Type.CLIENTCLOSEMESSAGE.getValue();
        headerConverter.encodeHeader(serverLength, serverType);
        return headerConverter.bytesHeader;
    }

//    public MessagePacket implementCloseMessagePacket(int clientRecieveMessageNum) throws JsonProcessingException {
//        byte[] sendJsonBytes = implementCloseBody(this.name, threadLocalClientSendMessageNum.get(),
//            clientRecieveMessageNum);
//        byte[] serverHeader = implementCloseHeader(sendJsonBytes);
//        MessagePacket messagePacket = new MessagePacket(sendJsonBytes, serverHeader);
//        return messagePacket;
//    }

    public void broadcastAllUser(HashMap<Socket, Integer> clients, Socket sock, MessagePacket messagePacket
        , ReentrantLock lockForClientsConcurrency) throws IOException {
        byte[] serverHeader = messagePacket.serverHeader;
        byte[] sendJsonBytes = messagePacket.sendJsonBytes;
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.decodeHeader(serverHeader);
        int type = headerConverter.messageType;
        if (type == Type.MESSAGETOCLIENT.getValue() || type == Type.IMAGETOCLIENT.getValue()
            || type == Type.CLIENTCLOSEMESSAGE.getValue()) {
            lockForClientsConcurrency.lock();
            try {
                for (Socket clinet : clients.keySet()) {
                    if (type == Type.IMAGETOCLIENT.getValue() && clinet == sock) {
                        continue;
                    }
                    RunnableServer.clientReceiveMessageNumPlus1(clinet);
                    DataOutputStream dos = new DataOutputStream(clinet.getOutputStream());
                    dos.write(serverHeader, 0, serverHeader.length);
                    dos.write(sendJsonBytes, 0, sendJsonBytes.length);
                    dos.flush();
                }
            }finally {
                lockForClientsConcurrency.unlock();
            }
        }
    }





}
