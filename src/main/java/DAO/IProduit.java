package DAO;

import MODEL.Produit;

import java.util.ArrayList;

public interface IProduit {
    boolean addProduit(String nomProduit, double PrixProduit);
    boolean deleteProduit(int id);
    boolean updateProduit(int id,String nomProduit, double PrixProduit);
    ArrayList<Produit> getAllProduit();
    int getProduitId(String nomProduit);
}
