public class Pallet {
    String product_type;
    int humidity;
    int producer_ID;
    int desiredX = -1;
    int desiredZ = -1;
    int shipping_day;
    int shipping_month;
    int shipping_year;

    public Pallet(String product_type, int humidity, int producer_ID, int desiredX, int desiredZ, int shipping_day, int shipping_month, int shipping_year) {
        this.product_type = product_type;
        this.humidity = humidity;
        this.producer_ID = producer_ID;
        this.desiredX = desiredX;
        this.desiredZ = desiredZ;
        this.shipping_day = shipping_day;
        this.shipping_month = shipping_month;
        this.shipping_year = shipping_year;
    }

    public String product_type() {
        return product_type;
    }

    public int humidity() {
        return humidity;
    }

    public int producer_ID() {
        return producer_ID;
    }

    public int desiredX() {
        return desiredX;
    }

    public int desiredZ(){
        return desiredZ;
    }

    public int shipping_day() {
        return shipping_day;
    }

    public int shipping_month(){
        return shipping_month;
    }

    public int shipping_year() {
        return shipping_year;
    }
}
