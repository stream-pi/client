// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.window.dashboard.actiongridpane;

import com.stream_pi.action_api.action.Location;

public interface ActionGridPaneListener
{
    void renderFolder(final String p0);
    
    void normalOrCombineActionClicked(final String p0);
    
    void toggleActionClicked(final String p0, final boolean p1);
    
    ActionBox getActionBoxByLocation(final Location p0);
    
    boolean isConnected();
}
