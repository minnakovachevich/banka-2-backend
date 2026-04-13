package rs.raf.banka2_bek;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class Banka2BekApplicationMainTest {

    @Test
    void main_callsSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            Banka2BekApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(any(Class.class), any(String[].class)));
        }
    }

    @Test
    void constructor_canBeInstantiated() {
        new Banka2BekApplication();
    }
}
