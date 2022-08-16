import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5510);
        System.out.println(serverSocket + " Creation of Server socket");
        while (true) {
            Socket client = serverSocket.accept();
            RunnableServer.addClientAndSetRecieveNumInClients(client);
            //Thread RunnableServer start
            RunnableServer myServer = new RunnableServer(client);
            Thread severThread = new Thread(myServer);
            severThread.start();
        }
    }
}