
import java.util.Scanner;

public class App {

    static boolean isLed1Interrupted = true;
    static boolean isLed2Interrupted = true;
    static boolean isSwitchesInterrupted = false;

    // AXIS THREADS
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

    // switches
    public static void RemoveAlerts(Pallet[][] s) throws InterruptedException {
        Thread axisXThread;
        Thread axisZThread;
        Mechanism mechanism = new Mechanism();
        int posXremovido = 0;
        int posZremovido = 0;
        AxisY axisY = new AxisY();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (s[i][j] != null) {
                    if (s[i][j].is_alert() == true) {
                        posXremovido = s[i][j].desiredX();
                        posZremovido = s[i][j].desiredZ();
                        axisXThread = axisXThread(posXremovido);
                        axisZThread = axisZThread(posZremovido);
                        axisXThread.join();
                        axisZThread.join();
                        mechanism.takePartInCell();
                        axisXThread = axisXThread(3);
                        axisZThread = axisZThread(1);
                        axisXThread.join();
                        axisZThread.join();
                        if (Mechanism.cageFull() == 1) {
                            System.out.println(
                                    "Click on the button \"takeFromCage\" to remove the pallet from the system");
                            do {
                            } while (Mechanism.cageFull() == 1);
                        }
                        axisY.gotoPos(1);
                        System.out.println("Pallet with info"
                                + "\nProductType: " + s[posXremovido - 1][posZremovido - 1].product_type()
                                + "\nHumidity: " + s[posXremovido - 1][posZremovido - 1].humidity()
                                + "\nproducer_ID: " + s[posXremovido - 1][posZremovido - 1].producer_ID()
                                + "\ndesiredX: " + s[posXremovido - 1][posZremovido - 1].desiredX()
                                + "\ndesiredZ: " + s[posXremovido - 1][posZremovido - 1].desiredZ()
                                + "\nShippingDate: " + s[posXremovido - 1][posZremovido - 1].shipping_day() + "/"
                                + s[posXremovido - 1][posZremovido - 1].shipping_month() + "/"
                                + s[posXremovido - 1][posZremovido - 1].shipping_year()
                                + "\nremoved!");
                        s[posXremovido - 1][posZremovido - 1] = null;
                        axisY.gotoPos(2);
                    }
                }
            }
        }
    }

    public static Thread switchesThread(Pallet[][] s) {
        Mechanism mechanism = new Mechanism();
        Thread swiThread = new Thread() {
            Menu menu = new Menu();

            public void run() {
                while (!isSwitchesInterrupted) {
                    if (mechanism.switch1Pressed()) {
                        try {
                            isLed1Interrupted = true;
                            RemoveAlerts(s);
                            menu.Calibration();
                        } catch (InterruptedException ex) {
                            System.out.println("Interrupted Exception in switches Thread!!!");
                        }

                        // Espera o botão ser solto antes de continuar
                        while (mechanism.switch1Pressed()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted Exception in switches Thread!!!");
                            }
                        }
                    }

                    try {
                        Thread.sleep(100); // evita busy-waiting agressivo
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted Exception in switches Thread!!!");
                    }
                }
            }
        };
        swiThread.start();
        return swiThread;
    }

    public static int[] VerMaisProximo(Pallet[][] s) {
        AxisX axisx10 = new AxisX();
        AxisZ axisz10 = new AxisZ();
        double menorD = 100;
        int maisProximos[] = { 0, 0 };
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                if (s[i - 1][j - 1] == null) {
                    double d = Math.sqrt(
                            Math.pow(Math.abs(i - axisx10.getPos()), 2) + Math.pow(Math.abs(j - axisz10.getPos()), 2));
                    if (d < menorD) {
                        menorD = d;
                        maisProximos[0] = i;
                        maisProximos[1] = j;
                    }
                }
            }
        }
        return maisProximos;
    }

    /*
     * public static void ShowMenu(Pallet[][] s, int Max_humidity, int Max_day, int
     * Max_month, int Max_year) {
     * System.out.println("\n\n**********STORAGE MENU**********");
     * System.out.println("1 - Move X to the left");
     * System.out.println("2 - Move X to the right");
     * System.out.println("3 - Stop X Axis");
     * System.out.println("4 - Move cage to the desired (X,Z) coordinates");
     * System.out.println("5 - Move only one axis to the desired position");
     * System.out.println("6 - Calibrate");
     * System.out.println("7 - Place a pallete");
     * System.out.println("8 - Withdraw pallete(s)");
     * System.out.println("9 - Verify if cage is full");
     * System.out.println("10 - Define Humidity Threshold (currently " +
     * Max_humidity + ")");
     * System.out.println(
     * "11 - Define maximum shipping date (currently " + Max_day + "/" + Max_month +
     * "/" + Max_year + ")");
     * System.out.println("12 - List all stored pallets");
     * System.out.
     * println("13 - Display information of a pallete by product type or by producer ID"
     * );
     * System.out.println("*****STORAGE STATE*****");
     * char X;
     * for (int i = 2; i >= 0; i--) {
     * for (int j = 0; j < 3; j++) {
     * if ((s[j][i] != null) && (s[j][i].is_alert() == false)) {
     * X = 'X';
     * } else if ((s[j][i] != null) && (s[j][i].is_alert() == true)) {
     * X = 'Y';
     * } else {
     * X = ' ';
     * }
     * System.out.print(" [" + X + "] ");
     * }
     * System.out.print("\n");
     * }
     * System.out.println("Captions:");
     * System.out.println("X - pallet there.");
     * System.out.println("Y - pallet with active alert.");
     * System.out.println("  - No pallet there.");
     * }
     */
    public static boolean verifyAlerts(Pallet[][] Storage, int Max_humidity, int Max_day, int Max_month, int Max_year) {
        boolean alertOn = false;
        for (int i = 2; i >= 0; i--) {
            for (int j = 0; j < 3; j++) {
                if (Storage[j][i] != null) {
                    if ((Storage[j][i].humidity() > Max_humidity) || (Storage[j][i].shipping_year() > Max_year)) {
                        Storage[j][i].change_alert(true);
                        System.out.printf("Pallet at position (%d, %d) is surpassing a threshold!!!", j + 1, i + 1);
                    } else if ((Storage[j][i].humidity() > Max_humidity) || (Storage[j][i].shipping_year() == Max_year)
                            && (Storage[j][i].shipping_month() > Max_month)) {
                        Storage[j][i].change_alert(true);
                        System.out.printf("Pallet at position (%d, %d) is surpassing a threshold!!!", j + 1, i + 1);
                    } else if ((Storage[j][i].humidity() > Max_humidity) || (Storage[j][i].shipping_year() == Max_year)
                            && (Storage[j][i].shipping_month() == Max_month)
                            && (Storage[j][i].shipping_day() > Max_day)) {
                        Storage[j][i].change_alert(true);
                        System.out.printf("Pallet at position (%d, %d) is surpassing a threshold!!!", j + 1, i + 1);
                    } else {
                        if (Storage[j][i].is_alert() == true) {
                            Storage[j][i].change_alert(false);
                            System.out.printf("Pallet at position (%d, %d) is no longer surpassing a threshold!!!",
                                    j + 1, i + 1);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Storage[i][j] != null) {
                    if ((Storage[i][j].humidity() > Max_humidity) || (Storage[i][j].shipping_year() > Max_year)) {
                        alertOn = true;
                    } else if ((Storage[i][j].humidity() > Max_humidity) || (Storage[i][j].shipping_year() == Max_year)
                            && (Storage[i][j].shipping_month > Max_month)) {
                        alertOn = true;
                    } else if ((Storage[i][j].humidity() > Max_humidity) || (Storage[i][j].shipping_year() == Max_year)
                            && (Storage[i][j].shipping_month == Max_month)
                            && (Storage[i][j].shipping_day() > Max_day)) {
                        alertOn = true;
                    }
                }
            }
        }
        return alertOn;
    }

    //
    public static void main(String[] args) throws Exception {
        System.out.println("Labwork 2 from Java!");

        Storage.initializeHardwarePorts();

        AxisX axisX = new AxisX();
        // AxisY axisY = new AxisY();
        // AxisZ axisZ = new AxisZ();

        // Mechanism mechanism = new Mechanism();

        // Pallet[][] Storage = new Pallet[3][3];

        int op = -1;
        // int op1;
        // int op2;

        Scanner scan = new Scanner(System.in);
        // Scanner myObj = new Scanner(System.in);

        // Thread axisXThread;
        // Thread axisZThread;
        // Thread axisYThread;

        // Thread Led1On;
        // Thread Led2On;

        // Thread switches;

        // boolean alertON = false;
        int Max_humidity = 100;
        int Max_day = 24;
        int Max_month = 11;
        int Max_year = 2025; // sim, isto e a data limite de entrega do trabalho

        Menu menu = new Menu();

        menu.Calibration();
        // switches = switchesThread(Storage);
        while (op != 0) {
            menu.ShowMenu(Max_humidity, Max_day, Max_month, Max_year);
            System.out.println("Enter an option:");
            op = scan.nextInt();
            switch (op) {
                case 1:
                    // PARA TIRAR
                    axisX.moveForward();
                    break;
                case 2:
                    // PARA TIRAR
                    axisX.moveBackward();
                    break;
                case 3:
                    // PARA TIRAR
                    axisX.stop();
                    break;
                case 4:
                    menu.manualPosition();
                    break;
                case 5:
                    menu.manualPositionAxis();
                    break;
                case 6:
                    menu.Calibration();
                    break;
                case 7:
                    menu.addNewPallete();
                    break;
                case 8:
                    menu.removePallete();
                    break;
                case 9:
                    // PARA TIRAR
                    int o = Mechanism.cageFull();
                    System.out.println("cage is: ");
                    System.out.println(o);
                    break;
                case 10:
                    menu.maxHumidity();
                    break;
                case 11:
                    menu.maxDate();
                    break;
                case 12:
                    menu.listPalletes();
                    break;
                case 13:
                    menu.searchPalleteType();
                    break;
                default:
                    System.out.println("Nao implementado!");
                    break;
            }
        }
        scan.close();
        isSwitchesInterrupted = true;
    }
}