import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import models.AnswerGomelRw;
import models.Employee;
import models.People;
import org.apache.hc.core5.http.HttpStatus;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static apiresponses.ProcessingApiResponses.getUserGomelRw;
import static databasequeries.GomelRwQueries.*;
import static utils.MySqlUtils.closeConnection;
import static utils.StringUtils.*;

public class TestGomelRw {
    private static final ISettingsFile TEST_DATA_FILE = new JsonSettingsFile("testData.json");
    private static final String DATE_FROM = TEST_DATA_FILE.getValue("/dateFrom").toString();
    private static final String DATE_TILL = TEST_DATA_FILE.getValue("/dateTill").toString();

    public List<LocalDate> getDatesFromInterval(LocalDate dateFrom, LocalDate dateTill) {
        return dateFrom.datesUntil(dateTill.plusDays(1))
                .collect(Collectors.toList());
    }

    @BeforeTest
    public void beforeTest() {
        closeConnection();
    }

    @Test(description = "Testing get gomel.rw")
    public void testGomelRw() {
        Logger.getInstance().info("------------Start Scan--------------");
        List<LocalDate> dates = getDatesFromInterval(convertStrToLocalDate(DATE_FROM), convertStrToLocalDate(DATE_TILL));
        for (LocalDate day : dates) {
            List<AnswerGomelRw> usersList = getUserGomelRw(HttpStatus.SC_OK, convertLocalDateToStr(day));
            for (AnswerGomelRw answer : usersList) {
                Employee employee = new Employee();
                People people = new People(fistUpCase(answer.f), fistUpCase(answer.i), fistUpCase(answer.o),
                        convertStrToDate(answer.dt_birthday), answer.sex.toUpperCase());
                employee.idPeople = getIdPeopleInBase(people);
                employee.idPost = getIdPostInBase(answer.namepost);
                employee.idPred = getIdPredInBase(answer.namepred);
                employee.idDivision = getIdDivisionInBase(answer.divisionname);
                employee.idTabNum = getIdTabNumInBase(answer.tabnum);
                addRecordInBase(employee);
            }
            Logger.getInstance().info(String.format("The number of people on %s is %s", day, usersList.size()));
        }
        Logger.getInstance().info("------------Poll completed--------------");
        int countScanPeopleToday = getCountScanPeopleToday();
        int totalEntries = getTotalEntries();
        Logger.getInstance().info(String.format("%s records were processed today", countScanPeopleToday));
        Logger.getInstance().info(String.format("Total %s entries in the database", totalEntries));
        Logger.getInstance().info(String.format("Number of new employees %s", getNumberNewEmployee()));

        int count = 0;
        Logger.getInstance().info("New employees:");
        for (AnswerGomelRw employees : getListNewEmployee()) {
            Logger.getInstance().info(String.format("%s. %s %s %s | %s | sex %s | %s | %s | %s |",
                    ++count, employees.f, employees.i, employees.o, convertStrToStr(employees.dt_birthday), employees.sex,
                    employees.namepred, employees.divisionname, employees.namepost));
        }
        count = 0;
        Logger.getInstance().info(String.format("Number of dismiss employees %s", totalEntries - countScanPeopleToday));
        Logger.getInstance().info("Dismiss employees:");
        for (AnswerGomelRw employees : getListDismissEmployee()) {
            Logger.getInstance().info(String.format("%s. %s %s %s | %s | sex %s | %s | %s | %s |",
                    ++count, employees.f, employees.i, employees.o, convertStrToStr(employees.dt_birthday), employees.sex,
                    employees.namepred, employees.divisionname, employees.namepost));
        }
        Logger.getInstance().info("Moved and deleted irrelevant data");
        moveAndDeleteData();
        Logger.getInstance().info("Number of employees in enterprises");
        addInBaseNumberEmployeeEnterprises();
        Logger.getInstance().info("------------Finish Scan--------------");
    }

    @AfterTest
    public void afterTest() {
        closeConnection();
    }
}