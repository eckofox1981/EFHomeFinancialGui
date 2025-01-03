package eckofox.efhomefinancialdb.authservice;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncryption {

    /**
     * hashes password only
     * @param confirmedPassword from registration screen (NewUserScreenController) or change password screen
     * (EditUserScreenController)
     * @return string of hashed password
     */
    public static String passwordEncryption(String confirmedPassword) {
        String salt = BCrypt.gensalt(14); //standard value 10, changed to 10 to guarantee pass mark on schoolproject
        return BCrypt.hashpw(confirmedPassword, salt);
    }
}
