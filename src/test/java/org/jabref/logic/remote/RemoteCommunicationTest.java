package org.jabref.logic.remote;

import java.io.IOException;

import org.jabref.logic.remote.client.RemoteClient;
import org.jabref.logic.remote.server.MessageHandler;
import org.jabref.logic.remote.server.RemoteListenerServerLifecycle;
import org.jabref.support.DisabledOnCIServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for the case where the client and server are set-up correctly. Testing the exceptional cases happens in {@link
 * RemoteSetupTest}.
 */
@DisabledOnCIServer("Tests fails sporadically on CI server")
class RemoteCommunicationTest {

    private RemoteClient client;
    private RemoteListenerServerLifecycle serverLifeCycle;
    private MessageHandler server;

    @BeforeEach
    void setUp() {
        final int port = 34567;

        server = mock(MessageHandler.class);
        serverLifeCycle = new RemoteListenerServerLifecycle();
        serverLifeCycle.openAndStart(server, port);

        client = new RemoteClient(port);
    }

    @AfterEach
    void tearDown() {
        serverLifeCycle.close();
    }

    @Test
    void pingReturnsTrue() throws IOException, InterruptedException {
        assertTrue(client.ping());
    }

    @Test
    void commandLineArgumentSinglePassedToServer() {
        final String[] message = new String[]{"my message"};

        client.sendCommandLineArguments(message);

        verify(server).handleCommandLineArguments(message);
    }

    @Test
    void commandLineArgumentTwoPassedToServer() {
        final String[] message = new String[]{"my message", "second"};

        client.sendCommandLineArguments(message);

        verify(server).handleCommandLineArguments(message);
    }

    @Test
    void commandLineArgumentMultiLinePassedToServer() {
        final String[] message = new String[]{"my message\n second line", "second \r and third"};

        client.sendCommandLineArguments(message);

        verify(server).handleCommandLineArguments(message);
    }

    // CS304 Issue Link: https://github.com/JabRef/jabref/issues/6487
    // Test method: Protocol.sendMessage and Protocol.receiveMessage
    // Test whitespaces and Chinese characters
    @Test
    /* default */ void commandLineArgumentEncodingAndDecoding() {
        final String[] message = new String[]{"D:\\T EST\\测试te st.bib"};
        // PMD: Array initialization can be written shorter

        // will be encoded as "D%3A%5CT+EST%5C%E6%B5%8B%E8%AF%95te+st.bib"
        client.sendCommandLineArguments(message);

        verify(server).handleCommandLineArguments(message);
        // PMD: Potential violation of Law of Demeter (method chain calls)
    }

    // CS304 Issue Link: https://github.com/JabRef/jabref/issues/6487
    @Test
    /* default */ void commandLineArgumentEncodingAndDecoding2() {
        final String[] message = new String[]{"D:\\TEST\\äöüß.bib"};

        // will be encoded as "D%3A%5CTEST%5C%C3%A4%C3%B6%C3%BC%C3%9F.bib"
        client.sendCommandLineArguments(message);

        verify(server).handleCommandLineArguments(message);
    }
}
