package com.StreamPi.Client.Window.Dashboard.ActionGridPane;

import com.StreamPi.ActionAPI.Action.Action;
import com.StreamPi.ActionAPI.OtherActions.FolderAction;

public interface ActionGridPaneListener {

    void renderFolder(String ID);

    void normalActionClicked(String ID);
    void combineActionClicked(String ID);
}
