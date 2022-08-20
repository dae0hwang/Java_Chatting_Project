//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.PrintStream;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.Socket;
//import java.nio.file.Files;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ServerHandlerTest {
//
//    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
//    ServerHandler serverHandler;
//    Body inputBody;
//    Socket socket = new Socket();
//
//    @BeforeEach
//    void init() {
//        System.setOut(new PrintStream(output));
//        serverHandler = new ServerHandler(socket);
//        inputBody = new Body();
//    }
//
//    @AfterEach
//    void restoresStreams() {
//        System.setOut(System.out);
//        output.reset();
//    }
//
//    @Test
//    void testPrintNameAndMesssage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class[] partypes = new Class[1];
//        partypes[0] = Body.class;
//        Method method = serverHandler.getClass().getDeclaredMethod("printNameAndMesssage", partypes);
//        method.setAccessible(true);
//
//        inputBody.setName("a");
//        String ss = "bb";
//        byte[] bytes = ss.getBytes();
//        inputBody.setBytes(bytes);
//        //when
//        method.invoke(serverHandler, inputBody);
//        String result = "a: bb" + "\n";
//        //then
//        assertEquals(output.toString(), result);
//    }
//
//    @Test
//    void testPrintCloseMessage() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class[] partypes = new Class[1];
//        partypes[0] = Body.class;
//        Method method = serverHandler.getClass().getDeclaredMethod("printCloseMessage", partypes);
//        method.setAccessible(true);
//
//        inputBody.setName("a");
//        inputBody.setSendNum(1);
//        inputBody.setRecieveNum(1);
//        //when
//        method.invoke(serverHandler, inputBody);
//        String result = "a is out || Number of sendMessageNum: 1, Number of recieveMessageNum : 1"+ "\n";
//        //then
//        assertEquals(output.toString(), result);
//    }

//    @Test
//    void testByteArrayConvertToImageFile() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class[] partypes = new Class[1];
//        partypes[0] = Body.class;
//        Method method = serverHandler.getClass().getDeclaredMethod("printCloseMessage", partypes);
//        method.setAccessible(true);
//
//        File file = new File("C:\\Users\\geung\\Desktop\\a.png");
//        inputBody.setName("a");
//        inputBody.setSendNum(1);
//        inputBody.setRecieveNum(1);
//        //when
//        method.invoke(serverHandler, inputBody);
//        String result = "a is out || Number of sendMessageNum: 1, Number of recieveMessageNum : 1"+ "\n";
//        //then
//        assertEquals(output.toString(), result);
//    }
//}