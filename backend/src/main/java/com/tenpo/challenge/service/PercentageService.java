package com.tenpo.challenge.service;

public interface PercentageService {

    Double getPercentage();

    Double fetchFromExternalService();

    void saveBackupPercentage(Double percentage);
}
