import no.ntnu.idata1002.g08.auth.AuthService;
import no.ntnu.idata1002.g08.data.User;
import org.junit.jupiter.api.Test;
import util.GlobalTestData;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

  @Test
  public void testUserPositive() {
    GlobalTestData.getInstance();
    AuthService authService = new AuthService();
    byte[] salt = authService.generateSalt();
    User user = new User("testUser", "testName", "testPassword", salt);
    assertEquals("testUser", user.getUsername());
    assertEquals("testPassword", user.getPassword());
    assertEquals("testName", user.getFullName());
  }

  @Test
  public void testUserWithInvalidInputs() {
    boolean exceptionThrown = false;
    AuthService authService = new AuthService();
    byte[] salt = authService.generateSalt();
    try {
      new User(" ", "testName", "testPassword", salt);
    } catch (IllegalArgumentException e) {
      if ("Username has to be at least 4 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      new User("", "testName", "testPassword", salt);
    } catch (IllegalArgumentException e) {
      if ("Username has to be at least 4 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      new User(null, "testName", "testPassword", salt);
    } catch (IllegalArgumentException e) {
      if ("Username has to be at least 4 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      new User("tes", "testName", "testPassword", salt);
    } catch (IllegalArgumentException e) {
      if ("Username has to be at least 4 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      new User("testUser", " ", "testPassword", salt);
    } catch (IllegalArgumentException e) {
      if ("Full name has to be at least 3 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      new User("testUser", "", "testPassword", salt);
    } catch (IllegalArgumentException e) {
      if ("Full name has to be at least 3 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      new User("testUser", null, "testPassword", salt);
    } catch (IllegalArgumentException e) {
      if ("Full name has to be at least 3 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      new User("testUser", "te", "testPassword", salt);
    } catch (IllegalArgumentException e) {
      if ("Full name has to be at least 3 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      User user = new User("testUser", "testName", " ", salt);
    } catch (IllegalArgumentException e) {
      if ("Password has to be at least 6 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      User user = new User("testUser", "testName", "test", salt);
    } catch (IllegalArgumentException e) {
      if ("Password has to be at least 6 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      User user = new User("testUser", "testName", null, salt);
    } catch (IllegalArgumentException e) {
      if ("Password has to be at least 6 characters long"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;

    try {
      User user = new User("testUser", "testName", "testPassword", null);
    } catch (IllegalArgumentException e) {
      if ("Salt cannot be null"== e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;
  }
}
