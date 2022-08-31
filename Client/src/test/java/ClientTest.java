//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ClientTest {
//    Client client;
//
//    @BeforeEach
//    void init() {
//        client = new Client();
//    }
//
//    @Test
//    void  TestSetType1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class[] partypes = new Class[2];
//        partypes[0] = boolean.class;
//        partypes[1] = String.class;
//        Method method = client.getClass().getDeclaredMethod("setType", partypes);
//        method.setAccessible(true);
//        //when
//        int expected = (int) method.invoke(client, true, "aa" );
//        int result = Type.RESISTERNAME.getValue();
//        //then
//        assertEquals(expected, result);
//    }
//
//    @Test
//    void  TestSetType2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class[] partypes = new Class[2];
//        partypes[0] = boolean.class;
//        partypes[1] = String.class;
//        Method method = client.getClass().getDeclaredMethod("setType", partypes);
//        method.setAccessible(true);
//        //when
//        int expected = (int) method.invoke(client, false, "image://C:\\Users\\" );
//        int result = Type.IMAGETOSERVER.getValue();
//        //then
//        assertEquals(expected, result);
//    }
//
//    @Test
//    void  TestSetType3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class[] partypes = new Class[2];
//        partypes[0] = boolean.class;
//        partypes[1] = String.class;
//        Method method = client.getClass().getDeclaredMethod("setType", partypes);
//        method.setAccessible(true);
//        //when
//        int expected = (int) method.invoke(client, false, "aaa" );
//        int result = Type.MESSAGETOSERVER.getValue();
//        //then
//        assertEquals(expected, result);
//    }
//
//
//
//    @Test
//    void TestConvertToBytes() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class[] partypes = new Class[2];
//        partypes[0] = int.class;
//        partypes[1] = String.class;
//        Method method = client.getClass().getDeclaredMethod("convertToBytes", partypes);
//        method.setAccessible(true);
//        //when
//        String filePath = "image://C:\\Users\\geung\\Desktop\\a.png";
//        byte[] expected = (byte[]) method.invoke(client, Type.IMAGETOSERVER.getValue(), filePath);
//        byte[] result = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 43, 0, 0, 0, 51, 8, 6, 0, 0, 0, 14, -40, -15, 54, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 18, 116, 0, 0, 18, 116, 1, -34, 102, 31, 120, 0, 0, 0, 89, 73, 68, 65, 84, 104, 67, -19, -50, -79, 9, -64, 64, 16, 3, -63, 123, -9, -33, -77, -3, -127, 91, -104, -32, 96, 7, -124, -46, 61, 51, -13, -34, -83, -16, -4, -65, 66, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, 74, -79, -54, -94, -40, -103, 15, 83, 32, 1, 101, 120, 87, -39, 93, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};
//        //then
//        assertArrayEquals(expected, result);
//
//    }
//
//    @Test
//    void TestConvertToBytes2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class[] partypes = new Class[2];
//        partypes[0] = int.class;
//        partypes[1] = String.class;
//        Method method = client.getClass().getDeclaredMethod("convertToBytes", partypes);
//        method.setAccessible(true);
//        //when
//        byte[] expected = (byte[]) method.invoke(client, Type.MESSAGETOSERVER.getValue(), "aa" );
//        byte[] result = {97, 97};
//        //then
//        assertArrayEquals(expected, result);
//    }
//
//    @Test
//    void testImplementMessageBody() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        //given
//        Class[] partypes = new Class[1];
//        partypes[0] = byte[].class;
//        Method method = client.getClass().getDeclaredMethod("implementMessageBody", partypes);
//        method.setAccessible(true);
//        //when
//        byte[] expected = (byte[]) method.invoke(client, new byte[] {1,1});
//        byte[] result = {123, 34, 110, 97, 109, 101, 34, 58, 110, 117, 108, 108, 44, 34, 115, 101, 110, 100, 78, 117, 109, 34, 58, 48, 44, 34, 114, 101, 99, 105, 101, 118, 101, 78, 117, 109, 34, 58, 48, 44, 34, 98, 121, 116, 101, 115, 34, 58, 34, 65, 81, 69, 61, 34, 125};
//        //then
//        assertArrayEquals(expected, result);
//    }
//}