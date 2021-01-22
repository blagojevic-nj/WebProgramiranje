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
			System.out.println(putanja);
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
	
	private Collection<Prodavac> getProdavci(){
		List<Prodavac> retVal = new ArrayList<>();
		for(Korisnik k : mapaKorisnika.values()) {
			if(k.getUloga() == Uloga.PRODAVAC) {
				retVal.add((Prodavac) k);
			}
		}
		
		return retVal;
	}
	
	public Korisnik registracija(Korisnik k) {
		if(mapaKorisnika.containsKey(k.getUsername())) {
			return null;
		}
		
		if(k.getUloga() == Uloga.KUPAC) {
			Kupac kupac = new Kupac(k.getUsername(), k.getPassword(), k.getIme(), k.getPrezime(), k.getPol(), k.getDatumRodjenja(), k.getUloga(), k.getObrisan(), false, new ArrayList<String>(), 0, -1);
			
			int id = tipoviKupaca.size() + 1;
			
			TipKupca tk = new TipKupca(id, k.getUsername(), ImeTipa.BRONZANI, 0, 0, false);
			tipoviKupaca.put(id,  tk);
			kupac.setTip(id);
			mapaKorisnika.put(k.getUsername(), kupac);
			sviKorisnici.add(kupac);
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
		else if(k.getUloga() == Uloga.PRODAVAC) {
			Prodavac prodavac = new Prodavac(k.getUsername(), k.getPassword(), k.getIme(), k.getPrezime(), k.getPol(), k.getDatumRodjenja(), k.getUloga(), k.getObrisan(), false, new ArrayList<>(), new ArrayList<>());
			mapaKorisnika.put(k.getUsername(), prodavac);
			sviKorisnici.add(prodavac);
			ObjectMapper maper = new ObjectMapper();
			try {
				maper.writeValue(Paths.get(putanja + "prodavci.json").toFile(), getProdavci());
			} catch (IOException e) {
				System.out.println("Greska prilikom dodavanja prodavaca!");
				return null;
			}
			return prodavac;
		}
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
	
	public TipKupca getTip(String username) {
		for(TipKupca tk: tipoviKupaca.values()) {
			if(tk.getUsernameKupca().equals(username))
				return tk;
		}
		return null;
	}
	
	public void brisanjeKorisnika(String username) {
		Korisnik k = mapaKorisnika.get(username);
		k.setObrisan(true);
		ObjectMapper maper = new ObjectMapper();
		if(k.getUloga() == Uloga.KUPAC) {
			try {
				maper.writeValue(Paths.get(putanja + "kupci.json").toFile(), getKupci());
			} catch (IOException e) {
				System.out.println("Greska prilikom brisanja kupca!");
			}
		}else if(k.getUloga() == Uloga.PRODAVAC) {
			try {
				maper.writeValue(Paths.get(putanja + "prodavci.json").toFile(), getProdavci());
			} catch (IOException e) {
				System.out.println("Greska prilikom brisanja prodavca!");
			}
		}
	}
	
	public void blokiranjeKorisnika(String username) {
		Korisnik k = mapaKorisnika.get(username);
		ObjectMapper maper = new ObjectMapper();
		if(k.getUloga() == Uloga.KUPAC) {
			Kupac kupac = (Kupac) k;
			kupac.setBlokiran(!kupac.getBlokiran());
			try {
				maper.writeValue(Paths.get(putanja + "kupci.json").toFile(), getKupci());
			} catch (IOException e) {
				System.out.println("Greska prilikom blokiranja kupca!");
			}
		}else if(k.getUloga() == Uloga.PRODAVAC) {
			try {
				Prodavac kupac = (Prodavac) k;
				kupac.setBlokiran(!kupac.getBlokiran());
				maper.writeValue(Paths.get(putanja + "prodavci.json").toFile(), getProdavci());
			} catch (IOException e) {
				System.out.println("Greska prilikom blokiranja prodavca!");
			}
		}
	}

	public void setNoviBodovi(String username,double cena,boolean odustaje)
	{
		//broj_bodova = cena_jedne_karte/1000 * 133		--kupuje
		//broj_bodova = cena_jedne_karte/1000 * 133*4	--odustaje
		int bodovi = (int)(cena/1000)*133;
		Kupac k = (Kupac)mapaKorisnika.get(username);
		if(odustaje) {
			k.setBrojBodova(k.getBrojBodova()-4*bodovi);
			if(k.getBrojBodova()<0)
				k.setBrojBodova(0);
		}else
		{
			k.setBrojBodova(k.getBrojBodova()+bodovi);
		}
		
	}
	public boolean kupiKarte(double cenaREGKarte) {

		return false;
	}
}
