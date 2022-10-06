package com.vodichian.packager.tool;

public enum ToolState {
    /**
     * The initial state of a tool, indicates the {@link ToolSettings} are not correctly set.
     */
    CONFIG_ERROR,
    /**
     * The tool is configured correctly and ready to run
     */
    READY,
    /**
     * The tool is currently executing its tasks
     */
    RUNNING,
    /**
     * The tool has successfully finished its tasks
     */
    SUCCESS,
    /**
     * The tool failed to successfully finish its tasks
     */
    FAILURE
}
