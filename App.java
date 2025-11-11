import java.util.Scanner;

public class App {

    public static Thread axisXThread(int pos) {
        AxisX axisX = new AxisX();
        Thread axisXThread = new Thread(){ 
            public void run(){
                axisX.gotoPos(pos);
            }
        }; 
        axisXThread.start();
        return axisXThread; 
        
    }

    public static Thread axisZThread(int pos) {
        AxisZ axisZ = new AxisZ();
        Thread axisZThread = new Thread(){ 
            public void run(){
                axisZ.gotoPos(pos);
            }
        }; 
        axisZThread.start();
        return axisZThread; 
        
    }

    public static Thread axisYThread(int pos) {
        AxisY axisY = new AxisY();
        Thread axisYThread = new Thread(){ 
            public void run(){
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
        int op = -1;
        int op1 = -1;
        int op2 = -1;
       
       
       
        Scanner scan = new Scanner(System.in);
        while(op != 0) {
            System.out.println("Enter an option:");
            op = scan.nextInt();
            switch(op) {
                case 1: axisX.moveForward(); break;
                case 2: axisX.moveBackward(); break;
                case 3: axisX.stop(); break;
                case 4: System.out.println("para que coordenadas quer ir? Coloque os inputs no formato X Z");
                        int posX = scan.nextInt();
                        int posZ = scan.nextInt();
                        Thread axisXThread = axisXThread(posX);
                        Thread axisZThread = axisZThread(posZ);
                        axisXThread.join();
                        axisZThread.join();
                        break;
                case 5: 
                    System.out.println("Which Axis do you want to move?: (1-X, 2-Y, 3-Z)");
                    op1 = scan.nextInt();
                    switch(op1){
                        case 1:
                            System.out.println("Which Position of Axis X do you want to move?: (1, 2, 3)");
                            op2 = scan.nextInt();
                            switch(op2){
                            case 1: axisX.gotoPos(1); break;
                            case 2: axisX.gotoPos(2); break;
                            case 3: axisX.gotoPos(3); break;
                        } break;
                        case 2:
                            System.out.println("Which Position of Axis Y do you want to move?: (1, 2, 3)");
                            op2 = scan.nextInt();
                            switch(op2){
                            case 1: axisY.gotoPos(1); break;
                            case 2: axisY.gotoPos(2); break;
                            case 3: axisY.gotoPos(3); break;
                        } break;
                        case 3:
                            System.out.println("Which Position of Axis Z do you want to move?: (1-1D, 10-1U , 2-2D, 20-2U, 3-3D, 30-3U)");
                            op2 = scan.nextInt();
                            switch(op2){
                            case 1: axisZ.gotoPos(1); break;
                            case 10: axisZ.gotoPos(10); break;
                            case 2: axisZ.gotoPos(2); break;
                            case 20: axisZ.gotoPos(20); break;
                            case 3: axisZ.gotoPos(3); break;
                            case 30: axisZ.gotoPos(30); break;
                        } break;
                    } break;
                case 6: System.out.println("System initializing calibration");
                        axisX.moveBackward();      
                        do {
                        axisY.moveBackward();      
                        } while (axisY.getPos()==-1);
                        axisZ.moveBackward();    
                Thread axisXThreadCalibration = axisXThread(1);
                        Thread axisZThreadCalibration = axisZThread(1);
                        Thread axisYThreadCalibration = axisYThread(2);
                        axisXThreadCalibration.join();
                        axisZThreadCalibration.join();
                        axisYThreadCalibration.join();
            }
        }
        scan.close();
    }
}
