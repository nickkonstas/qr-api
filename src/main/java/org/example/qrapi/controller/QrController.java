package org.example.qrapi.controller;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.example.qrapi.model.qr.QrCode;
import org.example.qrapi.services.QrService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class QrController {

    private final QrService qrService;

    @PostMapping(value = "/qrcodes",
    produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] generate(@RequestBody String request) throws WriterException, IOException {

        return qrService.generateQRCodeImage(request);
    }

    @GetMapping(value = "/qrcodes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<QrCode> retrieveQrById(@PathVariable Long id) throws AuthenticationException {

        return ResponseEntity.ok(qrService.retrieveQrById(id));
    }

    @DeleteMapping(value = "/qrcodes/{id}")
    public @ResponseBody ResponseEntity<String> deleteQrById(@PathVariable Long id) throws AuthenticationException {
        qrService.deleteQrById(id);

        return ResponseEntity.ok("Qr successfully deleted");
    }

    @PutMapping(value = "/qrcodes/{id}")
    public @ResponseBody ResponseEntity<String> updateById(@RequestBody String payload, @PathVariable Long id) throws AuthenticationException, IOException, WriterException {
        qrService.updateById(payload, id);

        return ResponseEntity.ok("Qr successfully updated");
    }

    @GetMapping(value = "/qrcodes/", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Page<QrCode>> retrieveQrs(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "3") int pageSize,
            @RequestParam(defaultValue = "") String type
    ) {

        return ResponseEntity.ok(qrService.retrieveQrCodes(pageNo, pageSize, type));

    }

}
