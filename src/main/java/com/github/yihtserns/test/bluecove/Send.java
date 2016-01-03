package com.github.yihtserns.test.bluecove;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;

/**
 *
 * @author yihtserns
 */
public class Send {

    public static void main(String[] args) throws IOException {
        ClientSession clientSession = (ClientSession) Connector.open("btgoep://A844814834C9:1;authenticate=false;encrypt=false;master=false");

        HeaderSet connectReply = clientSession.connect(null);
        if (connectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
            System.out.println("Failed to connect");
            return;
        }

        HeaderSet operation = clientSession.createHeaderSet();

        File file = new File("C:/workspace/tmp/px.png");
        operation.setHeader(HeaderSet.NAME, file.getName());
        operation.setHeader(HeaderSet.TYPE, "png");

        byte[] bytes = Files.readAllBytes(file.toPath());

        Operation putOperation = clientSession.put(operation);
        OutputStream os = putOperation.openOutputStream();
        os.write(bytes);
        os.close();

        putOperation.close();
        clientSession.disconnect(null);
        clientSession.close();
    }
}
