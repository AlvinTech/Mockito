import junit.framework.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Created by Alvin on 16/2/16.
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MockitoTest1 {
    //API documentation
    //http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html

    //verify some behaviour
    @Test public void test1() {
        // mock creation
        List mockedList = Mockito.mock(List.class);
        // using mock object - it does not throw any "unexpected interaction" exception
        mockedList.add("one");
        mockedList.clear();
        // selective, explicit, highly readable verification
        Mockito.verify(mockedList).add("one");
        //verify the add function was called.
        Mockito.verify(mockedList).clear();

        // Mockito.verify(mockedList).addAll(null);
        //throw an exception. because the addAll() function was not called.
    }

    //stubbing test
    @Test public void test2() {
        //You can mock concrete classes, not just interfaces
        LinkedList mockedList2 = Mockito.mock(LinkedList.class);

        //stubbing
        Mockito.when(mockedList2.get(0)).thenReturn("first");
        Mockito.when(mockedList2.get(1)).thenThrow(new RuntimeException());

        //following prints "first"
        System.out.println(mockedList2.get(0));

        //following throws runtime exception
        // System.out.println(mockedList2.get(1));

        //following prints "null" because get(999) was not stubbed
        System.out.println(mockedList2.get(999));

        //Although it is possible to verify a stubbed invocation, usually it's just redundant
        //If your code cares what get(0) returns, then something else breaks (often even before verify() gets executed).
        //If your code doesn't care what get(0) returns, then it should not be stubbed. Not convinced? See here.
        Mockito.verify(mockedList2).get(0);
    }

    //Argument matchers
    //http://site.mockito.org/mockito/docs/current/org/mockito/Matchers.html
    @Test public void test3() {
        List mockedList = Mockito.mock(List.class);
        //stubbing using built-in anyInt() argument matcher
        Mockito.when(mockedList.get(Mockito.anyInt())).thenReturn("element");

        //stubbing using custom matcher (let's say isValid() returns your own matcher implementation):
        //  Mockito.when(mockedList.contains(Mockito.argThat(isValid()))).thenReturn("element");

        //following prints "element"
        System.out.println(mockedList.get(999));

        //you can also verify using an argument matcher
        Mockito.verify(mockedList).get(Mockito.anyInt());

        // If you are using argument matchers, all arguments have to be provided by matchers.
        //verify(mock).someMethod(anyInt(), anyString(), eq("third argument"));
        //above is correct - eq() is also an argument matcher

        //verify(mock).someMethod(anyInt(), anyString(), "third argument");
        //above is incorrect - exception will be thrown because third argument is given without an argument matcher.
    }

    //Verifying exact number of invocations / at least x / never
    @Test public void test4() {

        List mockedList = Mockito.mock(List.class);
        //using mock
        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        //following two verifications work exactly the same - times(1) is used by default
        Mockito.verify(mockedList).add("once");
        Mockito.verify(mockedList, Mockito.times(1)).add("once");

        //exact number of invocations verification
        Mockito.verify(mockedList, Mockito.times(2)).add("twice");
        Mockito.verify(mockedList, Mockito.times(3)).add("three times");

        //verification using never(). never() is an alias to times(0)
        Mockito.verify(mockedList, Mockito.never()).add("never happened");

        //verification using atLeast()/atMost()
        Mockito.verify(mockedList, Mockito.atLeastOnce()).add("three times");
        Mockito.verify(mockedList, Mockito.atLeast(2)).add("five times");
        Mockito.verify(mockedList, Mockito.atMost(5)).add("three times");
    }

    //Stubbing void methods with exceptions
    @Test public void test5() {
        List mockedList = Mockito.mock(List.class);
        Mockito.doThrow(new RuntimeException()).when(mockedList).clear();

        //following throws RuntimeException:
        try {
            mockedList.clear();
            Assert.assertTrue(false);
        }
        catch (RuntimeException e) {
            Assert.assertTrue(true);
        }
    }

    //Verification in order
    @Test public void test6() {
        List singleMock = Mockito.mock(List.class);
        singleMock.add("was add first");
        singleMock.add("was add second");

        InOrder inOrder = Mockito.inOrder(singleMock);
        inOrder.verify(singleMock).add("was add first");
        inOrder.verify(singleMock).add("was add second");

        List firstMock = Mockito.mock(List.class);
        List secondMock = Mockito.mock(List.class);
        firstMock.add("was added first");
        secondMock.add("was added second");

        InOrder inOrder1 = Mockito.inOrder(firstMock, secondMock);

        inOrder1.verify(firstMock).add(Mockito.anyString());
        inOrder1.verify(secondMock).add(Mockito.anyString());

        //Verification in order is flexible - you don't have to verify all interactions one-by-one but only those that you are interested in testing in order

    }

    //Making sure interaction nerver happend on mock
    @Test
    public void test7(){
        List listMock = Mockito.mock(List.class);
        listMock.add("first");

        Mockito.verify(listMock).add(Mockito.anyString());
        Mockito.verify(listMock, Mockito.never()).add("second");
        //Mockito.verify(mockTwo,mockThree);
    }

    //Finding redundant invocations
    @Test
    public void test8(){
        List listMock = Mockito.mock(List.class);
        listMock.add("first");
        listMock.add("second");

        Mockito.verify(listMock).add(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(listMock);
        //exception because  have the invocation listMock.add("second");
    }

    //Stubbing consecutive calls(iteratoor-syle subbing)
    @Test
    public void test10(){
        List listMock = Mockito.mock(List.class);
      //  Mockito.when(listMock.get(Mockito.anyInt())).thenThrow(new RuntimeException()).thenReturn(1);
       // Mockito.when(listMock.get(Mockito.anyInt())).thenReturn(0).thenReturn(1);
        Mockito.when(listMock.get(Mockito.anyInt())).thenReturn(1, 2);

        System.out.println(listMock.get(0));//0
        System.out.println(listMock.get(0));//1
        System.out.println(listMock.get(0));//1
    }

    //Stubing with callbacks
    @Test
    public void test11(){
        List listMock = Mockito.mock(List.class);
        Mockito.when(listMock.get(Mockito.anyInt())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                Object mock = invocationOnMock.getMock();
                return mock.getClass().getSimpleName() + " called vith arguments: " + args;
            }
        });
        System.out.println(listMock.get(1));
    }


    //
    @Test
    public void test12(){
        List listMock = Mockito.mock(List.class);
        Mockito.doThrow(new RuntimeException()).when(listMock).isEmpty();
        Mockito.doReturn("1").when(listMock).get(0);
        Mockito.doNothing().when(listMock).clear();
        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return "this is return answer";
            }
        }).when(listMock).get(1);

        listMock.clear();
        System.out.println(listMock.get(0));
        System.out.println(listMock.get(1));
    }

}
