package MODEL;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class TableFactureItem extends AbstractTableModel {
    private final String[] columns = {"Nom_Produit", "Prix" , "Qte","Total"};
    private ArrayList<FactureItem> ListeItems;

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getRowCount() {
        return ListeItems.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return ListeItems.get(rowIndex).getNomProduit();
            case 1: return ListeItems.get(rowIndex).getPrice();
            case 2: return ListeItems.get(rowIndex).getQuantity();
            case 3: return ListeItems.get(rowIndex).getTotal();
            default: return null;
        }
    }

    public void chargIt(ArrayList<FactureItem> FactureItem) {
        this.ListeItems = FactureItem;
        fireTableDataChanged();
    }
}
