import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Menu {
    static boolean isLed1Interrupted = true;
    static boolean isLed2Interrupted = true;
    static boolean emergencyON = false;

    static AxisX axisX = new AxisX();
    static AxisY axisY = new AxisY();
    static AxisZ axisZ = new AxisZ();

    static Thread axisXThread;
    static Thread axisYThread;
    static Thread axisZThread;

    static Thread Led1On;
    static Thread Led2On;
    static Thread LedsOn;

    int op1;
    int op2;
    //
    Scanner scan = new Scanner(System.in);
    Scanner myObj = new Scanner(System.in);

    static Mechanism mechanism = new Mechanism();
    Pallet[][] storage = new Pallet[3][3];

    static boolean alertON = false;
    float Max_humidity = 50;
    int Max_day = 24;
    int Max_month = 11;
    int Max_year = 2025; // sim, isto e a data limite de entrega do trabalho xD

    static EmergnecyInfo info = new EmergnecyInfo();

    // threads
    public static Thread axisXThread(int pos) {
        info.guardaX(pos);
        axisXThread = new Thread() {
            public void run() {
                axisX.gotoPos(pos);
            }
        };
        axisXThread.start();
        return axisXThread;

    }

    public static Thread axisZThread(int pos) {
        info.guardaZ(pos);
        axisZThread = new Thread() {
            public void run() {
                axisZ.gotoPos(pos);
            }
        };
        axisZThread.start();
        return axisZThread;

    }

    public static Thread axisYThread(int pos) {
        info.guardaY(pos);
        axisYThread = new Thread() {
            public void run() {
                axisY.gotoPos(pos);
            }
        };
        axisYThread.start();
        return axisYThread;

    }

    public static Thread led1On() {
        Thread led1onThread = new Thread() {
            public void run() {
                while ((!isLed1Interrupted)&&(isLed2Interrupted)) {
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
                    try {Thread.sleep(250);} catch (InterruptedException e) {}
                    Mechanism.ledOn(2);
                    try {Thread.sleep(250);} catch (InterruptedException e) {}
                }
            }
        };
        led2onThread.start();
        return led2onThread;
    }

    public static Thread EmergencyThread() {
        Thread EThread = new Thread() {
            public void run() {
                while (true) { 
                    if (mechanism.bothSwitchesPressed()) {
                        //System.out.println("EMETRTTGUGJFIRGJFIGJFIGFJIFJGFI");       
                            axisZ.stop();
                            axisX.stop();
                            axisY.stop();
                            axisZThread.interrupt();
                            axisXThread.interrupt();
                            axisYThread.interrupt();
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

    public static Thread VerificaAlertasThread(Pallet [][] s) {
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
                            do{
                                axisX.moveBackward();
                            } while(axisX.getPos() == -1);
                            axisXThread = axisXThread(info.posX());
                            do{
                                axisZ.moveBackward();
                            } while(axisZ.getPos() == -1);
                            axisZThread = axisZThread(info.posZ());
                            axisYThread = axisYThread(info.posY());
                            
                            if ((info.acao() != 3)  && (alertON == true)){
                                try {
                                RemoveAlerts(s);
                                System.out.println("Click on number 3 to calibrate and return to the menu");
                                } catch (InterruptedException e){}
                            }

                            /* 
                            try {
                            axisXThread.join();
                            axisZThread.join(); 
                            axisYThread.join(); 
                            } catch (InterruptedException e) {
                            }
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

    public static Thread switchesThread(Pallet[][] s) throws InterruptedException{
        Thread swiThread = new Thread() {

            public void run() {
                while (true) {
                    if (mechanism.switch1Pressed() && (emergencyON == false)) {
                        try {
                            isLed1Interrupted = true;
                            Led1On = led1On();
                            RemoveAlerts(s);
                            mechanism.ledsOff();
                            System.out.println("Click on number 3 to calibrate and return to the menu");
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

    public static void RemoveAlerts(Pallet[][] s) throws InterruptedException {
        System.out.println("removing pallets with active alerts!!!");
        int posXremovido = 0;
        int posZremovido = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (s[i][j] != null) {
                    if (s[i][j].is_alert() == true) {
                        info.guardaAcao(3); //como esta a remover alertas, temos de dizer que isto é uma ação especial
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
                                + "\nHumidity: " + s[posXremovido - 1][posZremovido - 1].humidity() + " %"
                                + "\nproducer_ID: " + s[posXremovido - 1][posZremovido - 1].producer_ID()
                                + "\nX: " + s[posXremovido - 1][posZremovido - 1].desiredX()
                                + "\nZ: " + s[posXremovido - 1][posZremovido - 1].desiredZ()
                                + "\nShippingDate: " + s[posXremovido - 1][posZremovido - 1].shipping_day() + "/"
                                + s[posXremovido - 1][posZremovido - 1].shipping_month() + "/"
                                + s[posXremovido - 1][posZremovido - 1].shipping_year()
                                +"\nDestination: " + s[posXremovido - 1][posZremovido - 1].destination()
                                + "\nremoved!");
                        s[posXremovido - 1][posZremovido - 1] = null;
                        info.guardaY(2);
                        axisY.gotoPos(2);
                    }
                }
            }
        }
    }
    
    public static boolean verifyAlerts(Pallet[][] Storage, float Max_humidity, int Max_day, int Max_month, int Max_year) {
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

    // menu options
    public static void Calibration() throws InterruptedException {
        info.guardaAcao(0);
        System.out.println("System initializing calibration");
        axisX.moveBackward();
        axisY.moveBackward();
        do {
        } while (axisY.getPos() == -1);
        axisY.stop();
        axisZ.moveBackward();
        axisXThread = axisXThread(1);
        axisZThread = axisZThread(1);
        axisYThread = axisYThread(2);
        axisXThread.join();
        axisZThread.join();
        axisYThread.join();
    }

    public void ShowMenu(float Max_humidity, int Max_day, int Max_month, int Max_year) { //ve quais sao as cores que ficam mais bonitas sffv
        String State;
        if (emergencyON)
            State = "\u001B[31mEmergency\u001B[0m";
        else 
            State = "\u001B[32mNormal\u001B[0m";
        System.out.println("\n\n**********STORAGE MENU**********");
        System.out.println("1 - Move cage to the desired (X,Z) coordinates");
        System.out.println("2 - Move only one axis to the desired position");
        System.out.println("3 - Calibrate");
        System.out.println("4 - Place a pallete");
        System.out.println("5 - Withdraw pallete(s)");
        System.out.println("6 - Define Humidity Threshold (currently at " + String.format("%.1f", Max_humidity) +  " %)");
        System.out.println("7 - Define maximum shipping date (currently " + Max_day + "/" + Max_month + "/" + Max_year + ")");
        System.out.println("8 - List all stored pallets");
        System.out.println("9 - Display information of a pallete by product type or by producer ID");
        System.out.println("*****STORAGE STATE*****");
        String X;
        for (int i = 2; i >= 0; i--) {
            for (int j = 0; j < 3; j++) {
                if ((storage[j][i] != null) && (storage[j][i].is_alert() == false)) {
                    X = "\u001B[32mX\u001B[0m"; //X verde com background normal
                } else if ((storage[j][i] != null) && (storage[j][i].is_alert() == true)) {
                    X = "\u001B[31mY\u001B[0m"; //Y vermelho e com background normal
                } else {
                    X = " ";
                }
                System.out.print("\u001B[33m[\u001B[0m" + X + "\u001B[33m]\u001B[0m "); //[] com cor amarela e com background normal
            }
            System.out.print("\n");
        }
        System.out.println("Captions:");
        System.out.println("\u001B[32mX\u001B[0m - pallet there.");
        System.out.println("\u001B[31mY\u001B[0m - pallet with active alert.");
        System.out.println("  - No pallet there.");
        System.out.println("\nCurrently at "+ State +" State");
    }

    public void manualPosition() throws InterruptedException {
        int posX;
        int posZ;
        info.guardaY(2);
        info.guardaAcao(0);
        axisY.gotoPos(2); //para verificar que o sistema se move dentro dos seus limites
        do {
        System.out.println("Input X and Z:");
        posX = scan.nextInt();
        posZ = scan.nextInt();
        if ( (((posX <= 0) || (posX > 3)) && ((posZ != 0))) || (((posX != 0)) && ((posZ <= 0) || (posZ > 3))) )
            System.out.println("Invalid coordinates!!!");
        } while ( (((posX <= 0) || (posX > 3)) && ((posZ != 0))) || (((posX != 0)) && ((posZ <= 0) || (posZ > 3))) );
        
        if ((posX == 0) && (posZ == 0)) {
            int pos[] = VerMaisProximo(storage);
            posX = pos[0];
            posZ = pos[1];
            System.out.println("Coordinates of auto-pilot to reduce travel distance: (" + posX + "," + posZ + ")");
        }

        axisXThread = axisXThread(posX);
        axisZThread = axisZThread(posZ);
        axisXThread.join();
        axisZThread.join();

    }

    public void manualPositionAxis() { //inputs verificados!!!
    do {
        System.out.println("Which Axis do you want to move?: (1-X, 2-Y, 3-Z)");
        op1 = scan.nextInt();
        switch (op1) {
            case 1:
                do {
                System.out.println("Which Position of Axis X do you want to move?: (1, 2, 3)");
                op2 = scan.nextInt();
                if ((op2 < 0) || (op2>3))
                    System.out.println("Invalid position!");
                } while ((op2 < 0) || (op2>3));
                info.guardaAcao(0);
                info.guardaX(op2);
                axisX.gotoPos(op2);
                break;
            case 2:
                do {
                System.out.println("Which Position of Axis Y do you want to move?: (1, 2, 3)");
                op2 = scan.nextInt();
                if ((op2 < 0) || (op2>3))
                    System.out.println("Invalid Position!");
                } while ((op2 < 0) || (op2>3));
                info.guardaAcao(0);
                info.guardaY(op2);
                axisY.gotoPos(op2);
                break;
            case 3:
                do {
                System.out.println("Which Position of Axis Z do you want to move?: (1-1D, 10-1U , 2-2D, 20-2U, 3-3D, 30-3U)");
                op2 = scan.nextInt();
                if ((op2 < 0) || (op2>3))
                    System.out.println("Invalid Position!");
                } while ((op2 < 0) || (op2>3));
                info.guardaAcao(0);
                info.guardaZ(op2);
                axisZ.gotoPos(op2);
                break;
            default: System.out.println("Invalid axis!");
                break;
        }
        } while ((op1 < 0) || (op1>3));

    }

    public void addNewPallete() throws InterruptedException { //scans validos!
        int posXposto;
        int posZposto;
        float humidity;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateInput;
        boolean isValid;
        int shipping_day;
        int shipping_month;
        int shipping_year;
        String day;
        String month;
        axisXThread = axisXThread(1);
        axisZThread = axisZThread(1);
        axisXThread.join();
        axisZThread.join();
        info.guardaY(1);
        axisY.gotoPos(1);
        Boolean alertBoolean = false;

        System.out.println("Manual pallete storing: What is the pallete's metadata? Don't forget to put it on the cage!");
        System.out.println("Product type: ");
        String product_type = myObj.nextLine();
        do {
        System.out.println("Humidity: ");
        humidity = scan.nextFloat();
        if ((humidity < 0) || (humidity >100))
            System.out.println("Invalid humidity Value!");
        } while((humidity < 0) || (humidity >100));
        System.out.println("Producer ID: ");
        int producerID = scan.nextInt();
        do {
        System.out.println("Desired X: "); 
        posXposto = scan.nextInt();
        System.out.println("Desired Z: "); 
        posZposto = scan.nextInt();
        if ( (((posXposto <= 0) || (posXposto > 3)) && ((posZposto!= 0))) || (((posXposto != 0)) && ((posZposto <= 0) || (posZposto > 3))) )
            System.out.println("Invalid coordinates!");
        } while ( (((posXposto <= 0) || (posXposto > 3)) && ((posZposto!= 0))) || (((posXposto != 0)) && ((posZposto <= 0) || (posZposto > 3))) );
        do {
        System.out.println("Shipping day (put 0 on the back in case it is lower than 10): ");
        shipping_day = scan.nextInt();
        System.out.println("Shipping month(put 0 on the back in case it is lower than 10): ");
        shipping_month = scan.nextInt();
        System.out.println("Shipping year: ");
        shipping_year = scan.nextInt();
        if (shipping_day < 10) 
            day = "0" + shipping_day;
        else 
            day = "" + shipping_day;
        if (shipping_month < 10) 
            month = "0" + shipping_month;
        else 
            month = "" + shipping_month;
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
                axisXThread = axisXThread(posXposto);
                axisZThread = axisZThread(posZposto * 10);
                axisXThread.join();
                axisZThread.join();
                mechanism.putPartInCell();
                if (humidity > Max_humidity) {
                    isLed1Interrupted = false;
                    Led1On = led1On();
                    alertBoolean = true;
                }
                if (shipping_year > Max_year) {
                    isLed1Interrupted = false;
                    Led1On = led1On();
                    alertBoolean = true;
                } else if ((shipping_year == Max_year) && (shipping_month > Max_month)) {
                    isLed1Interrupted = false;
                    Led1On = led1On();
                    alertBoolean = true;
                } else if ((shipping_year == Max_year) && (shipping_month == Max_month)
                        && (shipping_day > Max_day)) {
                    isLed1Interrupted = false;
                    Led1On = led1On();
                    alertBoolean = true;
                }
                if (alertBoolean == true) {
                    p.change_alert(true);
                    System.out.println("Pallete with Alert!");
                }
                storage[posXposto - 1][posZposto - 1] = p;
            } else {
                System.out.println("You didn't put any pallete!!!");
            }
        } else {
            System.out.println("There is a pallete there! Info:"
                    + "\nProductType: " + storage[posXposto - 1][posZposto - 1].product_type()
                    + "\nHumidity: " + String.format("%.1f",storage[posXposto - 1][posZposto - 1].humidity()) + " %"
                    + "\nproducer_ID: " + storage[posXposto - 1][posZposto - 1].producer_ID()
                    + "\ndesiredX: " + storage[posXposto - 1][posZposto - 1].desiredX()
                    + "\ndesiredZ: " + storage[posXposto - 1][posZposto - 1].desiredZ()
                    + "\nShippingDate: " + storage[posXposto - 1][posZposto - 1].shipping_day() + "/"
                    + storage[posXposto - 1][posZposto - 1].shipping_month() + "/"
                    + storage[posXposto - 1][posZposto - 1].shipping_year()
                    + "\nDestination: " + storage[posXposto - 1][posZposto - 1].destination());
        }

    }

    public void removePallete() throws InterruptedException { //scans validados!
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
            if ((posXremovido <= 0 ) || (posXremovido > 3) || (posZremovido <= 0) || (posZremovido > 3))  
                System.out.println("Invalid coordinates!");
            } while ((posXremovido <= 0 ) || (posXremovido > 3) || (posZremovido <= 0) || (posZremovido > 3));
            Pallet p1 = storage[posXremovido - 1][posZremovido - 1];

            if (p1 != null) {
                axisXThread = axisXThread(posXremovido);
                axisZThread = axisZThread(posZremovido);
                axisXThread.join();
                axisZThread.join();
                info.guardaAcao(1);
                info.guardaPalete(false);
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
                info.guardaY(1);
                axisY.gotoPos(1);
                isLed2Interrupted = true;
                System.out.println("Pallet with info"
                        + "\nProductType: " + storage[posXremovido - 1][posZremovido - 1].product_type()
                        + "\nHumidity: " + String.format("%.1f",storage[posXremovido - 1][posZremovido - 1].humidity()) + " %"
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
            } else {
                System.out.println(
                        "There is no pallete at the coordinates (" + posXremovido + "," + posZremovido + ")!\n");
            }
        } else { // if de verificar se ha palete
            System.out.println("you already have a pallete inside the cage. Get rid of it!!!\n");
        }
    }

    public void maxHumidity() { //scans validados!
        do {
        System.out.println("What is the maximum humidity that this storage can take?");
        Max_humidity = scan.nextFloat();
        if ((Max_humidity< 0) || (Max_humidity>100)) {
            System.out.println("Invalid humidity value!");
        }
        } while ((Max_humidity< 0) || (Max_humidity>100));
        System.out.println("Verifying if any of the pallets surpasses a threshold...");
        alertON = verifyAlerts(storage, Max_humidity, Max_day, Max_month, Max_year);
        if (alertON) {
            isLed1Interrupted = false;
            Led1On = led1On();
        } else {
            isLed1Interrupted = true;
        }
    }

    public void maxDate() { //scans validados!
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
        if (Max_day < 10) 
            day = "0" + Max_day;
        else 
            day = "" + Max_day;
        if (Max_month < 10) 
            month = "0" + Max_month;
        else 
            month = "" + Max_month;
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

    public void listPalletes() {//nao ha scans!
        boolean isSEmpty = true;
        System.out.println("List of All pallets:");
        for (int i = 2; i >= 0; i--) {
            for (int j = 0; j < 3; j++) {
                if (storage[j][i] != null) {
                    isSEmpty = false;
                    System.out.println("\nPallet at (" + storage[j][i].desiredX + ","+ storage[j][i].desiredZ + ") With info:"
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
}
