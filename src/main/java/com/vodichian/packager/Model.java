package com.vodichian.packager;

import com.vodichian.packager.tool.ToolMessage;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Model for Packager, holds data that needs to persist between screen transitions.
 */
public class Model {
    private final ReadOnlyListWrapper<ToolMessage> MESSAGES = new ReadOnlyListWrapper<>(
            FXCollections.observableList(new ArrayList<>()));
    public final ReadOnlyListProperty<ToolMessage> messages = MESSAGES.getReadOnlyProperty();

    public Model() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onToolMessage(ToolMessage toolMessage) {
        Platform.runLater(() -> MESSAGES.add(toolMessage));
    }

}
