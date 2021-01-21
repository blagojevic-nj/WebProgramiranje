package dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Karta;


public class KarteDAO {
	private HashMap<String, Karta> mapaKarata;
	private String putanja;

	public KarteDAO() {
		mapaKarata = new HashMap<>();
	}

	public KarteDAO(String path) {
		mapaKarata = new HashMap<>();

		loadTickets(path);
	}

	public HashMap<String, Karta> getMapaKarata() {
		return mapaKarata;
	}

	public void setMapaKarata(HashMap<String, Karta> mapaKarata) {
		this.mapaKarata = mapaKarata;
	}

	public String getPutanja() {
		return putanja;
	}

	public void setPutanja(String putanja) {
		this.putanja = putanja;
	}

	private void loadTickets(String path) {
		ObjectMapper mapper = new ObjectMapper();
		String data = path + File.separator + "data" + File.separator;

		putanja = data;
		try {
			List<Karta> karte = Arrays.asList(mapper.readValue(Paths.get(data + "karte.json").toFile(), Karta[].class));

			for (Karta k : karte) {
				mapaKarata.put(k.getId(), k);
			}

		} catch (IOException e) {
			System.out.println("Nesto se dogodilo");
		}
	}
	
	public ArrayList<Integer> getManifestacijeZaKarte(Collection<String> lista){
		ArrayList<Integer> retVal = new ArrayList<Integer>();
		for(String s : lista) {
			retVal.add(mapaKarata.get(s).getManifestacija());
		}
		return retVal;
	}
}