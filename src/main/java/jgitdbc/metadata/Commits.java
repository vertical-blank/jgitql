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
  private Commits() {
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
  public ColumnMetaData[] getAllColumnDefsImpl(){
    return COL_DEFS;
  }
  
  @Override
  public List<ResultRow> getRows(GitRepository repo) throws IOException {
    List<ResultRow> rows = new ArrayList<ResultRow>();

    List<Commit> listCommits = repo.listCommits();
    for (Commit commit : listCommits) {
      PersonIdent author = commit.getAuthor();
      PersonIdent committer = commit.getCommitter();
      rows.add(Commits.createRow(
        author.getName(),
        author.getEmailAddress(),
        committer.getName(),
        committer.getEmailAddress(),
        commit.getObjectId().getName(),
        commit.getMessage(),
        commit.getFullMessage(),
        (long)commit.getTime() * 1000)
      );
    }

    return rows;
  }
  
  public static ResultRow createRow(
      String author, 
      String author_email, 
      String committer, 
      String committer_email, 
      String hash, 
      String message, 
      String full_message,
      long time){
    return new ResultRow(
      author,
      author_email,
      committer,
      committer_email,
      hash,
      message,
      full_message,
      new Date(time)
    );
  }
  
}
