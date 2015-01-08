
package insertcreativecompanynamehere.swifthockey.wificonn;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import insertcreativecompanynamehere.swifthockey.GameActivityMP;

public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler";
    private Handler handler;
    private P2PManager chat;
    private InetAddress mAddress;

    public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    GameActivityMP.SERVER_PORT), 5000);
            Log.d(TAG, "Launching the I/O handler");
            chat = new P2PManager(socket, handler);
            new Thread(chat).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                Log.d(TAG, "Closing Socket");
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return;
        }
    }

    public P2PManager getChat() {
        return chat;
    }

}
