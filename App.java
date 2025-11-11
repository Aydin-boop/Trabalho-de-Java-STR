import java.util.Scanner;

public class App {

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

    public static void main(String[] args) throws Exception {
        System.out.println("Labwork 2 from Java!");

        Storage.initializeHardwarePorts();

        AxisX axisX = new AxisX();
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();

        Mechanism mechanism = new Mechanism();

        Pallet[][] Storage = new Pallet[3][3]; 
       
        int op = -1;
        int op1 = -1;
        int op2 = -1;

        Scanner scan = new Scanner(System.in);
        
        Thread axisXThread;
        Thread axisZThread;

        while (op != 0) {
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
                    break;
                case 7:
                Scanner myObj = new Scanner(System.in);    
                System.out.println("\nWhat is the pallet's metadata? Introduce product type, humidity level, producerID, Xdestination, Zdestination, shipping date\n");
                    String product_type = myObj.nextLine();
                    int humidity = scan.nextInt();
                    int producerID = scan.nextInt();
                    int posXposto = scan.nextInt();
                    int posZposto = scan.nextInt();
                    int shipping_day = scan.nextInt();
                    int shipping_month = scan.nextInt();
                    int shipping_year = scan.nextInt();
                    Pallet p = new Pallet(product_type, humidity, producerID, posXposto, posZposto, shipping_day, shipping_month, shipping_year);
                    //System.out.printf("%b%n", (p.isTherePallet(posXposto, posZposto)));
                    if (Storage[posXposto - 1][posZposto - 1] == null) {
                    axisXThread = axisXThread(posXposto);
                    axisZThread = axisZThread(posZposto*10);
                    axisXThread.join();
                    axisZThread.join();
                    mechanism.putPartInCell();
                    Storage[posXposto - 1][posZposto - 1] = p;
                    } else {
                        System.out.println("There is a pallet there!!!!! Info....." + Storage[posXposto - 1][posZposto - 1].product_type() + Storage[posXposto - 1][posZposto - 1].humidity() + Storage[posXposto - 1][posZposto - 1].producer_ID() + Storage[posXposto - 1][posZposto - 1].desiredX() + Storage[posXposto - 1][posZposto - 1].desiredZ() + Storage[posXposto - 1][posZposto - 1].shipping_day() +"/"+Storage[posXposto - 1][posZposto - 1].shipping_month()+"/"+Storage[posXposto - 1][posZposto - 1].shipping_year());
                    }
                    break;
                case 8:
                    mechanism.takePartInCell();
                    break;
            }
        }
        scan.close();
    }
}

/*
 * 
 * ter duas matrizes 3 por 3
 * para meter    para tirar
 * 30           3
 * 20           2
 * 10           1
 *    1 2 3        1 2 3
 * 
 * 
 */