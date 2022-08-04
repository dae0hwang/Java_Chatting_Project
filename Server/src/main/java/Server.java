import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    protected static ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5510);
        System.out.println(serverSocket + " Creation of Server socket");

        while (true) {
            System.out.println(RunnableServer.clients);
            Socket client = serverSocket.accept();
            //lock
            lock.lock();
            try {
                RunnableServer.clients.put(client,0);
            }finally {
                lock.unlock();
            }
            //Thread start
            RunnableServer myServer = new RunnableServer(client);
            Thread severThread = new Thread(myServer);
            severThread.start();
        }
    }
}