import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RunnableServerServiceTest {

    ObjectMapper objectMapper;
    RunnableServerService serverService;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    Socket socket;
    HeaderConverter headerConverter;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        serverService = new RunnableServerService();
        dataOutputStream = mock(DataOutputStream.class);
        dataInputStream = mock(DataInputStream.class);
        socket = mock(Socket.class);
        headerConverter = new HeaderConverter();
    }

    @Test
    void recieveMessageHeaderFromClient() throws IOException {
        //given
        byte[] header = {0, 1, 2, 3, 4, 5, 6, 7};
        doAnswer(mockData -> {
            System.arraycopy(header, 0, mockData.getArguments()[0], 0, 8);
            return null;
        }).when(dataInputStream).readFully(any(byte[].class), eq(0), eq(8));

        when(socket.getInputStream()).thenReturn(dataInputStream);

        //when
        byte[] expected = serverService.recieveMessageHeaderFromClient(dataInputStream);

        //then
        assertArrayEquals(expected, header);
    }

    @Test
    void receiveMessageBodyFromClient() throws IOException {

        byte[] header = {0, 0, 0, 8, 0, 0, 0, 0};
        byte[] receiveData = {0, 1, 2, 3, 4, 5, 6, 7};
        doAnswer(mockData -> {
            System.arraycopy(receiveData, 0, mockData.getArguments()[0], 0, 8);
            return null;
        }).when(dataInputStream).readFully(any(byte[].class), eq(0), eq(8));

        when(socket.getInputStream()).thenReturn(dataInputStream);
        //when
        byte[] recieveBytes = serverService.receiveMessageBodyFromClient(dataInputStream, header);

        //then
        assertArrayEquals(receiveData, receiveData);
    }

    @Test
    void readType() {
        //given
        byte[] header1 = {0, 0, 0, 0, 0, 0, 4, 87};
        Type result1 = Type.RESISTERNAME;

        byte[] header2 = {0, 0, 0, 0, 0, 0, 8, -82};
        Type result2 = Type.MESSAGETOSERVER;

        byte[] header3 = {0, 0, 0, 0, 0, 0, 21, -77};
        Type result3 = Type.IMAGETOSERVER;

        byte[] header4 = {0, 0, 0, 0, 0, 0, 26, 10};
        Type result4 = null;

        //when1
        Type expected1 = serverService.readType(header1);
        //then1
        assertEquals(expected1, result1);

        //when2
        Type expected2 = serverService.readType(header2);
        //then2
        assertEquals(expected2, result2);

        //when3
        Type expected3 = serverService.readType(header3);
        //then4
        assertEquals(expected3, result3);

        //when4
        Type expected4 = serverService.readType(header4);
        //then4
        assertEquals(expected4, result4);
    }

    @Test
    void resisterName() throws IOException {
        //given
        ResisterNameMessageBodyDto resisterNameMessageBodyDto = new ResisterNameMessageBodyDto();
        resisterNameMessageBodyDto.setName("name");
        byte[] messageBody = objectMapper.writeValueAsBytes(resisterNameMessageBodyDto);
        String reuslt = "name";

        //when
        String expectedName = serverService.resisterName(messageBody);

        //then
        assertEquals(expectedName, reuslt);
    }

    @Test
    void implementStringMessageJsonBytes() throws IOException {
        //given
        StringMessageBodyDto stringMessageBodyDto = new StringMessageBodyDto();
        String str = "hi";
        stringMessageBodyDto.setStringMessageBytes(str.getBytes());
        byte[] stringMessageBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
        stringMessageBodyDto.setName("name");
        byte[] result = objectMapper.writeValueAsBytes(stringMessageBodyDto);

        //when
        byte[] expected = serverService.implementStringMessageJsonBytes(stringMessageBytes, "name");

        //then
        assertArrayEquals(expected, result);
    }

    @Test
    void implementStringMessageServerHeaderBytes() {
        //given
        byte[] stringMessageJsonBytes = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] reusultHeader = {0, 0, 0, 8, 0, 0, 13, 5};

        //when
        byte[] expectedHeader = serverService.implementStringMessageServerHeaderBytes(stringMessageJsonBytes);

        //then
        assertArrayEquals(expectedHeader, reusultHeader);
    }

    @Test
    void implementImageMessageJsonBytes() throws IOException {
        //given
        ImageMessageBodyDto imageMessageBodyDto = new ImageMessageBodyDto();
        byte[] imageBytes = {0, 1, 2, 3, 4, 5, 6, 7};
        imageMessageBodyDto.setImageMessageBytes(imageBytes);
        byte[] imageMessageBytes = objectMapper.writeValueAsBytes(imageMessageBodyDto);
        imageMessageBodyDto.setName("name");
        byte[] result = objectMapper.writeValueAsBytes(imageMessageBodyDto);

        //when
        byte[] expected = serverService.implementImageMessageJsonBytes(imageMessageBytes, "name");

        //then
        assertArrayEquals(expected, result);
    }

    @Test
    void implementImageMessageServerHeaderBytes() {
        //given
        byte[] imageMessageJsonBytes = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] reusultHeader = {0, 0, 0, 8, 0, 0, 26, 10};

        //when
        byte[] expectedHeader = serverService.implementImageMessageServerHeaderBytes(imageMessageJsonBytes);

        //then
        assertArrayEquals(expectedHeader, reusultHeader);
    }

    @Test
    void implementCloseBody() throws JsonProcessingException {
        //given
        CloseMessageBodyDto closeMessageBodyDto = new CloseMessageBodyDto();
        closeMessageBodyDto.setName("name");
        closeMessageBodyDto.setSendNum(10);
        closeMessageBodyDto.setRecieveNum(10);
        byte[] result = objectMapper.writeValueAsBytes(closeMessageBodyDto);

        //when
        byte[] expected = serverService.implementCloseBody("name", 10, 10);

        //then
        assertArrayEquals(expected, result);
    }

    @Test
    void implementCloseHeader() {
        //given
        byte[] sendJsonBytes = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] result = {0, 0, 0, 8, 0, 0, 17, 92};

        //when
        byte[] expected = serverService.implementCloseHeader(sendJsonBytes);

        //then
        assertArrayEquals(expected, result);
    }

    @Test
    void checkMessageType() {
        //given
        byte[] serverHeader1 = {0, 0, 0, 0, 0, 0, 13, 5};
        byte[] serverHeader2 = {0, 0, 0, 0, 0, 0, 26, 10};
        byte[] serverHeader3 = {0, 0, 0, 0, 0, 0, 17, 92};
        byte[] serverHeader4 = {0, 0, 0, 0, 0, 0, 21, -77};

        Type result1 = Type.MESSAGETOCLIENT;
        Type result2 = Type.IMAGETOCLIENT;
        Type result3 = Type.CLIENTCLOSEMESSAGE;
        Type result4 = null;

        //when1
        Type expectedType1 = serverService.checkMessageType(serverHeader1);
        //then1
        assertEquals(expectedType1, result1);

        //when1
        Type expectedType2 = serverService.checkMessageType(serverHeader2);
        //then1
        assertEquals(expectedType2, result2);

        //when1
        Type expectedType3 = serverService.checkMessageType(serverHeader3);
        //then1
        assertEquals(expectedType3, result3);

        //when1
        Type expectedType4 = serverService.checkMessageType(serverHeader4);
        //then1
        assertEquals(expectedType4, result4);

    }

    @Test
    void broadcastAllUser() throws IOException {
        //given
        ReentrantLock lock = mock(ReentrantLock.class);
        HashMap<Socket, Integer> clients = new HashMap<>();
        Socket socket1 = mock(Socket.class);
        Socket socket2 = mock(Socket.class);
        Socket socket3 = mock(Socket.class);
        clients.put(socket1, 0);
        clients.put(socket2, 0);
        clients.put(socket3, 0);
        DataOutputStreamFactory dataOutputStreamFactory = mock(DataOutputStreamFactory.class);
        when(dataOutputStreamFactory.createDataOutputStream(any(Socket.class))).thenReturn(dataOutputStream);

        byte[] sendJsonBytes1 = {1, 1, 1};
        byte[] serverHeader1 = {2, 2, 2};
        byte[] sendJsonBytes2 = {3, 3, 3};
        byte[] serverHeader2 = {4, 4, 4};
        byte[] sendJsonBytes3 = {5, 5, 5};
        byte[] serverHeader3 = {6, 6, 6};


        //when1
        serverService.broadcastAllUser(Type.MESSAGETOCLIENT, clients, dataOutputStreamFactory
            ,socket1, sendJsonBytes1, serverHeader1, lock);
        //then1
        verify(dataOutputStream,times(3)).write(serverHeader1, 0, serverHeader1.length);
        verify(dataOutputStream,times(3)).write(sendJsonBytes1, 0, sendJsonBytes1.length);
        verify(dataOutputStream,times(3)).flush();

        //when2
        serverService.broadcastAllUser(Type.CLIENTCLOSEMESSAGE, clients, dataOutputStreamFactory
            ,socket1, sendJsonBytes2, serverHeader2, lock);
        //then2
        verify(dataOutputStream,times(3)).write(serverHeader2, 0, serverHeader2.length);
        verify(dataOutputStream,times(3)).write(sendJsonBytes2, 0, sendJsonBytes2.length);
        verify(dataOutputStream,times(6)).flush();

        //when2
        serverService.broadcastAllUser(Type.IMAGETOCLIENT, clients, dataOutputStreamFactory
            ,socket1, sendJsonBytes3, serverHeader3, lock);
        //then2
        verify(dataOutputStream,times(2)).write(serverHeader3, 0, serverHeader3.length);
        verify(dataOutputStream,times(2)).write(sendJsonBytes3, 0, sendJsonBytes3.length);
        verify(dataOutputStream,times(8)).flush();
    }

    @Test
    void treatReceiveNumPlus() {
        //given
        ReentrantLock lock = new ReentrantLock();
        Socket socket1 = mock(Socket.class);
        Socket socket2 = mock(Socket.class);
        Socket socket3 = mock(Socket.class);
        RunnableServer.addClientAndSetRecieveNumInClients(socket1);
        RunnableServer.addClientAndSetRecieveNumInClients(socket2);
        RunnableServer.addClientAndSetRecieveNumInClients(socket3);

        //when1
        serverService.treatReceiveNumPlus(Type.MESSAGETOCLIENT, RunnableServer.clients, socket1, lock);

        //then1
        assertEquals(RunnableServer.clients.get(socket1), 1);
        assertEquals(RunnableServer.clients.get(socket2), 1);
        assertEquals(RunnableServer.clients.get(socket3), 1);

        //when2
        serverService.treatReceiveNumPlus(Type.CLIENTCLOSEMESSAGE, RunnableServer.clients, socket1, lock);

        //then2
        assertEquals(RunnableServer.clients.get(socket1), 2);
        assertEquals(RunnableServer.clients.get(socket2), 2);
        assertEquals(RunnableServer.clients.get(socket3), 2);

        //when3
        serverService.treatReceiveNumPlus(Type.IMAGETOCLIENT, RunnableServer.clients, socket1, lock);

        //then3
        assertEquals(RunnableServer.clients.get(socket1), 2);
        assertEquals(RunnableServer.clients.get(socket2), 3);
        assertEquals(RunnableServer.clients.get(socket3), 3);

        //when3
        serverService.treatReceiveNumPlus(Type.IMAGETOSERVER, RunnableServer.clients, socket1, lock);

        //then3
        assertEquals(RunnableServer.clients.get(socket1), 2);
        assertEquals(RunnableServer.clients.get(socket2), 3);
        assertEquals(RunnableServer.clients.get(socket3), 3);
    }
}