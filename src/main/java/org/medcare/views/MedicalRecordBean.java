//package org.medcare.views;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.faces.application.FacesMessage;
//import jakarta.faces.context.FacesContext;
//import jakarta.faces.view.ViewScoped;
//import jakarta.inject.Inject;
//import jakarta.inject.Named;
//import org.medcare.enums.RecordStatus;
//import org.medcare.models.Doctor;
//import org.medcare.models.MedicalRecord;
//import org.medcare.models.Patient;
//import org.medcare.service.DoctorService;
//import org.medcare.service.MedicalRecordService;
//import org.medcare.service.PatientService;
//import org.primefaces.PrimeFaces;
//
//import java.io.Serializable;
//import java.util.List;
//
//@Named
//@ViewScoped
//public class MedicalRecordBean implements Serializable {
//
//    @Inject private MedicalRecordService medicalRecordService;
//    @Inject private PatientService patientService;
//    @Inject private DoctorService doctorService;
//    @Inject private UserBean userBean;
//
//    private List<MedicalRecord> medicalRecords;
//    private List<Patient> availablePatients;
//    private List<Doctor> availableDoctors;
//    private MedicalRecord selectedRecord;
//
//    @PostConstruct
//    public void init() {
//        medicalRecords = medicalRecordService.getAllMedicalRecords();
//        availablePatients = patientService.getAllActive();
//        availableDoctors = doctorService.getAll();
//    }
//
//    public void openNew() {
//        selectedRecord = new MedicalRecord();
//        selectedRecord.setStatus(RecordStatus.ACTIVE);
//    }
//
//    public void saveMedicalRecord() {
//        try {
//            medicalRecordService.saveMedicalRecord(selectedRecord, userBean.getUser());
//            init(); // Refresh list
//            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Medical record saved successfully.");
//            PrimeFaces.current().executeScript("PF('manageMedicalRecordDialog').hide()");
//            PrimeFaces.current().ajax().update("recordForm:messages", "recordForm:dt-records");
//        } catch (Exception e) {
//            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not save medical record: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public void deleteMedicalRecord() {
//        try {
//            medicalRecordService.deleteMedicalRecord(selectedRecord, userBean.getUser());
//            init();
//            addMessage(FacesMessage.SEVERITY_WARN, "Deleted", "Medical record has been deleted.");
//        } catch (Exception e) {
//            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not delete medical record.");
//        }
//    }
//
//    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
//    }
//
//    // Getters and Setters
//    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
//    public void setMedicalRecords(List<MedicalRecord> medicalRecords) { this.medicalRecords = medicalRecords; }
//
//    public List<Patient> getAvailablePatients() { return availablePatients; }
//    public List<Doctor> getAvailableDoctors() { return availableDoctors; }
//
//    public MedicalRecord getSelectedRecord() { return selectedRecord; }
//    public void setSelectedRecord(MedicalRecord selectedRecord) { this.selectedRecord = selectedRecord; }
//
//    public RecordStatus[] getRecordStatuses() { return RecordStatus.values(); }
//}
