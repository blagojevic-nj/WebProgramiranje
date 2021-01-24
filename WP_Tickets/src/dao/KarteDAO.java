package dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Karta;
import beans.Kupac;
import beans.enums.StatusKarte;


public class KarteDAO {
	private HashMap<String, Karta> mapaKarata;
	private HashMap<String, ArrayList<String>> mapaOdustajanja;
	private String putanja;
	private static final long DUZINASIFRE = 10000000000L;
	private static long last = 0;
	
	public KarteDAO() {
		mapaKarata = new HashMap<>();
		mapaOdustajanja = new HashMap<>();
	}

	public KarteDAO(String path) {
		mapaKarata = new HashMap<>();
		mapaOdustajanja = new HashMap<>();
		loadTickets(path);
		loadOdustajanje(path);
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
	
	@SuppressWarnings("unchecked")
	private void loadOdustajanje(String path) {
		ObjectMapper mapper = new ObjectMapper();
		String data = path + File.separator + "data" + File.separator;

		putanja = data;
		try {
			Map<String, Object> tempMap = mapper.readValue(new File( putanja +
                    "odustajanje.json"), new TypeReference<Map<String, Object>>() {
            });

			for(String kor: tempMap.keySet()) {
				mapaOdustajanja.put(kor, (ArrayList<String>) tempMap.get(kor));
			}

		} catch (IOException e) {
			System.out.println("Nesto se dogodilo prilikom ucitavanja odustajanja");
		}
	}
	
	public ArrayList<Integer> getManifestacijeZaKarte(Collection<String> lista){
		ArrayList<Integer> retVal = new ArrayList<Integer>();
		for(String s : lista) {
			retVal.add(mapaKarata.get(s).getManifestacija());
		}
		return retVal;
	}
	
	/**Tip korisnika:
	 * 0-ADMIN
	 * 1-PRODAVAC
	 * 2-KUPAC*/
	public Collection<Karta>getKarte(int TipKorisnika,String username ){
		ArrayList<Karta>cardList = new ArrayList<Karta>();
		
		//admin
		if(TipKorisnika==0)
		{
			return mapaKarata.values();
		}
		//Prodavac
		else if(TipKorisnika == 1)
		{
			for(Karta k : mapaKarata.values())
			{
				if(k.getProdavac().equals(username))
				{
					cardList.add(k);
				}
			}
			
		}
		else {
			for(Karta k : mapaKarata.values())
			{
				if(k.getKupac().equals(username))
				{
					cardList.add(k);
				}
			}
		}
		
		return cardList;
		
	}
	
	public String generisiId() {
		
		String sifra;
		while(true)
		{
			long id = System.currentTimeMillis() % DUZINASIFRE;
			sifra = String.valueOf(id);
			if(!mapaKarata.containsKey(sifra)) break;
		}
		
		System.out.println(sifra);
		return sifra;

		
	}
	
	
	public void DodajNoveKarte(ArrayList<Karta>noveKarte)
	{
		for(Karta k : noveKarte)
		{
			mapaKarata.put(k.getId(), k);
		}
		
		upisiKarte();
	}
	
	public void upisiKarte() {
		
		ObjectMapper maper = new ObjectMapper();
		try {
			maper.writeValue(Paths.get(putanja+"karte.json").toFile(), mapaKarata.values());
		} catch (IOException e) {
			System.out.println("Greska prilikom upisivanja karata!");
		}
	}
	
	public boolean otkazi(String id, ManifestacijeDAO dao) {
		
		Karta k = mapaKarata.get(id);
		LocalDateTime now = LocalDateTime.now();

	    if(k.getDatumVremeManifestacije().isAfter(now)) {
	    	dao.proveriKartu(k.getManifestacija());
	    	k.setStatus(StatusKarte.ODUSTANAK);
			upisiKarte();
			
			System.out.println(mapaKarata.get(id).getStatus());
			// ovde ide ono za sredjivanje tipa
			if(mapaOdustajanja.containsKey(k.getKupac())) {
				mapaOdustajanja.get(k.getKupac()).add(now.toString());
			}else {
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(now.toString());
				mapaOdustajanja.put(k.getKupac(), tmp);
			}
			upisiOdustajanje();
			return true;
	    }
		
		return false;
	}
	
	public void upisiOdustajanje() {
		ObjectMapper maper = new ObjectMapper();
		try {
			maper.writeValue(new File(putanja+"odustajanje.json"), mapaOdustajanja);
		} catch (IOException e) {
			System.out.println("Greska prilikom upisivanja!");
		}
	}
	
	public Collection<Kupac> getSumljiviKorisnici(Collection<Kupac> kupci) {
		ArrayList<Kupac> retVal = new ArrayList<Kupac>();
		for(Kupac k: kupci) {
			if(sumnjivKupac(k.getUsername()))
				retVal.add(k);
		}
		return retVal;
	}
	
	private boolean sumnjivKupac(String username) {
		if(mapaOdustajanja.containsKey(username)) {
			LocalDateTime now = LocalDateTime.now();
			int brojac = 0;
			
			for(String datumOtkazivanja: mapaOdustajanja.get(username)) {
				LocalDateTime temp = LocalDateTime.parse(datumOtkazivanja);
				long days = temp.until( now, ChronoUnit.DAYS );
				if(days < 30) {
					brojac++;
				}
				
				if(brojac > 5)
					return true;
			}			
		}
		return false;
	}

	public Collection<Karta> searchNaziv(Collection<Karta> kolekcija, String naziv, ManifestacijeDAO daoManifestacije) {
		ArrayList<Karta>result = new ArrayList<Karta>();
		for(Karta k : kolekcija)
		{
			int idManifestacije = k.getManifestacija();
			String manifestacijaName = daoManifestacije.manifestacije.get(idManifestacije).getNaziv();
			if(manifestacijaName.toLowerCase().equals(naziv))
				result.add(k);
		}
		
		return result;
	}

	public Collection<Karta> searchCenaOd(Collection<Karta> kolekcija, String cenaOd) {
		try {
			double cena = Double.parseDouble(cenaOd);
			List<Karta> rez = kolekcija.stream().filter(k -> k.getCena() > cena ).collect(Collectors.toList());
			return new ArrayList<Karta>(rez);
		}catch (Exception e){
			return kolekcija;
		}

		
	}

	public Collection<Karta> searchCenaDo(Collection<Karta> kolekcija, String cenaDo) {
		try {
			double cena = Double.parseDouble(cenaDo);
			List<Karta> rez = kolekcija.stream().filter(k -> k.getCena() < cena ).collect(Collectors.toList());
			return new ArrayList<Karta>(rez);
		}catch (Exception e){
			return kolekcija;
		}
	}

	public Collection<Karta> searchDatumOd(Collection<Karta> kolekcija, String datumOd) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt;
		try {
			dt=sdf.parse(datumOd);
			List<Karta> rez = kolekcija.stream().filter(k -> java.sql.Timestamp.valueOf(k.getDatumVremeManifestacije()).after(dt)).collect(Collectors.toList());
			return new ArrayList<Karta>(rez);
		}catch (Exception e){
			return kolekcija;
		}
	}

	public Collection<Karta> searchDatumDo(Collection<Karta> kolekcija, String datumDo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt;
		try {
			dt=sdf.parse(datumDo);
			List<Karta> rez = kolekcija.stream().filter(k -> java.sql.Timestamp.valueOf(k.getDatumVremeManifestacije()).before(dt)).collect(Collectors.toList());
			return new ArrayList<Karta>(rez);
		}catch (Exception e){
			return kolekcija;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
