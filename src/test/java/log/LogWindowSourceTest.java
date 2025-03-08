package log;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogWindowSourceTest {

    @Test
    void testQueueDoesNotExceedMaxLength() {
        int maxQueueLength = 5;
        LogWindowSource logWindowSource = new LogWindowSource(maxQueueLength);

        for (int i = 0; i < 10; i++) {
            logWindowSource.append(LogLevel.Info, "Message " + i);
        }

        assertEquals(maxQueueLength, logWindowSource.size());
    }

    @Test
    void testOldestMessagesAreRemovedWhenQueueExceedsMaxLength() {
        int maxQueueLength = 3;
        LogWindowSource logWindowSource = new LogWindowSource(maxQueueLength);

        logWindowSource.append(LogLevel.Info, "Message 1");
        logWindowSource.append(LogLevel.Info, "Message 2");
        logWindowSource.append(LogLevel.Info, "Message 3");
        logWindowSource.append(LogLevel.Info, "Message 4");

        List<LogEntry> messages = new ArrayList<>();
        logWindowSource.all().forEach(messages::add);

        assertEquals(maxQueueLength, messages.size());
        assertEquals("Message 2", messages.get(0).getMessage());
        assertEquals("Message 3", messages.get(1).getMessage());
        assertEquals("Message 4", messages.get(2).getMessage());
    }
}