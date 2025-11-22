import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    static Thread menuT;
    static Menu menu = new Menu();
    static Pallet[][] storage = menu.Storage();

    public static Thread menuThread() {
        
        menuT = new Thread() {
        public void run() {
        int op = -1;
        Scanner scan = new Scanner(System.in);
        //Mechanism mechanism = new Mechanism();
        float Max_humidity = menu.Max_humidity();
        int Max_day = menu.Max_day();
        int Max_month = menu.Max_month();
        int Max_year = menu.Max_year();
        while (op != 0) {
            
            do{
                try {Thread.sleep(10);} catch (InterruptedException e){} 
                //isto e so para dar tempo para as outras threads dos switches fazerem o que teem a fazer. Nao sei pq mas meter um sleep fez isto correr melhor geralmente
            } while(Menu.isAnySwitch == true);

            storage = menu.Storage();
            Max_humidity = menu.Max_humidity();
            Max_day = menu.Max_day();
            Max_month = menu.Max_month();
            Max_year = menu.Max_year(); // atualiza os valores da storage e dos maximos permitidos pelo user
            menu.ShowMenu(Max_humidity, Max_day, Max_month, Max_year); //e coloca-os no menu

            try {
                op = scan.nextInt();
            } catch (InputMismatchException exep) {
                System.out.println("Invalid data! click on a number from the menu!");
                scan.nextLine(); //isto so serve para limpar o buffer
            }

            if(Menu.isSwitch1 == true) {
            op = 10000; //o user ficou preso de certeza absoluta no scan.nextInt(), dai termos pedido input na thread que verifica o switch1 fora da emergencia.
            Menu.isSwitch1 = false;  //esse input deve ser ignorado para dar refresh ao menu.
            }

            if (Menu.emergencyON) {
            op = -10000;    //se a emergencia esta ligada, entao ignoramos tudo o que o user meter no terminal. O suposto eh ele so poder mexer nos switches
            }

            switch (op) {
                case 1:
                    try {menu.manualPosition();} catch (InterruptedException ex) {}
                    //System.out.println("fjkdfdf");
                    break;

                case 2:
                    menu.manualPositionAxis();
                    break;
                case 3:
                    try {Menu.Calibration();} catch (InterruptedException ex) {}
                    break;

                case 4:
                    try {menu.addNewPallete();} catch (InterruptedException ex) {}
                    break;

                case 5:
                    try {menu.removePallete();} catch (InterruptedException ex) {}
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
                {
                    try {
                        menu.removeSearchedPallete();
                    } catch (InterruptedException ex) {
                    }
                }
                    break;
                case -10000: //nenhum destes casos faz alguma coisa, e so para forcar o programa a nao fazer nada caso esteja na emergencia 
                case 10000://ou caso esteja a sair de tirar alertas, porque o user pode ficar preso no scan do menu, e clicar num numero para dar refresh foi a melhor solucao que encontrei
                    break;
                default:
                    System.out.println("There is no such option in the menu!!!-");
                    break;
            }
        }
        scan.close();
            }
        };
        menuT.start();
        return menuT;
    }

    

    public static void main(String[] args) throws Exception { 
        Storage.initializeHardwarePorts();
        Menu.Calibration();
        Menu.switchesThread(storage); // Isto precisa de estar constantemente a correr para que o programa ande sempre
                                      // a verificar quando é que há switches
        Menu.EmergencyThread(); // verifica se o user pressiona os switches de emergência
        Menu.VerificaAlertasThread(storage); // isto serve para verificar o switch 1 quando estamos no modo de emrgencia
                                             // e assegura-se que o "RemoveAlerts" corre desde o inicio ao fim
        Menu.lastAxisPositionThread(); // isto ve o ultima posicao onde o cage passa pelo uma sensor e guarda os
                                       // proprios valores dos getPos axis no info.guardaLastXYZ
        menuT = menuThread();

    }
}