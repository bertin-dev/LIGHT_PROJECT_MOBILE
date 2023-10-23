package com.adsyst.light_project_mobile.web_service;

import java.util.List;
import java.util.Map;

public class ApiError {

    String message;
    Map<String, List<String>> errors;

    public String getMessage() {
        return message;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
