package com.jeroenmols.mockitoexample;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.AdditionalMatchers.and;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.AdditionalMatchers.leq;
import static org.mockito.AdditionalMatchers.lt;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.matches;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jeroen Mols on 08/06/16.
 */
public class UserTest {

    public static final int USER_ID = 1111007;
    public static final String PASSWORD = "n1c3try";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private WebService mockWebService;

    @Mock
    private LoginInterface mockLoginInterface;

    @Captor
    private ArgumentCaptor<Response> responseArgumentCaptor;

    @Test
    public void createUserWithoutAnnotation() throws Exception {
        WebService mockWebService = mock(WebService.class);

        new User(mockWebService, 0, null);
    }

    @Test
    public void createUser() throws Exception {
        new User(mockWebService, 0, null);
    }

    @Test
    public void logoutOfWebService() throws Exception {
        User user = new User(mockWebService, USER_ID, PASSWORD);

        user.logout();

        verify(mockWebService).logout();
        verify(mockWebService, times(1)).logout();
        verify(mockWebService, atLeast(1)).logout();
        verify(mockWebService, atLeastOnce()).logout();
        verify(mockWebService, atMost(1)).logout();
        verify(mockWebService, only()).logout();
        verify(mockWebService, never()).login(0, null, null);
    }

    @Test
    public void loginToWebService() throws Exception {
        User user = new User(mockWebService, USER_ID, PASSWORD);

        user.login(null);

        verify(mockWebService).login(anyInt(), anyString(), any(Response.class));
        verify(mockWebService).login(eq(USER_ID), eq(PASSWORD), any(Response.class));
        verify(mockWebService).login(not(eq(0)), not(eq("12345678")), any(Response.class));
        verify(mockWebService).login(gt(0), startsWith("n1"), any(Response.class));
        verify(mockWebService).login(leq(USER_ID), contains("c3"), isNotNull(Response.class));
        verify(mockWebService).login(and(gt(0), lt(Integer.MAX_VALUE)), matches("n[1-9]{1}c[1-9]{1}[a-z]{3}"), isNotNull(Response.class));

        // See Mockito.Matchers and Mockito.AdditionalMatchers for all matchers
        // http://site.mockito.org/mockito/docs/current/org/mockito/Matchers.html
        // http://site.mockito.org/mockito/docs/current/org/mockito/AdditionalMatchers.html
    }

    @Test
    public void loginThenLogoutToWebService() throws Exception {
        User user = new User(mockWebService, USER_ID, PASSWORD);

        user.login(null);
        user.logout();

        InOrder inOrder = inOrder(mockWebService);

        //following will make sure that add is first called with "was added first, then with "was added second"
        inOrder.verify(mockWebService).login(anyInt(), anyString(), any(Response.class));
        inOrder.verify(mockWebService).logout();
    }

    @Test
    public void isUserLoggedIn() throws Exception {
        User user = new User(mockWebService, USER_ID, PASSWORD);
        when(mockWebService.isNetworkOffline()).thenReturn(true);

        user.login(mockLoginInterface);

        verify(mockWebService, never()).login(anyInt(), anyString(), any(Response.class));
    }

    @Test
    public void isUserLoggedIn2() throws Exception {
        User user = new User(mockWebService, USER_ID, PASSWORD);
        when(mockWebService.isNetworkOffline()).thenReturn(true, false, true);

        user.login(mockLoginInterface);
        user.login(mockLoginInterface);
        user.login(mockLoginInterface);

        verify(mockWebService, times(1)).login(anyInt(), anyString(), any(Response.class));
    }

    @Test(expected = CustomException.class)
    public void isUserLoggedIn3() throws Exception {
        User user = new User(mockWebService, USER_ID, PASSWORD);
        when(mockWebService.isNetworkOffline()).thenThrow(CustomException.class);

        user.login(mockLoginInterface);
    }

    @Test
    public void loginResultToCallback() throws Exception {
        User user = new User(mockWebService, USER_ID, PASSWORD);
        user.login(mockLoginInterface);
        verify(mockWebService).login(anyInt(), anyString(), responseArgumentCaptor.capture());
        Response response = responseArgumentCaptor.getValue();
        
        response.onRequestCompleted(true, null);

        verify(mockLoginInterface).onLoginSuccess();
    }

    @Test
    public void needATestToDemonstrateUseOfCollectionMatchers() throws Exception {


    }

    public void login() {
        // implementation
    }
}