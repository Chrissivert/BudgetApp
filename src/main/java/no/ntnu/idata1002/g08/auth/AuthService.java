package no.ntnu.idata1002.g08.auth;

import jakarta.persistence.PersistenceException;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.data.User;
import no.ntnu.idata1002.g08.dao.UserDAO;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Class to register new users, login and hash + salt password.
 *
 * @author Anders Lund
 * @version 23.04.2023
 */
public class AuthService {
  /**
   * Register register new users, add them to the database,
   * and add them to GlobalData.
   *
   * @param username The username of the user
   * @param password The password of the user
   * @param fullName the full name of the user
   * @return return the result of the register attempt
   * @throws PersistenceException
   */
  public String register(String username, String password, String fullName) throws PersistenceException {
    UserDAO userDAO = new UserDAO();
    User existingUser = userDAO.getUserByUsername(username);
    if (existingUser != null) {
      return "Username already exists";
    }
    if (password.length() < 6) {
      return "Password is too short";
    }
    byte[] salt = generateSalt();
    String hashedPassword = hashPassword(password, salt);

    User user = new User(username, fullName, hashedPassword, salt);
    User userFromDB = userDAO.addOrUpdateUser(user);
    GlobalData.getInstance().setUser(userFromDB);

    return "success";
  }

  /**
   * Login the user if the username and the password match with the
   * database. Add user to GlobalData if the login attempt was
   * successful.
   *
   * @param username The username to login with
   * @param password Thr password to login with
   * @return return the result of the login attempt
   * @throws PersistenceException
   */
  public String login(String username, String password) throws PersistenceException {
    UserDAO userDAO = new UserDAO();
    User user = userDAO.getUserByUsername(username);
    if (user == null) {
      return "Username does not exist";
    }
    String hashedPassword = hashPassword(password, user.getSalt());
    if (hashedPassword.equals(user.getPassword())) {
      GlobalData.getInstance().setUser(user);
      return "success";
    }
    return "Wrong password";
  }

  /**
   * Hash the password and add salt.
   *
   * @param password The users Password
   * @param salt the salt to add to hash
   * @return return the hashed password
   */
  public String hashPassword(String password, byte[] salt) {

    String hashedPassword = null;

    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      md.update(salt);

      byte[] byteHashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
      StringBuilder stringBuilder = new StringBuilder();

      for (byte b : byteHashedPassword) {
        stringBuilder.append(String.format("%02x", b));
      }

      hashedPassword = stringBuilder.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return hashedPassword;
  }

  /**
   * Generate salt used to Hash the password of the user.
   *
   * @return return salt as byte[]
   */
  public byte[] generateSalt() {
    SecureRandom random = new SecureRandom();
    return random.generateSeed(16);
  }

  /**
   * When the user logout, remove them from the GlobalData.
   */
  public static void logout() {
    GlobalData.getInstance().reset();
  }
}
