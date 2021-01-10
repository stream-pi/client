package com.StreamPi.Client.Window;

import com.StreamPi.Util.Alert.StreamPiAlertType;
import com.StreamPi.Util.Exception.MinorException;
import com.StreamPi.Util.Exception.SevereException;

public interface ExceptionAndAlertHandler {
    void handleMinorException(MinorException e);
    void handleSevereException(SevereException e);
    void onAlert(String title, String body, StreamPiAlertType alertType);
}
