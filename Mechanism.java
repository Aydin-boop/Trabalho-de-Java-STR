public class Mechanism {

    public static void ledOn(int ledNumber) {
        Storage.ledOn(ledNumber);
    }

    public void ledsOff() {
        Storage.ledsOff();
    }

    public Boolean switch1Pressed() {
        return (Storage.getSwtich1() == 1);
    }

    public Boolean switch2Pressed() {
        return (Storage.getSwtich2() == 1);
    }

    public Boolean bothSwitchesPressed() {
        // todo for now returns false
        // if (Storage.getSwtich2() == 1); return true;
        return false;
    }

    public void putPartInCell() {
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();

        int i = axisZ.getPos();

        if (i != 10 && i != 20 && i != 30) {
            System.out.println("Cage nao se encontra numa posicao para colocar paletes");
            return;
        }

        axisY.gotoPos(3);
        axisZ.gotoPos(i / 10);
        axisY.gotoPos(2);
    }

    public void takePartInCell() {
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();

        int i = axisZ.getPos();

        if (i != 1 && i != 2 && i != 3) {
            System.out.println("Cage nao se encontra numa posicao para tirar paletes");
            return;
        }

        axisY.gotoPos(3);
        axisZ.gotoPos(i * 10);
        axisY.gotoPos(2);
    }

    public static int cageFull() {
        return Storage.cageFull();
    }

}
//