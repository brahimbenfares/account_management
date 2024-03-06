package standard.DAO;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import standard.Model.Account;

public class AccountDAO {
    private static final Logger logger = LoggerFactory.getLogger(AccountDAO.class);

            private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

 

    public Integer findAccountIdByPhoneOrEmail(String phone, String email) throws SQLException {
        String sql = "SELECT account_id FROM accounts WHERE phone_number = ? OR email_address = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, phone);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return rs.getInt("account_id");
            } else {
                return null;
            }
        }
    }
    

    public void deleteAccount(int accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, accountId);
            stmt.executeUpdate();
        }
    }
    
    public Account findAccountById(int accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("email_address"),
                        rs.getString("phone_number"),
                        rs.getString("profile_picture_url"));
            } else {
                return null;
            }
        }
    }
    


    /************************************************************************************ */
    /*                        creation of account start                                   */
    /************************************************************************************ */
    


    public Account createNewUserAccount(Account account) {
        try (Connection connection = getConnection()) {
    
            String sql = "INSERT INTO accounts (username, name, password, email_address, phone_number, profile_picture_url) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    
            // Hash the password before storing it
            String hashedPassword = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
    
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getName());
            preparedStatement.setString(3, hashedPassword); // Storing hashed password
            preparedStatement.setString(4, account.getEmail());
            preparedStatement.setString(5, account.getPhone());
            preparedStatement.setString(6, account.getProfilePictureUrl());
    
            int affectedRows = preparedStatement.executeUpdate();
    
            if (affectedRows > 0) {
                ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
                if (pkeyResultSet.next()) {
                    int generatedAccountId = pkeyResultSet.getInt(1);
                    return new Account(generatedAccountId, account.getUsername(), account.getName(), hashedPassword,
                            account.getEmail(), account.getPhone(), account.getProfilePictureUrl());
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException Message: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
        }
    
        return null;
    }
    
    


    
/********************************************************************************** */
/* checking if the username or email is exist in database when creating new account */
/********************************************************************************** */
    public boolean isUsernameTaken(String username) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM accounts WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    public boolean isEmailTaken(String email) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM accounts WHERE email_address = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    public boolean doesUsernameExist(String username) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM accounts WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean doesEmailAddressExist(String email) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM accounts WHERE email_address = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
        /****************************************************************************** */
         /*                    creation of account end                                  */
        /****************************************************************************** */




    /************************************************************************************ */
    /*                                   logins start                                     */
    /************************************************************************************ */
 //login by username 
 public Account getUserAccountByLogin(String username, String password) {
    try (Connection connection = getConnection()) {
        String sql = "SELECT * FROM accounts WHERE username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            String hashedPassword = rs.getString("password");
            if (BCrypt.checkpw(password, hashedPassword)) {
                int accountId = rs.getInt("account_id");
                String retrievedUsername = rs.getString("username");
                String retrievedEmailAddress = rs.getString("email_address");
                String retrievedPhoneNumber = rs.getString("phone_number");
                String retrieveUrl = rs.getString("profile_picture_url");
                return new Account(accountId, retrievedUsername, retrievedEmailAddress, retrievedPhoneNumber, retrieveUrl);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error accessing the database: " + e.getMessage());
        throw new RuntimeException("An error occurred while accessing the user account: " + e.getMessage());
    }
    return null;
}

   // by email address 
   public Account getUserAccountLoginByEmail(String email, String password) {
    try (Connection connection = getConnection()) {
        String sql = "SELECT * FROM accounts WHERE email_address = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, email);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            String hashedPassword = rs.getString("password");
            if (BCrypt.checkpw(password, hashedPassword)) {
                int accountId = rs.getInt("account_id");
                String retrievedUsername = rs.getString("username");
                String retrievedName=rs.getString("name");
                String retrievedEmailAddress = rs.getString("email_address");
                String retrievedPhoneNumber = rs.getString("phone_number");
                String retrievedUrl=rs.getString("profile_picture_url");
                return new Account(accountId, retrievedUsername,retrievedName, retrievedEmailAddress, retrievedPhoneNumber,retrievedUrl);
            }
        }
    } catch (SQLException e) {
    System.err.println("Error accessing the database: " + e.getMessage());
    throw new RuntimeException("An error occurred while accessing the user account. Please try again later.");
  }
    return null;
}
/* logins ends  */


/************************************************************************************************* */
//                                        get user infos
/************************************************************************************************* */
public Account getUserAccountByUsername(String username) {
    try (Connection connection = getConnection()) {
        String sql = "SELECT * FROM accounts WHERE username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            int accountId = rs.getInt("account_id");
            String retrievedUsername = rs.getString("username");
            String retrievedName = rs.getString("name");
            String retrievedEmailAddress = rs.getString("email_address");
            String retrievePhoneNumber = rs.getString("phone_number");
            String profilePictureUrl = rs.getString("profile_picture_url"); // Get the profile picture URL
            return new Account(accountId, retrievedUsername,retrievedName, retrievedEmailAddress, retrievePhoneNumber, profilePictureUrl);
        }
    } catch (SQLException e) {
        System.err.println("Error accessing the database: " + e.getMessage());
        throw new RuntimeException("An error occurred while accessing the user account: " + e.getMessage());
    }
    return null;
}
/********************************************************************************************** */
/*                                 reset password  start                                        */
/********************************************************************************************** */
public Account findAccountByEmail(String email) {
    logger.info("Searching for account with email: " + email);  // new log statement
    try (Connection connection = getConnection()) {
        String sql = "SELECT * FROM accounts WHERE email_address = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, email);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int accountId = resultSet.getInt("account_id");
            String username = resultSet.getString("username");
            String name =resultSet.getString("name");
            String password = resultSet.getString("password");
            String phoneNumber = resultSet.getString("phone_number");
            String profilePictureUrl = resultSet.getString("profile_picture_url"); 
            logger.info("Account found for email: " + email);  // new log statement
            return new Account(accountId, username,name, password, email, phoneNumber, profilePictureUrl);
        } else {
            logger.warn("No account found for email: " + email);  // new log statement
        }
    } catch (SQLException e) {
        logger.error("SQL exception when searching for account with email: " + email, e);  // new log statement
    }
    return null;
}



 /* generating token  */
 public String generatePasswordResetToken(Account account) {
    String token = UUID.randomUUID().toString();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, 24);
    //int accountId = account.getAccount_id();
    System.out.println("Generated token: " + token + " for accountId: " + account.getAccount_id());
    int generatedTokenId = -1;

    try (Connection connection = getConnection()) {
        String sql = "INSERT INTO password_reset_tokens (account_id, token, expiration_time) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, account.getAccount_id());
        preparedStatement.setString(2, token);
        preparedStatement.setTimestamp(3, new java.sql.Timestamp(calendar.getTimeInMillis()));
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if (pkeyResultSet.next()) {
                generatedTokenId = pkeyResultSet.getInt(1);
                System.out.println("Generated token_id: " + generatedTokenId);
            }
            return token;
        } else {
            System.out.println("Failed to insert token for accountId: " + account.getAccount_id());
            return null;
        }
    } catch (SQLException e) {
        System.out.println("Exception occurred while inserting token for accountId: " + account.getAccount_id());
        e.printStackTrace();
        return null;
    }
}





    public boolean validatePasswordResetToken(String token) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM password_reset_tokens WHERE token = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }




    public boolean resetPassword(int accountId, String verificationCode, String newPassword) {
        try (Connection connection = getConnection()) {
          String sql = "SELECT * FROM password_reset_tokens WHERE account_id = ? AND token = ?";
          PreparedStatement preparedStatement = connection.prepareStatement(sql);
          preparedStatement.setInt(1, accountId);
          preparedStatement.setString(2, verificationCode);
          ResultSet resultSet = preparedStatement.executeQuery();
      
          if (resultSet.next()) {
            
            // Hash the new password before storing it
            String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
      
            String updateSql = "UPDATE accounts SET password = ? WHERE account_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setString(1, hashedNewPassword);  // Storing hashed new password
            updateStatement.setInt(2, accountId);
            int affectedRows = updateStatement.executeUpdate();
      
            if (affectedRows > 0) {
              System.out.println("Password updated successfully for accountId: " + accountId);
              String deleteTokenSql = "DELETE FROM password_reset_tokens WHERE account_id = ? AND token = ?";
              PreparedStatement deleteTokenStatement = connection.prepareStatement(deleteTokenSql);
              deleteTokenStatement.setInt(1, accountId);
              deleteTokenStatement.setString(2, verificationCode);
              boolean deleteResult = deleteTokenStatement.execute();
      
              if (deleteResult) {
                System.out.println("Token deleted successfully for accountId: " + accountId);
              } else {
                System.out.println("Failed to delete token for accountId: " + accountId);
              }
      
              return true; // Password update and token deletion successful
            } else {
              System.out.println("Failed to update password for accountId: " + accountId);
            }
          }
      
          return false; // Invalid verification code or token not found
        } catch (SQLException e) {
          System.out.println(e.getMessage());
          return false; // Error occurred
        }
      }
      


public int getAccountIdFromToken(String token) {
    try (Connection connection = getConnection()) {
        String sql = "SELECT account_id FROM password_reset_tokens WHERE token = ? ";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, token);
        ResultSet result = statement.executeQuery();

        if (result.next()) {
            return result.getInt("account_id");
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }

    return -1;
}
/************************************************************************************* */
/*                            reset password ends                                      */
/************************************************************************************* */




}





