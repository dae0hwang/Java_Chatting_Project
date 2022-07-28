import java.io.*;
import java.net.Socket;
import java.util.Arrays;

// Thread for receive message from Server
class ServerHandler implements Runnable {
    Socket sock = null;

    public ServerHandler(Socket sock) {
        this.sock = sock;
    }

    public void run() {
        InputStream fromServer = null;
        DataInputStream dis = null;
        try {
            while (true) {
                fromServer = sock.getInputStream();
                dis = new DataInputStream(fromServer);

                //First input Header
                byte[] header = new byte[8];
                dis.readFully(header, 0, 8);
                Header.decodeHeader(header);
                int length = Header.messageLength;
                int type = Header.messageType;
                byte[] receiveBytes = new byte[length];
                
                ///type = 3333이면 -> name and Message
                if (type == 3333) {
                    String name = dis.readUTF();
                    dis.readFully(receiveBytes, 0, length);
                    String receiveMessage = new String(receiveBytes);
                    System.out.println(name + ": " + receiveMessage);
                }
                //type = 4444 -> close Message
                else if (type == 4444) {
                    System.out.println("타입4로 진입함");
                    String name = dis.readUTF();
                    int sendNum = dis.readInt();
                    System.out.println(name+"이 나갔습니다 || 보낸 메세지 수: "+sendNum );
                }
            }
        } catch (IOException ex) {
            System.out.println("연결 종료 (" + ex + ")");
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
}