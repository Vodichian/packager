package com.vodichian.packager.tool;

/**
 * A deliverable EventBus message
 */
public class ToolMessage {
    public final String message;

    public ToolMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
