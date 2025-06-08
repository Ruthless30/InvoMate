package DAO;

import MODEL.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientDAO implements IClient{
    private final static Connection con=DBConnection.getInstance();
    @Override
    public boolean addClient(String name, String deliveryTime) {
        // First check if client already exists (case-insensitive)
        String checkSql = "SELECT COUNT(*) FROM clients WHERE LOWER(name) = LOWER(?)";
        String insertSql = "INSERT INTO clients (name, deliveryTime) VALUES (?, ?)";

        try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setString(1, name);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Client already exists (case-insensitive match)
                return false;
            }

            // Client doesn't exist, proceed with insertion
            try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                insertPs.setString(1, name);
                insertPs.setString(2, deliveryTime);
                insertPs.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteClient(int id) {
        String sql="DELETE FROM clients WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateClient(int id,String name,String deliveryTime) {
        String sql="UPDATE clients SET name = ?, deliveryTime = ? WHERE id = ?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1,name);
            ps.setString(2,deliveryTime);
            ps.setInt(3,id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
        e.printStackTrace();
        }
        return false;
    }

    @Override
    public Client getClient(int id) {
        String sql="SELECT * FROM clients WHERE id = ?";
        Client client = new Client();
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                client.setIdClient(rs.getInt("id"));
                client.setName(rs.getString("name"));
                client.setCredit(rs.getDouble("credit"));
                client.setDeliveryTime(rs.getString("deliveryTime"));
            }
            return client;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Client> getAllClient() {
        String sql="SELECT * FROM clients";
        ArrayList<Client> clientList = new ArrayList<>();
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ResultSet rs=ps.executeQuery();
            while (rs.next()) {
                Client client = new Client();
                client.setIdClient(rs.getInt("id"));
                client.setName(rs.getString("name"));
                client.setCredit(rs.getDouble("credit"));
                client.setDeliveryTime(rs.getString("deliveryTime"));

                clientList.add(client);
            }
            return clientList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public boolean updateCredit(int id,double credit){
        String sql="UPDATE clients SET credit = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)){
            ps.setDouble(1,getClient(id).getCredit()+credit);
            ps.setInt(2,id);
            ps.executeUpdate();
            return true;

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
