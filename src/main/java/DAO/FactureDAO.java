package DAO;


import MODEL.Facture;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FactureDAO  implements IFacture{
    private final static Connection con = DBConnection.getInstance();

    @Override
    public boolean addFacture(String nom_client, String notes) {
        String sql="INSERT INTO invoices (nom_client,notes) VALUES (?,?)";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1, nom_client);
            ps.setString(2, notes);
            ps.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteFacture(int id) {
        String sql="DELETE FROM invoices WHERE id=?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateFacture(int id, String nom_client, String notes ,int ordre_index) {
        String sql="UPDATE invoices SET nom_client=?, notes=? WHERE id=?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1, nom_client);
            ps.setString(2, notes);
            ps.setInt(3, id);
            ps.setInt(4, ordre_index);
            ps.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateFacturePrint(int id,String printEtat) {
        String sql="UPDATE invoices SET print=? WHERE id=?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1, printEtat);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public boolean updateFactureOrder(int id,int ordre_index) {
        String sql="UPDATE invoices SET ordre_index=? WHERE id=?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1, ordre_index);
            ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Facture getFacture(int id) {
        String sql="SELECT * FROM invoices WHERE id=?";
        Facture facture= new Facture();
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                facture.setIdFacture(rs.getInt("id"));
                facture.setOrdre_index(rs.getInt("ordre_index"));
                facture.setNom_client(rs.getString("nom_client"));
                facture.setNotes(rs.getString("notes"));
            }
            return facture;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Facture> getAllFacture(String print_value) {
        String sql="SELECT * FROM invoices WHERE print = ? ORDER BY ordre_index ASC";
        ArrayList<Facture> factureList= new ArrayList<>();
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1, print_value);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Facture facture= new Facture();
                facture.setIdFacture(rs.getInt("id"));
                facture.setOrdre_index(rs.getInt("ordre_index"));
                facture.setNom_client(rs.getString("client_name"));
                facture.setNotes(rs.getString("notes"));

                factureList.add(facture);
            }
            return factureList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double getTotalFacture(int idFacture){
        double totalFacture= 0.0;
        String sql="SELECT * FROM facture_items WHERE id=?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1,idFacture);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                totalFacture= totalFacture + rs.getDouble("total");
            }
            return totalFacture;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return totalFacture;
    }
}
