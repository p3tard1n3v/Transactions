package com.merchant.transactions.service.impl;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.dto.UserDto;
import com.merchant.transactions.model.enums.MerchantStatus;
import com.merchant.transactions.model.enums.UserRole;
import com.merchant.transactions.service.MerchantService;
import com.merchant.transactions.service.RakeTaskService;
import com.merchant.transactions.service.UserService;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public void importMerchants(CSVParser csvParser) {
        Consumer<CSVRecord> populateAndSave = (csvRecord) -> {
            if (UserRole.ADMIN.equals(getUserRole(csvRecord))) {
                saveAdminUser(csvRecord);
            } else {
                saveMerchant(csvRecord);
            }
        };

        parseCSV(csvParser, populateAndSave);
    }

    private static UserRole getUserRole(CSVRecord csvRecord) {
        return csvRecord.get("role").toUpperCase().equals(UserRole.ADMIN.name())
                ? UserRole.ADMIN : UserRole.USER;
    }

    private void saveMerchant(CSVRecord csvRecord) {
        MerchantStatus status = csvRecord.get("status").toUpperCase().equals(MerchantStatus.INACTIVE.name())
                ? MerchantStatus.INACTIVE : MerchantStatus.ACTIVE;

        MerchantDto merchantDto = MerchantDto.builder()
                .name(csvRecord.get("name"))
                .password(csvRecord.get("password"))
                .description(csvRecord.get("description"))
                .email(csvRecord.get("email"))
                .status(status)
                .totalTransactionSum(new BigDecimal(csvRecord.get("total_transaction_sum")))
                .build();

        merchantService.save(merchantDto);
    }

    private void saveAdminUser(CSVRecord csvRecord) {
        UserDto userDto = UserDto.builder()
                .name(csvRecord.get("name"))
                .password(csvRecord.get("password"))
                .build();

        userService.save(userDto);
    }

    private void parseCSV(CSVParser csvParser, Consumer<CSVRecord> populateAndSave) {
        for (CSVRecord csvRecord : csvParser.getRecords()) {
            populateAndSave.accept(csvRecord);
        }
    }
}
