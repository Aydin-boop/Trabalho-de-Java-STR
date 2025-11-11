public class ServerThread extends Thread{
    private boolean interrupted;

    public boolean isInterrupted(){
        return interrupted;
    }

    public void setInterrupted(boolean isInterrupted){
        this.interrupted = interrupted;
    }

    public void run(){
        this.setInterrupted(false);
        while(!interrupted){
            //do something
            System.out.println("doing some task here until interrupted");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public static void main (String[] args) throws InterruptedException{
        ServerThread worker = new ServerThread();
        worker.start();
        Thread.sleep(3000);
        worker.setInterrupted(true);
        System.out.println("Thread was interrupted");
    }
}
