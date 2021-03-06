package com.geeksville.signalmesh;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.geeksville.mesh.IMeshService;

/**
 * A (hopefully) reusable container of client side goo needed to talk to the MeshService
 */
public class MeshClient {
    Context context;
    IMeshService service;

    public MeshClient() {
    }

    public void init(Context c) {
        this.context = c;
        Intent i = new Intent();
        i.setClassName("com.geeksville.mesh", "com.geeksville.mesh.MeshService");
        if (!context.bindService(i, connection, Context.BIND_AUTO_CREATE))
            Log.e("MeshClient", "FIXME, didn't bind to mesh service");
    }

    public void close() {
        if (context != null && connection != null)
            context.unbindService(connection);
    }

    /// Send data to the mesh
    public void sendData(String destId, byte[] payload, int typ) throws android.os.RemoteException {
        if(service != null)
            service.sendData(destId, payload, typ);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IMeshService.Stub.asInterface(iBinder);

            // FIXME - do actions for when we connect to the service
            Log.i("MeshClient", "did connect");
            try {
                service.subscribeReceiver(context.getPackageName(), "com.geeksville.signalmesh.MeshReceiver");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };
}
