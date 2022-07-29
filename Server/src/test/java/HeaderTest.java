import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeaderTest {

    @BeforeEach
    void init() {

    }

    @Test
    void testIntToByteArray() {
        //given

        //when
        int value = 10;
        byte[] bytes = Header.intToByteArray(value);
        //then
        assertArrayEquals(bytes, new byte[]{0, 0, 0, 10});
    }

    @Test
    void testByteArrayToInt() {
        //given

        //when
        byte[] value = {0, 10, 0, 10};
        int result = Header.byteArrayToInt(value);
        //then
        assertEquals(result, 655370);
    }

    @Test
    void testEncodeHeader() {
        //given

        //when
        int length = 10;
        int type = 1111;
        Header.encodeHeader(length, type);
        byte[] expencted = {0, 0, 0, 10, 0, 0, 4, 87};
        //then
        assertArrayEquals(Header.bytesHeader, expencted);
    }

    @Test
    void testDecodeHeader() {
        //given

        //when
        byte[] value = {0, 0, 0, 10, 0, 0, 4, 87};
        Header.decodeHeader(value);
        int expenctedLength = 10;
        int expenctedType = 1111;

        //then
        assertEquals(Header.messageLength, expenctedLength);
        assertEquals(Header.messageType, expenctedType);
    }
}