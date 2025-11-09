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
            case 2:
                do {
                moveBackward();
                while (getPos() != 1 || getPos() !=2){}
                stop();
                moveForward();
                while (getPos() !=3 || getPos() !=2){}
                stop();
                    } while (getPos!=2);
            case 3:
                moveForward();
                while (getPos()!=3) {}
                stop();
                break();
        }

        //throw new UnsupportedOperationException("Uninplemented method 'gotoPos'");
        //fsdjjkdfhkjghdsfkjhfsdkjfhdsgjhihfefkjlsdfhgjlksdfhklfjhu
        //utwhreiuhdrgiuhsdgfuhfskufdgskjfhkkjbskjsdfhbkjhsd uhg sdlg heraughpearyhpa43y89qt43y7t9ewgyhui43ht
        
    }
}



