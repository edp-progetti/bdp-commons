package it.bz.idm.bdp;

import it.bz.idm.bdp.ParkingClient;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.parking.ParkingStationDto;

import java.util.List;

import org.apache.xmlrpc.XmlRpcException;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "/META-INF/spring/applicationContext.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ThirdPartyIT extends AbstractJUnit4SpringContextTests {

	@Autowired
	private ParkingClient client;

	@Autowired
	private ParkingMeranoClient parkingMeranoClient;
	// defines the amount of parkingstations the client should return
	private final int PARKING_MERANO_SIZE = 5;

	@Test
	public void testParkingAreas() throws XmlRpcException {
		client.connect();
		Integer[] identifiersOfParkingPlaces = client.getIdentifiersOfParkingPlaces();
		assertNotNull(identifiersOfParkingPlaces);
		assertTrue(identifiersOfParkingPlaces.length > 0);

		// merano
		ParkingMeranoStationDto[] parkigStations = parkingMeranoClient.getParkingStations();
		assertNotNull(parkigStations);
		assertTrue(parkigStations.length == PARKING_MERANO_SIZE);
	}

	@Test
	public void testParkingMetaData() throws XmlRpcException {
		client.connect();
		for (Integer i : client.getIdentifiersOfParkingPlaces()) {
			StationDto parkingMetaData = client.getParkingMetaData(i);
			assertNotNull(parkingMetaData);
			assertTrue(parkingMetaData.getId() != null);
		}

		// merano
		ParkingStationDto[] parkingStations = ParkingMeranoClient.convert(parkingMeranoClient.getParkingStations());
		assertNotNull(parkingStations);
		assertTrue(parkingStations.length == PARKING_MERANO_SIZE);
		for (ParkingStationDto stationDto : parkingStations) {
			assertNotNull(stationDto);
			assertNotNull(stationDto.getId());
		}
	}

	@Test
	public void testParkingData() throws XmlRpcException {
		client.connect();
		for (Integer i : client.getIdentifiersOfParkingPlaces()) {
			List<Object> parkingData = client.getData(i);
			assertNotNull(parkingData);
			assertTrue(parkingData.size() > 0);
			Integer number = Integer.parseInt(parkingData.get(5).toString());
			assertTrue(number >= 0); // check number of free Parking places
		}

		// merano
		ParkingMeranoStationDto[] parkigStations = parkingMeranoClient.getParkingStations();
		assertNotNull(parkigStations);
		assertTrue(parkigStations.length == PARKING_MERANO_SIZE);
		for (ParkingMeranoStationDto stationDto : parkigStations) {
			assertNotNull(stationDto);
			assertTrue(stationDto.getTotalParkingSpaces() > 0);
			assertTrue(stationDto.getFreeParkingSpaces() > 0);
		}
	}
}
