package com.stream_pi.client.window.dashboard.actiongridpane;

import com.stream_pi.action_api.action.Location;

public interface ActionGridPaneListener
{
    void renderFolder(String ID);

    void normalActionClicked(String ID);

    void toggleActionClicked(String ID, boolean toggleState);

    boolean isConnected();

    ActionBox getActionBox(int col, int row);

    void showNonUsedBoxes(int col, int row, int colSpan, int rowSpan);
}
