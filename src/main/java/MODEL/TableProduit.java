package MODEL;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class TableProduit extends AbstractTableModel {
    private final String[] columns = {"ID", "Nom_Produit" , "Prix"};
    private ArrayList<Produit> produits;

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getRowCount() {
        return produits.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return produits.get(rowIndex).getIdProduit();
            case 1: return produits.get(rowIndex).getNomProduit();
            case 2: return produits.get(rowIndex).getPrixProduit();
            default: return null;
        }
    }

    public void chargIt(ArrayList<Produit> produits) {
        this.produits = produits;
        fireTableDataChanged();
    }
}
