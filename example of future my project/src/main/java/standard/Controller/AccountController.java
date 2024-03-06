package standard.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import io.jsonwebtoken.security.SignatureException;
import standard.Model.Account;
import standard.Model.Customer;
import standard.Model.JwtUtil;
import standard.Model.ResetPasswordRequest;
import standard.Service.AccountService;
import standard.Service.CustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountController {
    
    private final AccountService accountService;
    private final CustomerService customerService; 
    private final ObjectMapper mapper = new ObjectMapper(); 
    private Object email;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    // Updated constructor
    public AccountController(AccountService accountService, CustomerService customerService) {
        this.accountService = accountService;
        this.customerService = customerService;
    }

    public void createAccountHandler(Context ctx) throws SQLException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Account account = mapper.readValue(ctx.body(), Account.class);

            // Check if username already exists
            if (accountService.doesUsernameExist(account.getUsername())) {
                ctx.status(400).result("Username already exists. Please choose another one.");
                return;
            }

            // Check if email address already exists
            if (accountService.doesEmailAddressExist(account.getEmail())) {
                ctx.status(400).result("Email address already exists. Please use a different one or reset your password.");
                return;
            }

            Account createdAccount = accountService.createAccount(account);
            if (createdAccount != null && createdAccount.getUsername() != null && createdAccount.getPassword() != null
                    && !createdAccount.getUsername().isEmpty() && createdAccount.getPassword().length() >= 4) {
                ctx.json(createdAccount); // Send the created account as JSON response
            } else {
                ctx.status(400).result("Failed to create account. Please try again.");
            }
        } catch (JsonProcessingException | NullPointerException e) {
            ctx.status(400).result("Invalid request. Please provide valid account details.");
            e.printStackTrace();
        }
    }






        /* start the test using cookie */
        public void userAccountLogin(Context ctx) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Account account = mapper.readValue(ctx.body(), Account.class);
        
                Account loggedInAccount;
        
                if (account.getLoginIdentifier().contains("@")) {
                    loggedInAccount = accountService.getUserAccountLoginByEmail(account.getLoginIdentifier(), account.getPassword());
                } else {
                    loggedInAccount = accountService.getUserAccountLoginByUsername(account.getLoginIdentifier(), account.getPassword());
                }
        
                if (loggedInAccount != null) {
                    String token = JwtUtil.generateToken(loggedInAccount.getUsername());
        
                    logger.info("Generated JWT token for user {}: {}", loggedInAccount.getUsername(), token);
        

                    String cookieValue = String.format("userToken=%s; Max-Age=604800; Path=/; HttpOnly; Secure; SameSite=Lax", token);
                    ctx.header("Set-Cookie", cookieValue);

                    

                
                    ctx.json(Map.of("message", "Login successful"));
                } else {
                    ctx.status(401).result("Invalid username or password. Please try again.");
                }
            } catch (Exception e) {
                logger.error("Error in userAccountLogin: ", e);
                ctx.status(500).result("An error occurred during login.");
            }
        }
        


public void userAccountLogout(Context ctx) {
    ctx.removeCookie("userToken");
    ctx.status(200).result("Logged out successfully");
}






public void resetPasswordHandler(Context ctx) {
    try {
        ObjectMapper mapper = new ObjectMapper();
        ResetPasswordRequest resetRequest = mapper.readValue(ctx.body(), ResetPasswordRequest.class);

        String verificationCode = resetRequest.getVerificationCode();
        if (!accountService.isTokenValid(verificationCode)) {
            ctx.status(400).json(Map.of("status", 400, "message", "Invalid or expired verification code"));
            return;
        }
        String newPassword = resetRequest.getNewPassword();
        String confirmPassword = resetRequest.getConfirmPassword();
        System.out.println("Verification Code: " + verificationCode);

        int accountId = accountService.getAccountIdFromToken(verificationCode);
        if (accountId != -1 && newPassword.equals(confirmPassword)) {
            System.out.println("Executing SQL query to reset password...");
  
            logger.info("Password reset requested for email: {}", email); // Do not log sensitive information
            if (accountService.resetPassword(accountId, verificationCode, newPassword)) {
                logger.info("Password reset successful for accountId: {}", accountId);
            } else {
                logger.warn("Password reset failed for accountId: {}", accountId);
            }
            
        } else {
            System.out.println("Sending response: " + ctx.body()); // Add this line

            ctx.status(400).json(Map.of("status", 400, "message", "Invalid verification code or passwords do not match"));
        }
    } catch (Exception e) {
        System.out.println("Sending response: " + ctx.body()); // Add this line

        ctx.status(500).json(Map.of("status", 500, "message", "Server error"));
    }
}



public void resetPasswordRequestHandler(Context ctx) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(ctx.body());
        String email = jsonNode.get("email").asText();
        logger.info("Processing password reset request for email: " + email); 
        Account account = accountService.findAccountByEmail(email);

        if (account != null) {
            logger.info("Account found for email: " + email);  
            String token = accountService.generatePasswordResetToken(account);

            if (token != null) {
                ctx.status(200).result("If your email is registered, you will receive a password reset link.");
            } else {
                ctx.status(500).result("Failed to generate password reset token");
            }
        } else {
            logger.warn("Account not found for email: " + email);  
            ctx.status(400).result("Account not found");
        }
    } catch (Exception e) {
        logger.error("Failed to process password reset request", e);
        ctx.status(500).result("Failed to process password reset request");
    }
}



public void getUserInformationHandler(Context ctx) {
    try {
        String token = ctx.cookie("userToken");
        System.out.println("Token from cookie: " + token); 

        if (token == null || token.isEmpty()) {
            ctx.status(401).result("No authentication token found.");
            return;
        }

        String username = JwtUtil.getUsernameFromToken(token);
        System.out.println("Username from token: " + username); 

        Account userAccount = accountService.getUserAccountByUsername(username);
        System.out.println("User account from service: " + userAccount); 
        if (userAccount != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(userAccount);
            System.out.println("Final JSON result: " + json); 
            ctx.result(json);
            ctx.contentType("application/json");
        } else {
            ctx.status(404).result("User account not found.");
        }
    } catch (SignatureException e) {
        ctx.status(401).result("Invalid token.");
    } catch (Exception e) {
        System.err.println("Error in getUserInformationHandler: " + e.getMessage());
        e.printStackTrace();
        ctx.status(500).result(e.getMessage());
    }
}

public void uploadProfilePictureHandler(Context ctx) {
    try {
        String token = ctx.header("Authorization");
        String username = JwtUtil.getUsernameFromToken(token);
        System.out.println("Username: " + username); 
        UploadedFile uploadedFile = ctx.uploadedFile("profilePicture");
        InputStream inputStream = uploadedFile.content();

        // Read the input stream into a byte array
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] profilePicture = buffer.toByteArray();
        System.out.println("Uploaded file size: " + profilePicture.length); 

        // Upload the profile picture and get the signed URL
        String pictureUrl = accountService.uploadProfilePicture(username, profilePicture);
        System.out.println("Returned picture URL: " + pictureUrl); 

        Account userAccount = accountService.getUserAccountByUsername(username);
        userAccount.setProfilePictureUrl(pictureUrl);
        ctx.result("{\"message\": \"Profile picture uploaded successfully.\", \"profilePictureUrl\": \"" + pictureUrl + "\"}");
        ctx.contentType("application/json");
    } catch (IOException e) {
        e.printStackTrace();
        ctx.status(500).result("Failed to upload profile picture.");
    }
}


public void userCheckHandler(Context ctx) {
    String phone = ctx.queryParam("phone");
    String email = ctx.queryParam("email");
    boolean promptForAccount = false;

    if (phone == null && email == null) {
        ctx.status(400).json(Map.of("message", "Phone number or email is required."));
        return;
    }

    try {
        Integer accountId = accountService.findAccountIdByPhoneOrEmail(phone, email);
        if (accountId == null) {
            promptForAccount = true;
        }
        // Respond with just the flag
        ctx.status(200).json(Map.of("promptForAccount", promptForAccount));
    } catch (SQLException e) {
        ctx.status(500).json(Map.of("message", "An error occurred while processing your request."));
    }
}




 

public void createCustomerHandler(Context ctx) {
    try {
        Customer customer = mapper.readValue(ctx.body(), Customer.class);
   Customer createdCustomer = customerService.createCustomer(customer);
        if (createdCustomer != null && createdCustomer.getCustomerId() > 0) {
            ctx.status(201).json(createdCustomer);
        } else {
            ctx.status(400).result("Failed to create customer.");
        }
    } catch (JsonProcessingException | SQLException e) {
        ctx.status(400).result("Invalid request. Please provide valid customer details.");
        e.printStackTrace(); 
    }
}

}
