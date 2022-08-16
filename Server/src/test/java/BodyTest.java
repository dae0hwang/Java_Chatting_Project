import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BodyTest {
    Body body;

    @BeforeEach
    void init() {
        body = new Body();
    }

    @Test
    void getName() {
        //given
        body.setName("A");
        //when
        String expected = body.getName();
        //then
        assertEquals(expected, "A");
    }

    @Test
    void getSendNum() {
        //given
        body.setSendNum(10);
        //when
        int expected = body.getSendNum();
        //then
        assertEquals(expected, 10);
    }

    @Test
    void getRecieveNum() {
        //given
        body.setRecieveNum(10);
        //when
        int expected = body.getRecieveNum();
        //then
        assertEquals(expected, 10);
    }

    @Test
    void getBytes() {
        //given
        body.setBytes(new byte[]{1,1,1,1});
        //when
        byte[] expected = body.getBytes();
        byte[] result = new byte[]{1, 1, 1, 1};
        //then
        assertArrayEquals(expected, result);
    }

    @Test
    void setName() {
        //given
        body.setName("A");
        //when
        String expected = body.getName();
        //then
        assertEquals(expected, "A");

    }

    @Test
    void setSendNum() {
        //given
        body.setSendNum(10);
        //when
        int expected = body.getSendNum();
        //then
        assertEquals(expected, 10);
    }

    @Test
    void setRecieveNum() {
        //given
        body.setRecieveNum(10);
        //when
        int expected = body.getRecieveNum();
        //then
        assertEquals(expected, 10);
    }

    @Test
    void setBytes() {
        //given
        body.setBytes(new byte[]{1,1,1,1});
        //when
        byte[] expected = body.getBytes();
        byte[] result = new byte[]{1, 1, 1, 1};
        //then
        assertArrayEquals(expected, result);
    }

    @Test
    void testToString() {
        //given
        body.setName("A");
        body.setSendNum(10);
        body.setRecieveNum(10);
        body.setBytes(new byte[]{1,1,1,1});
        //when
        String expected = body.toString();
        String result = "Body(name=A, sendNum=10, recieveNum=10, bytes=[1, 1, 1, 1])";
        //then
        assertEquals(expected, result);
    }
}