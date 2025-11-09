public class AxisZ implements Axis{

    @Override
    public void moveForward(){
        Storage.moveZUp();
    }

    @Override
    public void moveBackward(){
        Storage.moveZDown();
    }

    @Override
    public void stop(){
        Storage.stopZ();
    }

    @Override
    public int getPos(){
        return Storage.getZPos();
    }

    @Override
    public void gotoPos(int pos){
        throw new UnsupportedOperationException("Uninplemented method 'gotoPos'");
    }
}
