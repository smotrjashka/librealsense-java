package org.intel.rs.processing;

import org.bytedeco.librealsense2.rs2_error;
import org.bytedeco.librealsense2.rs2_frame;
import org.bytedeco.librealsense2.rs2_source;
import org.intel.rs.frame.Frame;
import org.intel.rs.frame.FrameList;
import org.intel.rs.frame.VideoFrame;
import org.intel.rs.stream.StreamProfile;
import org.intel.rs.types.Extension;

import static org.bytedeco.librealsense2.global.realsense2.*;
import static org.intel.rs.util.RealSenseUtil.checkError;

public class FrameSource {
    rs2_source instance;

    public FrameSource(rs2_source instance) {
        this.instance = instance;
    }

    public VideoFrame allocateVideoFrame(StreamProfile profile, Frame original, int bpp, int width, int height, int stride) {
        return allocateVideoFrame(profile, original, bpp, width, height, stride, Extension.VideoFrame);
    }

    public VideoFrame allocateVideoFrame(StreamProfile profile, Frame original, int bpp, int width, int height, int stride, Extension extension) {
        rs2_error error = new rs2_error();
        rs2_frame fref = rs2_allocate_synthetic_video_frame(instance, profile.getInstance(), original.getInstance(), bpp, width, height, stride, extension.getIndex(), error);
        checkError(error);
        return new VideoFrame(fref);
    }

    public FrameList allocateCompositeFrame(Frame... frames) {
        rs2_error error = new rs2_error();
        rs2_frame[] frameRefs = new rs2_frame[frames.length];

        for (int i = 0; i < frames.length; i++) {
            Frame frame = frames[i];
            frameRefs[i] = frame.getInstance();

            rs2_frame_add_ref(frame.getInstance(), error);
        }

        // todo: not sure if this really works (native array passing from java)
        rs2_frame frame_ref = rs2_allocate_composite_frame(instance, frameRefs[0], frameRefs.length, error);
        return new FrameList(frame_ref);
    }

    public void frameReady(Frame frame) {
        rs2_error error = new rs2_error();
        rs2_frame_add_ref(frame.getInstance(), error);
        checkError(error);

        rs2_synthetic_frame_ready(instance, frame.getInstance(), error);
        checkError(error);
    }

    public void framesReady(FrameList frameList) {
        // todo: check for memory leak (release as frame result)
        frameReady(frameList.asFrame());
    }

    public rs2_source getInstance() {
        return instance;
    }
}
