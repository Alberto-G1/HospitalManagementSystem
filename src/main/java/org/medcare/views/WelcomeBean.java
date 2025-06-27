package org.medcare.views;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class WelcomeBean {

    public String navigateToLogin() {
        return "login.xhtml?faces-redirect=true";
    }

    public String navigateToRegister() {
        return "register.xhtml?faces-redirect=true";
    }

    public String navigateToAbout() {
        return "about.xhtml?faces-redirect=true";
    }
}