/*
 * Copyright (c) 2014, Petrozavodsk State University
 * Copyright (c) 2014, Open Innovations Framework Program FRUCT
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names of the copyright holders nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ru.cardiacare.cardiacare.ecgviewer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * This class contains common data and methods for all of the supported ECG
 * signal sources.
 *
 * @author Alexander Borodin
 * @author Yulia Zavyalova
 * @since 1.0
 */
abstract public class SignalSource {

    public static enum States {
        STATE_DISCONNECTED,
        STATE_CONNECTING,
        STATE_CONNECTED,
        STATE_DISCONNECTING
    };

    private States mState;
    private final Handler mRemoteHandler;
    private final Handler mLocalHandler;

    public SignalSource(Context context, Handler handler) {
        mState = States.STATE_DISCONNECTED;
        mRemoteHandler = handler;
        mLocalHandler = new Handler() {
            public void handleMessage(Message msg) {
                processMessage(msg);
            }
        };
    }

    public Handler getHandler() {
        return mLocalHandler;
    }

    private int processMessage(Message msg) {
        return 0;

    }

    /**
     *
     * @return
     */
    public synchronized States getState() {
        return mState;
    }

    private synchronized void setState(States state) {
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        //mRemoteHandler.obtainMessage(BluetoothFindActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized void connect(Bundle config) {

    }

    public synchronized void disconnect(Bundle config) {

    }

    private static class ConnectionThread extends Thread {

    }

    private static class WorkerThread extends Thread {

    }
}
