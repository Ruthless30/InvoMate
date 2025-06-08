package MODEL;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class TableClient extends AbstractTableModel {
    private final String[] columns = {"ID", "Nom" , "Temps_Livraison" , "Credit"};
    private ArrayList<Client> clients ;

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getRowCount() {
        return clients.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return clients.get(rowIndex).getIdClient();
            case 1: return clients.get(rowIndex).getName();
            case 2: return clients.get(rowIndex).getDeliveryTime();
            case 3: return clients.get(rowIndex).getCredit();
            default: return null;
        }
    }

    public void chargIt(ArrayList<Client> clients) {
        this.clients = clients;
        fireTableDataChanged();
    }
}
