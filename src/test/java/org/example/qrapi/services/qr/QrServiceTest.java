package org.example.qrapi.services.qr;

import com.google.zxing.WriterException;
import org.example.qrapi.model.qr.QrCode;
import org.example.qrapi.model.qr.QrType;
import org.example.qrapi.model.user.User;
import org.example.qrapi.repositories.qr.QrRepository;
import org.example.qrapi.services.QrServiceImpl;
import org.example.qrapi.services.user.UserService;
import org.example.qrapi.services.utils.Utils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
class QrServiceImplTest {

    private MockedStatic<Utils> mockStatic;

    @Before
    public void setUp() {
        // Registering a static mock for UserService before each test
        mockStatic = mockStatic(Utils.class);
    }

    @After
    public void tearDown() {
        // Closing the mockStatic after each test
        mockStatic.close();
    }

//    @BeforeEach
//    public void init(){
//        mockStatic(Utils.class);
//    }
//
//    @AfterEach
//    public void close() {
//        mockStatic(Utils.class).close();
//    }

    @Mock
    private QrRepository qrRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private QrServiceImpl qrService;


    @Test
    void saveQr_QrSavedSuccessfully() {
        // Given
        QrCode qrCode = new QrCode();

        // When
        qrService.saveQr(qrCode);

        // Then
        verify(qrRepository, times(1)).save(qrCode);
    }

    @Test
    void retrieveQrById_ExistingIdAndUserHasAccess_ReturnsQrCode() throws Exception {
        // Given
        User user = User.builder().userId(1L).email("user@example.com").password("password").build();
        Long id = 1L;
        QrCode qrCode = new QrCode();
        qrCode.setUser(user);
        when(qrRepository.findById(id)).thenReturn(Optional.of(qrCode));
        when(Utils.retrieveUserFromJwt()).thenReturn(user);
        when(userService.findByEmailAddress(user.getEmail())).thenReturn(user);

        // When
        QrCode result = qrService.retrieveQrById(id);

        // Then
        assertEquals(qrCode, result);
    }

    @Test
    void deleteQrById_ExistingIdAndUserHasAccess_QrDeletedSuccessfully() throws Exception {
        // Given
        Long id = 1L;
        User user = User.builder().userId(1L).email("user@example.com").password("password").build();
        QrCode qrCode = new QrCode();
        qrCode.setUser(user);
        when(qrRepository.findById(id)).thenReturn(Optional.of(qrCode));
        when(Utils.retrieveUserFromJwt()).thenReturn(user);
        when(userService.findByEmailAddress(user.getEmail())).thenReturn(user);

        // When
        qrService.deleteQrById(id);

        // Then
        verify(qrRepository, times(1)).deleteById(id);

    }

    @Test
    void updateById_ExistingIdAndUserHasAccess_QrUpdatedSuccessfully() throws Exception {
        // Given
        Long id = 1L;
        User user = User.builder().userId(1L).email("user@example.com").password("password").build();
        String payload = "{\"ssid\":\"TestSSID\",\"password\":\"TestPassword\"}";
        QrCode qrCode = new QrCode();
        qrCode.setUser(user);
        when(qrRepository.findById(id)).thenReturn(Optional.of(qrCode));
        mockStatic(Utils.class);
        when(Utils.retrieveUserFromJwt()).thenReturn(user);
        when(userService.findByEmailAddress(user.getEmail())).thenReturn(user);

        // When
        qrService.updateById(payload, id);

        // Then
        verify(qrRepository, times(1)).save(qrCode);

    }


    @Test
    void generateQRCodeImage_UrlRequest_ReturnsByteArray() throws IOException, WriterException {
        // Given
        Long id = 1L;
        User user = User.builder().userId(1L).email("user@example.com").password("password").build();
        String request = "https://example.com";
        QrCode qrCode = new QrCode();
        qrCode.setUser(user);
        when(Utils.retrieveUserFromJwt()).thenReturn(user);
        when(userService.findByEmailAddress(user.getEmail())).thenReturn(user);

        // When
        byte[] result = qrService.generateQRCodeImage(request);

        // Then
        assertNotNull(result);

    }
}
