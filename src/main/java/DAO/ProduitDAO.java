package DAO;

import MODEL.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProduitDAO implements IProduit{

    private final static Connection con=DBConnection.getInstance();
    @Override
    public boolean addProduit(String nomProduit, double PrixProduit) {
        // First check if product already exists
        String checkSql = "SELECT COUNT(*) FROM products WHERE LOWER(name) = LOWER(?)";
        String insertSql = "INSERT INTO products(name, price) VALUES(?, ?)";

        try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setString(1, nomProduit);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Product already exists
                return false;
            }

            try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                insertPs.setString(1, nomProduit);
                insertPs.setDouble(2, PrixProduit);
                insertPs.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteProduit(int id) {
        String sql="DELETE FROM products WHERE id=?";
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
    public boolean updateProduit(int id, String nomProduit, double PrixProduit) {
        String sql="UPDATE products SET name = ?, price = ? WHERE id = ?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,nomProduit);
            ps.setDouble(2,PrixProduit);
            ps.setInt(3,id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<Produit> getAllProduit() {
        String sql="SELECT * FROM products ";
        ArrayList<Produit> produitlist = new ArrayList<>();
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ResultSet rs=ps.executeQuery();
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setIdProduit(rs.getInt("id"));
                produit.setNomProduit(rs.getString("name"));
                produit.setPrixProduit(rs.getDouble("price"));

                produitlist.add(produit);
            }
            return produitlist;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    public static Produit getProduit(int id) {
        String sql="SELECT * FROM products WHERE id=?";
        Produit produit = new Produit();
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                produit.setIdProduit(rs.getInt("id"));
                produit.setNomProduit(rs.getString("name"));
                produit.setPrixProduit(rs.getDouble("price"));
            }
            return produit;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getProduitId(String nomProduit) {
        String sql="SELECT id FROM products where name=?";
        int id=-1;
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,nomProduit);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                id = rs.getInt("id");
            }
            return id;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return id;

    }
}
