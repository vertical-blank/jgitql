package jgitdbc;

import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class Driver implements java.sql.Driver {

  private static Driver registeredDriver;

  @Override
  public java.sql.Connection connect(String url, Properties info) throws SQLException {
    Properties defaults = new Properties();

    if (!url.startsWith("jdbc:gitql:")) {
      return null;
    }
    
    return new Connection(url, defaults);
  }

  @Override
  public boolean acceptsURL(String url) throws SQLException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
    return null;
  }

  @Override
  public int getMajorVersion() {
    return 0;
  }

  @Override
  public int getMinorVersion() {
    return 0;
  }

  @Override
  public boolean jdbcCompliant() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return null;
  }

  public static void register() throws SQLException {
    if (isRegistered()) {
      throw new IllegalStateException("Driver is already registered. It can only be registered once.");
    }
    Driver registeredDriver = new Driver();
    DriverManager.registerDriver(registeredDriver);
    Driver.registeredDriver = registeredDriver;
  }

  /**
   * According to JDBC specification, this driver is registered against
   * {@link DriverManager} when the class is loaded. To avoid leaks, this method
   * allow unregistering the driver so that the class can be gc'ed if necessary.
   * @throws IllegalStateException if the driver is not registered
   * @throws SQLException if deregistering the driver fails
   */
  public static void deregister() throws SQLException {
    if (!isRegistered()) {
      throw new IllegalStateException(
          "Driver is not registered (or it has not been registered using Driver.register() method)");
    }
    DriverManager.deregisterDriver(registeredDriver);
    registeredDriver = null;
  }

  /**
   * @return {@code true} if the driver is registered against
   *         {@link DriverManager}
   */
  public static boolean isRegistered() {
    return registeredDriver != null;
  }

}
