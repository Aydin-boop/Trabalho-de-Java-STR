
import java.util.Scanner;

public class App {

    static boolean isLed1Interrupted = true;
    static boolean isLed2Interrupted = true;
    static boolean isSwitchesInterrupted = false;

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

    public static Thread led1On() {
        Mechanism mechanism = new Mechanism();
        Thread led1onThread = new Thread() {
            public void run() {
                while (!isLed1Interrupted) {
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
        Mechanism mechanism = new Mechanism();
        Thread led2onThread = new Thread() {
            public void run() {
                while (!isLed2Interrupted) {
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

    public static void Calibration() throws InterruptedException {
        AxisX axisX = new AxisX();
        AxisZ axisZ = new AxisZ();
        AxisY axisY = new AxisY();
        System.out.println("System initializing calibration");
        axisX.moveBackward();
        axisY.moveBackward();
        do {
        } while (axisY.getPos() == -1);
        axisY.stop();
        axisZ.moveBackward();
        Thread axisXThreadCalibration = axisXThread(1);
        Thread axisZThreadCalibration = axisZThread(1);
        Thread axisYThreadCalibration = axisYThread(2);
        axisXThreadCalibration.join();
        axisZThreadCalibration.join();
        axisYThreadCalibration.join();
    }

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
                            System.out.println("Click on the button \"takeFromCage\" to remove the pallet from the system");
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
                    }
                }
            }
        }

        isSwitchesInterrupted = true;
    }

    public static Thread switchesThread(Pallet[][] s) {
        Mechanism mechanism = new Mechanism();
        Thread swiThread = new Thread() {
            public void run() {
                while (!isSwitchesInterrupted) {
                    if (mechanism.switch1Pressed()) {
                        try {
                            isLed1Interrupted = true;
                            RemoveAlerts(s);
                            Calibration();
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
        int maisProximos[] = {0, 0};
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                if (s[i - 1][j - 1] == null) {
                    double d = Math.sqrt(Math.pow(Math.abs(i - axisx10.getPos()), 2) + Math.pow(Math.abs(j - axisz10.getPos()), 2));
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

    public static void ShowMenu(Pallet[][] s, int Max_humidity, int Max_day, int Max_month, int Max_year) {
        System.out.println("\n\n**********STORAGE MENU**********");
        System.out.println("1 - Move X to the left");
        System.out.println("2 - Move X to the right");
        System.out.println("3 - Stop X Axis");
        System.out.println("4 - Move cage to the desired (X,Z) coordinates");
        System.out.println("5 - Move only one axis to the desired position");
        System.out.println("6 - Calibrate");
        System.out.println("7 - Place a pallete");
        System.out.println("8 - Withdraw pallete(s)");
        System.out.println("9 - Verify if cage is full");
        System.out.println("10 - Define Humidity Threshold (currently " + Max_humidity + ")");
        System.out.println("11 - Define maximum shipping date (currently " + Max_day + "/" + Max_month + "/" + Max_year + ")");
        System.out.println("12 - List all stored pallets");
        System.out.println("13 - Display information of a pallete by product type or by producer ID");
        System.out.println("*****STORAGE STATE*****");
        char X;
        for (int i = 2; i >= 0; i--) {
            for (int j = 0; j < 3; j++) {
                if ((s[j][i] != null) && (s[j][i].is_alert() == false)) {
                    X = 'X';
                } else if ((s[j][i] != null) && (s[j][i].is_alert() == true)) {
                    X = 'Y';
                } else {
                    X = ' ';
                }
                System.out.print(" [" + X + "] ");
            }
            System.out.print("\n");
        }
        System.out.println("Captions:");
        System.out.println("X - pallet there.");
        System.out.println("Y - pallet with active alert.");
        System.out.println("  - No pallet there.");
    }

    public static boolean verifyAlerts(Pallet[][] Storage, int Max_humidity, int Max_day, int Max_month, int Max_year) {
        boolean alertOn = false;
        for (int i = 2; i >= 0; i--) {
            for (int j = 0; j < 3; j++) {
                if (Storage[j][i] != null) {
                    if ((Storage[j][i].humidity() > Max_humidity) || (Storage[j][i].shipping_year() > Max_year)) {
                        Storage[j][i].change_alert(true);
                        System.out.printf("Pallet at position (%d, %d) is surpassing a threshold!!!", j+1, i+1);
                    } else if ((Storage[j][i].humidity() > Max_humidity) || (Storage[j][i].shipping_year() == Max_year) && (Storage[j][i].shipping_month() > Max_month)) {
                        Storage[j][i].change_alert(true);
                        System.out.printf("Pallet at position (%d, %d) is surpassing a threshold!!!", j+1, i+1);
                    } else if ((Storage[j][i].humidity() > Max_humidity) || (Storage[j][i].shipping_year() == Max_year) && (Storage[j][i].shipping_month() == Max_month) && (Storage[j][i].shipping_day() > Max_day)) {
                        Storage[j][i].change_alert(true);
                        System.out.printf("Pallet at position (%d, %d) is surpassing a threshold!!!", j+1, i+1);
                    } else {
                        if (Storage[j][i].is_alert() == true) {
                            Storage[j][i].change_alert(false);
                            System.out.printf("Pallet at position (%d, %d) is no longer surpassing a threshold!!!", j+1, i+1);
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
                    } else if ((Storage[i][j].humidity() > Max_humidity) || (Storage[i][j].shipping_year() == Max_year) && (Storage[i][j].shipping_month > Max_month)) {
                        alertOn = true;
                    } else if ((Storage[i][j].humidity() > Max_humidity) || (Storage[i][j].shipping_year() == Max_year) && (Storage[i][j].shipping_month == Max_month) && (Storage[i][j].shipping_day() > Max_day)) {
                        alertOn = true;
                    }
                }
            }
        }
        return alertOn;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Labwork 2 from Java!");

        Storage.initializeHardwarePorts();

        AxisX axisX = new AxisX();
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();

        Mechanism mechanism = new Mechanism();

        Pallet[][] Storage = new Pallet[3][3];

        int op = -1;
        int op1;
        int op2;

        Scanner scan = new Scanner(System.in);
        Scanner myObj = new Scanner(System.in);

        Thread axisXThread;
        Thread axisZThread;
        //Thread axisYThread;

        Thread Led1On;
        Thread Led2On;

        Thread switches;

        boolean alertON = false;
        int Max_humidity = 100;
        int Max_day = 24;
        int Max_month = 11;
        int Max_year = 2025; //sim, isto e a data limite de entrega do trabalho

        Calibration();
        switches = switchesThread(Storage);
        while (op != 0) {
            ShowMenu(Storage, Max_humidity, Max_day, Max_month, Max_year);
            System.out.println("Enter an option:");
            op = scan.nextInt();
            switch (op) {
                case 1:
                    axisX.moveForward();
                    break;
                case 2:
                    axisX.moveBackward();
                    break;
                case 3:
                    axisX.stop();
                    break;
                case 4:
                    System.out.println("para que coordenadas quer ir? Coloque os inputs no formato X Z");
                    int posX = scan.nextInt();
                    int posZ = scan.nextInt();
                    axisXThread = axisXThread(posX);
                    axisZThread = axisZThread(posZ);
                    axisXThread.join();
                    axisZThread.join();
                    break;
                case 5:
                    System.out.println("Which Axis do you want to move?: (1-X, 2-Y, 3-Z)");
                    op1 = scan.nextInt();
                    switch (op1) {
                        case 1:
                            System.out.println("Which Position of Axis X do you want to move?: (1, 2, 3)");
                            op2 = scan.nextInt();
                            axisX.gotoPos(op2);
                            break;
                        case 2:
                            System.out.println("Which Position of Axis Y do you want to move?: (1, 2, 3)");
                            op2 = scan.nextInt();
                            axisY.gotoPos(op2);
                            break;
                        case 3:
                            System.out.println(
                                    "Which Position of Axis Z do you want to move?: (1-1D, 10-1U , 2-2D, 20-2U, 3-3D, 30-3U)");
                            op2 = scan.nextInt();
                            axisZ.gotoPos(op2);
                            break;
                    }
                    break;
                case 6:
                    Calibration();
                    break;
                case 7:
                    AxisY axisY3 = new AxisY();
                    axisXThread = axisXThread(1);
                    axisZThread = axisZThread(1);
                    axisXThread.join();
                    axisZThread.join();
                    axisY3.gotoPos(1);

                    System.out.println("Manual pallete storing: What is the pallete's metadata? Don't forget to put it on the cage!");
                    System.out.println("Product type: ");
                    String product_type = myObj.nextLine();
                    System.out.println("Humidity: ");
                    int humidity = scan.nextInt();
                    System.out.println("Producer ID: ");
                    int producerID = scan.nextInt();
                    System.out.println("Desired X: ");
                    int posXposto = scan.nextInt();
                    System.out.println("Desired Z: ");
                    int posZposto = scan.nextInt();
                    System.out.println("Shipping day: ");
                    int shipping_day = scan.nextInt();
                    System.out.println("Shipping month: ");
                    int shipping_month = scan.nextInt();
                    System.out.println("Shipping year: ");
                    int shipping_year = scan.nextInt();

                    if ((posXposto == 0) && (posZposto == 0)) {
                        int pos[] = VerMaisProximo(Storage);
                        posXposto = pos[0];
                        posZposto = pos[1];
                        System.out.println("Coordinates of automatically allocated pallete: (" + posXposto + "," + posZposto + ")");
                    }

                    Pallet p = new Pallet(product_type, humidity, producerID, posXposto, posZposto, shipping_day, shipping_month, shipping_year);

                    if (Storage[posXposto - 1][posZposto - 1] == null) {
                        axisY3.gotoPos(2);
                        if (Mechanism.cageFull() == 1) {
                            axisXThread = axisXThread(posXposto);
                            axisZThread = axisZThread(posZposto * 10);
                            axisXThread.join();
                            axisZThread.join();
                            mechanism.putPartInCell();
                            if (humidity > Max_humidity) {
                                isLed1Interrupted = false;
                                Led1On = led1On();
                            }
                            if (shipping_year > Max_year) {
                                isLed1Interrupted = false;
                                Led1On = led1On();
                            } else if ((shipping_year == Max_year) && (shipping_month > Max_month)) {
                                isLed1Interrupted = false;
                                Led1On = led1On();
                            } else if ((shipping_year == Max_year) && (shipping_month == Max_month) && (shipping_day > Max_day)) {
                                isLed1Interrupted = false;
                                Led1On = led1On();
                            }
                            if (isLed1Interrupted == false) {
                                p.change_alert(true);
                                System.out.println("Pallete with Alert!");
                            }
                            Storage[posXposto - 1][posZposto - 1] = p;
                        } else {
                            System.out.println("You didn't put any pallete!!!");
                        }
                    } else {
                        System.out.println("There is a pallete there! Info:"
                                + "\nProductType: " + Storage[posXposto - 1][posZposto - 1].product_type()
                                + "\nHumidity: " + Storage[posXposto - 1][posZposto - 1].humidity()
                                + "\nproducer_ID: " + Storage[posXposto - 1][posZposto - 1].producer_ID()
                                + "\ndesiredX: " + Storage[posXposto - 1][posZposto - 1].desiredX()
                                + "\ndesiredZ: " + Storage[posXposto - 1][posZposto - 1].desiredZ()
                                + "\nShippingDate: " + Storage[posXposto - 1][posZposto - 1].shipping_day() + "/"
                                + Storage[posXposto - 1][posZposto - 1].shipping_month() + "/"
                                + Storage[posXposto - 1][posZposto - 1].shipping_year());
                    }

                    break;
                case 8:
                    System.out.println("What is the mode you want to unload the pallets with? Manual (1) or Assisted (2)?");
                    int opt = scan.nextInt();
                    if (Mechanism.cageFull() == 0) {
                        if (opt == 1) {
                            System.out.println("Manual pallete removal: What are the coordinates of the pallete you want to remove?");
                        } else {
                            System.out.println("Automatic pallete removal: What are the coordinates of the pallete you want to remove?");
                        }
                            System.out.println("Desired X: ");
                            int posXremovido = scan.nextInt();
                            System.out.println("Desired Z: ");
                            int posZremovido = scan.nextInt();
                            Pallet p1 = Storage[posXremovido - 1][posZremovido - 1];

                            if (p1 != null) {
                                axisXThread = axisXThread(posXremovido);
                                axisZThread = axisZThread(posZremovido);
                                axisXThread.join();
                                axisZThread.join();
                                mechanism.takePartInCell();
                                if (opt == 1) {
                                    axisXThread = axisXThread(3);
                                } else if (opt == 2) {
                                    axisXThread = axisXThread(posXremovido);
                                    isLed2Interrupted = false;
                                    Led2On = led2On();
                                }
                                axisZThread = axisZThread(1);
                                axisXThread.join();
                                axisZThread.join();
                                if (Mechanism.cageFull() == 1) {
                                    System.out.println("Click on the button \"takeFromCage\" to remove the pallete from the system");
                                    do {
                                    } while (Mechanism.cageFull() == 1);
                                }
                                axisY.gotoPos(1);
                                isLed2Interrupted = true;
                                System.out.println("Pallet with info"
                                        + "\nProductType: " + Storage[posXremovido - 1][posZremovido - 1].product_type()
                                        + "\nHumidity: " + Storage[posXremovido - 1][posZremovido - 1].humidity()
                                        + "\nproducer_ID: " + Storage[posXremovido - 1][posZremovido - 1].producer_ID()
                                        + "\ndesiredX: " + Storage[posXremovido - 1][posZremovido - 1].desiredX()
                                        + "\ndesiredZ: " + Storage[posXremovido - 1][posZremovido - 1].desiredZ()
                                        + "\nShippingDate: " + Storage[posXremovido - 1][posZremovido - 1].shipping_day() + "/"
                                        + Storage[posXremovido - 1][posZremovido - 1].shipping_month() + "/"
                                        + Storage[posXremovido - 1][posZremovido - 1].shipping_year()
                                        + "\nremoved!");
                                Storage[posXremovido - 1][posZremovido - 1] = null;
                                axisY.gotoPos(2);
                            } else {
                                System.out.println("There is no pallete at the coordinates (" + posXremovido + "," + posZremovido + ")!\n");
                            }
                    } else { //if de verificar se ha palete
                        System.out.println("you already have a pallete inside the cage. Get rid of it!!!\n");
                    }
                    break;
                case 9:

                    int o = Mechanism.cageFull();
                    System.out.println("cage is: ");
                    System.out.println(o);
                    break;
                case 10:
                    System.out.println("What is the maximum humidity that this storage can take?");
                    Max_humidity = scan.nextInt();
                    System.out.println("Verifying if any of the pallets surpasses the humidity threshold...");
                    alertON = verifyAlerts(Storage, Max_humidity, Max_day, Max_month, Max_year);
                    if (alertON) {
                        isLed1Interrupted = false;
                        Led1On = led1On();
                    } else {
                        isLed1Interrupted = true;
                    }
                    break;
                case 11:
                    System.out.println("What is the maximum shipping date for any of these pallets?");
                    System.out.println("Shipping Day: ");
                    Max_day = scan.nextInt();
                    System.out.println("Shipping Month: ");
                    Max_month = scan.nextInt();
                    System.out.println("Shipping Year: ");
                    Max_year = scan.nextInt();
                    System.out.println("Verifying if any of the pallets surpasses the shipping date threshold...");
                    alertON = verifyAlerts(Storage, Max_humidity, Max_day, Max_month, Max_year);
                    if (alertON) {
                        isLed1Interrupted = false;
                        Led1On = led1On();
                    } else {
                        isLed1Interrupted = true;
                    }
                    break;
                case 12:
                    boolean isSEmpty = true;
                    System.out.println("List of All pallets:");
                    for (int i = 2; i >= 0; i--) {
                        for (int j = 0; j < 3; j++) {
                            if (Storage[j][i] != null) {
                                isSEmpty = false;
                                System.out.println("\nPallet at (" + Storage[j][i].desiredX + "," + Storage[j][i].desiredZ + ") With info:"
                                        + "\nProduct Type: " + Storage[j][i].product_type()
                                        + "\nHumidity: " + Storage[j][i].humidity()
                                        + "\nproducer_ID" + Storage[j][i].producer_ID()
                                        + "\nShipping Date " + Storage[j][i].shipping_day() + "/" + Storage[j][i].shipping_month() + "/" + Storage[j][i].shipping_year()
                                        + "\nAlert State: " + Storage[j][i].is_alert());
                            }
                        }
                        if (isSEmpty == false) {
                            System.out.println("\n");
                        }
                    }
                    if (isSEmpty) {
                        System.out.println("Storage is still empty!");
                    }
                    break;
                case 13:
                    boolean isEmpty = true;
                    System.out.println("Do you want to look by type of product (1) or by producer ID (2)?");
                    int option = scan.nextInt();
                    switch (option) {
                        case 1:
                            System.out.println("What is the type of product you want to display?");
                            String productType = myObj.nextLine();
                            for (int i = 2; i >= 0; i--) {
                                for (int j = 0; j < 3; j++) {
                                    if (Storage[j][i] != null) {
                                        if (productType.equals(Storage[j][i].product_type())) {
                                            isEmpty = false;
                                            System.out.println("Pallete at (" + Storage[j][i].desiredX + "," + Storage[j][i].desiredZ + ") With info:"
                                                    + "\nProducer ID: " + Storage[j][i].producer_ID()
                                                    + "\nHumidity: " + Storage[j][i].humidity()
                                                    + "\nShipping Date " + Storage[j][i].shipping_day() + "/" + Storage[j][i].shipping_month() + "/" + Storage[j][i].shipping_year()
                                                    + "\nAlert State: " + Storage[j][i].is_alert());
                                        }
                                    }
                                }
                                if (isEmpty == false) {
                                    System.out.println("\n");
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
                                    if (Storage[j][i] != null) {
                                        if (prodID == Storage[j][i].producer_ID()) {
                                            isE = false;
                                            System.out.println("Pallete at (" + Storage[j][i].desiredX + "," + Storage[j][i].desiredZ + ") With info:"
                                                    + "\nProduct Type: " + Storage[j][i].product_type()
                                                    + "\nHumidity: " + Storage[j][i].humidity()
                                                    + "\nShipping Date " + Storage[j][i].shipping_day() + "/" + Storage[j][i].shipping_month() + "/" + Storage[j][i].shipping_year()
                                                    + "\nAlert State: " + Storage[j][i].is_alert());
                                        }
                                    }
                                }
                                if (isE == false) {
                                    System.out.println("\n");
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
                    break;
                default:
                    System.out.println("Nao implementado!");
                    break;
            }
        }
        scan.close();
        switchesThread(Storage).join();
    }
}

/*
 * 
 * ter duas matrizes 3 por 3
 * para meter para tirar
 * 30       3
 * 20       2
 * 10       1
 *  1 2 3     1 2 3
 * 
 * 
 */
