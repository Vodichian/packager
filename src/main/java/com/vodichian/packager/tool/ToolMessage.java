package com.vodichian.packager.tool;

/**
 * A deliverable EventBus message
 */
public class ToolMessage {
    public final String message;
    public final String sender;

    public ToolMessage(String sender, String message) {
        this.message = message;
        this.sender = sender;
    }

    @Override
    public String toString() {
        return sender + "> " + message;
    }
}
