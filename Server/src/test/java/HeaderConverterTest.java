//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class HeaderConverterTest {
//    HeaderConverter header;
//
//    @BeforeEach
//    void init() {
//        header = new HeaderConverter();
//    }
//
//    @Test
//    void decodeHeader() {
//        //given
//        byte[] value = {0, 0, 0, 10, 0, 0, 4, 87};
//        //when
//        header.decodeHeader(value);
//        //then
//        assertEquals(header.messageLength, 10);
//        assertEquals(header.messageType, 1111);
//    }
//
//    @Test
//    void encodeHeader() {
//        //given
//        int length = 10;
//        int type = 1111;
//        //when
//        header.encodeHeader(length, type);
//        byte[] result = {0, 0, 0, 10, 0, 0, 4, 87};
//        //then
//        assertArrayEquals(header.bytesHeader, result);
//    }
//}