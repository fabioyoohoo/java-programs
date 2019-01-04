package module3;
import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.providers.Google.*;

import java.util.List;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;

import java.util.HashMap;
import java.util.Map;

import de.fhpotsdam.unfolding.marker.Marker;


public class LifeExpectancy extends PApplet {

	UnfoldingMap map;
	Map<String, Float> lifeExpByCountry;
	List<Feature> countries;
	List<Marker> countryMarkers;
	
	public void setup() 
	{
		size(800, 600, OPENGL);		// size of the canvas
		map = new UnfoldingMap(this, 50, 50, 700, 500, 
				new Google.GoogleMapProvider());	// map + size (top to bottom)
		MapUtils.createDefaultEventDispatcher(this, map);	// map interactivity
	
		// Load lifeExpectancy data
		lifeExpByCountry = loadLifeExpectancyFromCSV("LifeExpectancyWorldBankModule3.csv");
		System.out.println("Loaded " + lifeExpByCountry.size() + " countries");
		
		// Load country polygons and adds them as markers
		countries = GeoJSONReader.loadData(this, "countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		map.addMarkers(countryMarkers);
		
		// Country markers are shaded according to life expectancy (only once)
		shadeCountries();
	}
	
	public void draw() {
		
		map.draw();
	}
	
	// Helper Method A
	private void shadeCountries() {
		// Find data for country of the current marker
		for (Marker marker : countryMarkers) {
			String countryId = marker.getId();
			
			// makes join between lifeExp data and Marker
			if (lifeExpByCountry.containsKey(countryId)) {
				float lifeExp = lifeExpByCountry.get(countryId);
				
				// Encode value as brightness (values range: 40-90 to range 10-255)
				int colorLevel = (int) map(lifeExp, 40, 90, 10, 255);	
						// map is a method that takes input value and input 
						// range (40-90) and returns an integer based on how 
						// it falls relative to the output range (10-255)
				
				// sets color based on the returned value above
				marker.setColor(color(255-colorLevel, 100, colorLevel));
			}
			else {
				// default gray color for no data :(
				marker.setColor(color(150,150,150));
			}
		}
	}
	
	// Helper Method B
	// this returns a key,value pair for country and life expectancy from the CSV
	private Map<String, Float> loadLifeExpectancyFromCSV(String fileName) {
		
		Map<String, Float> lifeExpMap = new HashMap<String, Float>(); // construct this bad boy
			// strange because we are going from Map to HashMap... hmmmm?
			// we want a map and the details of it is that it is a HashMap
		
		// Populate it!
		String[] rows = loadStrings(fileName);
		for (String row : rows)		// read countries by row
		{
			String[] columns = row.split(",");	// break out each single string into columns
			
			if (columns.length == 6 && !columns[5].equals("..")) // weed out bad rows
			{
				// Country code and LifeExp	
				float value = Float.parseFloat(columns[5]);	// convert string to float
				lifeExpMap.put(columns[4], value); 	// put key,value pair into the Map
			}	
		}
		return lifeExpMap;	// returns a HashMap of country code and life expectancy
							// example: (AFG ; 60.9314...)
	}
}
