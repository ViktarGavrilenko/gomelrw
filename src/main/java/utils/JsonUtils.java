package utils;

import aquality.selenium.core.logging.Logger;

public class JsonUtils {
    public static void isStatusCodeCorrect(int expectedStatus, int receivedStatus) {
        String messageExceptionStatusCode = String.format("The received code %s is not equal to the expected %s",
                receivedStatus, expectedStatus);
        if (receivedStatus != expectedStatus) {
            Logger.getInstance().info(messageExceptionStatusCode);
            throw new IllegalArgumentException(messageExceptionStatusCode);
        }
    }
}