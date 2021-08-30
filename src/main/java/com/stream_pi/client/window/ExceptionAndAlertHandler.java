package com.stream_pi.client.window;

import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;

public interface ExceptionAndAlertHandler
{
    void onAlert(String title, String body, StreamPiAlertType alertType);

    void handleMinorException(MinorException e);
    void handleMinorException(String message, MinorException e);
    void handleSevereException(SevereException e);
    void handleSevereException(String message, SevereException e);
}
