package rs.raf.banka2_bek.payment.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import rs.raf.banka2_bek.transaction.dto.TransactionResponseDto;

import java.io.IOException;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;

class PaymentReceiptPdfGeneratorFinalCoverageTest {

    @Test
    void generate_wrapsIOException() {
        try (MockedConstruction<PDDocument> docCtor = mockConstruction(PDDocument.class,
                (mock, ctx) -> {
                    doThrow(new IOException("forced")).when(mock).save(any(OutputStream.class));
                });
             MockedConstruction<PDPageContentStream> csCtor = mockConstruction(PDPageContentStream.class)) {

            PaymentReceiptPdfGenerator generator = new PaymentReceiptPdfGenerator();
            TransactionResponseDto dto = TransactionResponseDto.builder().id(1L).build();

            assertThatThrownBy(() -> generator.generate(dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Failed to generate transaction receipt PDF");
        }
    }
}
