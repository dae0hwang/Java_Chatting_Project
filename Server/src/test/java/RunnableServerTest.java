import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

//class RunnableServerTest {
//
//    @BeforeEach
//    void init() {
//
//    }
//
//    @Test
//    void testRemove() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException {
//        //when
//        Socket temp = new Socket();
//        RunnableServer runnableServer = new RunnableServer(temp);
//        Field field = runnableServer.getClass().getDeclaredField("clients");
//        ((HashMap)field.get(runnableServer)).put(a, 0);
//        ((HashMap)field.get(runnableServer)).put(b, 0);
//        ((HashMap)field.get(runnableServer)).put(c, 0);
//
//        Method method = runnableServer.getClass().getDeclaredMethod("remove");
//        method.setAccessible(true);
//        //given
//
//
//        //then
//        assert
//    }
//
//    @Test
//    void testSendNumPlus() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//        //when
//        Socket temp = new Socket();
//        Socket a = new Socket();
//        RunnableServer runnableServer = new RunnableServer(temp);
//        Field field = runnableServer.getClass().getDeclaredField("clients");
//        field.setAccessible(true);
//        ((HashMap)field.get(runnableServer)).put(a, 0);
//
//        Method method = runnableServer.getClass().getDeclaredMethod("sendNumPlus", Socket.class );
//        method.setAccessible(true);
//        //given
//        method.invoke(runnableServer, a);
//        int result = field.get(runnableServer).
//        //then
//        assert
//    }
//
//
//
//}