package com.github.hiteshsondhi88.libffmpeg;

import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

public class ExecuteBinaryResponseHandler implements FFmpegExecuteResponseHandler {

    @Override
    public void onSuccess(String message) throws FFmpegCommandAlreadyRunningException {

    }

    @Override
    public void onProgress(String message) {

    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }
}
