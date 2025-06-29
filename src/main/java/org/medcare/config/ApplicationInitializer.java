package org.medcare.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.medcare.models.User;
import org.medcare.service.UserService;

@WebListener
public class ApplicationInitializer implements ServletContextListener {

    // Use the special constructor to create a service instance with its dependencies.
    private final UserService userService = new UserService(true);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("========================================================");
        System.out.println(" MedCare HMS Application Starting Up...                 ");
        System.out.println("========================================================");
        bootstrapAdminUser();
    }

    private void bootstrapAdminUser() {
        // Now userService.userDAO will be properly initialized.
        User existingAdmin = userService.findByUsernameIncludeInactive("admin");

        if (existingAdmin == null) {
            System.out.println("Admin user not found. Creating initial admin account...");
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@medcare.com");
            admin.setRole(org.medcare.enums.Role.ADMIN);
            admin.setActive(true);

            userService.createUser(admin, "admin123");
            System.out.println(">>> Initial admin user 'admin' with password 'admin123' created successfully. <<<");
        } else {
            System.out.println("Admin user already exists. Skipping bootstrap.");
            // Ensure the admin account is always active on startup
            if (!existingAdmin.isActive()) {
                existingAdmin.setActive(true);
                userService.saveOrUpdate(existingAdmin);
                System.out.println(">>> Reactivated the existing admin account. <<<");
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("========================================================");
        System.out.println(" MedCare HMS Application Shutting Down...               ");
        System.out.println("========================================================");
    }
}