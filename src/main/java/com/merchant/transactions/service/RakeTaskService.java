package com.merchant.transactions.service;

import org.apache.commons.csv.CSVParser;

public interface RakeTaskService {
    void importMerchants(CSVParser csvParser);
}
