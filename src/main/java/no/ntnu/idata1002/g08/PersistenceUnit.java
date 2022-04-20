package no.ntnu.idata1002.g08;

/**
 * List of paths to the persistence unit configurations.
 *
 * @author Anders Lund
 * @version 25.04.2023
 */
public enum PersistenceUnit {
  /**
   * Local persistence unit.
   */
  LOCAL_PERSISTENCE_UNIT("PersonalEconomyPU"),
  /**
   * Remote persistence unit.
   */
  REMOTE_PERSISTENCE_UNIT("PersonalEconomyMySQLPU"),
  /**
   * Test persistence unit .
   */
  TEST_PERSISTENCE_UNIT("PersonalEconomyTestPU");

  private final String name;

  PersistenceUnit(String s) {
    name = s;
  }

  @Override
  public String toString() {
    return this.name;
  }
}