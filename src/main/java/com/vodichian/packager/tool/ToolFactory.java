package com.vodichian.packager.tool;

import com.vodichian.packager.PackagerException;

/**
 * Creates {@link AbstractTool} instances using provided settings
 */
public class ToolFactory {

    public static AbstractTool make(ToolSettings settings) throws PackagerException {
        AbstractTool tool;
        switch (settings.getName()) {
            case LAUNCH_4_J:
                tool = new Launch4jTool(settings);
                break;
            case INNO_SETUP:
                tool = new InnoTool(settings);
                break;
            default: {
                throw new PackagerException("Unsupported tool: " + settings.getName());
            }
        }
        return tool;
    }
}
