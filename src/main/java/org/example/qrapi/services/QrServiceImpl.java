package org.example.qrapi.services;
import org.apache.tomcat.websocket.AuthenticationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.example.qrapi.model.qr.QrCode;
import org.example.qrapi.model.qr.QrType;
import org.example.qrapi.model.qr.WifiConfig;
import org.example.qrapi.model.user.User;
import org.example.qrapi.repositories.qr.QrRepository;
import org.example.qrapi.services.user.UserService;
import org.example.qrapi.services.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService{
    private final QrRepository qrRepository;
    protected final UserService userService;

    private final Logger LOGGER = LoggerFactory.getLogger(QrServiceImpl.class);

    private QrType getPayloadQrType(String payload) {
        try {
            URL url = new URL(payload);
            return QrType.URL;
        } catch (MalformedURLException ignored) { }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WifiConfig wifiConfig = objectMapper.readValue(payload, WifiConfig.class);
            return QrType.WIFI_CONFIG;
        } catch (JsonProcessingException ignored) { }
        return QrType.TEXT;
    }

    public byte[] generateQRCodeImage(String request) throws WriterException, IOException {

        QrType qrType = getPayloadQrType(request);
        return generateQRCodeImage(request, qrType);
    }

    @Override
    public void saveQr(QrCode qrCode) {
        assert qrRepository != null;
        qrRepository.save(qrCode);
    }

    @Override
    public QrCode retrieveQrById(Long id) throws AuthenticationException {
        return getQrIfUserHasAccess(id);
    }

    private boolean validateUserHasAccessToQrCode(QrCode qrCode) {
        UserDetails userDetails = Utils.retrieveUserFromJwt();
        User user = userService.findByEmailAddress(userDetails.getUsername());
        return Objects.equals(user.getUserId(), qrCode.getUser().getUserId());
    }

    @Override
    public Page<QrCode> retrieveQrCodes(int pageNo, int pageSize, String type) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        if (!type.isEmpty()) {
//            return qrRepository.findAllByQrType(QrType.valueOf(type), pageable);
            return retrieveQrCodesOfUserByType(QrType.valueOf(type), pageable);
        }
//        return qrRepository.findAll(pageable);
        return retrieveQrCodesOfUser(pageable);
    }

    @Override
    public void deleteQrById(Long id) throws AuthenticationException {
        if (getQrIfUserHasAccess(id) != null) {
            qrRepository.deleteById(id);
        }
    }

    private QrCode getQrIfUserHasAccess(Long id) throws AuthenticationException {
        QrCode qrCode = qrRepository.findById(id).orElse(null);
        if (qrCode == null) {
            LOGGER.info("No Qr code found for id {}", id);
            return null;
        }
        if (!validateUserHasAccessToQrCode(qrCode)) {
            LOGGER.info("User doesn't have access to QR with id {}", qrCode.getId());
            throw new AuthenticationException("Unauthorized resource");
        }
        return qrCode;
    }

    @Override
    public void updateById(String payload, Long id) throws IOException, WriterException, AuthenticationException {
        QrCode qrCode = getQrIfUserHasAccess(id);
        if (qrCode != null) {
            QrType qrType = getPayloadQrType(payload);
            byte[] updatedQrImage = generateQRCodeImage(payload, qrType);
            qrCode.setLastUpdatedDate(LocalDate.now());
            qrCode.setQrType(qrType);
            qrCode.setQrImage(updatedQrImage);
            saveQr(qrCode);
        }
    }

    private Page<QrCode> retrieveQrCodesOfUser(Pageable pageable) {
        UserDetails userDetails = Utils.retrieveUserFromJwt();
        User user = userService.findByEmailAddress(userDetails.getUsername());
        return  qrRepository.findAllByUser(user, pageable);

    }

    private Page<QrCode> retrieveQrCodesOfUserByType(QrType qrType, Pageable pageable) {
        // Retrieve authenticated user
        UserDetails userDetails = Utils.retrieveUserFromJwt();
        User user = userService.findByEmailAddress(userDetails.getUsername());
        return qrRepository.findAllByQrTypeAndUser(qrType, user, pageable);
    }

    private byte[] generateQRCodeImage(String request, QrType qrType) throws WriterException, IOException {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix =
                barcodeWriter.encode(request, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);

        UserDetails userDetails = Utils.retrieveUserFromJwt();
        User user = userService.findByEmailAddress(userDetails.getUsername());

        QrCode qrCode = QrCode.builder()
                .qrImage(baos.toByteArray())
                .creationDate(LocalDate.now())
                .qrType(qrType)
                .user(user)
                .build();
        userService.addQrToUser(qrCode, user.getEmail());

        return baos.toByteArray();
    }
}
