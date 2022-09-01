import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RunnableServer implements Runnable {
    protected Socket sock;
    private String name = null;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static HashMap<Socket, Integer> clients = new HashMap<>();
    private static ReentrantLock lockForClientsConcurrency = new ReentrantLock();
    private static void removeClientInClients(Socket socket) {
        lockForClientsConcurrency.lock();
        try {
            clients.remove(socket);
        }finally {
            lockForClientsConcurrency.unlock();
        }
    }
    protected static void addClientAndSetRecieveNumInClients(Socket socket) {
        lockForClientsConcurrency.lock();
        int initialRecieveNum = 0;
        try {
            clients.put(socket, initialRecieveNum);
        }
        finally {
            lockForClientsConcurrency.unlock();
        }
    }
    private ThreadLocal<Integer> threadLocalClientSendMessageNum = new ThreadLocal<>();
    protected static  void clientReceiveMessageNumPlus1(Socket socket) {
        clients.put(socket, clients.getOrDefault(socket, 0) + 1);
    }

    RunnableServer(Socket socket) {
        this.sock = socket;
    }

    @Override
    public void run() {
        RunnableServerService serverService = new RunnableServerService();
        threadLocalClientSendMessageNum.set(0);
        InputStream fromClient;
        DataInputStream dataInputStream;
        try {
            System.out.println(sock + ": is connected");
            while (true) {
                fromClient = sock.getInputStream();
                dataInputStream = new DataInputStream(fromClient);
//                byte[] header = recieveMessageHeaderFromClient(dataInputStream);
                byte[] header = serverService.recieveMessageHeaderFromClient(dataInputStream);
//                byte[] messageBodyBytes = receiveMessageBodyFromClient(dataInputStream, header);
                byte[] messageBodyBytes = serverService.receiveMessageBodyFromClient(dataInputStream, header);
//                Type messageType = readType(header);
                Type messageType = serverService.readType(header);
                switch (messageType) {
                    case RESISTERNAME:
//                        resisterName(messageBodyBytes);
                        this.name = serverService.resisterName(messageBodyBytes);
                        break;
                    case MESSAGETOSERVER:
                        threadLocalClientSendMessageNum.set(threadLocalClientSendMessageNum.get()+1);
//                        byte[] stringMessageJsonBytes = implementStringMessageJsonBytes(messageBodyBytes);
                        byte[] stringMessageJsonBytes
                            = serverService.implementStringMessageJsonBytes(messageBodyBytes, this.name);
//                        byte[] stringMessageServerHeader
//                            = implementStringMessageServerHeaderBytes(stringMessageJsonBytes);
                        byte[] stringMessageServerHeader
                            = serverService.implementStringMessageServerHeaderBytes(stringMessageJsonBytes);
                        MessagePacket stringMessagePacket =
                            new MessagePacket(stringMessageJsonBytes, stringMessageServerHeader);
                        //wait
//                        broadcastAllUser(clients, sock, stringMessagePacket);
                        serverService.broadcastAllUser(clients, sock, stringMessagePacket,lockForClientsConcurrency);
                        break;
                    case IMAGETOSERVER:
                        threadLocalClientSendMessageNum.set(threadLocalClientSendMessageNum.get()+1);
//                        byte[] imageMessageJsonBytes
//                            = implementImageMessageJsonBytes(messageBodyBytes);
                        byte[] imageMessageJsonBytes
                            = serverService.implementImageMessageJsonBytes(messageBodyBytes,this.name);
//                        byte[] imageMessageServerHeader =
//                            implementImageMessageServerHeaderBytes(imageMessageJsonBytes);
                        byte[] imageMessageServerHeader =
                            serverService.implementImageMessageServerHeaderBytes(imageMessageJsonBytes);
                        MessagePacket ImagemssagePacket =
                            new MessagePacket(imageMessageJsonBytes, imageMessageServerHeader);
                        //wait
//                        broadcastAllUser(clients, sock, ImagemssagePacket);
                        serverService.broadcastAllUser(clients, sock, ImagemssagePacket, lockForClientsConcurrency);
                        break;
                }
            }
        } catch (IOException ex) {
            System.out.println(sock + ": error(" + ex + ")");
            System.out.println(name+"is out. ");
        } finally {
            try {
                int clientRecieveMessageNum = clients.get(sock);
                removeClientInClients(sock);
//                MessagePacket closeMessagePacket = implementCloseMessagePacket(clientRecieveMessageNum);
                byte[] closeMessageJsonBytes
                    = serverService.implementCloseBody
                    (this.name, threadLocalClientSendMessageNum.get(), clientRecieveMessageNum);
                byte[] closeMessageServerHeader = serverService.implementCloseHeader(closeMessageJsonBytes);
                MessagePacket closeMessagePacket = new MessagePacket(closeMessageJsonBytes, closeMessageServerHeader);
                //wait
//                broadcastAllUser(clients, sock, closeMessagePacket);
                serverService.broadcastAllUser(clients, sock, closeMessagePacket,lockForClientsConcurrency);
            } catch (IOException ex) {
            }
        }
    }

    private static byte[] recieveMessageHeaderFromClient(DataInputStream dis) throws IOException {
        byte[] header = new byte[8];
        dis.readFully(header, 0, header.length);
        return header;
    }

    private static byte[] receiveMessageBodyFromClient(DataInputStream dis, byte[] header) throws IOException {
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.decodeHeader(header);
        int length = headerConverter.messageLength;
        byte[] receiveBytes = new byte[length];
        dis.readFully(receiveBytes,0,length);
        System.out.println("server message received message header and body");
        return receiveBytes;
    }

    private static Type readType(byte[] header) {
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

    private void resisterName(byte[] messageBodyBytes) throws IOException {
        ResisterNameMessageBodyDto resisterNameMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, ResisterNameMessageBodyDto.class);
        this.name = resisterNameMessageBodyDto.getName();
    }

    private byte[] implementStringMessageJsonBytes(byte[] messageBodyBytes) throws IOException {
        StringMessageBodyDto stringMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, StringMessageBodyDto.class);
        stringMessageBodyDto.setName(this.name);
        byte[] stringMessageJsonBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
        return stringMessageJsonBytes;
    }

    private static byte[] implementStringMessageServerHeaderBytes(byte[] stringMessageJsonBytes) {
        HeaderConverter headerConverter = new HeaderConverter();
        int serverType = Type.MESSAGETOCLIENT.getValue();
        int serverLength = stringMessageJsonBytes.length;
        headerConverter.encodeHeader(serverLength, serverType);
        return headerConverter.bytesHeader;
    }

    private byte[] implementImageMessageJsonBytes(byte[] messageBodyBytes) throws IOException {
        ImageMessageBodyDto imageMessageBodyDto =
            objectMapper.readValue(messageBodyBytes, ImageMessageBodyDto.class);
        imageMessageBodyDto.setName(this.name);
        byte[] imageMessageJsonBytes = objectMapper.writeValueAsBytes(imageMessageBodyDto);
        return imageMessageJsonBytes;
    }

    private static byte[] implementImageMessageServerHeaderBytes(byte[] imageMessageJsonBytes) {
        HeaderConverter headerConverter = new HeaderConverter();
        int serverType = Type.IMAGETOCLIENT.getValue();
        int serverLength = imageMessageJsonBytes.length;
        headerConverter.encodeHeader(serverLength, serverType);
        return headerConverter.bytesHeader;
    }

//    private MessagePacket implementCloseMessagePacket(int clientRecieveMessageNum) throws JsonProcessingException {
//        byte[] sendJsonBytes = implementCloseBody(this.name, threadLocalClientSendMessageNum.get(),
//            clientRecieveMessageNum);
//        byte[] serverHeader = implementCloseHeader(sendJsonBytes);
//        MessagePacket messagePacket = new MessagePacket(sendJsonBytes, serverHeader);
//        return messagePacket;
//    }

    private static byte[] implementCloseBody(String name, int sendNum, int recieveNum) throws JsonProcessingException {
        CloseMessageBodyDto closeMessageBodyDto = new CloseMessageBodyDto();
        closeMessageBodyDto.setName(name);
        closeMessageBodyDto.setSendNum(sendNum);
        closeMessageBodyDto.setRecieveNum(recieveNum);
        return objectMapper.writeValueAsBytes(closeMessageBodyDto);
    }

    private static byte[] implementCloseHeader( byte[] sendJsonBytes) {
        HeaderConverter headerConverter = new HeaderConverter();
        int serverLength = sendJsonBytes.length;
        int serverType = Type.CLIENTCLOSEMESSAGE.getValue();
        headerConverter.encodeHeader(serverLength, serverType);
        return headerConverter.bytesHeader;
    }

    private static void broadcastAllUser(HashMap<Socket, Integer> clients, Socket sock,
                                                MessagePacket messagePacket) throws IOException {
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
                    clientReceiveMessageNumPlus1(clinet);
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