package jgitdbc.metadata;

import gristle.GitRepository;
import gristle.GitRepository.Commit;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.PersonIdent;

public class Commits extends TableMetaData {
  public static final String TABLE_NAME = "commits";
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
  public List<ResultRow> getRows(GitRepository repo) throws IOException {
    List<ResultRow> rows = new ArrayList<ResultRow>();

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
      rows.add(new ResultRow(this, filterColumns(allVals)));
    }

    return rows;
  }

}
