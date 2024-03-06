package standard.Service;


import java.sql.SQLException;
import standard.DAO.AccountDAO;
import standard.Model.Account;
import standard.Model.Customer;
import java.net.URL;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.BlobInfo;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountService {

    private final AccountDAO accountDAO;
    private Storage storage;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Account createAccount(Account account) throws SQLException {
        if (!isStrongPassword(account.getPassword())) {
            throw new SQLException("Password doesn't meet strength criteria.");
        }

        if (accountDAO.doesUsernameExist(account.getUsername())) {
            throw new SQLException("Username already exists.");
        }

        if (accountDAO.doesEmailAddressExist(account.getEmail())) {
            throw new SQLException("Email address already exists.");
        }

        return accountDAO.createNewUserAccount(account);
    }
    public Account createAccountFromCustomer(Customer customer, String username, String password) throws SQLException {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password); // Ensure this password is hashed
        account.setName(customer.getName());
        account.setEmail(customer.getEmail());
        account.setPhone(customer.getPhoneNumber());

        return createAccount(account);
    }
    public Account getAccountById(int accountId) throws SQLException {
        return accountDAO.findAccountById(accountId);
    }

    public void deleteAccount(int accountId) throws SQLException {
        accountDAO.deleteAccount(accountId);
    }

    

    public Integer findAccountIdByPhoneOrEmail(String phone, String email) throws SQLException {
        return accountDAO.findAccountIdByPhoneOrEmail(phone, email);
    }

    public Account getUserAccountLoginByUsername(String username, String password) {
        return accountDAO.getUserAccountByLogin(username, password);
    }

    public Account getUserAccountLoginByEmail(String email, String password) {
        return accountDAO.getUserAccountLoginByEmail(email, password);
    }

    public Account getUserAccountByUsername(String username) {
        return accountDAO.getUserAccountByUsername(username);
    }

    public boolean doesUsernameExist(String username) {
        return accountDAO.doesUsernameExist(username);
    }

    public boolean doesEmailAddressExist(String email) {
        return accountDAO.doesEmailAddressExist(email);
    }

    public boolean resetPassword(int accountId, String verificationCode, String newPassword) {
        return accountDAO.resetPassword(accountId, verificationCode, newPassword);
    }

    public Account findAccountByEmail(String email) {
        return accountDAO.findAccountByEmail(email);
    }

    public String generatePasswordResetToken(Account account) {
        logger.info("Generating password reset token for account: {}", account);
        String token = accountDAO.generatePasswordResetToken(account);
        if (token == null) {
            logger.warn("Failed to generate password reset token for account: {}", account);
        }
        return token;
    }



    public String uploadProfilePicture(String username, byte[] profilePicture) {
        String bucketName = "your-bucket-name"; // Replace with your actual bucket name
        String objectName = "profile-pictures/" + username;

        try {
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();
            storage.create(blobInfo, profilePicture);

            URL signedUrl = storage.signUrl(blobInfo, 365 * 24 * 100, TimeUnit.HOURS);
            return signedUrl.toString();
        } catch (StorageException e) {
            logger.error("Failed to upload profile picture: {}", e.getMessage());
            throw new RuntimeException("Failed to upload profile picture.");
        }
    }

    private boolean isStrongPassword(String password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(pattern);
    }

    public int getAccountIdFromToken(String verificationCode) {
        return accountDAO.getAccountIdFromToken(verificationCode);
    }
    // Validate token method
    public boolean isTokenValid(String token) {
        return accountDAO.validatePasswordResetToken(token);
    }
}



