package org.example.qrapi.controller;

import com.google.zxing.WriterException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.example.qrapi.model.qr.QrCode;
import org.example.qrapi.services.QrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QrControllerTest {

    @Mock
    private QrService qrService;

    @InjectMocks
    private QrController qrController;

    @Test
    void generate_GeneratesQrCode_ReturnsPngImage() throws WriterException, IOException {
        // Given
        String request = "Test QR Code";

        // When
        qrController.generate(request);

        // Then
        verify(qrService, times(1)).generateQRCodeImage(request);
    }

    @Test
    void retrieveQrById_ExistingId_ReturnsQrCode() throws WriterException, IOException, AuthenticationException {
        // Given
        Long id = 1L;
        QrCode qrCode = new QrCode();
        when(qrService.retrieveQrById(id)).thenReturn(qrCode);

        // When
        ResponseEntity<QrCode> response = qrController.retrieveQrById(id);

        // Then
        assertEquals(qrCode, response.getBody());
    }

    @Test
    void deleteQrById_ExistingId_ReturnsOkResponse() throws WriterException, IOException, AuthenticationException {
        // Given
        Long id = 1L;

        // When
        ResponseEntity<String> response = qrController.deleteQrById(id);

        // Then
        assertEquals("Qr successfully deleted", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(qrService, times(1)).deleteQrById(id);
    }

    @Test
    void updateById_ExistingId_ReturnsOkResponse() throws WriterException, IOException, AuthenticationException {
        // Given
        Long id = 1L;
        String payload = "Updated QR Code";

        // When
        ResponseEntity<String> response = qrController.updateById(payload, id);

        // Then
        assertEquals("Qr successfully updated", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(qrService, times(1)).updateById(payload, id);
    }

    @Test
    void retrieveQrs_ReturnsPageOfQrCodes() {
        // Given
        int pageNo = 0;
        int pageSize = 3;
        String type = "";

        Page page = mock(Page.class);
        when(qrService.retrieveQrCodes(pageNo, pageSize, type)).thenReturn(page);

        // When
        ResponseEntity<Page<QrCode>> response = qrController.retrieveQrs(pageNo, pageSize, type);

        // Then
        assertEquals(page, response.getBody());
    }
}
