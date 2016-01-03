package com.github.yihtserns.test.bluecove;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;

/**
 *
 * @author yihtserns
 */
public class Receive {

    public static void main(String[] args) throws BluetoothStateException, IOException, InterruptedException {
        LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);

        SessionNotifier conn = (SessionNotifier) Connector.open("btgoep://localhost:" + SearchServices.OBEX_OBJECT_PUSH_PROFILE + ";authenticate=false;encrypt=false;name=Test");

        while (true) {
            System.out.println("Ready to accept file");
            conn.acceptAndOpen(new ServerRequestHandler() {

                @Override
                public int onPut(Operation op) {
                    try {
                        HeaderSet hs = op.getReceivedHeaders();
                        String name = (String) hs.getHeader(HeaderSet.NAME);
                        File file = new File("c:/workspace/tmp", name);

                        InputStream is = op.openInputStream();
                        try {
                            Files.copy(is, file.toPath());
                        } finally {
                            op.close();
                        }

                        System.out.println("Copied to " + file.getAbsolutePath());

                        return ResponseCodes.OBEX_HTTP_OK;
                    } catch (Exception ex) {
                        return ResponseCodes.OBEX_HTTP_UNAVAILABLE;
                    }
                }
            });
        }
    }
}
