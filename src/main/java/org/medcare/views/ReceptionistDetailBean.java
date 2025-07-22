package org.medcare.views;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.medcare.models.Receptionist;
import org.medcare.service.interfaces.ReceptionistServiceInterface;

import java.io.Serializable;

@Named
@ViewScoped
public class ReceptionistDetailBean implements Serializable {

    @Inject private ReceptionistServiceInterface receptionistService;

    private int receptionistId;
    private Receptionist receptionist;

    /**
     * Called by f:viewAction. Loads the receptionist based on the 'id' URL parameter.
     */
    public void loadReceptionist() {
        if (receptionistId > 0) {
            this.receptionist = receptionistService.getByIdIncludeInactive(receptionistId);
            if (this.receptionist == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Receptionist not found.");
            }
        }
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getters and Setters
    public int getReceptionistId() { return receptionistId; }
    public void setReceptionistId(int receptionistId) { this.receptionistId = receptionistId; }
    public Receptionist getReceptionist() { return receptionist; }
    public void setReceptionist(Receptionist receptionist) { this.receptionist = receptionist; }
}