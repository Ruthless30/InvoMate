package MODEL;

public class FactureItem {
    private String nom_produit;
    private int quantity;
    private double price;
    private double Total=0.0;

    public FactureItem() {
        super();
    }

    public FactureItem(String produit, double price,int quantity) {
        this.nom_produit = produit;
        this.quantity = quantity;
        this.price = price;
        this.Total = price*quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNomProduit() {
        return nom_produit;
    }

    public void setNomProduit(String produit) {
        this.nom_produit = produit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double Total) {
        this.Total = Total;
    }

}
