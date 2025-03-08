package com.workshop.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workshop.Entity.onewayTrip;

@Repository
public interface OnewayTripRepo extends JpaRepository<onewayTrip, Long> {

	List<Trip> findBySourceCityAndDestinationCity(String to, String from);

	Optional<onewayTrip> findById(Long id);

	List<onewayTrip> findBySourceStateAndDestinationStateAndSourceCityAndDestinationCity(
			String sourceState, String destinationState, String sourceCity, String destinationCity);

	Optional<onewayTrip> getOneWayTripPriceBySourceStateAndDestinationStateAndSourceCityAndDestinationCity(
			String sourceState, String destinationState, String sourceCity, String destinationCity);

	List<onewayTrip> findBySourceStateAndSourceCityAndDestinationStateAndDestinationCity(
			String sourceState, String sourceCity, String destinationState, String destinationCity);

}
