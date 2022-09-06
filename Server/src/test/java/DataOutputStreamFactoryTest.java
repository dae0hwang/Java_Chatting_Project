import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataOutputStreamFactoryTest {
    @Disabled
    @Test
    void createDataOutputStream() throws IOException {
        DataOutputStreamFactory dataOutputStreamFactory = new DataOutputStreamFactory();
        //given
        Socket socket = mock(Socket.class);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        //when
        DataOutputStream expectedDataOutputStream = dataOutputStreamFactory.createDataOutputStream(socket);

//        assertSame(expectedDataOutputStream, dataOutputStream);
        assertEquals(expectedDataOutputStream, dataOutputStream);
    }
}