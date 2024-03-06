package standard.DAO;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import standard.Model.Customer;

public class CustomerDAO {
        private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }
    public Customer saveCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (Name, PhoneNumber, ShippingAddress, City, State, ZipCode, Email, AccountID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setString(3, customer.getShippingAddress());
            stmt.setString(4, customer.getCity());
            stmt.setString(5, customer.getState());
            stmt.setString(6, customer.getZipCode());
            stmt.setString(7, customer.getEmail());
            stmt.setObject(8, customer.getAccountID(), java.sql.Types.INTEGER);  // Handle nullable AccountID
        
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
        return customer;
    }

    public Customer findCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE CustomerID = ?";
        Customer customer = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                customer = new Customer(
                    rs.getInt("CustomerID"),
                    rs.getString("Name"),
                    rs.getString("PhoneNumber"),
                    rs.getString("ShippingAddress"),
                    rs.getString("City"),
                    rs.getString("State"),
                    rs.getString("Email"),
                    rs.getString("ZipCode"),
                    
                    (Integer) rs.getObject("AccountID") 
                );
            }
        }
        return customer;
    }
    

    public Integer findCustomerIdByPhoneOrEmail(String phoneNumber, String email) throws SQLException {
        String sql = "SELECT CustomerID FROM customers WHERE PhoneNumber = ? OR Email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phoneNumber);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("CustomerID");
            }
        }
        return null;
    }


    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET Name = ?, PhoneNumber = ?, ShippingAddress = ?, City = ?, State = ?, ZipCode = ?, AccountID = ? WHERE CustomerID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setString(3, customer.getShippingAddress());
            stmt.setString(4, customer.getCity());
            stmt.setString(5, customer.getState());
            stmt.setString(6, customer.getZipCode());
            stmt.setObject(7, customer.getAccountID(), java.sql.Types.INTEGER);  // Handle nullable AccountID
            stmt.setInt(8, customer.getCustomerId());
    
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating customer failed, no rows affected.");
            }
        }
    }
    public Integer findCustomerIdByEmail(String email) throws SQLException {
        String sql = "SELECT CustomerID FROM customers WHERE Email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return rs.getInt("CustomerID");
            } else {
                return null; // No customer found for this email
            }
        } catch (SQLException e) {
            throw e; 
        }
    }



    public Integer findCustomerIdByAccountId(Integer accountId) throws SQLException {
        String sql = "SELECT CustomerID FROM customers WHERE AccountID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return rs.getInt("CustomerID");
            }
        }
        return null;
    }
}
