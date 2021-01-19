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

import beans.Korisnik;
import beans.Kupac;
import beans.Prodavac;
import beans.TipKupca;
import beans.enums.ImeTipa;
import beans.enums.Uloga;

public class KorisniciDAO {
	private HashMap<String, Korisnik> mapaKorisnika;
	private ArrayList<Korisnik> sviKorisnici;
	private HashMap<Integer, TipKupca> tipoviKupaca;
	private String putanja;

	public KorisniciDAO() {
		mapaKorisnika = new HashMap<>();
	}
	
	public KorisniciDAO(String path) {
		mapaKorisnika = new HashMap<>();
		sviKorisnici = new ArrayList<>();
		tipoviKupaca = new HashMap<>();
		
		loadUsers(path);
		loadUsersTypes();
		
	}

	public Collection<Korisnik> getAllUsers() {
		return sviKorisnici;
	}

	private void loadUsers(String path) {
		ObjectMapper mapper = new ObjectMapper();
		String data = path + File.separator +  "data" + File.separator;
		
		putanja = data;
		try {
			List<Korisnik> admini = Arrays
					.asList(mapper.readValue(Paths.get(data + "admini.json").toFile(), Korisnik[].class));
			
			System.out.println(admini);
			for (Korisnik k : admini) {
				if(!k.getObrisan()) {
					mapaKorisnika.put(k.getUsername(), k);
				}
				
				sviKorisnici.add(k);
			}
				

			List<Prodavac> prodavci = Arrays
					.asList(mapper.readValue(Paths.get(data + "prodavci.json").toFile(), Prodavac[].class));
			for (Prodavac k : prodavci) {
				if(!k.getObrisan())
					mapaKorisnika.put(k.getUsername(), k);
				
				sviKorisnici.add(k);
			}
				

			List<Kupac> kupci = Arrays.asList(mapper.readValue(Paths.get(data + "kupci.json").toFile(), Kupac[].class));
			for (Kupac k : kupci) {
				if(!k.getObrisan())
					mapaKorisnika.put(k.getUsername(), k);
				
				sviKorisnici.add(k);
			}
				

		} catch (IOException e) {
			System.out.println("Nesto se dogodilo");
		}
	}
	
	private void loadUsersTypes() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<TipKupca> tipovi = Arrays
					.asList(mapper.readValue(Paths.get(putanja + "tipoviKupaca.json").toFile(), TipKupca[].class));
			
			for(TipKupca tk: tipovi) {
				tipoviKupaca.put(tk.getId(), tk);
			}
		} catch (IOException e) {
			System.out.println("Greska prilikom ucitavanja tipova kupaca!");
		}
	}

	public Korisnik getByUsername(String username) {
		return mapaKorisnika.get(username);
	}

	public Korisnik prijava(String username, String password) {
		if (mapaKorisnika.containsKey(username)) {
			if (mapaKorisnika.get(username).getPassword().equals(password))
				return mapaKorisnika.get(username);
		}
		return null;
	}
	
	private Collection<Kupac> getKupci(){
		List<Kupac> retVal = new ArrayList<>();
		for(Korisnik k : mapaKorisnika.values()) {
			if(k.getUloga() == Uloga.KUPAC) {
				retVal.add((Kupac) k);
			}
		}
		
		return retVal;
	}
	
	public Korisnik registracija(Korisnik k) {
		if(mapaKorisnika.containsKey(k.getUsername())) {
			return null;
		}
		
		if(k.getUloga() == Uloga.KUPAC) {
			Kupac kupac = new Kupac(k.getUsername(), k.getPassword(), k.getIme(), k.getPrezime(), k.getPol(), k.getDatumRodjenja(), k.getUloga(), k.getObrisan(), new ArrayList<Integer>(), 0, -1);
			kupac.setBlokiran(false);
			
			int id = tipoviKupaca.size() + 1;
			
			TipKupca tk = new TipKupca(id, k.getUsername(), ImeTipa.BRONZANI, 0, 0, false);
			tipoviKupaca.put(id,  tk);
			kupac.setTip(id);
			mapaKorisnika.put(k.getUsername(), kupac);
			ObjectMapper maper = new ObjectMapper();
			try {
				System.out.println(putanja + "kupci.json");
				maper.writeValue(Paths.get(putanja + "kupci.json").toFile(), getKupci());
				maper.writeValue(Paths.get(putanja + "tipoviKupaca.json").toFile(), tipoviKupaca.values());
			} catch (IOException e) {
				System.out.println("Greska prilikom dodavanja kupca!");
				return null;
			}
			return kupac;
		}
		// treba uraditi i za prodavca
		
		return null;
	}
	
	public void izmena(Korisnik k) {
		if(!mapaKorisnika.containsKey(k.getUsername())) {
			return;
		}
		
		mapaKorisnika.get(k.getUsername()).setIme(k.getIme());
		mapaKorisnika.get(k.getUsername()).setPrezime(k.getPrezime());
		mapaKorisnika.get(k.getUsername()).setPassword(k.getPassword());
		ObjectMapper maper = new ObjectMapper();
		try {
			if(k.getUloga() == Uloga.KUPAC) {
				maper.writeValue(Paths.get(putanja + "kupci.json").toFile(), getKupci());
			} else if(k.getUloga() == Uloga.PRODAVAC) {
				maper.writeValue(Paths.get(putanja + "prodavci.json").toFile(), getKupci());
			} else
				maper.writeValue(Paths.get(putanja + "admini.json").toFile(), getKupci());
		} catch (Exception e) {
			System.out.println("Greska prilikom upisa u fajl!");
		}
		
	}
	
	public Collection<Korisnik> zaProdavca(String username){
		return null;
	}
	
}
