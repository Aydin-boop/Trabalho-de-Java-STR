public class PlaceBlock {
    // quando esta no posicao certo
    public void place() {
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();

        int[] myArray = new int[] { 1, 10, 2, 20, 3, 30 };

        int i = axisZ.getPos();
        int x, z = 0;
        for (; z < 6; z++) {
            x = myArray[z];
            if (x == i)
                break;
        }

        axisY.gotoPos(3);
        axisZ.gotoPos(myArray[z - 1]);
        axisY.gotoPos(2);
    }

}
