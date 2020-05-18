package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

//import Thread;

public class ClientThread extends Thread {
    private String address;
    private int port;
    private String currency;
    private TextView currencyTextView;
    private Socket socket;

    public ClientThread(String address, int port, String currency, TextView currencyTextView) {
        this.address = address;
        this.port = port;
        this.currency = currency;
        this.currencyTextView = currencyTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(currency);
            printWriter.flush();
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                final String finalizedInformation = result;
                Log.d(Constants.TAG, result);
                currencyTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        currencyTextView.setText(finalizedInformation);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
