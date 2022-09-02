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
    protected static void removeClientInClients(Socket socket) {
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
                byte[] header = serverService.recieveMessageHeaderFromClient(dataInputStream);
                byte[] messageBodyBytes = serverService.receiveMessageBodyFromClient(dataInputStream, header);
                Type messageType = serverService.readType(header);
                switch (messageType) {
                    case RESISTERNAME:
                        this.name = serverService.resisterName(messageBodyBytes);
                        break;
                    case MESSAGETOSERVER:
                        threadLocalClientSendMessageNum.set(threadLocalClientSendMessageNum.get()+1);
                        byte[] stringMessageJsonBytes
                            = serverService.implementStringMessageJsonBytes(messageBodyBytes, this.name);
                        byte[] stringMessageServerHeader
                            = serverService.implementStringMessageServerHeaderBytes(stringMessageJsonBytes);
                        MessagePacket stringMessagePacket =
                            new MessagePacket(stringMessageJsonBytes, stringMessageServerHeader);
                        serverService.broadcastAllUser(clients, sock, stringMessagePacket,lockForClientsConcurrency);
                        break;
                    case IMAGETOSERVER:
                        threadLocalClientSendMessageNum.set(threadLocalClientSendMessageNum.get()+1);
                        byte[] imageMessageJsonBytes
                            = serverService.implementImageMessageJsonBytes(messageBodyBytes,this.name);
                        byte[] imageMessageServerHeader =
                            serverService.implementImageMessageServerHeaderBytes(imageMessageJsonBytes);
                        MessagePacket ImagemssagePacket =
                            new MessagePacket(imageMessageJsonBytes, imageMessageServerHeader);
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
                byte[] closeMessageJsonBytes
                    = serverService.implementCloseBody
                    (this.name, threadLocalClientSendMessageNum.get(), clientRecieveMessageNum);
                byte[] closeMessageServerHeader = serverService.implementCloseHeader(closeMessageJsonBytes);
                MessagePacket closeMessagePacket = new MessagePacket(closeMessageJsonBytes, closeMessageServerHeader);
                serverService.broadcastAllUser(clients, sock, closeMessagePacket,lockForClientsConcurrency);
            } catch (IOException ex) {
            }
        }
    }
}