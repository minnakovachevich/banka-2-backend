package rs.raf.banka2_bek.actuary.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.raf.banka2_bek.actuary.dto.UpdateActuaryLimitDto;
import rs.raf.banka2_bek.actuary.repository.ActuaryInfoRepository;
import rs.raf.banka2_bek.actuary.service.implementation.ActuaryServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Coverage test targeting remaining missed lines/branches in ActuaryServiceImpl:
 *  - resetUsedLimit when actuary record not found (L86 orElseThrow)
 *  - getAuthenticatedUsername: authentication != null but !isAuthenticated() (L111 branch)
 *  - getAuthenticatedUsername: principal is not UserDetails (L117 branch false + L121 throw)
 */
@ExtendWith(MockitoExtension.class)
class ActuaryServiceImplCoverageTest {

    @Mock
    private ActuaryInfoRepository actuaryInfoRepository;

    @InjectMocks
    private ActuaryServiceImpl actuaryService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("resetUsedLimit throws EntityNotFoundException when actuary record missing")
    void resetUsedLimit_notFound() {
        when(actuaryInfoRepository.findByEmployeeId(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> actuaryService.resetUsedLimit(999L));

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    @DisplayName("updateAgentLimit throws when authentication present but not authenticated")
    void updateAgentLimit_authenticationNotAuthenticated() {
        // AnonymousAuthenticationToken with setAuthenticated(false) triggers the !isAuthenticated() branch.
        Authentication unauth = new UsernamePasswordAuthenticationToken(
                "anonymous", null);
        // Default constructor without authorities leaves token as not authenticated.
        SecurityContextHolder.getContext().setAuthentication(unauth);

        UpdateActuaryLimitDto dto = new UpdateActuaryLimitDto();
        dto.setNeedApproval(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> actuaryService.updateAgentLimit(1L, dto));

        assertTrue(ex.getMessage().contains("Authenticated user is required"));
    }

    @Test
    @DisplayName("updateAgentLimit throws when principal is not a UserDetails")
    void updateAgentLimit_principalNotUserDetails() {
        // Authenticated token whose principal is a plain String, not UserDetails.
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "plain-string-principal",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UpdateActuaryLimitDto dto = new UpdateActuaryLimitDto();
        dto.setDailyLimit(new BigDecimal("1000"));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> actuaryService.updateAgentLimit(1L, dto));

        assertTrue(ex.getMessage().contains("Authenticated user is required"));
    }
}
