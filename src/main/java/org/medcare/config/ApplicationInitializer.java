package org.medcare.config;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.medcare.enums.Role;
import org.medcare.models.User;
import org.medcare.service.UserService;

/**
 * Initializes the application on startup.
 * Currently used to bootstrap the initial admin user if it doesn't exist.
 */
@WebListener
public class ApplicationInitializer implements ServletContextListener {

    // CDI doesn't work in a listener by default in some environments,
    // so we'll instantiate the service manually. For a robust app,
    // you might use programmatic CDI lookup.
    private final UserService userService = new UserService();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("========================================================");
        System.out.println(" MedCare HMS Application Starting Up...                 ");
        System.out.println("========================================================");
        bootstrapAdminUser();
    }

    private void bootstrapAdminUser() {
        // We cannot use @Inject here directly in a standard ServletContextListener
        // with basic Weld setup. We'll manually create the dependency chain.
        // In a full Java EE container, injection would work.
        User existingAdmin = userService.findByUsername("admin");

        if (existingAdmin == null) {
            System.out.println("Admin user not found. Creating initial admin account...");
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@medcare.com");
            admin.setRole(Role.ADMIN);

            // In a real scenario, this default password should be configured securely.
            String defaultPassword = "admin123";

            userService.createUser(admin, defaultPassword);
            System.out.println(">>> Initial admin user 'admin' with password 'admin123' created successfully. <<<");
            System.out.println(">>> PLEASE CHANGE THIS PASSWORD IN A PRODUCTION ENVIRONMENT. <<<");
        } else {
            System.out.println("Admin user already exists. Skipping bootstrap.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("========================================================");
        System.out.println(" MedCare HMS Application Shutting Down...               ");
        System.out.println("========================================================");
    }
}
