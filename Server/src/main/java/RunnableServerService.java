import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;


public class RunnableServerService {
    ObjectMapper objectMapper = new ObjectMapper();
    DataOutputStreamFactory dataOutputStreamFactory = new DataOutputStreamFactory();

    public byte[] recieveMessageHeaderFromClient(DataInputStream dis) throws IOException {
        byte[] header = new byte[8];
        dis.readFully(header, 0, header.length);
        return header;
    }

    public byte[] receiveMessageBodyFromClient(DataInputStream dis, byte[] header) throws IOException {
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.decodeHeader(header);
        int length = headerConverter.messageLength;
        byte[] receiveBytes = new byte[length];
        dis.readFully(receiveBytes, 0, length);
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

    public byte[] implementCloseHeader(byte[] sendJsonBytes) {
        HeaderConverter headerConverter = new HeaderConverter();
        int serverLength = sendJsonBytes.length;
        int serverType = Type.CLIENTCLOSEMESSAGE.getValue();
        headerConverter.encodeHeader(serverLength, serverType);
        return headerConverter.bytesHeader;
    }

    public Type checkMessageType(byte[] serverHeader) {
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.decodeHeader(serverHeader);
        int type = headerConverter.messageType;
        if (type == Type.MESSAGETOCLIENT.getValue()) {
            return Type.MESSAGETOCLIENT;
        } else if (type == Type.IMAGETOCLIENT.getValue()) {
            return Type.IMAGETOCLIENT;
        } else if (type == Type.CLIENTCLOSEMESSAGE.getValue()) {
            return Type.CLIENTCLOSEMESSAGE;
        }
        return null;
    }

    public void broadcastAllUser(Type messageType, HashMap<Socket, Integer> clients
        , DataOutputStreamFactory dataOutputStreamFactory, Socket socket
        , byte[] sendJsonBytes, byte[] serverHeader, ReentrantLock lockForClientsConcurrency)
        throws IOException {
        if (messageType == Type.MESSAGETOCLIENT || messageType == Type.CLIENTCLOSEMESSAGE) {
            lockForClientsConcurrency.lock();
            try {
                for (Socket client : clients.keySet()) {
                    DataOutputStream dataOutputStream = dataOutputStreamFactory.createDataOutputStream(client);
                    dataOutputStream.write(serverHeader, 0, serverHeader.length);
                    dataOutputStream.write(sendJsonBytes, 0, sendJsonBytes.length);
                    dataOutputStream.flush();
                }
            } finally {
                lockForClientsConcurrency.unlock();
            }
        } else if (messageType == Type.IMAGETOCLIENT) {
            lockForClientsConcurrency.lock();
            try {
                for (Socket client : clients.keySet()) {
                    if (client == socket) {
                        continue;
                    }
                    DataOutputStream dataOutputStream = dataOutputStreamFactory.createDataOutputStream(client);
                    dataOutputStream.write(serverHeader, 0, serverHeader.length);
                    dataOutputStream.write(sendJsonBytes, 0, sendJsonBytes.length);
                    dataOutputStream.flush();
                }
            } finally {
                lockForClientsConcurrency.unlock();
            }
        }
    }

    public void treatReceiveNumPlus(Type messageType, HashMap<Socket, Integer> clients
        , Socket socket, ReentrantLock lockForClientsConcurrency) {
        if (messageType == Type.MESSAGETOCLIENT || messageType == Type.CLIENTCLOSEMESSAGE) {
            lockForClientsConcurrency.lock();
            try {
                for (Socket client : clients.keySet()) {
                    RunnableServer.clientReceiveMessageNumPlus1(client);
                }
            } finally {
                lockForClientsConcurrency.unlock();
            }
        } else if (messageType == Type.IMAGETOCLIENT) {
            lockForClientsConcurrency.lock();
            try {
                for (Socket clinet : clients.keySet()) {
                    if (clinet == socket) {
                        continue;
                    }
                    RunnableServer.clientReceiveMessageNumPlus1(clinet);
                }
            } finally {
                lockForClientsConcurrency.unlock();
            }
        }
    }
}
