public class AxisZ implements Axis {

    @Override
    public void moveForward() {
        Storage.moveZUp();
    }

    @Override
    public void moveBackward() {
        Storage.moveZDown();
    }

    @Override
    public void stop() {
        Storage.stopZ();
    }

    @Override
    public int getPos() {
        return Storage.getZPos();
    }

    //
    @Override
    public void gotoPos(int pos) {
        switch (pos) {

            case 1: // CHECK
                moveBackward();
                while (getPos() != 1) {
                }
                stop();
                break;
            case 10: // CHECK
                if (getPos() == 2 || getPos() == 20 || getPos() == 3 || getPos() == 30) {
                    moveBackward();
                    while (getPos() != 1 && getPos() != 10) {
                    }
                    stop();
                } else if (getPos() == 1) {
                    moveForward();
                    while (getPos() != 10) {
                    }
                    stop();
                }
                break;

            case 2: // CHECK
                if (getPos() == 20 || getPos() == 3 || getPos() == 30) {
                    moveBackward();
                    while (getPos() != 2) {
                    }
                    stop();
                } else if (getPos() == 1 || getPos() == 10) {
                    moveForward();
                    while (getPos() != 2) {
                    }
                    stop();
                }
                break;
            case 20: // CHECK
                if (getPos() == 3 || getPos() == 30) {
                    moveBackward();
                    while (getPos() != 1 && getPos() != 20) {
                    }
                    stop();
                } else if (getPos() == 1 || getPos() == 10 || getPos() == 2) {
                    moveForward();
                    while (getPos() != 20) {
                    }
                    stop();
                }
                break;

            case 3: // CHECK
                if (getPos() == 30) {
                    moveBackward();
                    while (getPos() != 1 && getPos() != 3) {
                    }
                    stop();
                } else if (getPos() == 1 || getPos() == 10 || getPos() == 2 || getPos() == 20) {
                    moveForward();
                    while (getPos() != 3) {
                    }
                    stop();
                }
                break;
            case 30: // CHECK
                moveForward();
                while (getPos() != 30) {
                }
                stop();
                break;
        }
    }
}
