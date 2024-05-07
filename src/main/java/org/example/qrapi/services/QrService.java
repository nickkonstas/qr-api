package org.example.qrapi.services;

import com.google.zxing.WriterException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.example.qrapi.model.qr.QrCode;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface QrService {
    public byte[] generateQRCodeImage(String request) throws WriterException, java.io.IOException;
    public void saveQr(QrCode qrCode);

    public QrCode retrieveQrById(Long id) throws AuthenticationException;

    Page<QrCode> retrieveQrCodes(int pageNo, int pageSize, String type);

    void deleteQrById(Long id) throws AuthenticationException;

    void updateById(String payload, Long id) throws IOException, WriterException, AuthenticationException;
}
