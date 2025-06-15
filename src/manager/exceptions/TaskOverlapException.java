package manager.exceptions;

import java.io.IOException;

public class TaskOverlapException extends IOException {
    public TaskOverlapException(String message) {
        super(message);
    }
}
