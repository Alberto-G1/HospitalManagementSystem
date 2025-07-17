package org.medcare.service.interfaces;

import org.medcare.models.User;

import java.util.List;

public interface UserServiceInterface {
    String hashPassword(String password);
    boolean checkPassword(String plainPassword, String hashedPassword);
    void createUser(User user, String plainPassword);
    boolean changePassword(User user, String oldPassword, String newPassword);
    void adminResetPassword(User user, String newPassword, User admin);
    void updateUser(User user);
    User authenticate(String username, String plainPassword);
    void softDelete(User user);
    void reactivateUser(User user);
    User findByUsername(String username);
    User findByUsernameIncludeInactive(String username);
    User findByEmail(String email);
    User findByEmailIncludeInactive(String email);
    List<User> getAll();
    List<User> getAllIncludeInactive();
    void saveOrUpdate(User user);
    User findById(int id);
}
