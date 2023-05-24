package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {
    private String address;
    private int port;
    private String pokemonName;
    private TextView pokemonDetailsTextView;
    private TextView pokemonTypesTextView;
    private Socket socket;

    public ClientThread(String address, int port, String pokemonName, TextView pokemonDetailsTextView, TextView pokemonTypesTextView) {
        this.address = address;
        this.port = port;
        this.pokemonName = pokemonName;
        this.pokemonDetailsTextView = pokemonDetailsTextView;
        this.pokemonTypesTextView = pokemonTypesTextView;

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
            printWriter.println(pokemonName);
            printWriter.flush();

            String pokemonDetails, pokemonTypes;

            pokemonDetails = bufferedReader.readLine();
            if (pokemonDetails == null) {
                throw new IOException("no pokemon details");
            }
            final String finalizedPokemonDetails = pokemonDetails;
            pokemonDetailsTextView.post(new Runnable() {
                @Override
                public void run() {
                    pokemonDetailsTextView.setText(finalizedPokemonDetails);
                }
            });

            pokemonTypes = bufferedReader.readLine();
            if (pokemonTypes == null) {
                throw new IOException("no pokemon types");
            }
            final String finalizedPokemonTypes = pokemonTypes;
            pokemonTypesTextView.post(new Runnable() {
                @Override
                public void run() {
                    pokemonTypesTextView.setText(finalizedPokemonTypes);
                }
            });

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
