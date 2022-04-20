package util;

import no.ntnu.idata1002.g08.JPAConnection;
import no.ntnu.idata1002.g08.PersistenceUnit;
import no.ntnu.idata1002.g08.auth.AuthService;

public class GlobalTestData {
  private static GlobalTestData instance = null;

  private GlobalTestData() {
    JPAConnection.getInstance().getEntityManagerFactory(PersistenceUnit.TEST_PERSISTENCE_UNIT);
    registerUser();
  }

  public static synchronized GlobalTestData getInstance() {
    if (instance == null) {
      instance = new GlobalTestData();
    }
    return instance;
  }

  private void registerUser() {
    AuthService authService = new AuthService();
    authService.register("testUser", "testName", "testPassword");
  }


}
