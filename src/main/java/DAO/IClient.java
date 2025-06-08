package DAO;

import MODEL.Client;

import java.util.ArrayList;

public interface IClient {
    boolean addClient(String name,String deliveryTime);
    boolean deleteClient(int id);
    boolean updateClient(int id,String name,String deliveryTime);
    boolean updateCredit(int id,double credit);
    Client getClient(int id);
    ArrayList<Client> getAllClient();

}
