import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5510);
        System.out.println(serverSocket + " 서버 소켓 생성");

        while (true) {
            Socket client = serverSocket.accept();
            //여기도 수정.
            RunnableServer.clients.add(client);

            //Thread start
            RunnableServer myServer = new RunnableServer(client);
            Thread severThread = new Thread(myServer);
            severThread.start();
        }
    }
}
