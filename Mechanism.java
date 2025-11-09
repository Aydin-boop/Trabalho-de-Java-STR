public class Mechanism {
    
    public void ledOn(int ledNumber) {
        Storage.ledOn(ledNumber);
    }

    public void ledsOff() {
        Storage.ledsOff();
    }

    public Boolean switch1Pressed() {
        //todo for now returns false
        //if (Storage.getSwtich1() == 1); return true;
        return false;
    }

    public Boolean switch2Pressed() {
        //todo for now returns false
        //if (Storage.getSwtich2() == 1); return true;
        return false;
    }

    public Boolean bothSwitchesPressed() {
        //todo for now returns false
        //if (Storage.getSwtich2() == 1); return true;
        return false;
    }

    public void putPartInCell(){

    }

    public void takePartInCell(){
        
    }

}
