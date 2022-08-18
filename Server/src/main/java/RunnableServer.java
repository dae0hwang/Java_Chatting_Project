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
    private static  void clientReceiveMessageNumPlus1(Socket socket) {
        clients.put(socket, clients.getOrDefault(socket, 0) + 1);
    }

    RunnableServer(Socket socket) {
        this.sock = socket;
    }

    @Override
    public void run() {
        threadLocalClientSendMessageNum.set(0);
        InputStream fromClient;
        DataInputStream dataInputStream;
        try {
            System.out.println(sock + ": 연결됨");
            while (true) {
                fromClient = sock.getInputStream();
                dataInputStream = new DataInputStream(fromClient);
                byte[] header = recieveMessageHeaderFromClient(dataInputStream);
                MessageBody recieveMessageBody = recieveMessageBodyFromClient(dataInputStream, header);
                //Processing message Body to set name or make message packet(header and body)
                MessagePacket messagePacket =
                processingMessageBody(recieveMessageBody, header, threadLocalClientSendMessageNum);
                if (messagePacket != null) {
                    broadcastAllUser(clients, sock, messagePacket);
                }
            }
        } catch (IOException ex) {
            System.out.println(sock + ": error(" + ex + ")");
            System.out.println(name+"is out. ");
        } finally {
            try {
                int clientRecieveMessageNum = clients.get(sock);
                removeClientInClients(sock);
                MessagePacket closeMessagePacket = implementCloseMessagePacket(clientRecieveMessageNum);
                broadcastAllUser(clients, sock, closeMessagePacket);
            } catch (IOException ex) {
            }
        }
    }

    private static byte[] recieveMessageHeaderFromClient(DataInputStream dis) throws IOException {
        byte[] header = new byte[8];
        dis.readFully(header, 0, header.length);
        return header;
    }

    private static MessageBody recieveMessageBodyFromClient(DataInputStream dis, byte[] header) throws IOException {
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.decodeHeader(header);
        int length = headerConverter.messageLength;
        byte[] receiveBytes = new byte[length];
        dis.readFully(receiveBytes,0,length);
        MessageBody inputMessageBody = objectMapper.readValue(receiveBytes, MessageBody.class);
        System.out.println("server message received message header and body");
        return inputMessageBody;
    }

    private MessagePacket processingMessageBody(MessageBody messageBody, byte[] header,
                                                ThreadLocal<Integer> threadLocalClientSendMessageNum)
        throws JsonProcessingException {
        HeaderConverter headerConverter = new HeaderConverter();
        headerConverter.decodeHeader(header);
        int type = headerConverter.messageType;
        if (type == Type.RESISTERNAME.getValue()) {
            this.name = new String(messageBody.getBytes());
            return null;
        } else {
            threadLocalClientSendMessageNum.set(threadLocalClientSendMessageNum.get()+1);
            byte[] sendJsonBytes = implementMessageBody(this.name, messageBody.getBytes());
            byte[] serverHeader = implementServerHeader(type, sendJsonBytes);
            MessagePacket messagePacket = new MessagePacket(sendJsonBytes, serverHeader);
            return messagePacket;
        }
    }

    class MessagePacket {
        byte[] sendJsonBytes;
        byte[] serverHeader;
        MessagePacket(byte[] sendJsonBytes, byte[] serverHeader) {
            this.sendJsonBytes = sendJsonBytes;
            this.serverHeader = serverHeader;
        }
    }

    private static byte[] implementMessageBody(String name, byte[] contentsBytes) throws JsonProcessingException {
        MessageBody outputBody = new MessageBody();
        outputBody.setName(name);
        outputBody.setBytes(contentsBytes);
        return objectMapper.writeValueAsBytes(outputBody);
    }

    private static byte[] implementServerHeader(int type, byte[] sendJsonBytes) {
        HeaderConverter headerConverter = new HeaderConverter();
        int serverType = 0;
        int serverLength = sendJsonBytes.length;
        if (type == Type.MESSAGETOSERVER.getValue()) {
            serverType = Type.MESSAGETOCLIENT.getValue();
        } else if (type == Type.IMAGETOSERVER.getValue()) {
            serverType = Type.IMAGETOCLIENT.getValue();
        }
        headerConverter.encodeHeader(serverLength, serverType);
        return headerConverter.bytesHeader;
    }

    private MessagePacket implementCloseMessagePacket(int clientRecieveMessageNum) throws JsonProcessingException {
        byte[] sendJsonBytes = implementCloseBody(this.name, threadLocalClientSendMessageNum.get(),
            clientRecieveMessageNum);
        byte[] serverHeader = implementCloseHeader(sendJsonBytes);
        MessagePacket messagePacket = new MessagePacket(sendJsonBytes, serverHeader);
        return messagePacket;
    }

    private static byte[] implementCloseBody(String name, int sendNum, int recieveNum) throws JsonProcessingException {
        MessageBody outputBody = new MessageBody();
        outputBody.setName(name);
        outputBody.setSendNum(sendNum);
        outputBody.setRecieveNum(recieveNum);
        return objectMapper.writeValueAsBytes(outputBody);
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