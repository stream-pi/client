// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.window;

import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.alert.StreamPiAlertType;

public interface ExceptionAndAlertHandler
{
    void onAlert(final String p0, final String p1, final StreamPiAlertType p2);
    
    void handleMinorException(final MinorException p0);
    
    void handleMinorException(final String p0, final MinorException p1);
    
    void handleSevereException(final SevereException p0);
    
    void handleSevereException(final String p0, final SevereException p1);
}
