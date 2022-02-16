package apiresponses;

import aquality.selenium.core.logging.Logger;
import aquality.selenium.core.utilities.ISettingsFile;
import aquality.selenium.core.utilities.JsonSettingsFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.AnswerGomelRw;

import java.net.http.HttpResponse;
import java.util.List;

import static utils.APIUtils.sendGet;
import static utils.JsonUtils.isStatusCodeCorrect;

public class ProcessingApiResponses {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ISettingsFile CONFIG_FILE = new JsonSettingsFile("configData.json");
    private static final String MAIN_URL = CONFIG_FILE.getValue("/mainPage").toString();
    private static final String NOT_DESERIALIZE = "Could not deserialize: ";

    public static List<AnswerGomelRw> getUserGomelRw(int statusCode, String date) {
        HttpResponse<String> response = sendGet(String.format(MAIN_URL, date));
        isStatusCodeCorrect(statusCode, response.statusCode());
        try {
            return MAPPER.readValue(response.body(), new TypeReference<List<AnswerGomelRw>>() {
            });
        } catch (JsonProcessingException e) {
            Logger.getInstance().error(NOT_DESERIALIZE + e);
            throw new IllegalArgumentException(NOT_DESERIALIZE, e);
        }
    }
}