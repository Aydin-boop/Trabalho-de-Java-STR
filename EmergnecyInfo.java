public class EmergnecyInfo {
    int posX;
    int posZ;
    int posY;
    int posLastX;
    int posLastZ;
    int posLastY;
    int acao; // 0 se for só para mover a cage, 1 para tirar palete, 2 para colocar palete, e
              // 3 se estiver a remover alertas. Por enquanto, isto só serviu para verificar
              // se estou ou não na função "RemoveAlerts" do "Menu.java"
    boolean temPalete; // isto é inutil por enquanto, mas serviria para verificar se estou com uma
                       // palete na cage ou nao

    static AxisX axisX = new AxisX();
    static AxisY axisY = new AxisY();
    static AxisZ axisZ = new AxisZ();

    // meti o posLastXYZ como parametro aqui mas nao percebo pq lol
    public void Info(int posX, int posZ, int posY, int acao, boolean temPlaete, int posLastX, int posLastY,
            int posLastZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.posLastX = posLastX;
        this.posLastY = posLastY;
        this.posLastZ = posLastZ;
        this.acao = acao;
        this.temPalete = temPlaete;
    }

    public int posX() {
        return this.posX;
    }

    public int posZ() {
        return this.posZ;
    }

    public int posY() {
        return this.posY;
    }

    public int acao() {
        return this.acao;
    }

    public boolean temPalete() {
        return this.temPalete;
    }

    public void guardaX(int X) {
        this.posX = X;
    }

    public void guardaZ(int Z) {
        this.posZ = Z;
    }

    public void guardaY(int Y) {
        this.posY = Y;
    }

    public void guardaAcao(int acao) {
        this.acao = acao;
    }

    public void guardaPalete(boolean temPalete) {
        this.temPalete = temPalete;
    }

    //
    public void guardaLastX(int lastX) {
        this.posLastX = lastX;
    }

    public void guardaLastZ(int lastZ) {
        this.posLastZ = lastZ;
    }

    public void guardaLastY(int lastY) {
        this.posLastY = lastY;
    }

    public int posLastX() {
        return this.posLastX;
    }

    public int posLastZ() {
        return this.posLastZ;
    }

    public int posLastY() {
        return this.posLastY;
    }

    public void axisGoingTo() {
        if (posX() < posLastX())
            axisX.moveBackward();
        else if (posX() > posLastX())
            axisX.moveForward();

        // caso temos o valor de z maior do que 10
        if (posZ > 8)
            posZ = posZ / 10;
        if (posLastZ > 8)
            posLastZ = posLastZ / 10;

        if (posZ() < posLastZ())
            axisZ.moveBackward();
        else if (posZ() > posLastZ())
            axisZ.moveForward();

        if (posY() < posLastY())
            axisY.moveBackward();
        else if (posY() > posLastY())
            axisY.moveForward();
    }
    // queremos saber a posicao onde vai. assim vais ver com posX e comparar
}
