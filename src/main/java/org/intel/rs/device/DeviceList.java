package org.intel.rs.device;

import org.intel.rs.util.NativeDecorator;

import static org.intel.rs.api.RealSense.*;
import static org.intel.rs.util.RealSenseUtil.checkError;
import static org.intel.rs.util.RealSenseUtil.toBoolean;

public class DeviceList implements NativeDecorator<rs2_device_list> {
    protected rs2_device_list instance;

    public DeviceList(rs2_device_list instance) {
        this.instance = instance;
    }

    public Device create(int index) {
        rs2_error error = new rs2_error();
        rs2_device device = rs2_create_device(instance, index, error);
        checkError(error);
        return new Device(device);
    }

    public boolean contains(Device device) {
        rs2_error error = new rs2_error();
        int result = rs2_device_list_contains(instance, device.instance, error);
        checkError(error);
        return toBoolean(result);
    }

    public int count() {
        rs2_error error = new rs2_error();
        int deviceCount = rs2_get_device_count(instance, error);
        checkError(error);

        return deviceCount;
    }

    @Override
    public rs2_device_list getInstance() {
        return instance;
    }

    @Override
    public void release() {
        rs2_delete_device_list(instance);
    }
}