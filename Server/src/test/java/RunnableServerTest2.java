//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.BindException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.HashMap;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class RunnableServerTest {
//    RunnableServer runnableServer;
//    MessageBody messageBody;
//    ObjectMapper objectMapper;
//    DataInputStream dataInputStream;
//    @BeforeEach
//    void init() throws IOException {
//        runnableServer = new RunnableServer(new Socket());
//        messageBody = new MessageBody();
//        objectMapper = new ObjectMapper();
//
//        ServerSocket serverSocket = new ServerSocket(5510);
//        Socket socket = serverSocket.accept();
////        Socket outputSocket = new Socket("127.0.0.1", 5510);
//        dataInputStream = new DataInputStream(socket.getInputStream());
////        dataOutputStream = new DataOutputStream(outputSocket.getOutputStream());
//
//    }
//    @Test
//    void testRecieveMessageHeaderFromClient() throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException {
//        //given
//        Socket outputSocket = new Socket("127.0.0.1", 5510);
//        DataOutputStream dataOutputStream = new DataOutputStream(outputSocket.getOutputStream());
//        Class partypes[] = new Class[1];
//        partypes[0] = DataInputStream.class;
//        Method method = runnableServer.getClass().getDeclaredMethod("recieveMessageHeaderFromClient", partypes);
//        method.setAccessible(true);
//        //when
//        byte[] outputHeader = {1, 1, 1, 1, 1, 1, 1, 1};
//        dataOutputStream.write(outputHeader, 0, 8);
//        dataOutputStream.flush();
//        byte[] expected = (byte[]) method.invoke(runnableServer, dataInputStream);
//        //then
//        assertArrayEquals(expected, outputHeader);
//    }










//
//
//
//
//@Test
//void testProcessingMessageBody() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//    //given
//    Class partypes[] = new Class[2];
//    partypes[0] = MessageBody.class;
//    partypes[1] = byte[].class;
//    Method method = runnableServer.getClass().getDeclaredMethod("processingMessageBody", partypes);
//    method.setAccessible(true);
//    //when
//
//    byte[] expected = (byte[]) method.invoke(runnableServer, "aa", new byte[]{1, 1});
//    byte[] result = {123, 34, 110, 97, 109, 101, 34, 58, 34, 97, 97, 34, 44, 34, 115, 101, 110, 100, 78, 117, 109, 34, 58, 48, 44, 34, 114, 101, 99, 105, 101, 118, 101, 78, 117, 109, 34, 58, 48, 44, 34, 98, 121, 116, 101, 115, 34, 58, 34, 65, 81, 69, 61, 34, 125};
//    //then
//    assertArrayEquals(expected, result);
//}
//
//    @Test
//    void testImplementBody() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class partypes[] = new Class[2];
//        partypes[0] = String.class;
//        partypes[1] = byte[].class;
//        Method method = runnableServer.getClass().getDeclaredMethod("implementBody", partypes);
//        method.setAccessible(true);
//        //when
//        byte[] expected = (byte[]) method.invoke(runnableServer, "aa", new byte[]{1, 1});
//        byte[] result = {123, 34, 110, 97, 109, 101, 34, 58, 34, 97, 97, 34, 44, 34, 115, 101, 110, 100, 78, 117, 109, 34, 58, 48, 44, 34, 114, 101, 99, 105, 101, 118, 101, 78, 117, 109, 34, 58, 48, 44, 34, 98, 121, 116, 101, 115, 34, 58, 34, 65, 81, 69, 61, 34, 125};
//        //then
//        assertArrayEquals(expected, result);
//    }
//
//    @Test
//    void testImplementServerHeader() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class partypes[] = new Class[2];
//        partypes[0] = int.class;
//        partypes[1] = byte[].class;
//        Method method = runnableServer.getClass().getDeclaredMethod("implementServerHeader", partypes);
//        method.setAccessible(true);
//        //when
//        byte[] expected = (byte[]) method.invoke(runnableServer, Type.MESSAGETOSERVER.getValue(), new byte[]{1, 1} );
//        byte[] result = {0, 0, 0, 2, 0, 0, 13, 5};
//        //then
//        assertArrayEquals(expected, result);
//    }
//
//    @Test
//    void testImplementServerHeader2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class partypes[] = new Class[2];
//        partypes[0] = int.class;
//        partypes[1] = byte[].class;
//        Method method = runnableServer.getClass().getDeclaredMethod("implementServerHeader", partypes);
//        method.setAccessible(true);
//        //when
//        byte[] expected = (byte[]) method.invoke(runnableServer, Type.IMAGETOSERVER.getValue(), new byte[]{1, 1} );
//        byte[] result = {0, 0, 0, 2, 0, 0, 26, 10};
//        //then
//        assertArrayEquals(expected, result);
//    }
//
//    @Test
//    void testImplementCloseBody() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class partypes[] = new Class[3];
//        partypes[0] = String.class;
//        partypes[1] = int.class;
//        partypes[2] = int.class;
//        Method method = runnableServer.getClass().getDeclaredMethod("implementCloseBody", partypes);
//        method.setAccessible(true);
//        //when
//        byte[] expected = (byte[]) method.invoke(runnableServer,"a",1,1 );
//        byte[] result = {123, 34, 110, 97, 109, 101, 34, 58, 34, 97, 34, 44, 34, 115, 101, 110, 100, 78, 117, 109, 34, 58, 49, 44, 34, 114, 101, 99, 105, 101, 118, 101, 78, 117, 109, 34, 58, 49, 44, 34, 98, 121, 116, 101, 115, 34, 58, 110, 117, 108, 108, 125};
//        //then
//        assertArrayEquals(expected, result);
//    }
//
//    @Test
//    void testImplementCloseHeader() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class partypes[] = new Class[1];
//        partypes[0] = byte[].class;
//        Method method = runnableServer.getClass().getDeclaredMethod("implementCloseHeader", partypes);
//        method.setAccessible(true);
//        //when
//        byte[] expected = (byte[]) method.invoke(runnableServer, new byte[]{1, 1});
//        byte[] result = {0, 0, 0, 2, 0, 0, 17, 92};
//        //then
//        assertArrayEquals(expected, result);
//    }
//
//    @Test
//    void testSendNumPlus() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        //given
//        Field field = runnableServer.getClass().getDeclaredField("clients");
//        field.setAccessible(true);
//        Socket socket = new Socket();
//        ((HashMap) field.get(runnableServer)).put(socket, 0);
//        Method method = runnableServer.getClass().getDeclaredMethod("receiveNumPlus", Socket.class);
//        method.setAccessible(true);
//        //when
//        method.invoke(runnableServer, socket);
//        int expected = (int) ((HashMap) field.get(runnableServer)).get(socket);
//        int result = 1;
//        //then
//        assertEquals(expected, result);
//    }
//
//
//
//}