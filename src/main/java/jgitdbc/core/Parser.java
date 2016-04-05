package jgitdbc.core;

import gristle.GitRepository;
import gristle.GitRepository.Branch;
import gristle.GitRepository.Commit;
import gristle.GitRepository.Tag;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgitdbc.metadata.BaseMetaData;
import jgitdbc.metadata.Branches;
import jgitdbc.metadata.Commits;
import jgitdbc.metadata.ResultRow;
import jgitdbc.metadata.Tags;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

import org.eclipse.jgit.lib.PersonIdent;

public class Parser {

  private Statement statement;
  private String sql;

  Parser(Statement statement, String sql) throws SQLException {
    this.statement = statement;
    this.sql = sql;
  }

  public java.sql.ResultSet getResultSet() throws SQLException {
    final GitRepository repo = this.statement.getConnection().getRepo();

    try {
      Select selectStatement = (Select) CCJSqlParserUtil.parse(sql);
      PlainSelect select = (PlainSelect) selectStatement.getSelectBody();

      FromItem fromItem = select.getFromItem();
      /*
      List<SelectItem> selectItems = select.getSelectItems();
      Expression where = select.getWhere();
      List<OrderByElement> orderByElements = select.getOrderByElements();
      */
      
      List<SelectItem> selectItems = select.getSelectItems();
      for (SelectItem selectItem : selectItems) {
        System.out.println(selectItem.toString());
      }

      String tableName = fromItem.toString().toLowerCase();
      
      ListFunction listFunction = listFuncs.get(tableName);
      
      List<ResultRow> rows = listFunction.getRows(repo, select);

      Limit limit = select.getLimit();
      if (limit != null){
        long rowCount = limit.getRowCount();
        if (rows.size() >= rowCount){
          rows = rows.subList(0, (int) rowCount);
        }
      }
      
      return new ResultSet(statement, listFunction.getMetaData(), rows);

    } catch (Exception e) {
      throw new SQLException(e);
    }
  }

  private interface ListFunction {
    List<ResultRow> getRows(GitRepository repo, PlainSelect select) throws IOException;
    BaseMetaData getMetaData();
  }

  private static final Map<String, ListFunction> listFuncs = new HashMap<String, ListFunction>();
  static {
    listFuncs.put("branches", new ListFunction() {
      @Override
      public BaseMetaData getMetaData() { return Branches.INSTANCE; }
      @Override
      public List<ResultRow> getRows(GitRepository repo, PlainSelect select) throws IOException {
        List<ResultRow> rows = new ArrayList<ResultRow>();

        List<Branch> listBranches = repo.listBranches();
        for (Branch branch : listBranches) {
          rows.add(Branches.createRow(branch.name, "refs/tags/" + branch.name, branch.head().getObjectId().getName()));
        }

        return rows;
      }
    });
    listFuncs.put("tags", new ListFunction() {
      @Override
      public BaseMetaData getMetaData() { return Tags.INSTANCE; }
      @Override
      public List<ResultRow> getRows(GitRepository repo, PlainSelect select) throws IOException {
        List<ResultRow> rows = new ArrayList<ResultRow>();

        List<Tag> listTags = repo.listTags();
        for (Tag tag : listTags) {
          rows.add(Tags.createRow(tag.name, "refs/tags/" + tag.name, tag.getCommit().getObjectId().getName()));
        }

        return rows;
      }
    });
    listFuncs.put("commits", new ListFunction() {
      @Override
      public BaseMetaData getMetaData() { return Commits.INSTANCE; }
      @Override
      public List<ResultRow> getRows(GitRepository repo, PlainSelect select) throws IOException {
        List<ResultRow> rows = new ArrayList<ResultRow>();

        List<Commit> listCommits = repo.listCommits();
        for (Commit commit : listCommits) {
          PersonIdent author = commit.getAuthor();
          PersonIdent committer = commit.getCommitter();
          rows.add(Commits.createRow(author.getName(), author.getEmailAddress(), committer.getName(),
              committer.getEmailAddress(), commit.getObjectId().getName(), commit.getMessage(), commit.getFullMessage()));
        }

        return rows;
      }
    });
  }
}
