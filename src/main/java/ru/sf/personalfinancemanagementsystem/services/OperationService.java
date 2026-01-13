package ru.sf.personalfinancemanagementsystem.services;

import ru.sf.personalfinancemanagementsystem.domains.GeneralReport;
import ru.sf.personalfinancemanagementsystem.domains.OperationDataForCreate;
import ru.sf.personalfinancemanagementsystem.domains.SavedOperation;

import java.util.UUID;


public interface OperationService {

    SavedOperation createOperation(UUID userId, OperationDataForCreate data);
    GeneralReport getGeneralReport(UUID userId);

}
