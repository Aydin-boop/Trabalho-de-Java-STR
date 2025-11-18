
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Storage.initializeHardwarePorts();

        int op = -1;
        Scanner scan = new Scanner(System.in);

        Menu menu = new Menu();
        Pallet[][] Storage = menu.Storage();
        float Max_humidity = menu.Max_humidity();
        int Max_day = menu.Max_day();
        int Max_month = menu.Max_month();
        int Max_year = menu.Max_year(); // sim, isto e a data limite de entrega do trabalho

        Menu.Calibration();
        Menu.switchesThread(Storage); // Isto precisa de estar constantemente a correr para que o programa ande sempre
                                      // a verificar quando é que há switches
        Menu.EmergencyThread(); // verifica se o user pressiona os switches de emergência
        Menu.VerificaAlertasThread(Storage); // isto serve para verificar o switch 1 quando estamos no modo de emrgencia
                                             // e assegura-se que o "RemoveAlerts" corre desde o inicio ao fim

        Menu.lastAxisPositionThread(); // isto ve o ultima posicao onde o cage passa pelo uma sensor e guarda os
                                       // proprios valores
                                       // dos getPos axis no info.guardaLastXYZ
        while (op != 0) {
            Storage = menu.Storage();
            Max_humidity = menu.Max_humidity();
            Max_day = menu.Max_day();
            Max_month = menu.Max_month();
            Max_year = menu.Max_year();// atualiza os valores da storage e dos maximos permitidos pelo user

            menu.ShowMenu(Max_humidity, Max_day, Max_month, Max_year);
            System.out.println("Enter an option:");
            // if (menu.is_switch() == false) {
            op = scan.nextInt();
            // } else {
            // op = 100000;
            // menu.changeSwitch(); //vai meter o switch a false
            // }
            switch (op) {
                case 1:
                    menu.manualPosition();
                    break;
                case 2:
                    menu.manualPositionAxis();
                    break;
                case 3:
                    Menu.Calibration();
                    break;
                case 4:
                    menu.addNewPallete();
                    break;
                case 5:
                    menu.removePallete();
                    break;
                case 6:
                    menu.maxHumidity();
                    break;
                case 7:
                    menu.maxDate();
                    break;
                case 8:
                    menu.listPalletes();
                    break;
                case 9:
                    menu.searchPalleteType();
                    break;
                case 10:
                    menu.removeSearchedPallete();
                    break;
                case 100000:
                    break;
                default:
                    System.out.println("Nao implementado!");
                    break;
            }
        }
        scan.close();
    }
}