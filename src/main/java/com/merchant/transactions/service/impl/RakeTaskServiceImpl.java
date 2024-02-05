package com.merchant.transactions.service.impl;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.dto.UserDto;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.model.enums.UserRole;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.RakeTaskService;
import com.merchant.transactions.service.UserService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

@Service
public class RakeTaskServiceImpl implements RakeTaskService {
    private final UserService userService;
    private final MerchantService merchantService;

    public RakeTaskServiceImpl (final UserService userService, final MerchantService merchantService) {
        this.userService = userService;
        this.merchantService = merchantService;
    }
    @Override
    public void importMerchants(String fileName) {
        Consumer<CSVRecord> populateAndSave = (csvRecord) -> {
            MerchantStatus status = csvRecord.get("status").toUpperCase().equals(MerchantStatus.INACTIVE.name())
                    ? MerchantStatus.INACTIVE : MerchantStatus.ACTIVE;

            MerchantDto merchantDto = MerchantDto.builder()
                    .name(csvRecord.get("name"))
                    .description(csvRecord.get("description"))
                    .email(csvRecord.get("email"))
                    .status(status)
                    .totalTransactionSum(new BigDecimal(csvRecord.get("total_transaction_sum")))
                    .user(userService.findByUsername(csvRecord.get("username")))
                    .build();

            merchantService.save(merchantDto);
        };

        parseCSV(fileName, populateAndSave);
    }

    @Override
    public void importUsers(String fileName) {
        Consumer<CSVRecord> populateAndSave = (csvRecord) -> {
            UserRole role = csvRecord.get("role").toUpperCase().equals(UserRole.ADMIN.name())
                    ? UserRole.ADMIN : UserRole.USER;
            UserDto userDto = UserDto.builder()
                    .username(csvRecord.get("username"))
                    .password(csvRecord.get("password"))
                    .role(role).build();

            userService.save(userDto);
        };

        parseCSV(fileName, populateAndSave);
    }

    private void parseCSV(String fileName, Consumer<CSVRecord> populateAndSave) {
        try (CSVParser csvParser = new CSVParser(Files.newBufferedReader(Paths.get(fileName)),
                CSVFormat.RFC4180.withFirstRecordAsHeader())) {
            for (CSVRecord csvRecord : csvParser) {
                populateAndSave.accept(csvRecord);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
