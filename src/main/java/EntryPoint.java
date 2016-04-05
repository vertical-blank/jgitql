import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


public class EntryPoint {
  
  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    
    Class.forName("jgitdbc.Driver");
    
    Connection con = DriverManager.getConnection("jdbc:jgitql:C:\\Users\\P000163\\turqey_gh\\.git");
    
    Statement statement = con.createStatement();
    
    ResultSet resultSet = statement.executeQuery("SELECT hash FROM branches limit 3");
    
    // とりあえずbranches決め打ち
    
    ResultSetMetaData metaData = resultSet.getMetaData();
    
    int columnCount = metaData.getColumnCount();
    for (int i = 0; i < columnCount; i++) {
      System.out.println(metaData.getColumnName(i));
    }
    
    while (resultSet.next()){
      for (int i = 0; i < columnCount; i++) {
        System.out.println(resultSet.getString(i));  
      }
    }
    
  }
  
}
