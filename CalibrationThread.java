
public class CalibrationThread {
    private final Axis axis;

    public CalibrationThread(Axis axis) {
        this.axis = axis;
    }

    public static Thread axisXThread(int pos) {
        AxisX axisX = new AxisX();
        Thread axisXThread = new Thread() {
            public void run() {
                axisX.gotoPos(pos);
            }
        };
        axisXThread.start();
        return axisXThread;

    }

    public static Thread axisZThread(int pos) {
        AxisZ axisZ = new AxisZ();
        Thread axisZThread = new Thread() {
            public void run() {
                axisZ.gotoPos(pos);
            }
        };
        axisZThread.start();
        return axisZThread;

    }

    public static Thread axisYThread(int pos) {
        AxisY axisY = new AxisY();
        Thread axisYThread = new Thread() {
            public void run() {
                axisY.gotoPos(pos);
            }
        };
        axisYThread.start();
        return axisYThread;

    }

    public static void initializeCalibration() throws Exception {
        AxisX axisX = new AxisX();
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();

        System.out.println("System initializing calibration");
        axisX.moveBackward();
        do {
            axisY.moveBackward();
        } while (axisY.getPos() == -1);
        axisZ.moveBackward();
        Thread axisXThreadCalibration = axisXThread(1);
        Thread axisZThreadCalibration = axisZThread(1);
        Thread axisYThreadCalibration = axisYThread(2);
        axisXThreadCalibration.join();
        axisZThreadCalibration.join();
        axisYThreadCalibration.join();
    }

}
