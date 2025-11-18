package dev.chrisjosue.xatruchbarbershopapi.bussiness.facade.impl;

import dev.chrisjosue.xatruchbarbershopapi.bussiness.facade.BookingFacade;
import dev.chrisjosue.xatruchbarbershopapi.bussiness.mapper.booking.BookingRequestToDomainMapper;
import dev.chrisjosue.xatruchbarbershopapi.bussiness.mapper.booking.DomainToBookingDtoMapper;
import dev.chrisjosue.xatruchbarbershopapi.bussiness.mapper.booking.DomainToBookingGeneralDtoMapper;
import dev.chrisjosue.xatruchbarbershopapi.bussiness.service.*;
import dev.chrisjosue.xatruchbarbershopapi.bussiness.cases.BookingCases;
import dev.chrisjosue.xatruchbarbershopapi.domain.dto.request.BookingRequest;
import dev.chrisjosue.xatruchbarbershopapi.domain.dto.response.BookingDetailDto;
import dev.chrisjosue.xatruchbarbershopapi.domain.dto.response.BookingDto;
import dev.chrisjosue.xatruchbarbershopapi.domain.dto.response.BookingGeneralDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingFacadeImpl implements BookingFacade {
    private final BookingService bookingService;
    private final BookingDetailService bookingDetailService;
    private final BookingCartService bookingCartService;
    private final BarberService barberService;
    private final UserService userService;
    private final SettingService settingService;
    private final EmailService emailService;
    private final BookingCases bookingCases;
    private final BookingRequestToDomainMapper bookingRequestToDomainMapper;
    private final DomainToBookingDtoMapper domainToBookingDtoMapper;
    private final DomainToBookingGeneralDtoMapper domainToBookingGeneralDtoMapper;

    @Override
    public List<LocalTime> findAvailability(Long barberId, LocalDate date) {
        var barberExists = barberService.findById(barberId);
        var activeHours = settingService.findActiveHours();
        return bookingService.findAvailableTimeBarbers(barberExists.getId(), date, activeHours);
    }

    @Override
    public BookingDto bookingASession(BookingRequest bookingRequest, Long userId) {
        var barberExists = barberService.findById(bookingRequest.getBarberId());
        var userLogged = userService.findById(userId);
        var activeSetting = settingService.findActiveSetting();

        var bookingDomain = bookingRequestToDomainMapper.toDomain(bookingRequest);

        var bookingSet = bookingCases.setBookingToSave(activeSetting, bookingDomain, barberExists, userLogged);

        /* Finding Booked Services by User to Add to BookingDetails */
        var currentCart = bookingCartService.findAll(userLogged.getId());
        var bookSaved = bookingService.bookingSession(bookingSet, currentCart);

        /* Send Email with Detail */
//        emailService.sendBookingEmail(bookSaved);

        // Map to bookingDto
        return domainToBookingDtoMapper.toDto(bookSaved);
    }

    @Override
    public List<BookingGeneralDto> findAllBookings() {
        return bookingService.findAll()
                .stream()
                .map(domainToBookingGeneralDtoMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingGeneralDto> findAllBookingsByUser(Long userId) {
        var userFound = userService.findById(userId);
        return bookingService.findAllUserBookings(userFound.getId())
                .stream()
                .map(domainToBookingGeneralDtoMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDetailDto> findBookingById(Long bookingId) {
        return bookingDetailService.findAllBookingDetailById(bookingId)
                .stream()
                .map(domainToBookingDtoMapper::toDto)
                .toList();
    }

    @Override
    public List<BookingDetailDto> findBookingUserById(Long bookingId, Long userId) {
        var userFound = userService.findById(userId);
        return bookingDetailService.findAllBookingDetailByUser(userFound.getId(), bookingId)
                .stream()
                .map(domainToBookingDtoMapper::toDto)
                .toList();
    }

}
