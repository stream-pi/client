package com.stream_pi.client.window.dashboard.actiongridpane;

import com.stream_pi.actionapi.action.Action;
import com.stream_pi.actionapi.otheractions.FolderAction;

public interface ActionGridPaneListener {

    void renderFolder(String ID);

    void normalActionClicked(String ID);
    void combineActionClicked(String ID);
}
