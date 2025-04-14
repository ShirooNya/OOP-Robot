package gui;

import log.LogWindowSource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class LogWindowTest {

    @Test
    void testListenerIsRegisteredOnCreation() {
        LogWindowSource mockLogSource = Mockito.mock(LogWindowSource.class);
        LogWindow logWindow = new LogWindow(mockLogSource);
        verify(mockLogSource, times(1)).registerListener(logWindow);
    }

    @Test
    void testListenerIsUnregisteredOnDispose() {
        LogWindowSource mockLogSource = Mockito.mock(LogWindowSource.class);
        LogWindow logWindow = new LogWindow(mockLogSource);
        logWindow.dispose();
        verify(mockLogSource, times(1)).unregisterListener(logWindow);
    }
}