package DAO;

import MODEL.FactureItem;
import MODEL.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FactureItemDAO  implements IFactureItem{
    private final static Connection con= DBConnection.getInstance();
    @Override
    public boolean addFactureItem(int id_facture,int id_produit,String nomProduit,double prix ,int quantity ,double total) {
        String sql = "INSERT INTO invoice_items(invoice_id,product_id,product_name,quantity,price,total) VALUES(?,?,?,?,?,?)";
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, id_facture);
            ps.setInt(2, id_produit);
            ps.setString(3,nomProduit);
            ps.setInt(4, quantity);
            ps.setDouble(5, prix);
            ps.setDouble(6, total);
            ps.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteFactureItem(int idFacture, int idItem) {
        String sql = "DELETE FROM invoice_items WHERE invoice_id = ? AND product_id = ?";
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, idFacture);
            ps.setInt(2, idItem);
            ps.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateFactureItem(int idFacture, String nomProduit, int quantity, double price ) {
        String sql = "UPDATE invoice_items set quantity=? , price=? ,total=? WHERE invoice_id = ? AND product_name = ?";
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, quantity);
            ps.setDouble(2, price);
            ps.setDouble(3, price*quantity);
            ps.setInt(4, idFacture);
            ps.setString(5, nomProduit);
            ps.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public FactureItem getFactureItem(int idFacture, int idItem) {
        String sql = "SELECT * FROM invoice_items WHERE invoice_id = ? AND product_id = ?";
        FactureItem factureItem = new FactureItem();
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, idFacture);
            ps.setInt(2, idItem);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Produit produit = ProduitDAO.getProduit(idItem);
                factureItem.setNomProduit(produit.getNomProduit());
                factureItem.setQuantity(rs.getInt("quantity"));
                factureItem.setPrice(rs.getDouble("prixProduit"));
                factureItem.setTotal(rs.getDouble("total"));
            }
            return factureItem;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return factureItem;
    }

    @Override
    public ArrayList<FactureItem> getAllFactureItem(int idFacture) {
        String sql = "SELECT * FROM invoice_items WHERE invoice_id = ?";
        ArrayList<FactureItem> factureItemlist = new ArrayList<>();
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, idFacture);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FactureItem factureItem = new FactureItem();
                factureItem.setNomProduit(rs.getString("product_name"));
                factureItem.setPrice(rs.getDouble("price"));
                factureItem.setQuantity(rs.getInt("quantity"));
                factureItem.setTotal(rs.getDouble("total"));

                factureItemlist.add(factureItem);
            }
            return factureItemlist;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return factureItemlist;
    }
    @Override
    public boolean updateInvoiceOrderAndNotes(int invoiceId, int newOrderIndex, String notes) {
        PreparedStatement ps;
        try {
            // Get current order index of the invoice being modified
            String getCurrentOrderQuery = "SELECT ordre_index FROM invoices WHERE id = ?";
            ps = con.prepareStatement(getCurrentOrderQuery);
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            int currentOrderIndex = 0;
            if (rs.next()) {
                currentOrderIndex = rs.getInt("ordre_index");
            }
            rs.close();
            ps.close();

            // If order index is changing, we need to reorder other invoices
            if (currentOrderIndex != newOrderIndex) {
                // Case 1: Moving to a higher position
                if (newOrderIndex > currentOrderIndex) {
                    // Shift down all invoices between current+1 and new position
                    String shiftDownQuery = "UPDATE invoices SET ordre_index = ordre_index - 1 " +
                            "WHERE ordre_index > ? AND ordre_index <= ? AND id != ?";
                    ps = con.prepareStatement(shiftDownQuery);
                    ps.setInt(1, currentOrderIndex);
                    ps.setInt(2, newOrderIndex);
                    ps.setInt(3, invoiceId);
                    ps.executeUpdate();
                    ps.close();
                }
                // Case 2: Moving to a lower position
                else if (newOrderIndex < currentOrderIndex) {
                    // Shift up all invoices between new position and current-1
                    String shiftUpQuery = "UPDATE invoices SET ordre_index = ordre_index + 1 " +
                            "WHERE ordre_index >= ? AND ordre_index < ? AND id != ?";
                    ps = con.prepareStatement(shiftUpQuery);
                    ps.setInt(1, newOrderIndex);
                    ps.setInt(2, currentOrderIndex);
                    ps.setInt(3, invoiceId);
                    ps.executeUpdate();
                    ps.close();
                }
            }

            // Update the target invoice with new order index and notes
            String updateInvoiceQuery = "UPDATE invoices SET ordre_index = ?, notes = ? WHERE id = ?";
            ps = con.prepareStatement(updateInvoiceQuery);
            ps.setInt(1, newOrderIndex);
            ps.setString(2, notes);
            ps.setInt(3, invoiceId);
            ps.executeUpdate();
            ps.close();

            System.out.println("Invoice updated successfully!");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
