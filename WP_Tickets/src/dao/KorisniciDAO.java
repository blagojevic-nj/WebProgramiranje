package dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Karta;
import beans.Korisnik;
import beans.Kupac;
import beans.Manifestacija;
import beans.Prodavac;
import beans.TipKupca;
import beans.enums.ImeTipa;
import beans.enums.Uloga;

public class KorisniciDAO {
	private HashMap<String, Korisnik> mapaKorisnika;
	private ArrayList<Korisnik> sviKorisnici;
	private HashMap<Integer, TipKupca> tipoviKupaca;
	private String putanja;
	private Comparator<Korisnik> sorterBodovi,sorterIme,sorterPrezime,sorterUsername;
	public KorisniciDAO() {
		mapaKorisnika = new HashMap<>();
		initSorter();
	}
	
	public KorisniciDAO(String path) {
		mapaKorisnika = new HashMap<>();
		sviKorisnici = new ArrayList<>();
		tipoviKupaca = new HashMap<>();
		initSorter();
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
	
	private Collection<Korisnik> getAdmini(){
		List<Korisnik> retVal = new ArrayList<>();
		for(Korisnik k : mapaKorisnika.values()) {
			if(k.getUloga() == Uloga.ADMIN) {
				retVal.add((Korisnik) k);
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
				maper.writeValue(Paths.get(putanja + "prodavci.json").toFile(), getProdavci());
			} else
				maper.writeValue(Paths.get(putanja + "prodavci.json").toFile(), getAdmini());
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
	
	public double getCenaForKupac(Korisnik trenutni, double cenaRegKarte, int tipKarte)
	{
		Kupac kupac = (Kupac)trenutni;
		int koeficijent=1;
		if(tipKarte==0)
		{
			koeficijent = 4;
		}
		else if(tipKarte==2)
		{
			koeficijent = 2;
		}
		
		int popust = 0;
		//zlatni
		if(kupac.getTip()==0)
		{
			popust=10;
		}
		//srebrni
		else if(kupac.getTip()==1)
		{
			popust = 5;
		}
		
		double cena = koeficijent*cenaRegKarte*(100-popust)/100;
		
		
		return cena;
	}

	public Korisnik getProdavacZaManifestaciju(String idManifestacije) {
		
		for(Korisnik k : sviKorisnici)
		{
			//ako je prodavac
			if(k.getUloga()==Uloga.PRODAVAC)
			{
				Prodavac prodavac = (Prodavac)k;
				for(int id : prodavac.getManifestacije())
				{
					//id manifestacije se podudara??
					if(id == Integer.parseInt(idManifestacije))
					{
						return prodavac;
					}
					
				}
				
			}
			
		}
		
		return null;
	}
	
	public void dodajKarteKupcu(Korisnik trenutni, ArrayList<Karta>noveKarte)
	{
		Kupac kupac = (Kupac)trenutni;	
		for(Karta k : noveKarte)
			kupac.getKarte().add(k.getId());		
		
		izmena(kupac);
	}

	public void dodajKarteProdavcu(ArrayList<Karta>noveKarte)
	{
		Prodavac p = (Prodavac)mapaKorisnika.get(noveKarte.get(0).getProdavac());	
		for(Karta k : noveKarte)
			p.getKarte().add(k.getId());		
		
		izmena(p);
	}

	public void dodajBodove(int broj, double cenaForKupac, Korisnik trenutni) {

		Kupac kupac = (Kupac)trenutni;
		int noviBodovi = broj*(int)cenaForKupac/1000 * 133;
		kupac.setBrojBodova(kupac.getBrojBodova()+noviBodovi);
		checkAndSetTipKorisnika(kupac);
	}

	private void checkAndSetTipKorisnika(Kupac kupac) {
		int bodovi = kupac.getBrojBodova();
		TipKupca tipKupca = tipoviKupaca.get(kupac.getTip());
		
		//ako je zlatni ne proveravaj nista
		if(tipKupca.getImeTipa() == ImeTipa.ZLATNI)
			return;
		else if(tipKupca.getImeTipa() == ImeTipa.SREBRNI)
		{
			if(kupac.getBrojBodova()>tipKupca.getBrojBodova())
			{
				tipKupca.setImeTipa(ImeTipa.ZLATNI);
				kupac.setBrojBodova(0);
			}
			return;
		}
		else if(tipKupca.getImeTipa() == ImeTipa.BRONZANI)
		{
			if(kupac.getBrojBodova()>tipKupca.getBrojBodova())
			{
				tipKupca.setImeTipa(ImeTipa.SREBRNI);
				kupac.setBrojBodova(0);
			}
			return;
		}
		return;
	}
	
	public Collection<Korisnik> getByName(Collection<Korisnik> kolekcija, String name) {
		ArrayList<Korisnik>filteredList = new ArrayList<Korisnik>(); 
		for(Korisnik k : kolekcija) {
			if(k.getIme().toLowerCase().equals(name))
			{
				filteredList.add(k);
			}
		}
		return filteredList;
	}

	public Collection<Korisnik> getBySurname(Collection<Korisnik> kolekcija, String prezime) {
		ArrayList<Korisnik>filteredList = new ArrayList<Korisnik>(); 
		for(Korisnik k : kolekcija) {
			if(k.getPrezime().toLowerCase().equals(prezime))
			{
				filteredList.add(k);
			}
		}
		return filteredList;
	}

	public Collection<Korisnik> filtriraj(Collection<Korisnik> kolekcija, Collection<String> uslovi) {
		ArrayList<Korisnik> filtrirano = new ArrayList<Korisnik>();
		ArrayList<String>listaUslova = (ArrayList<String>)uslovi;
		boolean admin,prodavac,kupac,zlatni,srebrni,bronzani;
		admin = imaUslov("admin", listaUslova);
		prodavac = imaUslov("prodavac", listaUslova);
		kupac = imaUslov("kupac", listaUslova);
		zlatni = imaUslov("zlatni", listaUslova);
		srebrni = imaUslov("srebrni", listaUslova);
		bronzani = imaUslov("bronzani", listaUslova);
		
		if(admin) {
			for(Korisnik k : kolekcija)
			{
				if(k.getUloga()==Uloga.ADMIN)
					filtrirano.add(k);
			}
		}
		if(prodavac) {
			for(Korisnik k : kolekcija)
			{
				if(k.getUloga()==Uloga.PRODAVAC)
					filtrirano.add(k);
			}
		}
		if(kupac) {
			for(Korisnik k : kolekcija)
			{
				//ako je kupac ako nije stavio nista onda sve kupce ako je stavio nesto onda samo njih
				if(k.getUloga()==Uloga.KUPAC)
					{
						Kupac kupac1 = (Kupac)k;
						if(!zlatni  && !srebrni && !bronzani )
							{
								filtrirano.add(k);
							}
						else {
							if(zlatni)
							{
								if(tipoviKupaca.get(kupac1.getTip()).getImeTipa()==ImeTipa.ZLATNI)
								{
									filtrirano.add(k);
								}
							}
							if(srebrni)
							{
								if(tipoviKupaca.get(kupac1.getTip()).getImeTipa()==ImeTipa.SREBRNI)
								{
									filtrirano.add(k);
								}
							}
							if(bronzani)
							{
								if(tipoviKupaca.get(kupac1.getTip()).getImeTipa()==ImeTipa.BRONZANI)
								{
									filtrirano.add(k);
								}
							}
						}
					}
			}
		}
				
		return filtrirano;
	}
	
	private boolean imaUslov(String uslov,ArrayList<String>listaUslova ) {
		for(String s : listaUslova)
		{
			if(s.equals(uslov))
				return true;
		}
		
		return false;
	}

	public List<Korisnik> sortirajPoImenu(List<Korisnik> result, boolean opadajuce) {

		Collections.sort(result, sorterIme);
		if (opadajuce) {
			Collections.reverse(result);
		}
		return result;
	}

	public List<Korisnik> sortirajPoPrezimenu(List<Korisnik> result, boolean opadajuce) {
		Collections.sort(result, sorterPrezime);
		if (opadajuce) {
			Collections.reverse(result);
		}
		return result;
	}

	public List<Korisnik> sortirajPoBodovima(List<Korisnik> result, boolean opadajuce) {
		result.removeIf(k -> k.getUloga() != Uloga.KUPAC);
		Collections.sort(result, sorterBodovi);
		if (opadajuce) {
			Collections.reverse(result);
		}
		return result;
	}
	
	public List<Korisnik> sortirajPoUsername(List<Korisnik> result, boolean opadajuce) {
		Collections.sort(result, sorterUsername);
		if (opadajuce) {
			Collections.reverse(result);
		}
		return result;
	}

	private void initSorter()
{
	/*----------------------sorteri------------------*/
	sorterBodovi = new Comparator<Korisnik>() {

		@Override
		public int compare(Korisnik o1, Korisnik o2) {
			Kupac k1 = (Kupac)o1;
			Kupac k2 = (Kupac)o2;
			return  Integer.compare(k1.getBrojBodova(), k2.getBrojBodova());
		}

	};
	
	
	sorterIme = new Comparator<Korisnik>() {

		@Override
		public int compare(Korisnik o1, Korisnik o2) {
			return  o1.getIme().toLowerCase().compareTo(o2.getIme().toLowerCase());
		}

	};
	
	sorterPrezime = new Comparator<Korisnik>() {

		@Override
		public int compare(Korisnik o1, Korisnik o2) {
			return  o1.getPrezime().toLowerCase().compareTo(o2.getPrezime().toLowerCase());
		}

	};
	
	sorterUsername = new Comparator<Korisnik>() {

		@Override
		public int compare(Korisnik o1, Korisnik o2) {
			return  o1.getUsername().toLowerCase().compareTo(o2.getUsername().toLowerCase());
		}

	};
}
	
}
