package com.cbasolutions.cbapos.helper;

import android.util.Log;

import com.cbasolutions.cbapos.activity.Application;
import com.couchbase.lite.replicator.RemoteRequestResponseException;
import com.couchbase.lite.replicator.Replication;


public class ReplicationChangeListener implements Replication.ChangeListener {

    private Application application;

    public ReplicationChangeListener(Application application) {
        this.application = application;
    }

    @Override
    public void changed(Replication.ChangeEvent event) {
        if (event.getError() != null) {
            Throwable lastError = event.getError();
            Log.d(Application.TAG, String.format("Replication Error: %s", lastError.getMessage()));
            if (lastError instanceof RemoteRequestResponseException) {
                RemoteRequestResponseException exception = (RemoteRequestResponseException) lastError;
                if (exception.getCode() == 401) {
                    //application.showErrorMessage("Your username or password is not correct.", null);
                    application.logout();
                }
            }
        }
    }
}
