package eckofox.efhomefinancialdb.authservice;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncryption {

    public static String passwordEncryption(String confirmedPassword) {
        String salt = BCrypt.gensalt(14); //standard value 10, changed to 10 to guarantee pass mark on schoolproject
        return BCrypt.hashpw(confirmedPassword, salt);
    }
}
