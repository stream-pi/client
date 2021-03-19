package com.stream_pi.client.window.dashboard.actiongridpane;

import com.stream_pi.action_api.action.Action;
import com.stream_pi.action_api.otheractions.FolderAction;

public interface ActionGridPaneListener
{
    void renderFolder(String ID);

    void normalActionClicked(String ID);
    void combineActionClicked(String ID);
    void toggleActionClicked(String ID, boolean toggleState);

    boolean isConnected();
}
