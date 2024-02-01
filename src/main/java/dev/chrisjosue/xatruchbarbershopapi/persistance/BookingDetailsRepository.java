package dev.chrisjosue.xatruchbarbershopapi.persistance;

import dev.chrisjosue.xatruchbarbershopapi.domain.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailsRepository extends JpaRepository<BookingDetail, Long> {
    @Query("""
    SELECT bd FROM BookingDetail bd
    INNER JOIN Booking b on bd.booking.id = b.id
    INNER JOIN User u on b.user.id = u.id
    INNER JOIN ShopService s on s.id = bd.barberService.id
    WHERE b.user.id = :userId AND bd.booking.id = :bookingId
    """)
    List<BookingDetail> findBookingDetailByUser(@Param("userId") Long userId, @Param("bookingId") Long bookingId);

    @Query("""
    SELECT bd FROM BookingDetail bd
    INNER JOIN Booking b on bd.booking.id = b.id
    INNER JOIN ShopService s on s.id = bd.barberService.id
    WHERE bd.booking.id = :bookingId
    """)
    List<BookingDetail> findBookingDetailById(@Param("bookingId") Long bookingId);
}
