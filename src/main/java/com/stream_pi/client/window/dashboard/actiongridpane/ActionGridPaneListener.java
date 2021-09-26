package com.stream_pi.client.window.dashboard.actiongridpane;

import com.stream_pi.action_api.action.Location;

public interface ActionGridPaneListener
{
    void renderFolder(String ID);

    void normalActionClicked(String ID);

    void toggleActionClicked(String ID, boolean toggleState);

    ActionBox getActionBoxByLocation(Location location);

    boolean isConnected();
}
