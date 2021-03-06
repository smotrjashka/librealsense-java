package org.intel.rs.sensor;

import org.intel.rs.option.SensorOptions;
import org.intel.rs.stream.StreamProfile;
import org.intel.rs.frame.FrameQueue;
import org.intel.rs.stream.StreamProfileList;
import org.intel.rs.stream.VideoStreamProfile;
import org.intel.rs.types.CameraInfo;
import org.intel.rs.types.Extension;
import org.intel.rs.util.NativeDecorator;

import static org.bytedeco.librealsense2.global.realsense2.*;

import org.bytedeco.librealsense2.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.intel.rs.util.RealSenseUtil.checkError;
import static org.intel.rs.util.RealSenseUtil.toBoolean;

public class Sensor implements NativeDecorator<rs2_sensor> {
    rs2_sensor instance;
    private SensorOptions options;

    public Sensor(rs2_sensor instance) {
        this.instance = instance;
    }

    //region Sensor Info
    public String getName() {
        return getInfo(RS2_CAMERA_INFO_NAME);
    }

    public String getInfo(CameraInfo info) {
        return getInfo(info.getIndex());
    }

    public String getInfo(int info) {
        // check if info is supported
        rs2_error error = new rs2_error();
        boolean isSupported = toBoolean(rs2_supports_sensor_info(instance, info, error));
        checkError(error);

        if (!isSupported)
            return null;

        // read device info
        String infoText = rs2_get_sensor_info(instance, info, error).getString();
        checkError(error);

        return infoText;
    }
    //endregion

    @Override
    public rs2_sensor getInstance() {
        return instance;
    }

    @Override
    public void release() {
        rs2_delete_sensor(instance);
    }

    // todo: implement sensor roi settings (AutoExposureSettings)

    public SensorOptions getSensorOptions() {
        // todo: make this thread safe!
        if (options == null)
            options = new SensorOptions(instance);

        return options;
    }

    //region Sensor Stream Commands
    public void open(StreamProfile streamProfile) {
        rs2_error error = new rs2_error();
        rs2_open(instance, streamProfile.getInstance(), error);
        checkError(error);
    }

    public void open(StreamProfile[] streamProfiles) {
        rs2_stream_profile[] nativeProfiles = new rs2_stream_profile[streamProfiles.length];
        for (int i = 0; i < streamProfiles.length; i++) {
            nativeProfiles[i] = streamProfiles[i].getInstance();
        }

        // todo: test this code!
        rs2_stream_profile arrayPointer = nativeProfiles[0];
        rs2_error error = new rs2_error();
        rs2_open_multiple(instance, arrayPointer, nativeProfiles.length, error);
        checkError(error);
    }

    public void start(FrameQueue queue) {
        rs2_error error = new rs2_error();
        rs2_start_queue(instance, queue.getInstance(), error);
        checkError(error);
    }

    // todo: implement start with FrameCallback

    public void stop() {
        rs2_error error = new rs2_error();
        rs2_close(instance, error);
        checkError(error);
    }

    public void closeSensor() {
        rs2_error error = new rs2_error();
        rs2_close(instance, error);
        checkError(error);
    }
    //endregion

    public float getDepthScale() {
        rs2_error error = new rs2_error();
        float depthScale = rs2_get_depth_scale(instance, error);
        checkError(error);

        return depthScale;
    }

    public StreamProfileList getStreamProfiles() {
        rs2_error error = new rs2_error();
        StreamProfileList list = new StreamProfileList(rs2_get_stream_profiles(instance, error));
        checkError(error);
        return list;
    }


    public List<VideoStreamProfile> getVideoStreamProfiles() {
        List<VideoStreamProfile> videoProfiles = new ArrayList<VideoStreamProfile>();
        for (StreamProfile profile : getStreamProfiles()) {
            if (profile instanceof VideoStreamProfile)
                videoProfiles.add((VideoStreamProfile) profile);
        }
        return videoProfiles;
    }

    public boolean isExtendableTo(Extension extension) {
        rs2_error error = new rs2_error();
        int result = rs2_is_sensor_extendable_to(instance, extension.getIndex(), error);
        checkError(error);
        return toBoolean(result);
    }
}