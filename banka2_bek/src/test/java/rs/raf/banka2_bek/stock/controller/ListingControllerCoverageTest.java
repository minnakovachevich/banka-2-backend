package rs.raf.banka2_bek.stock.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rs.raf.banka2_bek.stock.service.ListingService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ListingControllerCoverageTest {

    @Mock private ListingService listingService;

    @InjectMocks private ListingController controller;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void refreshPrices_returnsOk() throws Exception {
        doNothing().when(listingService).refreshPrices();
        mvc.perform(post("/listings/refresh").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(listingService).refreshPrices();
    }
}
