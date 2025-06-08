package MODEL;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class TableFacture extends AbstractTableModel {
    private final String[] columns = {"ID_Facture", "Nom_Client" , "Ordre" , "Notes"};
    private ArrayList<Facture> factures ;

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getRowCount() {
        return factures.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return factures.get(rowIndex).getIdFacture();
            case 1: return factures.get(rowIndex).getNom_client();
            case 2: return factures.get(rowIndex).getOrdre_index();
            case 3: return factures.get(rowIndex).getNotes();
            default: return null;
        }
    }

    public void chargIt(ArrayList<Facture> factures) {
        this.factures = factures;
        fireTableDataChanged();
    }
}
