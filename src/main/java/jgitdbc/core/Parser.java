package jgitdbc.core;

import gristle.GitRepository;

import java.sql.SQLException;
import java.util.List;

import jgitdbc.metadata.ResultRow;
import jgitdbc.metadata.SimpleSelectMetaData;
import jgitdbc.metadata.Tables;
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
      String tableName = fromItem.toString().toLowerCase();
      
      List<SelectItem> selectItems = select.getSelectItems();
      
      SimpleSelectMetaData metaData = Tables.get(tableName, selectItems);
      
      /*
      List<SelectItem> selectItems = select.getSelectItems();
      Expression where = select.getWhere();
      List<OrderByElement> orderByElements = select.getOrderByElements();
      */
      
      //select.getWhere().accept(new ExpressionVisitor() {});
      
      List<ResultRow> rows = metaData.getRows(repo);
      
      WhereExpressionVisiter whereExpressionVisiter = new WhereExpressionVisiter();
      if (select.getWhere() != null){
        select.getWhere().accept(whereExpressionVisiter);
        Expression expression = whereExpressionVisiter.getExpression();
        for (ResultRow resultRow : rows) {
          expression.eval(resultRow);
        }
      }

      Limit limit = select.getLimit();
      if (limit != null){
        long rowCount = limit.getRowCount();
        if (rows.size() >= rowCount){
          rows = rows.subList(0, (int) rowCount);
        }
      }
      
      return new ResultSet(statement, metaData, rows);

    } catch (Exception e) {
      throw new SQLException(e);
    }
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

