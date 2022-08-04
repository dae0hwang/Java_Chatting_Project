import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5510);
        System.out.println(serverSocket + " Creation of Server socket");

        while (true) {
            System.out.println(RunnableServer.clients);
            Socket client = serverSocket.accept();
            //여기다가 락걸어줘야 함.
            RunnableServer.clients.put(client,0);

            //Thread start
            RunnableServer myServer = new RunnableServer(client);
            Thread severThread = new Thread(myServer);
            severThread.start();
        }
    }
}