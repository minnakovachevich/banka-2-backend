package rs.raf.banka2_bek.account.controller;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rs.raf.banka2_bek.account.dto.AccountResponseDto;
import rs.raf.banka2_bek.account.dto.CreateAccountDto;
import rs.raf.banka2_bek.account.model.AccountRequest;
import rs.raf.banka2_bek.account.model.AccountType;
import rs.raf.banka2_bek.account.repository.AccountRequestRepository;
import rs.raf.banka2_bek.account.service.AccountService;
import rs.raf.banka2_bek.auth.config.GlobalExceptionHandler;
import rs.raf.banka2_bek.client.repository.ClientRepository;
import rs.raf.banka2_bek.currency.model.Currency;
import rs.raf.banka2_bek.currency.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
@DisplayName("AccountController coverage tests")
class AccountControllerCoverageTest {

    private MockMvc mockMvc;

    @Mock private AccountService accountService;
    @Mock private AccountRequestRepository accountRequestRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private CurrencyRepository currencyRepository;

    @InjectMocks private AccountController accountController;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        SimpleModule pageModule = new SimpleModule();
        pageModule.addSerializer(Page.class, new JsonSerializer<Page>() {
            @Override
            public void serialize(Page value, JsonGenerator gen, SerializerProvider serializers) throws java.io.IOException {
                gen.writeStartObject();
                gen.writeObjectField("content", value.getContent());
                gen.writeNumberField("totalElements", value.getTotalElements());
                gen.writeNumberField("totalPages", value.getTotalPages());
                gen.writeNumberField("number", value.getNumber());
                gen.writeNumberField("size", value.getSize());
                gen.writeEndObject();
            }
        });
        mapper.registerModule(pageModule);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(accountController)
                .setMessageConverters(converter)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("employee@banka.rs", null));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private AccountResponseDto sampleAccountDto() {
        return AccountResponseDto.builder()
                .id(1L)
                .accountNumber("222000112345678910")
                .accountType("CHECKING")
                .status("ACTIVE")
                .ownerName("Marko Markovic")
                .balance(new BigDecimal("100000"))
                .availableBalance(new BigDecimal("100000"))
                .currencyCode("RSD")
                .build();
    }

    // ---------- GET /accounts/all ----------

    @Test
    @DisplayName("GET /accounts/all - 200 paginated default params")
    void getAllAccounts_defaultParams() throws Exception {
        Page<AccountResponseDto> page = new PageImpl<>(List.of(sampleAccountDto()));
        when(accountService.getAllAccounts(0, 10, null)).thenReturn(page);

        mockMvc.perform(get("/accounts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @DisplayName("GET /accounts/all?ownerName=Marko&page=2&limit=5 - 200 filtered")
    void getAllAccounts_withFilter() throws Exception {
        Page<AccountResponseDto> page = new PageImpl<>(Collections.emptyList());
        when(accountService.getAllAccounts(2, 5, "Marko")).thenReturn(page);

        mockMvc.perform(get("/accounts/all")
                        .param("page", "2")
                        .param("limit", "5")
                        .param("ownerName", "Marko"))
                .andExpect(status().isOk());

        verify(accountService).getAllAccounts(2, 5, "Marko");
    }

    // ---------- GET /accounts/requests (status blank branch) ----------

    @Test
    @DisplayName("GET /accounts/requests?status=   - prazan status ide na findAll")
    void getAllAccountRequests_blankStatus_usesFindAll() throws Exception {
        Page<AccountRequest> page = new PageImpl<>(Collections.emptyList());
        when(accountRequestRepository.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/accounts/requests").param("status", "   "))
                .andExpect(status().isOk());

        verify(accountRequestRepository).findAll(any(Pageable.class));
        verify(accountRequestRepository, never()).findByStatus(anyString(), any(Pageable.class));
    }

    // ---------- PATCH /accounts/requests/{id}/approve sa createCard=true ----------

    @Test
    @DisplayName("PATCH /accounts/requests/10/approve - sa createCard=true (non-null true branch)")
    void approveAccountRequest_createCardTrue() throws Exception {
        Currency rsd = Currency.builder().id(1L).code("RSD").build();
        AccountRequest req = AccountRequest.builder()
                .id(10L)
                .accountType(AccountType.CHECKING)
                .currency(rsd)
                .initialDeposit(new BigDecimal("1000"))
                .createCard(true)
                .clientEmail("marko@banka.rs")
                .clientName("Marko Markovic")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRequestRepository.findById(10L)).thenReturn(Optional.of(req));
        when(accountRequestRepository.save(any(AccountRequest.class))).thenReturn(req);
        when(accountService.createAccount(any(CreateAccountDto.class))).thenReturn(sampleAccountDto());

        mockMvc.perform(patch("/accounts/requests/10/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        ArgumentCaptor<CreateAccountDto> captor = ArgumentCaptor.forClass(CreateAccountDto.class);
        verify(accountService).createAccount(captor.capture());
        org.junit.jupiter.api.Assertions.assertTrue(captor.getValue().getCreateCard(),
                "Ocekuje se da createCard=true bude prosledjeno service-u");
    }

    @Test
    @DisplayName("PATCH /accounts/requests/10/approve - sa initialDeposit=null")
    void approveAccountRequest_nullInitialDeposit() throws Exception {
        Currency rsd = Currency.builder().id(1L).code("RSD").build();
        AccountRequest req = AccountRequest.builder()
                .id(10L)
                .accountType(AccountType.CHECKING)
                .currency(rsd)
                .initialDeposit(null)
                .createCard(false)
                .clientEmail("marko@banka.rs")
                .clientName("Marko Markovic")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRequestRepository.findById(10L)).thenReturn(Optional.of(req));
        when(accountRequestRepository.save(any(AccountRequest.class))).thenReturn(req);
        when(accountService.createAccount(any(CreateAccountDto.class))).thenReturn(sampleAccountDto());

        mockMvc.perform(patch("/accounts/requests/10/approve"))
                .andExpect(status().isOk());

        ArgumentCaptor<CreateAccountDto> captor = ArgumentCaptor.forClass(CreateAccountDto.class);
        verify(accountService).createAccount(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(0.0, captor.getValue().getInitialDeposit());
    }
}
