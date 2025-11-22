public class AxisX implements Axis {

    @Override
    public void moveForward() {
        Storage.moveXRight();
    }

    @Override
    public void moveBackward() {
        Storage.moveXLeft();
    }

    @Override
    public void stop() {
        Storage.stopX();
    }

    //
    @Override
    public int getPos() {
        return Storage.getXPos();
    }

    @Override
    public void gotoPos(int pos) {

        if (Menu.isSwitch2) {
            return;
        }

        switch (pos) {
            case 1:
                moveBackward();
                while (getPos() != 1) {
                }
                stop();
                break;
            case 2:
                if (getPos() == 1) {
                    moveForward();
                    while (getPos() != 2) {
                    }
                    stop();
                } else if (getPos() == 3) {
                    moveBackward();
                    while (getPos() != 2) {
                    }
                    stop();
                }
                break;
            case 3:
                moveForward();
                while (getPos() != 3) {
                }
                stop();
                break;
        }

    }
}
