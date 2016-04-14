package jgitdbc.metadata;

import gristle.GitRepository;
import gristle.GitRepository.Commit;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import jgitdbc.core.parser.Parser.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;

import org.eclipse.jgit.lib.PersonIdent;

public class Commits extends TableMetaData {
  public static final String TABLE_NAME = "commits";
  
  private static final TableMetaData ALL = new Commits();
  
  public Commits() {
    super(TABLE_NAME);
  }
  
  private static final ColumnMetaData[] COL_DEFS = {
    new ColumnMetaData("author", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("author_email", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("committer", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("committer_email", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("hash", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("message", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("full_message", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("date", Types.TIMESTAMP, java.util.Date.class, ResultSetMetaData.columnNoNulls)
  };
  
  @Override
  public ColumnMetaData[] getAllColumnDefs(){
    return COL_DEFS;
  }
  
  @Override
  public List<ResultRow> getRows(GitRepository repo, Expression expression, List<OrderByElement> orderByElements) throws IOException, SQLException {
    List<ResultRow> temp = new ArrayList<ResultRow>();

    List<Commit> listCommits = repo.listCommits();
    for (Commit commit : listCommits) {
      PersonIdent author = commit.getAuthor();
      PersonIdent committer = commit.getCommitter();
      
      Object[] allVals = new Object[] {
        author.getName(),
        author.getEmailAddress(),
        committer.getName(),
        committer.getEmailAddress(),
        commit.getObjectId().getName(),
        commit.getMessage(),
        commit.getFullMessage(),
        new Date((long)commit.getTime() * 1000)
      };
      temp.add(new ResultRow(ALL, allVals));
    }
    
    temp.sort(new RowComparator(orderByElements));
    return filterRowsAndCols(temp, expression);
  }

}
