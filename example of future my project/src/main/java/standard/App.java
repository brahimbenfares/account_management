package standard;

import java.io.File;
import java.io.IOException;

import org.slf4j.LoggerFactory;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SslConnectionFactory;

import standard.Controller.AccountController;

import standard.Model.JwtUtil;
import standard.DAO.AccountDAO;
import standard.DAO.CustomerDAO;

import standard.Service.AccountService;
import standard.Service.CustomerService;
import org.slf4j.Logger;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        System.out.println("Current directory: " + new File(".").getCanonicalPath());

        // Initialize DAOs
        @SuppressWarnings("unused")
        AccountDAO accountDAO = new AccountDAO();
        CustomerDAO customerDAO = new CustomerDAO();

       AccountService accountService = new AccountService();
        CustomerService customerService = new CustomerService(customerDAO);

        AccountController accountController = new AccountController(accountService, customerService);
        startAPI( accountController);
    }


    private static void startAPI(  AccountController accountController) {
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        });

       // Configure the server to use HTTPS
       Server server = new Server();

       HttpConfiguration httpsConfig = new HttpConfiguration();
       httpsConfig.addCustomizer(new SecureRequestCustomizer());
   
       SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
       sslContextFactory.setKeyStorePath("path to SSL SSL_certificate\\certificate.p12");
   
   
       sslContextFactory.setKeyStoreType("PKCS12");
   
       ServerConnector sslConnector = new ServerConnector(server,
               new SslConnectionFactory(sslContextFactory, "http/1.1"),
               new HttpConnectionFactory(httpsConfig));
       sslConnector.setPort(8443); 
       server.addConnector(sslConnector);
   
       app.start(8443);

        

        app.get("api/auth-check", ctx -> {
            String token = ctx.cookie("userToken");
            if (token != null && JwtUtil.validateToken(token)) {
                ctx.status(200);
            } else {
                ctx.status(401).result("Unauthorized");
            }
        });


        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "https://localhost");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
            ctx.header("Access-Control-Allow-Credentials", "true");
            addCspHeader(ctx);

        });

        app.before("/api/reset-password-request", ctx -> {
            String clientIp = ctx.ip();
            if (RateLimiter.isRateLimited(clientIp)) {
                ctx.status(429).result("Too many requests");
            } else {
                RateLimiter.recordRequest(clientIp);
            }
        });
        
        
        app.options("/*", ctx -> {
            ctx.header("Access-Control-Allow-Origin", "https://localhost");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
            ctx.header("Access-Control-Allow-Credentials", "true");
            ctx.status(200);
        });


        
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("An unexpected error occurred:", e);

            ctx.status(500).result("Internal Server Error");
        });
    
        setupRoutes(app,  accountController);
    }
    
  
    
    private static void addCspHeader(Context ctx) {
        ctx.header("Content-Security-Policy", "default-src 'self'; script-src 'self' 'unsafe-inline' https://code.jquery.com https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com https://cdnjs.cloudflare.com https://use.fontawesome.com; img-src 'self' data:; font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://use.fontawesome.com; connect-src 'self' https://localhost;");
    }
    
    
    
    
    
 
    private static void setupRoutes(Javalin app,AccountController accountController) {


        
        

        app.post("api/register", accountController::createAccountHandler);
        app.post("api/login", accountController::userAccountLogin);
        app.post("api/reset-password", accountController::resetPasswordHandler);
        app.post("api/reset-password-request", accountController::resetPasswordRequestHandler);
        app.get("api/user", accountController::getUserInformationHandler);
        app.post("api/upload-profile-picture", accountController::uploadProfilePictureHandler);
        app.get("api/user-check",accountController::userCheckHandler);
        app.post("api/create-customer", accountController::createCustomerHandler);



        /* logout */
        app.post("api/logout", accountController::userAccountLogout);
    }
}
