package standard.Model;


import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Account {
    private int account_id;
    private String username;
    private String name;
    private String email_address; 
    private String phoneNumber;

    private String password;

    @JsonIgnore
    private String confirmPassword;


    private String loginIdentifier;
    //private byte[] profilePicture;
    private String profilePictureUrl;

    public Account() {

    }
 
    public Account(String username,String name, String password, String email_address, String phoneNumber,String profilePictureUrl) {
        this.username = username;
        this.name=name;
        this.password = password;
        this.email_address = email_address;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl=profilePictureUrl;
    }   

    public Account(int account_id, String username,String name, String password, String email_address, String phoneNumber,String profilePictureUrl) {
        this.account_id = account_id;
        this.username = username;
        this.name=name;
        this.password = password;
        this.email_address = email_address;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl=profilePictureUrl;
    }  
    
    
    public Account(int account_id, String username,String name, String email_address, String phoneNumber, String profilePictureUrl) {
        this.account_id = account_id;
        this.username = username;
        this.name=name;
        this.email_address = email_address;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
    }
    


    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email_address;
    }

    public void setEmail(String email) {
        this.email_address = email;
    }

    public String getPhone() {
        return phoneNumber;
    }
    
    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

  

     public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }

  public Account(int account_id, String username,String name, String email_address, String phoneNumber) {
    this.account_id = account_id;
    this.name=name;
    this.username = username;
    this.email_address = email_address;
    this.phoneNumber = phoneNumber;
    
}


public String getProfilePictureUrl() {
    return profilePictureUrl;
}

public void setProfilePictureUrl(String profilePictureUrl) {
    this.profilePictureUrl = profilePictureUrl;
}

 @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Account account = (Account) o;
    return Objects.equals(loginIdentifier, account.loginIdentifier) &&
           Objects.equals(password, account.password);
}


    @Override
    public String toString() {
        return "Account{" +
                "account_id=" + account_id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email_address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
               '}';
    }

    public static Account createFromFields(String username, 
    String name,
    String email_address, 
    String phoneNumber,
    String profilePictureUrl) {
Account account = new Account();
account.setUsername(username);
account.setName(name);
account.setEmail(email_address);
account.setPhone(phoneNumber);
if (profilePictureUrl == null) {
account.setProfilePictureUrl("default_url");
} else {
account.setProfilePictureUrl(profilePictureUrl);
}
return account;
}



}