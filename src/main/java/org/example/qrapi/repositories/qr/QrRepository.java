package org.example.qrapi.repositories.qr;

import org.example.qrapi.model.qr.QrCode;
import org.example.qrapi.model.qr.QrType;
import org.example.qrapi.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QrRepository extends JpaRepository<QrCode, Long> {
    Page<QrCode> findAllByQrType(QrType type, Pageable pageable);
    Page<QrCode> findAllByQrTypeAndUser(QrType type, User user, Pageable pageable);
    Page<QrCode> findAllByUser(User user, Pageable pageable);


}
