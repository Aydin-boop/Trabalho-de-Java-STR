public class AxisX implements Axis{

    @Override
    public void moveForward(){
            Storage.moveXRight();
    }

    @Override
    public void moveBackward(){
        Storage.moveXLeft();
    }

    @Override
    public void stop(){
        Storage.stopX();
    }

    @Override
    public int getPos(){
        return Storage.getXPos();
    }

    @Override
    public void gotoPos(int pos){
        
        switch(pos){
            case 1: 
            moveBackward();
            while (getPos() != 1){}
            stop();
                break;
        }

        //throw new UnsupportedOperationException("Uninplemented method 'gotoPos'");
        //fsdjjkdfhkjghdsfkjhfsdkjfhdsgjhihfefkjlsdfhgjlksdfhklfjhu
        
    }
}

