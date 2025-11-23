
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {

    static boolean isLed1Interrupted = true;
    static boolean isLed2Interrupted = true;
    static boolean emergencyON = false;
    static boolean isSwitch1 = false;
    static boolean isAnySwitch = false;
    static boolean isSwitch2 = false;

    static AxisX axisX = new AxisX();
    static AxisY axisY = new AxisY();
    static AxisZ axisZ = new AxisZ();

    static Thread axisXThread;
    static Thread axisYThread;
    static Thread axisZThread;

    static Thread Led1On;
    static Thread Led2On;
    static Thread LedsOn;

    static Thread lastAxisPositionThread;

    int op1;
    int op2;
    //
    Scanner scan = new Scanner(System.in);
    Scanner myObj = new Scanner(System.in);

    static Mechanism mechanism = new Mechanism();
    Pallet[][] storage = new Pallet[3][3];

    static boolean alertON = false;
    float Max_humidity = 50;
    int Max_day = 31;
    int Max_month = 3;
    int Max_year = 2026;

    static EmergnecyInfo info = new EmergnecyInfo();

    // threads
    public static void axisXThread(int pos) {
        info.guardaX(pos);
        axisXThread = new Thread() {
            public void run() {
                axisX.gotoPos(pos);
            }
        };
        axisXThread.start();
      
    }

    public static void axisZThread(int pos) {
        info.guardaZ(pos);
        axisZThread = new Thread() {
            public void run() {
                axisZ.gotoPos(pos);
            }
        };
        axisZThread.start();
        
    }

    public static void axisYThread(int pos) {
        info.guardaY(pos);
        axisYThread = new Thread() {
            public void run() {
                axisY.gotoPos(pos);
            }
        };
        axisYThread.start();
      
    }

    public static Thread led1On() {
        Thread led1onThread = new Thread() {
            public void run() {
                while ((!isLed1Interrupted) && (isLed2Interrupted)) {
                    Mechanism.ledOn(1);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mechanism.ledsOff();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        led1onThread.start();
        return led1onThread;
    }

    public static Thread led2On() {
        Thread led2onThread = new Thread() {
            public void run() {
                while ((!isLed2Interrupted) && (isLed1Interrupted)) {
                    Mechanism.ledOn(2);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mechanism.ledsOff();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        led2onThread.start();
        return led2onThread;
    }

    public static Thread BothLeds() {
        Thread led2onThread = new Thread() {
            public void run() {
                while ((!isLed2Interrupted) && (!isLed1Interrupted)) {
                    Mechanism.ledOn(1);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                    }
                    Mechanism.ledOn(2);
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        led2onThread.start();
        return led2onThread;
    }

    // guarda a ultima posicao onde o cage e detedado no info.guardalastXYZ
    public static Thread lastAxisPositionThread() {
        Thread lastAxisPositionThread = new Thread() {
            public void run() {
                while (true) {
                    if (axisX.getPos() != -1) {
                        // System.out.println(axisX.getPos());//meter um print no thread nao e bom ideia. lol, isso vai te dar um montao de prints pelo meio das cenas...
                        info.guardaLastX(axisX.getPos());
                    }
                    if (axisY.getPos() != -1) {
                        // System.out.println(axisY.getPos());
                        info.guardaLastY(axisY.getPos());
                    }
                    if (axisZ.getPos() != -1) {
                        // System.out.println(axisZ.getPos());
                        info.guardaLastZ(axisZ.getPos());
                    }
                }
            }
        };
        lastAxisPositionThread.start();
        return lastAxisPositionThread;
    }

    public static Thread EmergencyThread() {
        Thread EThread = new Thread() {
            public void run() {
                while (true) {
                    if ((mechanism.bothSwitchesPressed()) && (emergencyON == false)) {
                        axisZ.stop();
                        axisX.stop();
                        axisY.stop();
                        //axisZThread.interrupt();
                        //axisXThread.interrupt();
                        //axisYThread.interrupt();
                        emergencyON = true;
                        isLed1Interrupted = false;
                        isLed2Interrupted = false;
                        LedsOn = BothLeds();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted Exception in switches Thread!!!");
                        }
                        // Espera o botão ser solto antes de continuar
                        while (mechanism.bothSwitchesPressed()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted Exception in switches Thread!!!");
                            }
                        }
                        try {
                            Thread.sleep(100); // evita busy-waiting agressivo
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted Exception in switches Thread!!!");
                        }

                    }
                }
            }
        };
        EThread.start();
        return EThread;
    }

    public static Thread VerificaAlertasThread(Pallet[][] s) {
        Thread EThread = new Thread() {
            public void run() {
                while (true) {
                    if (mechanism.switch1Pressed() && (emergencyON == true)) {
                        mechanism.ledsOff();
                        BothLeds().interrupt();
                        mechanism.ledsOff();
                        emergencyON = false;
                        isLed1Interrupted = true;
                        isLed2Interrupted = true;
                        /*
                         * try {
                         * Calibration();
                         * } catch (InterruptedException e) {
                         * }
                        
                        /*if ((info.acao() != 3) && (alertON == true)) {
                            try {
                                RemoveAlerts(s);
                                System.out.println("Click on a number and click Enter to return to the menu");
                            } catch (InterruptedException e) {
                            }
                        }*/

                        //System.out.println("going towards right sentido " + info.posX() + " " + info.posY() + " " + info.posZ() + " e "+ info.posLastX() + " " + info.posLastY() + " " + info.posLastZ());
                        info.axisGoingTo();
                        axisXThread(info.posX());
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                        axisZThread(info.posZ());
                        axisYThread(info.posY());


                        /*
                         * try {
                         * axisXThread.join();
                         * axisZThread.join();
                         * axisYThread.join();
                         * } catch (InterruptedException e) {
                         * }
                         */
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
        EThread.start();
        return EThread;
    }

    public static Thread switchesThread(Pallet[][] s) throws InterruptedException {
        Thread swiThread = new Thread() {

            public void run() {
                while (true) {
                    if (mechanism.switch1Pressed() && (emergencyON == false)) {
                        try {
                            isLed1Interrupted = false;
                            if (led1On().isAlive() == false) {
                                Led1On = led1On();
                            }
                            if(Mechanism.cageFull() == 1) {
                                do { 
                                    try { Thread.sleep(100);} catch(InterruptedException e){}
                                } while (Mechanism.cageFull() == 1);
                            }
                            RemoveAlerts(s);
                            isLed1Interrupted = true;
                            mechanism.ledsOff();
                            Calibration();
                            isSwitch1 = true;
                            isAnySwitch = false;
                            System.out.println("Click on a number and click Enter to return to the menu");
                        } catch (InterruptedException ex) {
                            System.out.println("Interrupted Exception in switches Thread!!!");
                        }

                        while (mechanism.switch1Pressed()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted Exception in switches Thread!!!");
                            }
                        }
                    }

                    if (mechanism.switch2Pressed() && (emergencyON == true)) {
                        mechanism.ledsOff();
                        BothLeds().interrupt();
                        mechanism.ledsOff();
                        emergencyON = false;
                        isLed1Interrupted = true;
                        isLed2Interrupted = true;
                        isSwitch2 = true;
                        if (info.temPalete() == true) {
                            System.out.println("Remove the pallet from the cage before proceeding with the reset!");
                            do {
                            } while (Mechanism.cageFull() == 1);
                        }
                        try {
                            Calibration();
                        } catch (InterruptedException e) {
                        }
                        try {
                            removeAll(s);
                        } catch (InterruptedException e) {
                        }

                        //System.out.println("Click on a number and Enter to return to the menu");
                        while (mechanism.switch2Pressed()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted Exception in switches Thread!!!");
                            }
                        }
                        isAnySwitch = false;
                        isSwitch2 = false;
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
    // functions

    public Pallet[][] Storage() {
        return this.storage;
    }

    public float Max_humidity() {
        return this.Max_humidity;
    }

    public int Max_day() {
        return this.Max_day;
    }

    public int Max_month() {
        return this.Max_month;
    }

    public int Max_year() {
        return this.Max_year;
    }

    public static boolean isValidDate(String dateStr, DateTimeFormatter formatter) {
        try {
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static void removeAll(Pallet[][] s) throws InterruptedException {
        System.out.println("removing all pallets!!");
        int posXremovido = 0;
        int posZremovido = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (s[i][j] != null) {
                    posXremovido = s[i][j].desiredX();
                    posZremovido = s[i][j].desiredZ();
                    axisXThread(posXremovido);
                    axisZThread(posZremovido);
                    axisXThread.join();
                    axisZThread.join();
                    mechanism.takePartInCell();
                    axisXThread(3);
                    axisZThread(1);
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
                            + "\nHumidity: " + s[posXremovido - 1][posZremovido - 1].humidity() + " %"
                            + "\nproducer_ID: " + s[posXremovido - 1][posZremovido - 1].producer_ID()
                            + "\nX: " + s[posXremovido - 1][posZremovido - 1].desiredX()
                            + "\nZ: " + s[posXremovido - 1][posZremovido - 1].desiredZ()
                            + "\nShippingDate: " + s[posXremovido - 1][posZremovido - 1].shipping_day() + "/"
                            + s[posXremovido - 1][posZremovido - 1].shipping_month() + "/"
                            + s[posXremovido - 1][posZremovido - 1].shipping_year()
                            + "\nDestination: " + s[posXremovido - 1][posZremovido - 1].destination()
                            + "\nremoved!");
                    s[posXremovido - 1][posZremovido - 1] = null;
                    info.guardaY(2);
                    axisY.gotoPos(2);
                }
            }
        }
        // System.out.println("ja saiu do remove all");
                                try {
                            Calibration();
                        } catch (InterruptedException e) {
                        }
    }

    public static void RemoveAlerts(Pallet[][] s) throws InterruptedException {
        System.out.println("removing pallets with active alerts!!!");
        int posXremovido = 0;
        int posZremovido = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (s[i][j] != null) {
                    if (s[i][j].is_alert() == true) {
                        info.guardaAcao(3); // como esta a remover alertas, temos de dizer que isto é uma ação especial
                        posXremovido = s[i][j].desiredX();
                        posZremovido = s[i][j].desiredZ();
                        axisXThread(posXremovido);
                        axisZThread(posZremovido);
                        do {
                        } while ((axisZThread.isAlive()) || (axisXThread.isAlive())); //espera as threads acabarem ou serem interrompidas
                        do {
                        } while (emergencyON); //espera sair do modo de emergencia
                        mechanism.takePartInCell();
                        axisXThread(3);
                        axisZThread(1);
                        do {
                        } while ((axisZThread.isAlive()) || (axisXThread.isAlive())); //espera as threads acabarem ou serem interrompidas
                        do {
                        } while (emergencyON); //espera sair do modo de emergencia
                        if (Mechanism.cageFull() == 1) {
                            System.out.println(
                                    "Click on the button \"takeFromCage\" to remove the pallet from the system");
                            do {
                            } while (Mechanism.cageFull() == 1);
                        }
                        axisY.gotoPos(1);
                        System.out.println("Pallet with info"
                                + "\nProductType: " + s[posXremovido - 1][posZremovido - 1].product_type()
                                + "\nHumidity: " + s[posXremovido - 1][posZremovido - 1].humidity() + " %"
                                + "\nproducer_ID: " + s[posXremovido - 1][posZremovido - 1].producer_ID()
                                + "\nX: " + s[posXremovido - 1][posZremovido - 1].desiredX()
                                + "\nZ: " + s[posXremovido - 1][posZremovido - 1].desiredZ()
                                + "\nShippingDate: " + s[posXremovido - 1][posZremovido - 1].shipping_day() + "/"
                                + s[posXremovido - 1][posZremovido - 1].shipping_month() + "/"
                                + s[posXremovido - 1][posZremovido - 1].shipping_year()
                                + "\nDestination: " + s[posXremovido - 1][posZremovido - 1].destination()
                                + "\nremoved!");
                        s[posXremovido - 1][posZremovido - 1] = null;
                        info.guardaY(2);
                        axisY.gotoPos(2);
                    }
                }
            }
        }
    }

    public static boolean verifyAlerts(Pallet[][] Storage, float Max_humidity, int Max_day, int Max_month,
            int Max_year) {
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

    public static int[] VerMaisProximo(Pallet[][] s) {
        AxisX axisx10 = new AxisX();
        AxisZ axisz10 = new AxisZ();
        double menorD = 100;
        int maisProximos[] = {0, 0};
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

    // menu options
    public static void Calibration() throws InterruptedException {
        info.guardaAcao(0);
        System.out.println("System initializing calibration");
        do {
            axisY.moveBackward();
        } while (axisY.getPos() == -1);
        axisY.stop();

        axisYThread(2);
        axisYThread.join();
        axisXThread(1);
        axisZThread(1);
        axisXThread.join();
        axisZThread.join();
    }

    public void ShowMenu(float Max_humidity, int Max_day, int Max_month, int Max_year) { // ve quais sao as cores que
        // ficam mais bonitas sffv
        String State;
        if (emergencyON) {
            State = "\u001B[31mEmergency\u001B[0m";
        } else {
            State = "\u001B[32mNormal\u001B[0m";
        }
        System.out.println("\n\u001B[34m**********STORAGE MENU**********\u001B[0m");
        System.out.println("\u001B[34m1\u001B[0m - Move cage to the desired (X,Z) coordinates");
        System.out.println("\u001B[34m2\u001B[0m - Move only one axis to the desired position");
        System.out.println("\u001B[34m3\u001B[0m - Calibrate");
        System.out.println("\u001B[34m4\u001B[0m - Place a pallete");
        System.out.println("\u001B[34m5\u001B[0m - Withdraw pallete(s)");
        System.out.println("\u001B[34m6\u001B[0m - Define Humidity Threshold (currently at " + String.format("%.1f", Max_humidity) + " %)");
        System.out.println("\u001B[34m7\u001B[0m - Define maximum shipping date (currently " + Max_day + "/" + Max_month + "/" + Max_year + ")");
        System.out.println("\u001B[34m8\u001B[0m - List all stored pallets");
        System.out.println("\u001B[34m9\u001B[0m - Display information of a pallete by product type or by producer ID");
        System.out.println("\u001B[34m10\u001B[0m - Deliver by product type or by producer ID");
        System.out.println("\u001B[36m*****STORAGE STATE*****\u001B[0m");
        String X;
        for (int i = 2; i >= 0; i--) {
            for (int j = 0; j < 3; j++) {
                if ((storage[j][i] != null) && (storage[j][i].is_alert() == false)) {
                    X = "\u001B[32mX\u001B[0m"; // X verde com background normal
                } else if ((storage[j][i] != null) && (storage[j][i].is_alert() == true)) {
                    X = "\u001B[31mY\u001B[0m"; // Y vermelho e com background normal
                } else {
                    X = " ";
                }
                System.out.print("\u001B[33m[\u001B[0m" + X + "\u001B[33m]\u001B[0m "); // [] com cor amarela e com
                // background normal
            }
            System.out.print("\n");
        }
        System.out.println("Captions:");
        System.out.println("\u001B[32mX\u001B[0m - pallet there.");
        System.out.println("\u001B[31mY\u001B[0m - pallet with active alert.");
        System.out.println("  - No pallet there.");
        System.out.println("\nCurrently at " + State + " State.");
        if (emergencyON == true) {
            System.out.println("\u001B[31mCurrently, all movements and operations are frozen!!!\u001B[0m");
            System.out.println("You can either RESUME operations (switch 1) or RESET the system (switch 2).");
        } else {
            System.out.println("Enter an option: ");
        }
    }

    public void manualPosition() throws InterruptedException {
        int posX;
        int posZ;
        info.guardaY(2);
        info.guardaAcao(0);
        axisY.gotoPos(2); // para verificar que o sistema se move dentro dos seus limites
        do {
            System.out.println("Input X and then Z:");
            posX = scan.nextInt();
            posZ = scan.nextInt();
            if ((((posX <= 0) || (posX > 3)) && ((posZ != 0))) || (((posX != 0)) && ((posZ <= 0) || (posZ > 3)))) {
                System.out.println("Invalid coordinates!!!");
            }
        } while ((((posX <= 0) || (posX > 3)) && ((posZ != 0))) || (((posX != 0)) && ((posZ <= 0) || (posZ > 3))));

        if ((posX == 0) && (posZ == 0)) {
            int pos[] = VerMaisProximo(storage);
            posX = pos[0];
            posZ = pos[1];
            System.out.println("Coordinates of auto-pilot to reduce travel distance: (" + posX + "," + posZ + ")");
        }
        
        axisXThread(posX);
        axisZThread(posZ);
            do {
            } while ((axisXThread.isAlive()) || (axisZThread.isAlive())); //espera ate que as duas threads sejam interrompidas, basicamente o mesmo que dois joins seguidos mas so que sai quando as threads sao interrompidas tbm...
    }

    public void manualPositionAxis() {
        do {
            System.out.println("Which Axis do you want to move?: (1-X, 2-Y, 3-Z)");
            try {
                op1 = scan.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input!!!");
                scan.nextLine();
            }
            switch (op1) {
                case 1:
                    do {
                        System.out.println("Which Position of Axis X do you want to move?: (1, 2, 3)");
                        try {
                            op2 = scan.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input!!!");
                            scan.nextLine();
                        }
                        if ((op2 < 0) || (op2 > 3)) {
                            System.out.println("Invalid position!");
                        }
                    } while ((op2 < 0) || (op2 > 3));
                    info.guardaAcao(0);
                    info.guardaX(op2);
                    axisX.gotoPos(op2);
                    break;
                case 2:
                    do {
                        System.out.println("Which Position of Axis Y do you want to move?: (1, 2, 3)");
                        try {
                            op2 = scan.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input!!!");
                            scan.nextLine();
                        }
                        if ((op2 < 0) || (op2 > 3)) {
                            System.out.println("Invalid Position!");
                        }
                    } while ((op2 < 0) || (op2 > 3));
                    info.guardaAcao(0);
                    info.guardaY(op2);
                    axisY.gotoPos(op2);
                    break;
                case 3:
                    do {
                        System.out.println(
                                "Which Position of Axis Z do you want to move?: (1-1D, 10-1U , 2-2D, 20-2U, 3-3D, 30-3U)");
                        op2 = scan.nextInt();
                        if ((op2 < 0) || (op2 > 3)) {
                            System.out.println("Invalid Position!");
                        }
                    } while ((op2 < 0) || (op2 > 3));
                    info.guardaAcao(0);
                    info.guardaZ(op2);
                    axisZ.gotoPos(op2);
                    break;
                default:
                    System.out.println("Invalid axis!");
                    break;
            }
        } while ((op1 < 0) || (op1 > 3));

    }

    public void addNewPallete() throws InterruptedException { // scans validos!
        String product_type;
        int posXposto = 0;
        int posZposto = 0;
        float humidity = 0;
        int producerID = -1289289; //duvido que alguem coloque este valor de id no terminal
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //isto e o formato para o qual o programa vai transformar a data introduzida pelo utilizador.
        String dateInput;
        boolean isValid;
        int shipping_day = 0;
        int shipping_month = 0;
        int shipping_year = 0;
        String day;
        String month;
        axisXThread(1);
        axisZThread(1);
        axisXThread.join();
        axisZThread.join();
        info.guardaY(1);
        axisY.gotoPos(1);
        Boolean alertBoolean = false;

        System.out.println("Manual pallete storing: What is the pallete's metadata? Don't forget to put it on the cage!");
        System.out.println("Product type: ");
        product_type = myObj.nextLine();
        do {
            System.out.println("Humidity: (%)");
            try {
                humidity = scan.nextFloat();
            } catch (InputMismatchException e) {
                scan.nextLine();
                humidity = -1;
                System.out.println("Humidity value must be a number!");
            }
            if ((humidity < 0) || (humidity > 100)) {
                System.out.println("Invalid humidity Value!");
            }
        } while ((humidity < 0) || (humidity > 100));

        do {
            System.out.println("Producer ID: ");
            try {
                producerID = scan.nextInt();
            } catch (InputMismatchException e) {
                scan.nextLine();
                producerID = -1289289; //ninguem me vai colocar este valor no terminal, e so mesmo para forçar o programa a pedir input de novo na condicao do while
                System.out.println("Producer ID must be a number!");
            }
        } while (producerID == -1289289);

        do {
            try {
                System.out.println("Desired X: ");
                posXposto = scan.nextInt();
                System.out.println("Desired Z: ");
                posZposto = scan.nextInt();
            } catch (InputMismatchException e) {
                scan.nextLine();
                posXposto = -12;
                posZposto = 43784374;
                System.out.println("Desired X and Z values must be numbers!");
            }
            if ((((posXposto <= 0) || (posXposto > 3)) && ((posZposto != 0)))
                    || (((posXposto != 0)) && ((posZposto <= 0) || (posZposto > 3)))) {
                System.out.println("Invalid coordinates!");
            }
        } while ((((posXposto <= 0) || (posXposto > 3)) && ((posZposto != 0)))
                || (((posXposto != 0)) && ((posZposto <= 0) || (posZposto > 3))));

        do {
            try {
                System.out.println("Shipping day: ");
                shipping_day = scan.nextInt();
                System.out.println("Shipping month: ");
                shipping_month = scan.nextInt();
                System.out.println("Shipping year: ");
                shipping_year = scan.nextInt();
            } catch (InputMismatchException e) {
                shipping_day = -1;
                shipping_month = 13;
                shipping_year = 120310102;
                scan.nextLine();
                System.out.println("Shipping date values must be numbers!");
            }
            if (shipping_day < 10) {
                day = "0" + shipping_day;
            } else {
                day = "" + shipping_day;
            }
            if (shipping_month < 10) {
                month = "0" + shipping_month;
            } else {
                month = "" + shipping_month;
            }
            dateInput = (day + "/" + month + "/" + shipping_year);
            isValid = isValidDate(dateInput, formatter);

            if (isValid == false) {
                System.out.println("Invalid Date!!!");
            }
        } while (isValid == false);

        System.out.println("Destination: ");
        String destination = myObj.nextLine();

        if ((posXposto == 0) && (posZposto == 0)) {
            int pos[] = VerMaisProximo(storage);
            posXposto = pos[0];
            posZposto = pos[1];
            System.out.println("Coordinates of automatically allocated pallete: (" + posXposto + "," + posZposto + ")");
        }

        Pallet p = new Pallet(product_type, humidity, producerID, posXposto, posZposto, shipping_day,
                shipping_month, shipping_year, destination);

        if (storage[posXposto - 1][posZposto - 1] == null) {
            info.guardaY(2);
            axisY.gotoPos(2);
            if (Mechanism.cageFull() == 1) {
                info.guardaAcao(2);
                info.guardaPalete(true);
                axisXThread(posXposto);
                axisZThread(posZposto * 10);

                do {
                } while ((axisZThread.isAlive()) || (axisXThread.isAlive())); //espera as threads acabarem ou serem interrompidas
                do {
                } while (emergencyON); //espera sair do modo de emergencia

                if (isSwitch2 == false) {

                    if (humidity > Max_humidity) {
                        isLed1Interrupted = false;
                        if (led1On().isAlive() == false) {
                            Led1On = led1On();
                        }
                        alertBoolean = true;
                    }
                    if (shipping_year > Max_year) {
                        isLed1Interrupted = false;
                        if (led1On().isAlive() == false) {
                            Led1On = led1On();
                        }
                        alertBoolean = true;
                    } else if ((shipping_year == Max_year) && (shipping_month > Max_month)) {
                        isLed1Interrupted = false;
                        if (led1On().isAlive() == false) {
                            Led1On = led1On();
                        }
                        alertBoolean = true;
                    } else if ((shipping_year == Max_year) && (shipping_month == Max_month)
                            && (shipping_day > Max_day)) {
                        isLed1Interrupted = false;
                        if (led1On().isAlive() == false) {
                            Led1On = led1On();
                        }
                        alertBoolean = true;
                    }
                    if (alertBoolean == true) {
                        p.change_alert(true);
                        System.out.println("Pallete with Alert!");
                    }
                    do {
                    } while ((axisZThread.isAlive()) || (axisXThread.isAlive()));
                    try { Thread.sleep(500);} catch(InterruptedException e){} //isto e so para garantir que ele faz as verificacoes no sitio certo
                    mechanism.putPartInCell();
                    if (Mechanism.cageFull() == 0)
                    storage[posXposto - 1][posZposto - 1] = p;
                }
            } else {
                System.out.println("You didn't put any pallete!!!");
            }
        } else {
            System.out.println("\nThere is a pallete there! Info:"
                    + "\nProductType: " + storage[posXposto - 1][posZposto - 1].product_type()
                    + "\nHumidity: " + String.format("%.1f", storage[posXposto - 1][posZposto - 1].humidity()) + " %"
                    + "\nproducer_ID: " + storage[posXposto - 1][posZposto - 1].producer_ID()
                    + "\ndesiredX: " + storage[posXposto - 1][posZposto - 1].desiredX()
                    + "\ndesiredZ: " + storage[posXposto - 1][posZposto - 1].desiredZ()
                    + "\nShippingDate: " + storage[posXposto - 1][posZposto - 1].shipping_day() + "/"
                    + storage[posXposto - 1][posZposto - 1].shipping_month() + "/"
                    + storage[posXposto - 1][posZposto - 1].shipping_year()
                    + "\nDestination: " + storage[posXposto - 1][posZposto - 1].destination());
        }

    }

    public void removePallete() throws InterruptedException { // scans validados!
        int posXremovido;
        int posZremovido;
        System.out.println("What is the mode you want to unload the pallets with? Manual (1) or Assisted (2)?");
        int opt = scan.nextInt();
        if (Mechanism.cageFull() == 0) {
            if (opt == 1) {
                System.out
                        .println("Manual pallete removal: What are the coordinates of the pallete you want to remove?");
            } else {
                System.out.println(
                        "Automatic pallete removal: What are the coordinates of the pallete you want to remove?");
            }
            do {
                System.out.println("Desired X: ");
                posXremovido = scan.nextInt();
                System.out.println("Desired Z: ");
                posZremovido = scan.nextInt();
                if ((posXremovido <= 0) || (posXremovido > 3) || (posZremovido <= 0) || (posZremovido > 3)) {
                    System.out.println("Invalid coordinates!");
                }
            } while ((posXremovido <= 0) || (posXremovido > 3) || (posZremovido <= 0) || (posZremovido > 3));
            Pallet p1 = storage[posXremovido - 1][posZremovido - 1];

            if (p1 != null) {
                axisXThread(posXremovido);
                axisZThread(posZremovido);
                do {
                } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                do {
                } while (emergencyON == true);
                if (isSwitch2 == false) {
                    axisXThread(posXremovido);
                    axisZThread(posZremovido);
                    do {
                    } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                    try { Thread.sleep(500);} catch(InterruptedException e){}
                    info.guardaAcao(1);
                    info.guardaPalete(false);
                    mechanism.takePartInCell();
                    if (opt == 1) {
                        axisXThread(3);
                    } else if (opt == 2) {
                        axisXThread(posXremovido);
                        isLed2Interrupted = false;
                        if (led2On().isAlive() == false) {
                            Led2On = led2On();
                        }
                    }
                    axisZThread(1);
                    do {
                    } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                    do {
                    } while (emergencyON == true);
                    if (isSwitch2 == false) {
                        if (opt == 1) {
                           axisXThread(3);
                        } else if (opt == 2) {
                            axisXThread(posXremovido);
                            isLed2Interrupted = false;
                            if (led2On().isAlive() == false) {
                                Led2On = led2On();
                            }
                        }
                        axisZThread(1);
                        do {
                        } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                        if (Mechanism.cageFull() == 1) {
                            System.out.println("Click on the button \"takeFromCage\" to remove the pallete from the system");
                            do {
                            } while (Mechanism.cageFull() == 1);
                        }
                        info.guardaY(1);
                        axisY.gotoPos(1);
                        isLed2Interrupted = true;
                        System.out.println("Pallet with info"
                                + "\nProductType: " + storage[posXremovido - 1][posZremovido - 1].product_type()
                                + "\nHumidity: " + String.format("%.1f", storage[posXremovido - 1][posZremovido - 1].humidity())
                                + " %"
                                + "\nproducer_ID: " + storage[posXremovido - 1][posZremovido - 1].producer_ID()
                                + "\ndesiredX: " + storage[posXremovido - 1][posZremovido - 1].desiredX()
                                + "\ndesiredZ: " + storage[posXremovido - 1][posZremovido - 1].desiredZ()
                                + "\nShippingDate: " + storage[posXremovido - 1][posZremovido - 1].shipping_day() + "/"
                                + storage[posXremovido - 1][posZremovido - 1].shipping_month() + "/"
                                + storage[posXremovido - 1][posZremovido - 1].shipping_year()
                                + "\nDestination: " + storage[posXremovido - 1][posZremovido - 1].destination()
                                + "\nremoved!");
                        storage[posXremovido - 1][posZremovido - 1] = null;
                        info.guardaY(2);
                        axisY.gotoPos(2);
                    }
                }
            } else {
                System.out.println(
                        "There is no pallete at the coordinates (" + posXremovido + "," + posZremovido + ")!\n");
            }
        } else { // if de verificar se ha palete
            System.out.println("you already have a pallete inside the cage. Get rid of it!!!\n");
        }
    }

    public void maxHumidity() { // scans validados!
        do {
            System.out.println("What is the maximum humidity that this storage can take?");
            Max_humidity = scan.nextFloat();
            if ((Max_humidity < 0) || (Max_humidity > 100)) {
                System.out.println("Invalid humidity value!");
            }
        } while ((Max_humidity < 0) || (Max_humidity > 100));
        System.out.println("Verifying if any of the pallets surpasses a threshold...");
        alertON = verifyAlerts(storage, Max_humidity, Max_day, Max_month, Max_year);
        if (alertON) {
            isLed1Interrupted = false;
            Led1On = led1On();
        } else {
            isLed1Interrupted = true;
        }
    }

    public void maxDate() { // scans validados!
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateInput;
        boolean isValid;
        String day;
        String month;
        do {
            System.out.println("What is the maximum shipping date for any of these pallets?");
            System.out.println("Shipping Day: ");
            Max_day = scan.nextInt();
            System.out.println("Shipping Month: ");
            Max_month = scan.nextInt();
            System.out.println("Shipping Year: ");
            Max_year = scan.nextInt();
            if (Max_day < 10) {
                day = "0" + Max_day;
            } else {
                day = "" + Max_day;
            }
            if (Max_month < 10) {
                month = "0" + Max_month;
            } else {
                month = "" + Max_month;
            }
            dateInput = (day + "/" + month + "/" + Max_year);
            isValid = isValidDate(dateInput, formatter);
            if (isValid == false) {
                System.out.println("Invalid Date!!!");
            }
        } while (isValid == false);
        System.out.println("Verifying if any of the pallets surpasses a threshold...");
        alertON = verifyAlerts(storage, Max_humidity, Max_day, Max_month, Max_year);
        if (alertON) {
            isLed1Interrupted = false;
            Led1On = led1On();
        } else {
            isLed1Interrupted = true;
        }
    }

    public void listPalletes() {// nao ha scans!
        boolean isSEmpty = true;
        System.out.println("List of All pallets:");
        for (int i = 2; i >= 0; i--) {
            for (int j = 0; j < 3; j++) {
                if (storage[j][i] != null) {
                    isSEmpty = false;
                    System.out.println(
                            "\nPallet at (" + storage[j][i].desiredX + "," + storage[j][i].desiredZ + ") With info:"
                            + "\nProduct Type: " + storage[j][i].product_type()
                            + "\nHumidity: " + String.format("%.1f", storage[j][i].humidity()) + " %"
                            + "\nproducer_ID: " + storage[j][i].producer_ID()
                            + "\nShipping Date: " + storage[j][i].shipping_day() + "/"
                            + storage[j][i].shipping_month() + "/" + storage[j][i].shipping_year()
                            + "\nDestination: " + storage[j][i].destination()
                            + "\nAlert State: " + storage[j][i].is_alert());
                }
            }
            if (isSEmpty == false) {
                System.out.println("");
            }
        }
        if (isSEmpty) {
            System.out.println("Storage is still empty!");
        }
    }

    public void searchPalleteType() { // os scans estão verificados!
        boolean isEmpty = true;
        int option;
        System.out.println("Do you want to look by type of product (1) or by producer ID (2)?");
        option = scan.nextInt();
        switch (option) {
            case 1:
                System.out.println("What is the type of product you want to display?");
                String productType = myObj.nextLine();
                for (int i = 2; i >= 0; i--) {
                    for (int j = 0; j < 3; j++) {
                        if (storage[j][i] != null) {
                            if (productType.equals(storage[j][i].product_type())) {
                                isEmpty = false;
                                System.out.println("Pallete at (" + storage[j][i].desiredX + ","
                                        + storage[j][i].desiredZ + ") With info:"
                                        + "\nProducer ID: " + storage[j][i].producer_ID()
                                        + "\nHumidity: " + String.format("%.1f", storage[j][i].humidity()) + " %"
                                        + "\nShipping Date " + storage[j][i].shipping_day() + "/"
                                        + storage[j][i].shipping_month() + "/"
                                        + storage[j][i].shipping_year()
                                        + "\nDestination: " + storage[j][i].destination()
                                        + "\nAlert State: " + storage[j][i].is_alert());
                            }
                        }
                    }
                    if (isEmpty == false) {
                        System.out.println("");
                    }
                }
                if (isEmpty) {
                    System.out.println("There are no matches for that product type!");
                }
                break;
            case 2:
                boolean isE = true;
                System.out.println("What is the producer ID you want to display?");
                int prodID = scan.nextInt();
                for (int i = 2; i >= 0; i--) {
                    for (int j = 0; j < 3; j++) {
                        if (storage[j][i] != null) {
                            if (prodID == storage[j][i].producer_ID()) {
                                isE = false;
                                System.out.println("Pallete at (" + storage[j][i].desiredX + ","
                                        + storage[j][i].desiredZ + ") With info:"
                                        + "\nProduct Type: " + storage[j][i].product_type()
                                        + "\nHumidity: " + String.format("%.1f", storage[j][i].humidity()) + " %"
                                        + "\nShipping Date " + storage[j][i].shipping_day() + "/"
                                        + storage[j][i].shipping_month() + "/"
                                        + storage[j][i].shipping_year()
                                        + "\nDestination: " + storage[j][i].destination()
                                        + "\nAlert State: " + storage[j][i].is_alert());
                            }
                        }
                    }
                    if (isE == false) {
                        System.out.println("");
                    }
                }
                if (isE) {
                    System.out.println("There are no matches for that producer ID!");
                }
                break;
            default:
                System.out.println("Incorrect value for an option!");
                break;

        }
    }

    public void removeSearchedPallete() throws InterruptedException {
        boolean isEmpty = true;
        int option;
        System.out.println("Do you want to remove by type of product (1) or by producer ID (2)?");
        try {
            option = scan.nextInt();
            switch (option) {
                case 1:
                    System.out.println("What is the type of product you want to deliver?");
                    String productType = myObj.nextLine();
                    for (int i = 2; i >= 0; i--) {
                        for (int j = 0; j < 3; j++) {
                            if (storage[j][i] != null) {
                                if (productType.equals(storage[j][i].product_type())) {
                                    isEmpty = false; // pq? R: e para nao falhar caso nao haja nada lol
                                    System.out.println("Removing pallete at (" + storage[j][i].desiredX + ","
                                            + storage[j][i].desiredZ + ") With info:"
                                            + "\nProducer ID: " + storage[j][i].producer_ID()
                                            + "\nHumidity: " + String.format("%.1f", storage[j][i].humidity()) + " %"
                                            + "\nShipping Date " + storage[j][i].shipping_day() + "/"
                                            + storage[j][i].shipping_month() + "/"
                                            + storage[j][i].shipping_year()
                                            + "\nDestination: " + storage[j][i].destination()
                                            + "\nAlert State: " + storage[j][i].is_alert());

                                }
                            }
                        }

                        if (isEmpty == false) {
                            System.out.println("");
                        }
                    }
                    if (isEmpty == false) {
                        System.out.println("removing pallets with type of product (1)");
                        int posXremovido = 0;
                        int posZremovido = 0;
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                if (storage[i][j] != null) {
                                    // System.out.println(storage[j][i].product_type());
                                    if (productType.equals(storage[j][i].product_type())) {
                                        info.guardaAcao(3); // como esta a remover alertas, temos de dizer que isto é uma ação
                                        // especial. Não sei se isto é necessário, mas ok ok
                                        posXremovido = storage[i][j].desiredX();
                                        posZremovido = storage[i][j].desiredZ();
                                        System.out.println(storage[i][j].desiredZ() + " " + storage[i][j].desiredX());
                                       axisXThread(posXremovido);
                                        axisZThread(posZremovido);
                                        do {
                                        } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                                        do {
                                        } while (emergencyON == true);
                                        if (emergencyON == false) {
                                            axisXThread(posXremovido);
                                            axisZThread(posZremovido);
                                            do {
                                            } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                                            mechanism.takePartInCell();
                                            axisXThread(3);
                                            axisZThread(1);
                                            do {
                                            } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                                            do {
                                            } while (emergencyON == true);
                                            if (emergencyON == false) {
                                                axisXThread(3);
                                                axisZThread(1);
                                                do {
                                                } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                                                if (Mechanism.cageFull() == 1) {
                                                    System.out.println(
                                                            "Click on the button \"takeFromCage\" to remove the pallet from the system");
                                                    do {
                                                    } while (Mechanism.cageFull() == 1);
                                                }
                                                info.guardaY(1);
                                                axisY.gotoPos(1);
                                                System.out.println("Pallet removed!");
                                                storage[posXremovido - 1][posZremovido - 1] = null;
                                                info.guardaY(2);
                                                axisY.gotoPos(2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("There are no matches for that product type!");
                    }
                    break;
                case 2:
                    boolean isE = true;
                    System.out.println("What is the producer ID you want to deliver?");
                    int prodID = scan.nextInt();
                    for (int i = 2; i >= 0; i--) {
                        for (int j = 0; j < 3; j++) {
                            if (storage[j][i] != null) {
                                if (prodID == storage[j][i].producer_ID()) {
                                    isE = false;
                                    System.out.println("Removing Pallete at (" + storage[j][i].desiredX + ","
                                            + storage[j][i].desiredZ + ") With info:"
                                            + "\nProduct Type: " + storage[j][i].product_type()
                                            + "\nHumidity: " + String.format("%.1f", storage[j][i].humidity()) + " %"
                                            + "\nShipping Date " + storage[j][i].shipping_day() + "/"
                                            + storage[j][i].shipping_month() + "/"
                                            + storage[j][i].shipping_year()
                                            + "\nDestination: " + storage[j][i].destination()
                                            + "\nAlert State: " + storage[j][i].is_alert());
                                }
                            }
                        }
                        if (isE == false) {
                            System.out.println("");
                        }
                    }
                    if (isE == false) {
                        System.out.println("removing pallets with producer ID (2)");
                        int posXremovido = 0;
                        int posZremovido = 0;

                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                if (storage[i][j] != null) {
                                    // System.out.println(storage[j][i].product_type());
                                    if (prodID == storage[i][j].producer_ID()) {
                                        info.guardaAcao(3); // como esta a remover alertas, temos de dizer que isto é uma ação
                                        // especial. Não sei se isto é necessário, mas ok ok
                                        posXremovido = storage[i][j].desiredX();
                                        posZremovido = storage[i][j].desiredZ();
                                        System.out.println(storage[i][j].desiredZ() + " " + storage[i][j].desiredX());
                                        axisXThread(posXremovido);
                                        axisZThread(posZremovido);
                                        do {
                                        } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                                        do {
                                        } while (emergencyON == true);
                                        if (isSwitch2 == false) {
                                            axisXThread(posXremovido);
                                            axisZThread(posZremovido);
                                            do {
                                            } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                                            mechanism.takePartInCell();
                                            axisXThread(3);
                                            axisZThread(1);
                                            do {
                                            } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                                            do {
                                            } while (emergencyON == true);
                                            if (isSwitch2 == false) {
                                                axisXThread(3);
                                                axisZThread(1);
                                                do {
                                                } while ((axisXThread.isAlive()) || (axisZThread.isAlive()));
                                                if (Mechanism.cageFull() == 1) {
                                                    System.out.println(
                                                            "Click on the button \"takeFromCage\" to remove the pallet from the system");
                                                    do {
                                                    } while (Mechanism.cageFull() == 1);
                                                }
                                                info.guardaY(1);
                                                axisY.gotoPos(1);
                                                System.out.println("Pallet removed!");
                                                storage[posXremovido - 1][posZremovido - 1] = null;
                                                info.guardaY(2);
                                                axisY.gotoPos(2);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        System.out.println("There are no matches for that producer ID!");
                    }
                    break;
                default:
                    System.out.println("Incorrect value for an option!");
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid Format!!! Input a number!");
            scan.nextLine();
        }
    }

}
