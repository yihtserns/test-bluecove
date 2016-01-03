package com.github.yihtserns.test.bluecove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

/**
 *
 * @author yihtserns
 */
public class SearchServices {

    public static final UUID OBEX_OBJECT_PUSH_PROFILE = new UUID(0x1105);
    private static final UUID OBEX_FILE_TRANSFER_PROFILE = new UUID(0x1106);
    private static final int NAME = 0x0100;

    public static void main(String[] args) throws Exception {
        RemoteDevice device = Search.findRemoteDevice("A844814834C9");

        for (ServiceRecord serviceRecord : findServicesOfType(OBEX_OBJECT_PUSH_PROFILE, device)) {
            System.out.println(serviceRecord);
            String url = serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            if (url == null) {
                continue;
            }

            DataElement serviceName = serviceRecord.getAttributeValue(NAME);
            if (serviceName == null) {
                System.out.printf("Found service [name: %s, url: %s]\n", serviceName.getValue(), url);
            } else {
                System.out.printf("Found service [url: %s]\n", url);
            }
        }
        for (ServiceRecord serviceRecord : findServicesOfType(OBEX_FILE_TRANSFER_PROFILE, device)) {
            System.out.println(serviceRecord);
            String url = serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
            if (url == null) {
                continue;
            }

            DataElement serviceName = serviceRecord.getAttributeValue(NAME);
            if (serviceName == null) {
                System.out.printf("Found service [name: %s, url: %s]\n", serviceName.getValue(), url);
            } else {
                System.out.printf("Found service [url: %s]\n", url);
            }
        }
    }

    public static List<ServiceRecord> findServicesOfType(UUID uuid, RemoteDevice device) throws BluetoothStateException, InterruptedException {
        final List<ServiceRecord> allServiceRecord = new ArrayList<ServiceRecord>();
        final CountDownLatch latch = new CountDownLatch(1);

        DiscoveryListener discoveryListener = new DiscoveryListener() {

            public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {
                allServiceRecord.addAll(Arrays.asList(serviceRecords));
            }

            public void serviceSearchCompleted(int i, int i1) {
                latch.countDown();
            }

            public void deviceDiscovered(RemoteDevice rd, DeviceClass dc) {
            }

            public void inquiryCompleted(int i) {
            }
        };

        LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(
                new int[]{NAME},
                new UUID[]{uuid},
                device,
                discoveryListener);
        System.out.println("Searching for services...");
        latch.await();

        return allServiceRecord;
    }

}
