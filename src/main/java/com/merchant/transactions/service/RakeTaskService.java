package com.merchant.transactions.service;

public interface RakeTaskService {
    void importMerchants(String fileName);
    void importUsers(String fileName);
}
