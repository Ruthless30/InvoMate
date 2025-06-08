package MODEL;

public class Client {
    private int idClient;
    private String name;
    private String deliveryTime;
    private double credit=0.0;

    public Client() {
        super();
    }

    public Client(String name, String deliveryTime) {
        this.name = name;
        this.deliveryTime = deliveryTime;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }
}
