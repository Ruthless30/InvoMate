package DAO;

import MODEL.FactureItem;
import MODEL.Produit;

import java.util.ArrayList;

public interface IFactureItem {
    boolean addFactureItem(int id_facture,int id_produit,String nomProduit,double prix ,int quantity ,double total);
    boolean deleteFactureItem(int idFacture,int idItem);
    boolean updateFactureItem(int idFacture, String nomProduit, int quantity, double price);
    FactureItem getFactureItem(int idFacture,int idItem);
    ArrayList<FactureItem> getAllFactureItem(int idFacture);
    boolean updateInvoiceOrderAndNotes(int invoiceId, int newOrderIndex, String notes);

}
