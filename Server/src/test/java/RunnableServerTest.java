import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class RunnableServerTest {

    @Test
    void removeClientInClients() {
        //given
        Socket socket1 = new Socket();
        Socket socket2 = new Socket();
        Socket socket3 = new Socket();
        RunnableServer.addClientAndSetRecieveNumInClients(socket1);
        RunnableServer.addClientAndSetRecieveNumInClients(socket2);
        RunnableServer.addClientAndSetRecieveNumInClients(socket3);

        HashMap<Socket, Integer> result = new HashMap<>();
        result.put(socket2, 0);
        result.put(socket3, 0);

        //when
        RunnableServer.removeClientInClients(socket1);

        //then
        assertEquals(RunnableServer.clients, result);
    }
}