package standard.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;
@JsonIgnoreProperties(ignoreUnknown = true)

public class Customer {
    private int customerId;
    private String name;
    private String phoneNumber;
    private String shippingAddress;
    private String city;
    private String state;
    private String zipCode;
    private String email;
    private Integer accountID;  
    public Customer() {
    }

    // Constructor
    public Customer(int customerId, String name, String phoneNumber, String shippingAddress, String city, String state, String zipCode, String email, Integer accountID) {
        this.customerId = customerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.email=email;
        this.accountID = accountID;  
    }

    public Customer(String name, String phoneNumber, String shippingAddress, String city, String state, String zipCode,String email, Integer accountID) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.email=email;
        this.accountID = accountID;  
    }

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAccountID() {
        return accountID;
    }
    
    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }
    
    

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", email='" + email + '\'' +
                ", accountID=" + accountID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId &&
                Objects.equals(name, customer.name) &&
                Objects.equals(phoneNumber, customer.phoneNumber) &&
                Objects.equals(shippingAddress, customer.shippingAddress) &&
                Objects.equals(city, customer.city) &&
                Objects.equals(state, customer.state) &&
                Objects.equals(zipCode, customer.zipCode) &&
                Objects.equals(email, customer.email) &&
                Objects.equals(accountID, customer.accountID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, name, phoneNumber, shippingAddress, city, state, zipCode, email, accountID);
    }

    public void setId(int id) {
        this.customerId = id;
    }

    public int getId() {
        return customerId;
    }
}

