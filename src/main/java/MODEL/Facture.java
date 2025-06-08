package MODEL;

public class Facture {
    private int idFacture;
    private int ordre_index;
    private String nom_client;
    private String notes="";

    public Facture() {
        super();
    }

    public Facture(String nom_client, String notes) {
        this.nom_client = nom_client;
        this.notes = notes;
    }

    public int getOrdre_index() {
        return ordre_index;
    }

    public void setOrdre_index(int ordre_index) {
        this.ordre_index = ordre_index;
    }

    public String getNom_client() {
        return nom_client;
    }

    public void setNom_client(String nom_client) {
        this.nom_client = nom_client;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }
}
