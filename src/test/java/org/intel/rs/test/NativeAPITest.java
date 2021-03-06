package org.intel.rs.test;

import org.junit.Test;

import static org.bytedeco.librealsense2.global.realsense2.*;
import org.bytedeco.librealsense2.*;

public class NativeAPITest {

    @Test
    public void createContext() {
        rs2_error error = new rs2_error();
        rs2_context context = rs2_create_context(RS2_API_VERSION, error);
        assert error.isNull();
    }

    @Test
    public void countDevices() {
        rs2_error error = new rs2_error();

        // create context
        rs2_context context = rs2_create_context(RS2_API_VERSION, error);
        assert error.isNull();

        // list devices
        rs2_device_list devices = rs2_query_devices(context, error);
        assert error.isNull();

        // count devices
        int deviceCount = rs2_get_device_count(devices, error);
        assert error.isNull();

        System.out.println("Device Count: " + deviceCount);
    }
}
