//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.Socket;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class RunnableServerTest {
//    RunnableServer runnableServer;
//
//    Socket socket;
//    DataInputStream dataInputStream;
//    HeaderConverter headerConverter;
//    ObjectMapper objectMapper;
//
//    @BeforeEach
//    void init() {
//        socket = mock(Socket.class);
//        runnableServer = new RunnableServer(socket);
//        dataInputStream = mock(DataInputStream.class);
//        headerConverter = new HeaderConverter();
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    void recieveMessageHeaderFromClientTest()
//        throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //reflection
//        Method method =
//            runnableServer.getClass().getDeclaredMethod("recieveMessageHeaderFromClient", DataInputStream.class);
//        method.setAccessible(true);
//
//        //given
//        byte[] headerData = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
//
//        doAnswer(mockData -> {
//            System.arraycopy(headerData, 0, mockData.getArguments()[0], 0, 8);
//            return null;
//        }).when(dataInputStream).readFully(any(byte[].class), eq(0), eq(8));
//
//        when(socket.getInputStream()).thenReturn(dataInputStream);
//
//        //when
//        var header = method.invoke(runnableServer, dataInputStream);
//
//        //then
//        assertArrayEquals((byte[]) header, headerData);
//    }
//
//    @Test
//    void receiveMessageBodyFromClientTest()
//        throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //reflection
//        Method method =
//            runnableServer.getClass().getDeclaredMethod
//                ("receiveMessageBodyFromClient", DataInputStream.class, byte[].class);
//        method.setAccessible(true);
//
//        //given
//        byte[] headerData = new byte[]{0, 0, 0, 6, 0, 0, 4, 87};
//        headerConverter.decodeHeader(headerData);
//        int messageLength = headerConverter.messageLength;
//        byte[] receiveBytes = new byte[]{0, 1, 2, 3, 4, 5};
//
//        doAnswer(mockData -> {
//            System.arraycopy(receiveBytes, 0, mockData.getArguments()[0], 0, 6);
//            return null;
//        }).when(dataInputStream).readFully(any(byte[].class), eq(0), eq(messageLength));
//
//        when(socket.getInputStream()).thenReturn(dataInputStream);
//
//        //when
//        var receiveData = method.invoke(runnableServer, dataInputStream, headerData);
//
//        //then
//        assertArrayEquals((byte[]) receiveData, receiveBytes);
//    }
//
//    @Test
//    void readTypeTest()
//        throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //reflection
//        Method method =
//            runnableServer.getClass().getDeclaredMethod
//                ("readType", byte[].class);
//        method.setAccessible(true);
//
//        //given
//        byte[] headerDataOne = new byte[]{0, 0, 0, 1, 0, 0, 4, 87};
//        Type typeOneResult = Type.RESISTERNAME;
//        byte[] headerDataTwo = new byte[]{0, 0, 0, 1, 0, 0, 8, -82};
//        Type typeTwoResult = Type.MESSAGETOSERVER;
//        byte[] headerDataThree = new byte[]{0, 0, 0, 1, 0, 0, 21, -77};
//        Type typeThreeResult = Type.IMAGETOSERVER;
//        byte[] headerDataFour = new byte[]{0, 0, 0, 1, 0, 0, 0, 0};
//        Type typeFourResult = null;
//
//
//        //when
//        Type typeOne = (Type) method.invoke(runnableServer, headerDataOne);
//        Type typeTwo = (Type) method.invoke(runnableServer, headerDataTwo);
//        Type typeThree = (Type) method.invoke(runnableServer, headerDataThree);
//        Type typeFour = (Type) method.invoke(runnableServer, headerDataFour);
//
//        //then
//        assertAll(
//            () -> assertEquals(typeOneResult, typeOne),
//            () -> assertEquals(typeTwoResult, typeTwo),
//            () -> assertEquals(typeThreeResult, typeThree),
//            () -> assertEquals(typeFourResult, typeFour)
//        );
//    }
//
////    @Test
////    //어렵다 그냥 thisname이라는 변수 만들어서 해도 되나?
////    void resisterNameTest()
////        throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
////        //reflection
////        Method method =
////            runnableServer.getClass().getDeclaredMethod("resisterName", byte[].class);
////        method.setAccessible(true);
////
////        //given
////        byte[] headerData = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
////        doAnswer().when(runnableServer);
////        doAnswer(mockData -> {
////            System.arraycopy(headerData, 0, mockData.getArguments()[0], 0, 8);
////            return null;
////        }).when(dataInputStream).readFully(any(byte[].class), eq(0), eq(8));
////
////        when(socket.getInputStream()).thenReturn(dataInputStream);
////
////        //when
////        var header = method.invoke(runnableServer, dataInputStream);
////
////        //then
////        assertArrayEquals((byte[]) header, headerData);
////    }
//    //this name때문에 이것도 오바다.
////    @Test
////    void implementStringMessageJsonBytesTest()
////        throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
////        //reflection
////        Method method =
////            runnableServer.getClass().getDeclaredMethod
////                ("implementStringMessageJsonBytes", byte[].class);
////        method.setAccessible(true);
////
////        //given
////        StringMessageBodyDto stringMessageBodyDto = new StringMessageBodyDto();
////        String str = new String("a");
////        stringMessageBodyDto.setStringMessageBytes(str.getBytes("UTF-8"));
////        byte[] messageBodyBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
////        String name = "name";
////        stringMessageBodyDto.setName(name);
////        byte[] resultJsonBytes = {123, 34, 110, 97, 109, 101, 34, 58, 34, 110, 97, 109, 101, 34, 44, 34, 115, 116, 114,
////            105, 110, 103, 77, 101, 115, 115, 97, 103, 101, 66, 121, 116, 101, 115, 34, 58, 34, 89, 81, 61, 61, 34,
////            125};
//////        byte[] stringMessageJsonBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
////
////        //when
////        byte[] stringMessageJsonBytes = (byte[]) method.invoke(runnableServer, messageBodyBytes);
////
////        //then
////        assertArrayEquals(stringMessageJsonBytes, resultJsonBytes);
////    }
////
//    @Test
//    void implementStringMessageServerHeaderBytesTest()
//        throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //reflection
//        Method method =
//            runnableServer.getClass().getDeclaredMethod
//                ("implementStringMessageServerHeaderBytes", byte[].class);
//        method.setAccessible(true);
//
//        //given
//        byte[] stringMessageJsonBytes = {123, 34, 110, 97, 109, 101, 34, 58, 110, 117, 108, 108, 44, 34, 115, 116, 114,
//            105, 110, 103, 77, 101, 115, 115, 97, 103, 101, 66, 121, 116, 101, 115, 34, 58, 34, 89, 81, 61, 61, 34,
//            125};
//        headerConverter.encodeHeader(stringMessageJsonBytes.length, Type.MESSAGETOCLIENT.getValue());
//        byte[] resultHeaderBytes = headerConverter.bytesHeader;
//
//        //when
//        byte[] expectedHeaderBytes = (byte[]) method.invoke(runnableServer, stringMessageJsonBytes);
//
//        //then
//        assertArrayEquals(expectedHeaderBytes, resultHeaderBytes);
//    }
//
////    @Test
////    void implementImageMessageJsonBytesTest()
////        throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
////        //reflection
////        Method method =
////            runnableServer.getClass().getDeclaredMethod
////                ("implementImageMessageJsonBytes", byte[].class);
////        method.setAccessible(true);
////
////        //given
////        ImageMessageBodyDto imageMessageBodyDto = new ImageMessageBodyDto();
////        imageMessageBodyDto.setImageMessageBytes(new byte[]{0,1,2,3,4});
////        String str = new String("a");
////        stringMessageBodyDto.setStringMessageBytes(str.getBytes("UTF-8"));
////        byte[] messageBodyBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
////        byte[] resultJsonBytes = {123, 34, 110, 97, 109, 101, 34, 58, 110, 117, 108, 108, 44, 34, 115, 116, 114, 105
////            , 110, 103, 77, 101, 115, 115, 97, 103, 101, 66, 121, 116, 101, 115, 34, 58, 34, 89, 81, 61, 61, 34, 125};
//////        byte[] stringMessageJsonBytes = objectMapper.writeValueAsBytes(stringMessageBodyDto);
////
////        //when
////        byte[] stringMessageJsonBytes = (byte[]) method.invoke(runnableServer, messageBodyBytes);
////
////        //then
////        assertArrayEquals(stringMessageJsonBytes, resultJsonBytes);
////    }
//}