package org.example.qrapi.model.qr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrWithMetadata {
    private byte[] image;
    private QrType type;

    private LocalDate creationDate;
}
