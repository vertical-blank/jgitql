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
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
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
      
      //select.getWhere().accept(new ExpressionVisitor() {});
      
      List<SelectItem> selectItems = select.getSelectItems();
      for (SelectItem selectItem : selectItems) {
        System.out.println(selectItem.toString());
      }
      
      WhereExpressionVisiter whereExpressionVisiter = new WhereExpressionVisiter();
      select.getWhere().accept(whereExpressionVisiter);
      Expression expression = whereExpressionVisiter.getExpression();

      String tableName = fromItem.toString().toLowerCase();
      
      ListFunction listFunction = listFuncs.get(tableName);
      
      List<ResultRow> rows = listFunction.getRows(repo, select);
      
      for (ResultRow resultRow : rows) {
        expression.eval(resultRow);
      }

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
          rows.add(Branches.createRow(
            branch.name,
            "refs/tags/" + branch.name,
            branch.head().getObjectId().getName())
          );
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
          rows.add(Tags.createRow(
            tag.name,
            "refs/tags/" + tag.name,
            tag.getCommit().getObjectId().getName())
          );
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
    });
  }
  
  
  interface Expression {
    boolean eval(ResultRow row);
  }

  enum CompareOperator {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    GREATER_EQUALS,
    LESS_THAN,
    LESS_EQUALS,
  }
  enum Operator {
    AND,
    OR,
  }

  class ColumnExpression implements Expression {
    Column column = null;
    CompareOperator opr = null;
    String stringValue = null;
    Long longValue = null;

    ColumnExpression(Column col, CompareOperator opr, String value){
      this.column = col;
      this.opr = opr;
      this.stringValue = value;
    }
    ColumnExpression(Column col, CompareOperator opr, long value){
      this.column = col;
      this.opr = opr;
      this.longValue = Long.valueOf(value);
    }
    
    public boolean eval(ResultRow row) {
      switch (this.opr) {
      case EQUALS:
        //return row.getString(columnNumber)
      case NOT_EQUALS:
        //return ;
      default:
      }
      return false;
    }
    
    public String toString() {
      return String.format("%s %s %s", column, opr, this.stringValue != null ? this.stringValue: this.longValue);
    }
  }
  
  class ExpressionPair implements Expression {
    Expression left;
    Operator opr;
    Expression right;

    ExpressionPair(Expression left, Operator opr, Expression right) {
      this.left = left;
      this.opr = opr;
      this.right = right;
    }
    public boolean eval(ResultRow row) {
      return true;
    }
  }
  
  class WhereExpressionVisiter extends ExpressionVisitorAdapter {
    Column column = null;
    StringValue stringValue = null;
    LongValue longValue = null;

    CompareOperator cmpOpr = null;
    Operator opr = null;

    Object getValue() {
      if (this.stringValue != null) return this.stringValue;
      if (this.longValue   != null) return this.longValue;
      return null;
    }

    Expression getExpression() {
      if (this.column != null){
        if (this.longValue != null){
          return new ColumnExpression(this.column, this.cmpOpr, this.longValue.getValue());
        }
        else {
          return new ColumnExpression(this.column, this.cmpOpr, this.stringValue.getValue());
        }
      } else {
        return null;//new ExpressionPair();
      }
    }

    public void visit(Column column) {
      this.column = column;
    }
    public void visit(AndExpression ex) {
      ex.getLeftExpression().accept(new WhereExpressionVisiter());
      ex.getRightExpression().accept(new WhereExpressionVisiter());
    }
    public void visit(OrExpression ex) {
      ex.getLeftExpression().accept(new WhereExpressionVisiter());
      ex.getRightExpression().accept(new WhereExpressionVisiter());
    }

    public void visit(Parenthesis ex) {
      ex.getExpression().accept(new WhereExpressionVisiter());
    }

    public void visit(StringValue stringValue) {
      this.stringValue = stringValue;
    }
    public void visit(LongValue longValue) {
      this.longValue = longValue;
    }
    
    // compare expressions
    public void visit(EqualsTo ex) {
      WhereExpressionVisiter left  = new WhereExpressionVisiter();
      WhereExpressionVisiter right = new WhereExpressionVisiter();
      ex.getLeftExpression().accept(left);
      ex.getRightExpression().accept(right);

      this.column = left.column;
      this.stringValue = right.stringValue;
      this.longValue = right.longValue;
      this.cmpOpr = CompareOperator.EQUALS;
    }
    public void visit(NotEqualsTo ex) {
      WhereExpressionVisiter left  = new WhereExpressionVisiter();
      WhereExpressionVisiter right = new WhereExpressionVisiter();
      ex.getLeftExpression().accept(left);
      ex.getRightExpression().accept(right);

      this.column = left.column;
      this.stringValue = right.stringValue;
      this.longValue = right.longValue;
      this.cmpOpr = CompareOperator.NOT_EQUALS;
    }
    public void visit(MinorThanEquals ex) {
      WhereExpressionVisiter left  = new WhereExpressionVisiter();
      WhereExpressionVisiter right = new WhereExpressionVisiter();
      ex.getLeftExpression().accept(left);
      ex.getRightExpression().accept(right);

      this.column = left.column;
      this.stringValue = right.stringValue;
      this.longValue = right.longValue;
      this.cmpOpr = CompareOperator.LESS_THAN;
    }
    public void visit(MinorThan ex) {
      WhereExpressionVisiter left  = new WhereExpressionVisiter();
      WhereExpressionVisiter right = new WhereExpressionVisiter();
      ex.getLeftExpression().accept(left);
      ex.getRightExpression().accept(right);

      this.column = left.column;
      this.stringValue = right.stringValue;
      this.longValue = right.longValue;
      this.cmpOpr = CompareOperator.LESS_THAN;
    }
    public void visit(GreaterThanEquals ex) {
      WhereExpressionVisiter left  = new WhereExpressionVisiter();
      WhereExpressionVisiter right = new WhereExpressionVisiter();
      ex.getLeftExpression().accept(left);
      ex.getRightExpression().accept(right);

      this.column = left.column;
      this.stringValue = right.stringValue;
      this.longValue = right.longValue;
      this.cmpOpr = CompareOperator.GREATER_EQUALS;
    }
    public void visit(GreaterThan ex) {
      WhereExpressionVisiter left  = new WhereExpressionVisiter();
      WhereExpressionVisiter right = new WhereExpressionVisiter();
      ex.getLeftExpression().accept(left);
      ex.getRightExpression().accept(right);

      this.column = left.column;
      this.stringValue = right.stringValue;
      this.longValue = right.longValue;
      this.cmpOpr = CompareOperator.GREATER_THAN;
    }
  }


}

