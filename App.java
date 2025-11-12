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

    public static int VerMaisProximo(char pos, Pallet[][] s, int Xprox) {
        int menor = 2;
        int maisProximo = 0;
        for (int i = 0; i<3; i++) {
            for (int j = 0; j<3; j++) {
                if (pos == 'x') {
                    AxisX axisX = new AxisX();
                    if ((Math.abs(axisX.getPos()-1 - i) < menor) && (s[i][j] == null)) {
                        menor = axisX.getPos()-1 - i;
                        maisProximo = axisX.getPos() - menor; 
                    }
                } else {
                    AxisZ axisZ = new AxisZ();
                    if ((Math.abs(axisZ.getPos()-1 - j) < menor) && s[Xprox- 1][j] == null) { //isto nunca da erro porque ele so entra no caso dos zzs depois de ter entrado no dos xxs
                        menor = axisZ.getPos()-1 - j;
                        maisProximo = axisZ.getPos() - menor; 
                    } 
                }
            }
        }
        return maisProximo;
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
                //if (existe palete na cage) {
                    Scanner myObj = new Scanner(System.in);    
                    System.out.println("\n Manual pallet storing: What is the pallet's metadata? Introduce product type, humidity level, producerID, Xdestination, Zdestination, shipping date\n");
                    System.out.println("Product type: ");    String product_type = myObj.nextLine();
                    System.out.println("Humidity: ");   int humidity = scan.nextInt();
                    System.out.println("Producer ID: ");    int producerID = scan.nextInt();
                    System.out.println("Desired X: ");    int posXposto = scan.nextInt();
                    System.out.println("Desired Z: ");    int posZposto = scan.nextInt();
                    System.out.println("Shipping day: ");    int shipping_day = scan.nextInt();
                    System.out.println("Shipping month: ");    int shipping_month = scan.nextInt();
                    System.out.println("Shipping year: ");    int shipping_year = scan.nextInt();
                    if ((posXposto == 0) && (posZposto == 0)) {
                        posXposto = VerMaisProximo('x', Storage, -1);
                        posZposto = VerMaisProximo('z', Storage, posXposto);
                    }
                    Pallet p = new Pallet(product_type, humidity, producerID, posXposto, posZposto, shipping_day, shipping_month, shipping_year);

                    if (Storage[posXposto - 1][posZposto - 1] == null) {
                    axisXThread = axisXThread(posXposto);
                    axisZThread = axisZThread(posZposto*10);
                    axisXThread.join();
                    axisZThread.join();
                    mechanism.putPartInCell();
                    Storage[posXposto - 1][posZposto - 1] = p;
                    } else {
                        System.out.println("There is a pallet there! Info:" +
                        "\nProductType: " + Storage[posXposto - 1][posZposto - 1].product_type() +
                        "\nHumidity: " + Storage[posXposto - 1][posZposto - 1].humidity() + 
                        "\nproducer_ID: " + Storage[posXposto - 1][posZposto - 1].producer_ID() + 
                        "\ndesiredX: " + Storage[posXposto - 1][posZposto - 1].desiredX() + 
                        "\ndesiredZ: " + Storage[posXposto - 1][posZposto - 1].desiredZ() + 
                        "\nShippingDate: " + Storage[posXposto - 1][posZposto - 1].shipping_day() +"/"+Storage[posXposto - 1][posZposto - 1].shipping_month()+"/"+Storage[posXposto - 1][posZposto - 1].shipping_year());
                    }
                    // else { println ("Nao meteste palete na cage!!!"); }
                    break;
                case 8:
                    //if(! existe palete na cage) {
                    System.out.println("Manual pallet removal: What are the coordinates of the pallet you want to remove?");
                    System.out.println("Desired X: "); int posXremovido = scan.nextInt();
                    System.out.println("Desired Z: "); int posZremovido = scan.nextInt();
                    Pallet p1 = Storage[posXremovido - 1][posZremovido - 1];

                    if (p1 != null) {
                        axisXThread = axisXThread(posXremovido);
                        axisZThread = axisZThread(posZremovido);
                        axisXThread.join();
                        axisZThread.join();
                        mechanism.takePartInCell();
                        System.out.println("Pallet with info" +
                            "\nProductType: " + Storage[posXremovido - 1][posZremovido - 1].product_type() +
                            "\nHumidity: " + Storage[posXremovido - 1][posZremovido - 1].humidity() + 
                            "\nproducer_ID: " + Storage[posXremovido - 1][posZremovido - 1].producer_ID() + 
                            "\ndesiredX: " + Storage[posXremovido - 1][posZremovido - 1].desiredX() + 
                            "\ndesiredZ: " + Storage[posXremovido - 1][posZremovido - 1].desiredZ() + 
                            "\nShippingDate: " + Storage[posXremovido - 1][posZremovido - 1].shipping_day() +"/"+Storage[posXremovido - 1][posZremovido - 1].shipping_month()+"/"+Storage[posXremovido - 1][posZremovido - 1].shipping_year() + 
                            "\nremoved!"); 
                        Storage[posXremovido - 1][posZremovido - 1] = null;
                    }  else {
                        System.out.println("There is no pallet at the coordinates ("+ posXremovido + "," + posZremovido + ")!\n");
                    }
                    // } else{ println(ja tens uma palete na cage!!! livra-te dela!!!); } 
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