package com.merchant.transactions.service;

import com.merchant.transactions.dto.MerchantDto;
import com.merchant.transactions.dto.UserDto;
import com.merchant.transactions.service.impl.RakeTaskServiceImpl;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RakeTaskServiceTests {
    @Mock
    private UserService userService;
    @Mock
    private MerchantService merchantService;
    @InjectMocks
    RakeTaskServiceImpl rakeTaskService;
    @Captor
    ArgumentCaptor<MerchantDto> merchantDtoCaptor;
    @Captor
    ArgumentCaptor<UserDto> userDtoArgumentCaptor;

    @Test
    public void testImportMerchants() {
        CSVParser csvParser = mock(CSVParser.class);

        CSVRecord csvRecordMerchant = mock(CSVRecord.class);
        lenient().when(csvRecordMerchant.get("name")).thenReturn("Bagira");
        lenient().when(csvRecordMerchant.get("role")).thenReturn("user");
        lenient().when(csvRecordMerchant.get("password")).thenReturn("BagiraPass");
        lenient().when(csvRecordMerchant.get("description")).thenReturn("Bagira description");
        lenient().when(csvRecordMerchant.get("email")).thenReturn("bagira@bagira.bg");
        lenient().when(csvRecordMerchant.get("status")).thenReturn("active");
        lenient().when(csvRecordMerchant.get("total_transaction_sum")).thenReturn("0");

        CSVRecord csvRecordAdminUser = mock(CSVRecord.class);
        lenient().when(csvRecordAdminUser.get("name")).thenReturn("testAdmin");
        lenient().when(csvRecordAdminUser.get("password")).thenReturn("adminPass");
        lenient().when(csvRecordAdminUser.get("role")).thenReturn("admin");
        lenient().when(csvParser.getRecords()).thenReturn(List.of(csvRecordMerchant, csvRecordAdminUser));

        rakeTaskService.importMerchants(csvParser);

        verify(merchantService).save(merchantDtoCaptor.capture());
        MerchantDto merchantDto = merchantDtoCaptor.getValue();
        assertThat(csvRecordMerchant.get("name")).isEqualTo(merchantDto.getName());
        assertThat(csvRecordMerchant.get("password")).isEqualTo(merchantDto.getPassword());
        assertThat(csvRecordMerchant.get("description")).isEqualTo(merchantDto.getDescription());
        assertThat(csvRecordMerchant.get("email")).isEqualTo(merchantDto.getEmail());
        assertThat(csvRecordMerchant.get("status")).isEqualTo(merchantDto.getStatus().toString().toLowerCase());
        assertThat(new BigDecimal(csvRecordMerchant.get("total_transaction_sum"))).isEqualTo(merchantDto.getTotalTransactionSum());

        verify(userService).save(userDtoArgumentCaptor.capture());
        UserDto userDto =userDtoArgumentCaptor.getValue();
        assertThat(csvRecordAdminUser.get("name")).isEqualTo(userDto.getName());
        assertThat(csvRecordAdminUser.get("password")).isEqualTo(userDto.getPassword());

    }

//    @Test
//    public void testImportMerchants() {
//        try (MockedStatic<Files> filesMockedStatic = mockStatic(Files.class);
//             MockedStatic<Paths> pathsMockedStatic = mockStatic(Paths.class);
//             MockedStatic<CSVParser> csvParserMockedStatic = mockStatic(CSVParser.class)) {
//            String fileName = "merchants.csv";
//            Path path = mock(Path.class);
//            pathsMockedStatic.when((MockedStatic.Verification) Paths.get(fileName)).thenReturn(path)      ;
//            BufferedReader bufferedReader = mock(BufferedReader.class);
//            //when(bufferedReader.readLine()).thenReturn("atr1,atr2", "34,23", "56,23");
//            filesMockedStatic.when((MockedStatic.Verification) Files.newBufferedReader(any()))
//                    .thenReturn(bufferedReader);
//            CSVParser csvParser = mock(CSVParser.class);
//            csvParserMockedStatic.when( (MockedStatic.Verification) CSVParser.parse(bufferedReader, any()))
//                    .thenReturn(csvParser);
//
//            CSVRecord csvRecord = mock(CSVRecord.class);
//            lenient().when(csvRecord.get("role")).thenReturn("admin");
//            CSVRecord csvRecord1 = mock(CSVRecord.class);
//            lenient().when(csvRecord1.get("role")).thenReturn("user");
//            List<CSVRecord> records = List.of(csvRecord, csvRecord1);
//            when(csvParser.getRecords()).thenReturn(records);
//
//            rakeTaskService.importMerchants(fileName);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
