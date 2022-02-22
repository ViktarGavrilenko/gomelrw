package databasequeries;

import aquality.selenium.core.logging.Logger;
import models.AnswerGomelRw;
import models.Employee;
import models.People;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static databasequeries.TableColumnNames.*;
import static databasequeries.TableNumberPeopleEnterprises.namepred;
import static databasequeries.TableNumberPeopleEnterprises.number;
import static utils.MySqlUtils.*;
import static utils.StringUtils.convertDateToStr;

public class GomelRwQueries {
    private static final String SELECT_IS_PEOPLE_IN_BASE =
            "SELECT * FROM people where " +
                    "firstname='%s' and name='%s' and middlename='%s' and dt_birthday='%s' and sex='%s'";
    private static final String INSERT_PEOPLE_IN_BASE =
            "INSERT INTO people (firstname, name, middlename, dt_birthday, sex) VALUES ('%s','%s','%s','%s','%s')";

    private static final String SELECT_IS_POST_IN_BASE = "SELECT * FROM post where namepost='%s'";
    private static final String INSERT_POST_IN_BASE = "INSERT INTO post (namepost) VALUES ('%s')";

    private static final String SELECT_IS_PRED_IN_BASE = "SELECT * FROM pred where namepred='%s'";
    private static final String INSERT_PRED_IN_BASE = "INSERT INTO pred (namepred) VALUES ('%s')";

    private static final String SELECT_IS_DIVISION_IN_BASE = "SELECT * FROM division where divisionname='%s'";
    private static final String INSERT_DIVISION_IN_BASE = "INSERT INTO division (divisionname) VALUES ('%s')";

    private static final String SELECT_IS_TABNUM_IN_BASE = "SELECT * FROM tabnum where tabnum='%s'";
    private static final String INSERT_TABNUM_IN_BASE = "INSERT INTO tabnum (tabnum) VALUES ('%s')";

    private static final String SELECT_IS_RECORD_IN_BASE = "SELECT * FROM data_gomelrw where " +
            "id_people='%s' and id_pred='%s' and id_division='%s' and id_post='%s' and id_tabnum='%s'";
    private static final String INSERT_RECORD_IN_BASE = "insert into data_gomelrw " +
            "(id_people, id_pred, id_division, id_post, id_tabnum) VALUES  (%s, %s, %s, %s, %s)";

    private static final String INSERT_IN_GOMELRW_OLD =
            "insert into data_gomelrw_old (id_people, id_pred, id_division, id_post, " +
                    "id_tabnum, work_tel, e_mail, datasaveinbase ) VALUES  (%s, %s, %s, %s, %s, '%s', '%s', '%s')";
    private static final String SELECT_PEOPLE_ID = "SELECT * FROM data_gomelrw where id_people='%s'";
    private static final String DELETE_FROM_GOMELRW = "DELETE FROM data_gomelrw WHERE id_people=%s";
    private static final String UPDATE_IN_GOMELRW = "UPDATE data_gomelrw SET datasaveinbase=NOW() WHERE id=%s";

    private static final String NUMBER_SCANNED_PEOPLE_TODAY =
            "SELECT count(*) FROM data_gomelrw WHERE date_format(datasaveinbase, '%Y-%m-%d')=CURDATE()";
    private static final String TOTAL_ENTRIES = "SELECT count(*) FROM data_gomelrw";

    private static final String NUMBER_NEW_EMPLOYEE = "SELECT count(*) FROM data_gomelrw dg, people p WHERE  " +
            "dg.id_people=p.id and date_format(p.datawriteinbase, '%Y-%m-%d')=CURDATE()";

    private static final String LIST_NEW_EMPLOYEE = "SELECT firstname, name, middlename, dt_birthday, sex, namepost, " +
            "divisionname, tabnum, namepred, work_tel, e_mail, dg.datasaveinbase FROM people p, data_gomelrw dg, " +
            "post, division d, tabnum, pred where dg.id_people=p.id and dg.id_post=post.id and " +
            "dg.id_division=d.id and dg.id_pred=pred.id and dg.id_tabnum=tabnum.id and " +
            "date_format(p.datawriteinbase, '%Y-%m-%d')=CURDATE() order by namepred, firstname, name";

    private static final String DISMISS_EMPLOYEE = "SELECT firstname, name, middlename, dt_birthday, sex, namepost, " +
            "divisionname, tabnum, namepred, work_tel, e_mail, dg.datasaveinbase FROM people p, data_gomelrw dg, " +
            "post, division d, tabnum, pred where dg.id_people=p.id and dg.id_post=post.id and dg.id_division=d.id " +
            "and dg.id_pred=pred.id and dg.id_tabnum=tabnum.id and " +
            "date_format(datasaveinbase, '%Y-%m-%d')!=CURDATE() order by namepred, firstname, name";

    private static final String SELECT_NOT_RELEVANT_TODAY = "SELECT * FROM data_gomelrw WHERE datasaveinbase < CURDATE()";
    private static final String INSERT_ENTRY_IN_GOMELRW_FORMER = "insert into data_gomelrw_former " +
            "(id_people, id_pred, id_division, id_post, id_tabnum, work_tel, e_mail, datasaveinbase) VALUES  " +
            "(%s, %s, %s, %s, %s, '%s', '%s', '%s')";

    private static final String SELECT_NUMBER_PEOPLE_ENTERPRISES = "SELECT count(*) as number, id_pred, namepred  " +
            "FROM pred, data_gomelrw WHERE pred.id = data_gomelrw.id_pred group by id_pred order by number DESC";
    private static final String INSERT_NUMBER_PEOPLE_ENTERPRISES = "insert into number_people_enterprises " +
            "(id_pred, number_people) VALUES (%s, %s)";

    public static int getIdPeopleInBase(People people) {
        String selectQuery = String.format(SELECT_IS_PEOPLE_IN_BASE,
                people.f, people.i, people.o, convertDateToStr(people.dt_birthday), people.sex);
        String insertQuery = String.format(INSERT_PEOPLE_IN_BASE,
                people.f, people.i, people.o, convertDateToStr(people.dt_birthday), people.sex);
        return getIdAndAddIfNot(insertQuery, selectQuery);
    }

    public static int getIdPostInBase(String postName) {
        String selectQuery = String.format(SELECT_IS_POST_IN_BASE, postName);
        String insertQuery = String.format(INSERT_POST_IN_BASE, postName);
        return getIdAndAddIfNot(insertQuery, selectQuery);
    }

    public static int getIdPredInBase(String predName) {
        String selectQuery = String.format(SELECT_IS_PRED_IN_BASE, predName);
        String insertQuery = String.format(INSERT_PRED_IN_BASE, predName);
        return getIdAndAddIfNot(insertQuery, selectQuery);
    }

    public static int getIdDivisionInBase(String divisionName) {
        String selectQuery = String.format(SELECT_IS_DIVISION_IN_BASE, divisionName);
        String insertQuery = String.format(INSERT_DIVISION_IN_BASE, divisionName);
        return getIdAndAddIfNot(insertQuery, selectQuery);
    }

    public static int getIdTabNumInBase(String tabNum) {
        String selectQuery = String.format(SELECT_IS_TABNUM_IN_BASE, tabNum);
        String insertQuery = String.format(INSERT_TABNUM_IN_BASE, tabNum);
        return getIdAndAddIfNot(insertQuery, selectQuery);
    }

    public static void addRecordInBase(Employee employee) {
        String selectQuery = String.format(SELECT_IS_RECORD_IN_BASE,
                employee.idPeople, employee.idPred, employee.idDivision, employee.idPost, employee.idTabNum);
        String insertQuery = String.format(INSERT_RECORD_IN_BASE,
                employee.idPeople, employee.idPred, employee.idDivision, employee.idPost, employee.idTabNum);

        ResultSet resultSet = sendSelectQuery(selectQuery);
        try {
            if (!resultSet.next()) {
                resultSet = sendSelectQuery(
                        String.format(SELECT_PEOPLE_ID, employee.idPeople));
                if (resultSet.next()) {
                    sendSqlQuery(String.format(INSERT_IN_GOMELRW_OLD,
                            resultSet.getInt(id_people.toString()),
                            resultSet.getInt(id_pred.toString()),
                            resultSet.getInt(id_division.toString()),
                            resultSet.getInt(id_post.toString()),
                            resultSet.getInt(id_tabnum.toString()),
                            resultSet.getString(work_tel.toString()),
                            resultSet.getString(e_mail.toString()),
                            resultSet.getString(datasaveinbase.toString())
                    ));
                    sendSqlQuery(String.format(DELETE_FROM_GOMELRW, employee.idPeople));
                }
                sendSqlQuery(insertQuery);
            } else {
                sendSqlQuery(String.format(UPDATE_IN_GOMELRW, resultSet.getInt(id.toString())));
            }
        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
    }

    public static int getCountScanPeopleToday() {
        return getFirstColumn(NUMBER_SCANNED_PEOPLE_TODAY);
    }

    public static int getTotalEntries() {
        return getFirstColumn(TOTAL_ENTRIES);
    }

    public static int getNumberNewEmployee() {
        return getFirstColumn(NUMBER_NEW_EMPLOYEE);
    }

    public static List<AnswerGomelRw> getListNewEmployee() {
        return getListEmployee(LIST_NEW_EMPLOYEE);
    }

    public static List<AnswerGomelRw> getListDismissEmployee() {
        return getListEmployee(DISMISS_EMPLOYEE);
    }

    public static void moveAndDeleteData() {
        ResultSet resultSet = sendSelectQuery(SELECT_NOT_RELEVANT_TODAY);
        try {
            while (resultSet.next()) {
                sendSqlQuery(String.format(INSERT_ENTRY_IN_GOMELRW_FORMER,
                        resultSet.getInt(id_people.toString()),
                        resultSet.getInt(id_pred.toString()),
                        resultSet.getInt(id_division.toString()),
                        resultSet.getInt(id_post.toString()),
                        resultSet.getInt(id_tabnum.toString()),
                        resultSet.getString(work_tel.toString()),
                        resultSet.getString(e_mail.toString()),
                        resultSet.getString(datasaveinbase.toString())
                ));
                sendSqlQuery(String.format(DELETE_FROM_GOMELRW, resultSet.getInt(id_people.toString())));
            }

        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
    }

    public static void addInBaseNumberEmployeeEnterprises() {
        ResultSet resultSet = sendSelectQuery(SELECT_NUMBER_PEOPLE_ENTERPRISES);
        try {
            while (resultSet.next()) {
                sendSqlQuery(String.format(INSERT_NUMBER_PEOPLE_ENTERPRISES,
                        resultSet.getInt(TableNumberPeopleEnterprises.id_pred.toString()),
                        resultSet.getInt(number.toString())));
                Logger.getInstance().info(String.format("%s - %s",
                        resultSet.getInt(number.toString()), resultSet.getString(namepred.toString())));
            }

        } catch (SQLException e) {
            Logger.getInstance().error(SQL_QUERY_FAILED + e);
            throw new IllegalArgumentException(SQL_QUERY_FAILED, e);
        }
    }
}