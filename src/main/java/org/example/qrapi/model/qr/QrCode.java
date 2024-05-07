package org.example.qrapi.model.qr;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.qrapi.model.user.User;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tqr")
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private byte[] qrImage;

    @NotNull
    private LocalDate creationDate;

    private LocalDate lastUpdatedDate;

    @NotNull
    private QrType qrType;

    @JsonIgnoreProperties("qrCodes")
    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

}
