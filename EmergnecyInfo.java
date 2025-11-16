public class EmergnecyInfo {
    int posX;
    int posZ;
    int posY;
    int acao; //0 se for só para mover a cage, 1 para tirar palete, 2 para colocar palete, e 3 se estiver a remover alertas. Por enquanto, isto só serviu para verificar se estou ou não na função "RemoveAlerts" do "Menu.java"
    boolean temPalete; //isto é inutil por enquanto, mas serviria para verificar se estou com uma palete na cage ou nao
    
    public void Info(int posX, int posZ, int posY, int acao, boolean temPlaete) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
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

    public void guardaX (int X) {
        this.posX = X;
    }

    public void guardaZ (int Z) {
        this.posZ = Z;
    }

    public void guardaY (int Y) {
        this.posY = Y;
    }

    public void guardaAcao (int acao) {
        this.acao = acao;
    }

    public void guardaPalete (boolean temPalete) {
        this.temPalete = temPalete;
    }
}
