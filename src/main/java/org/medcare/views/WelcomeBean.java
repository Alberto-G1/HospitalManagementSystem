package org.medcare.views;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;

@Named("welcomeBean")
@RequestScoped
public class WelcomeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public WelcomeBean() {
        System.out.println("WelcomeBean created successfully!");
    }

    /**
     * Navigate to login page
     */
    public String navigateToLogin() {
        System.out.println("navigateToLogin method called");
        return "login?faces-redirect=true";
    }

    /**
     * Navigate to registration page
     */
    public String navigateToRegister() {
        System.out.println("navigateToRegister method called");
        return "register?faces-redirect=true";
    }

    /**
     * Alternative navigation method using programmatic redirect
     */
    public void redirectToLogin() {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.redirect(externalContext.getRequestContextPath() + "/login.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Alternative navigation method using programmatic redirect
     */
    public void redirectToRegister() {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.redirect(externalContext.getRequestContextPath() + "/register.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
