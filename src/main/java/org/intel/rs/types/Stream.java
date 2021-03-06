package org.intel.rs.types;

public enum Stream {
    Any(0),
    Depth(1),
    Color(2),
    Infrared(3),
    Fisheye(4),
    Gyro(5),
    Accel(6),
    Gpio(7),
    Pose(8),
    Confidence (9);

    private int index;

    Stream(int index) {
        this.index = index;
    }

    public static Stream fromIndex(int index) {
        for (Stream type : values()) {
            if (type.getIndex() == index) {
                return type;
            }
        }
        return null;
    }

    public int getIndex() {
        return index;
    }
}
