package DAO;

import MODEL.Facture;

import java.util.ArrayList;

public interface IFacture {
    boolean addFacture(String nom_client, String notes);
    boolean deleteFacture(int id);
    boolean updateFacture(int id ,String nom_client, String notes,int ordre_index);
    boolean updateFacturePrint(int id,String printEtat);
    boolean updateFactureOrder(int id,int ordre_index);
    Facture getFacture(int id);
    ArrayList<Facture> getAllFacture(String imprimer_value);
    Double getTotalFacture(int idFacture);
}
