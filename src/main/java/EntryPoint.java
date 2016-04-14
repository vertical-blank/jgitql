import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.jsqlparser.JSQLParserException;

public class EntryPoint {

  public static void main(String[] args) throws SQLException, ClassNotFoundException, JSQLParserException {

    Class.forName("jgitdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:jgitql:C:\\Users\\P000163\\turqey_gh\\.git");
    Statement statement = con.createStatement();
    ResultSet resultSet = statement.executeQuery("SELECT * FROM commits");
    ResultSetMetaData metaData = resultSet.getMetaData();
    
    int columnCount = metaData.getColumnCount();
    for (int i = 0; i < columnCount; i++) {
      System.out.print(metaData.getColumnName(i + 1) + "\t");
    }
    
    System.out.println();
    
    while (resultSet.next()) {
      for (int i = 0; i < columnCount; i++) {
        System.out.print(resultSet.getString(i + 1) + "\t");
      }
      System.out.println();
    }
    
  }

}
