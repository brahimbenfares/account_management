package standard.Service;
import java.sql.SQLException;

import standard.DAO.CustomerDAO;
import standard.Model.Customer;
public class CustomerService {

    private CustomerDAO customerDAO; 

    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public Customer createCustomer(Customer customer) throws SQLException {
        return customerDAO.saveCustomer(customer);
    }

    public void updateCustomer(Customer customer) throws SQLException {
        customerDAO.updateCustomer(customer);
    }

    public Integer findCustomerIdByPhoneOrEmail(String phone, String email) throws SQLException {
        return customerDAO.findCustomerIdByPhoneOrEmail(phone, email);
    }

    public Customer findCustomerById(int customerId) throws SQLException {
        return customerDAO.findCustomerById(customerId);
    }
    
}
