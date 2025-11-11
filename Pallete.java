public class Pallete {
    // quando esta no posicao certo
    public void placePallete() {
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();

        // int[] myArray = new int[] { 1, 10, 2, 20, 3, 30 };

        int i = axisZ.getPos();

        if (i != 10 && i != 20 && i != 30) {

        }

        // int x, z = 0;

        /*
         * for (; z < 6; z++) {
         * x = myArray[z];
         * if (x == i)
         * break;
         * }
         */

        axisY.gotoPos(3);
        axisZ.gotoPos(i / 10);
        axisY.gotoPos(2);
    }

    public void takePallete() {
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();

        // int[] myArray = new int[] { 1, 10, 2, 20, 3, 30 };

        int i = axisZ.getPos();
        /*
         * int x, z = 0;
         * for (; z < 6; z++) {
         * x = myArray[z];
         * if (x == i)
         * break;
         * }
         */

        axisY.gotoPos(3);
        axisZ.gotoPos(i * 10);
        axisY.gotoPos(2);
    }

}
