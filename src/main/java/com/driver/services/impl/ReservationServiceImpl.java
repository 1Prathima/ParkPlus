package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.

        //check for valid parkingLot and user
        ParkingLot parkingLot;
        try{
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        }
        catch (Exception e){
            throw new Exception("Cannot make reservation");
        }
        User user;
        try{
            user = userRepository3.findById(userId).get();
        }
        catch (Exception e){
            throw new Exception("Cannot make reservation");
        }

        //check for available spots and assign
        Spot spot = null;
        int min = Integer.MAX_VALUE;
        for(Spot spot1 : parkingLot.getSpotList()){
            if(numberOfWheels>=0 && numberOfWheels<=2){
                if(spot1.getOccupied()==false && spot1.getPricePerHour()<min &&
                        (spot1.getSpotType().equals(SpotType.TWO_WHEELER) ||
                         spot1.getSpotType().equals(SpotType.FOUR_WHEELER) ||
                         spot1.getSpotType().equals(SpotType.OTHERS))){
                    min = spot1.getPricePerHour();
                    spot = spot1;
                }
            }
            else if(numberOfWheels>=3 && numberOfWheels<=4){
                if(spot1.getOccupied()==false && spot1.getPricePerHour()<min &&
                        (spot1.getSpotType().equals(SpotType.FOUR_WHEELER) ||
                         spot1.getSpotType().equals(SpotType.OTHERS))){
                    min = spot1.getPricePerHour();
                    spot = spot1;
                }
            }
            else{
                if(spot1.getOccupied()==false && spot1.getPricePerHour()<min &&
                        (spot1.getSpotType().equals(SpotType.OTHERS))){
                    min = spot1.getPricePerHour();
                    spot = spot1;
                }
            }
        }

        if(spot == null){
            throw new Exception("Cannot make reservation");
        }

        //make a reservation
        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(spot);
        reservation.setUser(user);

        spot.setOccupied(true);
        spot.getReservationList().add(reservation);
        user.getReservationList().add(reservation);

//        return reservationRepository3.save(reservation);  //saving reservation
        userRepository3.save(user);
        spotRepository3.save(spot);
        return reservation;
    }
}
