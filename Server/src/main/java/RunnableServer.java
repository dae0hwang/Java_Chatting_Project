import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class RunnableServer implements Runnable {
    protected Socket sock;
    private static ObjectMapper objectMapper = new ObjectMapper();
    protected static HashMap<Socket, Integer> clients = new HashMap<>();
    //client number of send message
    private ThreadLocal<Integer> sendNum = new ThreadLocal<>();
    //recieveMessage num++
    private static  void receiveNumPlus(Socket socket) {
        clients.put(socket, clients.getOrDefault(socket, 0) + 1);
    }
    RunnableServer(Socket socket) {
        this.sock = socket;
    }

    @Override
    public void run() {
        sendNum.set(0);
        InputStream fromClient;
        DataInputStream dis;
        String name = null;
        //implement header for encoding and decoding.
        Header makeHeader = new Header();
        try {
            System.out.println(sock + ": 연결됨");
            while (true) {
                fromClient = sock.getInputStream();
                dis = new DataInputStream(fromClient);
                //first input message header
                byte[] header = recHeader(dis);
                //second input message body
                byte[] contentsBytes = recBody(dis, header);
                //handlePacketByType - set name
                name = handlePacketByType1(header, contentsBytes);
                //handlePacketByType - Send Message or Image,
                //sendNum ++
                Object[] Packets = handlePacketByType2(header, contentsBytes, name, sendNum);
                byte[] sendJsonBytes = (byte[]) Packets[0];
                byte[] serverHeader = (byte[]) Packets[1];
                //broadcastAllUser
                System.out.println(Arrays.toString(sendJsonBytes));
                System.out.println(Arrays.toString(serverHeader));
                broadcastAllUser(clients, sock, serverHeader, sendJsonBytes);
            }
        } catch (IOException ex) {
            System.out.println(sock + ": 에러(" + ex + ")");
            System.out.println(name+"나갔음. ");
        } finally {
            try {
                //remove lock
                int recieveNum = clients.get(sock);
                Server.lock.lock();
                try {
                    clients.remove(sock);
                }finally {
                    Server.lock.unlock();
                }
                //when socket is closed. server send information of client to others.
                //implement close Header, Body.
                byte[] sendJsonBytes = implementCloseBody(name, sendNum.get(), recieveNum);
                byte[] serverHeader = implementCloseHeader(sendJsonBytes);
                // send close message to clients.
                broadcastAllUser(clients, sock, serverHeader, sendJsonBytes);
                fromClient = null;
            } catch (IOException ex) {
            }
        }
    }

    private static byte[] implementBody(String name, byte[] contentsBytes) throws JsonProcessingException {
        Body outputBody = new Body();
        outputBody.setName(name);
        outputBody.setBytes(contentsBytes);
        return objectMapper.writeValueAsBytes(outputBody);
    }

    private static byte[] implementServerHeader(int type, byte[] sendJsonBytes) {
        Header makeHeader = new Header();
        int serverType = 0;
        int serverLength = sendJsonBytes.length;
        if (type == Type.MESSAGETOSERVER.getValue()) {
            serverType = Type.MESSAGETOCLIENT.getValue();
        } else if (type == Type.IMAGETOSERVER.getValue()) {
            serverType = Type.IMAGETOCLIENT.getValue();
        }
        makeHeader.encodeHeader(serverLength, serverType);
        return makeHeader.bytesHeader;
    }

    private static byte[] implementCloseBody(String name, int sendNum, int recieveNum) throws JsonProcessingException {
        Body outputBody = new Body();
        outputBody.setName(name);
        outputBody.setSendNum(sendNum);
        outputBody.setRecieveNum(recieveNum);
        return objectMapper.writeValueAsBytes(outputBody);
    }

    private static byte[] implementCloseHeader( byte[] sendJsonBytes) {
        Header makeHeader = new Header();
        int serverLength = sendJsonBytes.length;
        int serverType = Type.CLIENTCLOSEMESSAGE.getValue();
        makeHeader.encodeHeader(serverLength, serverType);
        return makeHeader.bytesHeader;
    }

    private static byte[] recHeader(DataInputStream dis) throws IOException {
        byte[] header = new byte[8];
        Header makeHeader = new Header();
        dis.readFully(header, 0, 8);
        makeHeader.decodeHeader(header);
        return header;
    }

    private static byte[] recBody(DataInputStream dis, byte[] header) throws IOException {
        Header makeHeader = new Header();
        makeHeader.decodeHeader(header);
        int length = makeHeader.messageLength;
        byte[] receiveBytes = new byte[length];
        dis.readFully(receiveBytes,0,length);
        Body inputBody = objectMapper.readValue(receiveBytes, Body.class);
        byte[] contentsBytes = inputBody.getBytes();
        System.out.println("server message received message header and body");
        return contentsBytes;
    }

    private static String handlePacketByType1(byte[] header, byte[] contentsBytes) {
        String name = null;
        Header makeHeader = new Header();
        makeHeader.decodeHeader(header);
        int type = makeHeader.messageType;
        //process input data
        //type == 1111 -> set name
        if (type == Type.RESISTERNAME.getValue()) {
            name = new String(contentsBytes);
        }
        return name;
    }

    private static Object[] handlePacketByType2(byte[] header, byte[] contentsBytes, String name, ThreadLocal<Integer> sendNum) throws JsonProcessingException {
        Header makeHeader = new Header();
        makeHeader.decodeHeader(header);
        int type = makeHeader.messageType;
        int length = makeHeader.messageLength;
        byte[] sendJsonBytes = new byte[length];
        byte[] serverHeader = new byte[8];
        if (type != Type.RESISTERNAME.getValue()) {
            //sendmessage num ++
            sendNum.set(sendNum.get() + 1);
            //implement sendJsonBytes
            sendJsonBytes = implementBody(name, contentsBytes);
            //implement serverHeader
            serverHeader = implementServerHeader(type, sendJsonBytes);
        }
        Object[] objects = new Object[2];
        objects[0] = sendJsonBytes;
        objects[1] = serverHeader;
        return objects;
    }

    private static void broadcastAllUser(HashMap<Socket, Integer> clients, Socket sock,
                                         byte[] serverHeader, byte[] sendJsonBytes) throws IOException {
        // send close message to clients.
        Header makeHeader = new Header();
        makeHeader.decodeHeader(serverHeader);
        int type = makeHeader.messageType;
        if (type == Type.MESSAGETOCLIENT.getValue() || type == Type.IMAGETOCLIENT.getValue() || type == Type.CLIENTCLOSEMESSAGE.getValue()) {
            Server.lock.lock();
            try {
                for (Socket s : clients.keySet()) {
                    if (type == Type.IMAGETOCLIENT.getValue() && s == sock) {
                        continue;
                    }
                    Server.lock.lock();
                    try {
                        receiveNumPlus(s);
                    }
                    finally {
                        Server.lock.unlock();
                    }
                    OutputStream toClient = s.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(toClient);
                    dos.write(serverHeader, 0, 8);
                    dos.write(sendJsonBytes, 0, sendJsonBytes.length);
                    dos.flush();
                }
            }finally {
                Server.lock.unlock();
            }
        }
    }
}