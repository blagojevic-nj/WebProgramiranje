package dao;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Komentar;

public class KomentariDAO {
	public HashMap<Integer, Komentar> mapaKomentara;
	private String putanja;
	
	public KomentariDAO() {
		mapaKomentara = new HashMap<>();
		
	}
	
	public KomentariDAO(String path) {
		putanja = path + "/data/komentari.json";
		mapaKomentara = new HashMap<>();
		
		loadKomentare();
	}
	
	private void loadKomentare() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<Komentar> komentari = Arrays.asList(mapper.readValue(Paths.get(putanja).toFile(), Komentar[].class));

			for (Komentar k : komentari) {
				mapaKomentara.put(k.getId(), k);
			}

		} catch (IOException e) {
			System.out.println("Nesto se dogodilo prilikom ucitavanja komentara!");
		}
	}
	
	private void upisiKomentare() {
		ObjectMapper maper = new ObjectMapper();
		try {
			maper.writeValue(Paths.get(putanja).toFile(), mapaKomentara.values());
		} catch (IOException e) {
			System.out.println("Greska prilikom upisivanja karata!");
		}
	}
	
	public Collection<Komentar> zaAdmina(int idMan){
		ArrayList<Komentar> retVal = new ArrayList<>();
		
		for(Komentar k: mapaKomentara.values()) {
			if(k.getManifestacija() == idMan && !k.getObrisan())
				retVal.add(k);
		}
		
		return retVal;
	}
	
	public Collection<Komentar> zaKupca(int idMan){
		ArrayList<Komentar> retVal = new ArrayList<>();
		
		for(Komentar k: mapaKomentara.values()) {
			if(k.getManifestacija() == idMan && !k.getObrisan() && k.getOdobren())
				retVal.add(k);
		}
		
		return retVal;
	}
	
	public Collection<Komentar> zaProdavca(int idMan){
		ArrayList<Komentar> retVal = new ArrayList<>();
		
		for(Komentar k: mapaKomentara.values()) {
			if(k.getManifestacija() == idMan && !k.getObrisan())
				retVal.add(k);
		}
		
		return retVal;
	}
	
	public float getOcena(int idMan) {
		int cnt = 0;
		float suma = 0;
		
		for(Komentar k:mapaKomentara.values()) {
			if(!k.getObrisan() && k.getOdobren() && k.getManifestacija() == idMan) {
				cnt++;
				suma+=k.getOcena();
			}
		}
		
		if(cnt == 0) return 0;
		
		return suma/cnt;
	}
	
	public void dodajKomentar(Komentar k) {
		k.setId(mapaKomentara.values().size()+1);
		mapaKomentara.put(k.getId(), k);		
		upisiKomentare();
	}
	
	public void odobriKomentar(int kId) {
		if(mapaKomentara.containsKey(kId)) {
			mapaKomentara.get(kId).setOdobren(true);
			upisiKomentare();
		}
	}
	
	public void odbijKomentar(int kId) {
		if(mapaKomentara.containsKey(kId)) {
			mapaKomentara.get(kId).setObrisan(true);;
			upisiKomentare();
		}
	}
}
