package com.github.yihtserns.test.bluecove;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
 *
 * @author yihtserns
 */
public class Search {

    public static void main(String[] args) throws BluetoothStateException, InterruptedException {
        List<RemoteDevice> devices = findRemoteDevices();
        for (RemoteDevice device : devices) {
            try {
                String name = device.getFriendlyName(false);
                System.out.printf("Bluetooth Device [address: %s, name: %s]\n", device.getBluetoothAddress(), name);
            } catch (IOException ex) {
                System.out.printf("Bluetooth Device [address: %s]\n", device.getBluetoothAddress());
            }
        }
    }

    public static List<RemoteDevice> findRemoteDevices() throws InterruptedException, BluetoothStateException {
        final List<RemoteDevice> devices = new ArrayList<RemoteDevice>();
        final CountDownLatch latch = new CountDownLatch(1);

        DiscoveryListener discoveryListener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice device, DeviceClass deviceClass) {
                devices.add(device);
            }

            public void inquiryCompleted(int i) {
                latch.countDown();
            }

            public void servicesDiscovered(int txId, ServiceRecord[] serviceRecords) {
            }

            public void serviceSearchCompleted(int txId, int respCode) {
            }
        };

        boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, discoveryListener);
        if (!started) {
            throw new IllegalStateException("Cannot start inquiry");
        }
        System.out.println("Searching...");
        latch.await();

        return devices;
    }

    public static RemoteDevice findRemoteDevice(String bluetoothAddress) throws InterruptedException, BluetoothStateException {
        for (RemoteDevice device : findRemoteDevices()) {
            if (bluetoothAddress.equals(device.getBluetoothAddress())) {
                return device;
            }
        }
        throw new RuntimeException("Cannot find bluetooth device with address '" + bluetoothAddress + "'");
    }
}
