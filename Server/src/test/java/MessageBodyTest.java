//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class MessageBodyTest {
//    MessageBody messageBody;
//
//    @BeforeEach
//    void init() {
//        messageBody = new MessageBody();
//    }
//
//    @Test
//    void getName() {
//        //given
//        messageBody.setName("A");
//        //when
//        String expected = messageBody.getName();
//        //then
//        assertEquals(expected, "A");
//    }
//
//    @Test
//    void getSendNum() {
//        //given
//        messageBody.setSendNum(10);
//        //when
//        int expected = messageBody.getSendNum();
//        //then
//        assertEquals(expected, 10);
//    }
//
//    @Test
//    void getRecieveNum() {
//        //given
//        messageBody.setRecieveNum(10);
//        //when
//        int expected = messageBody.getRecieveNum();
//        //then
//        assertEquals(expected, 10);
//    }
//
//    @Test
//    void getBytes() {
//        //given
//        messageBody.setBytes(new byte[]{1,1,1,1});
//        //when
//        byte[] expected = messageBody.getBytes();
//        byte[] result = new byte[]{1, 1, 1, 1};
//        //then
//        assertArrayEquals(expected, result);
//    }
//
//    @Test
//    void setName() {
//        //given
//        messageBody.setName("A");
//        //when
//        String expected = messageBody.getName();
//        //then
//        assertEquals(expected, "A");
//
//    }
//
//    @Test
//    void setSendNum() {
//        //given
//        messageBody.setSendNum(10);
//        //when
//        int expected = messageBody.getSendNum();
//        //then
//        assertEquals(expected, 10);
//    }
//
//    @Test
//    void setRecieveNum() {
//        //given
//        messageBody.setRecieveNum(10);
//        //when
//        int expected = messageBody.getRecieveNum();
//        //then
//        assertEquals(expected, 10);
//    }
//
//    @Test
//    void setBytes() {
//        //given
//        messageBody.setBytes(new byte[]{1,1,1,1});
//        //when
//        byte[] expected = messageBody.getBytes();
//        byte[] result = new byte[]{1, 1, 1, 1};
//        //then
//        assertArrayEquals(expected, result);
//    }
//
//    @Test
//    void testToString() {
//        //given
//        messageBody.setName("A");
//        messageBody.setSendNum(10);
//        messageBody.setRecieveNum(10);
//        messageBody.setBytes(new byte[]{1,1,1,1});
//        //when
//        String expected = messageBody.toString();
//        String result = "MessageBody(name=A, sendNum=10, recieveNum=10, bytes=[1, 1, 1, 1])";
//        //then
//        assertEquals(expected, result);
//    }
//}