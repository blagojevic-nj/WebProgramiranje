package dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Karta;
import beans.Kupac;
import beans.enums.StatusKarte;
import beans.enums.TipKarte;



public class KarteDAO {
	private HashMap<String, Karta> mapaKarata;
	private HashMap<String, ArrayList<String>> mapaOdustajanja;
	private String putanja;
	private static final long DUZINASIFRE = 10000000000L;
	private static long last = 0;
	private Comparator<Karta> sorterCena,sorterDatum;
	private Comparator<SimpleEntry<Karta, String>>sorterNaziv;
	
	
	public KarteDAO() {
		mapaKarata = new HashMap<>();
		mapaOdustajanja = new HashMap<>();
		initSortere();
	}

	public KarteDAO(String path) {
		mapaKarata = new HashMap<>();
		mapaOdustajanja = new HashMap<>();
		loadTickets(path);
		loadOdustajanje(path);
		initSortere();
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
			if(!retVal.contains(mapaKarata.get(s).getManifestacija()) && mapaKarata.get(s).getStatus() == StatusKarte.REZERVISANA)
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
				if(k.getProdavac().equals(username) && k.getStatus() == StatusKarte.REZERVISANA)
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
	
	public ArrayList<String> generisiId(int numOfIds) {
		ArrayList<String> result = new ArrayList<String>();
		for(int i=0;i<numOfIds;i++)
		{
			String sifra;
			while(true)
			{
				long id = System.currentTimeMillis() % DUZINASIFRE;
				sifra = String.valueOf(id);
				if(!mapaKarata.containsKey(sifra) && id != last) {
					last = id;
					break;
				}
			}
			result.add(sifra);
			System.out.println(sifra);
		}

		return result;

		
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
	
	public boolean otkazi(String id, ManifestacijeDAO dao, KorisniciDAO korisnici) {
		
		Karta k = mapaKarata.get(id);
		LocalDateTime now = LocalDateTime.now();

	    if(k.getDatumVremeManifestacije().isAfter(now) && now.until(k.getDatumVremeManifestacije(), ChronoUnit.DAYS) >= 7) {
	    	dao.proveriKartu(k.getManifestacija());
	    	k.setStatus(StatusKarte.ODUSTANAK);
	    	korisnici.otkazi(k);
			upisiKarte();
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
	
	public Collection<Kupac> getSumnjiviKorisnici(Collection<Kupac> kupci) {
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

	public Collection<Karta> filtriraj(Collection<Karta> kolekcija, Collection<String> listaUslova) {
		//lista vip regular fanpit rezervisana odustanak
		List<Karta> result = new ArrayList<Karta>();
		ArrayList<Karta>temp;
		Set<String>uslovi = new HashSet<String>(listaUslova);
		if(uslovi.contains("vip"))
		{
			temp = (ArrayList<Karta>) kolekcija.stream().filter(k -> k.getTipKarte()==TipKarte.VIP).collect(Collectors.toList());
			result.addAll(temp);
		}
		if(uslovi.contains("regular"))
		{
			temp = (ArrayList<Karta>) kolekcija.stream().filter(k -> k.getTipKarte()==TipKarte.REGULAR).collect(Collectors.toList());
			result.addAll(temp);
		}
		if(uslovi.contains("fanpit"))
		{
			temp = (ArrayList<Karta>) kolekcija.stream().filter(k -> k.getTipKarte()==TipKarte.FAN_PIT).collect(Collectors.toList());	
			result.addAll(temp);
		}
		if(uslovi.contains("rezervisana"))
		{
			temp = (ArrayList<Karta>) kolekcija.stream().filter(k -> k.getStatus()==StatusKarte.REZERVISANA).collect(Collectors.toList());
			result.addAll(temp);
		}
		if(uslovi.contains("odustanak"))
		{
			temp = (ArrayList<Karta>) kolekcija.stream().filter(k -> k.getStatus()==StatusKarte.ODUSTANAK).collect(Collectors.toList());
			result.addAll(temp);
		}
	 
		
		return result;
	}

	public List<Karta> sortirajPoImenuManifestacije(List<Karta> kolekcija, ManifestacijeDAO daoManifestacije, boolean opadajuce) {

		ArrayList<SimpleEntry<Karta, String>>mapa =new ArrayList<SimpleEntry<Karta,String>>();
		ArrayList<Karta>result = new ArrayList<Karta>();
		for(Karta k : kolekcija)
		{
			String manifestacija = daoManifestacije.getManifestacijaById(k.getManifestacija()).getNaziv();
			SimpleEntry<Karta, String> par = new SimpleEntry<Karta, String>(k, manifestacija);
			mapa.add(par);
		}
		
		Collections.sort(mapa,sorterNaziv);
		if (opadajuce) {
			Collections.reverse(mapa);
		}
		for(SimpleEntry<Karta, String> p : mapa )
		{
			result.add(p.getKey());
		}
		
		return result;
	}
	
	public List<Karta> sortirajPoCeniKarte(List<Karta> kolekcija, boolean opadajuce) {

		Collections.sort(kolekcija, sorterCena);
		if (opadajuce) {
			Collections.reverse(kolekcija);
		}
		ArrayList<Karta> result = new ArrayList<>(kolekcija);
		return result;
	}
	
	public List<Karta> sortirajPoDatumu(List<Karta> kolekcija, boolean opadajuce) {

		Collections.sort(kolekcija, sorterDatum);
		if (opadajuce) {
			Collections.reverse(kolekcija);
		}
		ArrayList<Karta> result = new ArrayList<>(kolekcija);
		return result;
	}
	
	
	private void initSortere()
	{
		sorterCena = new Comparator<Karta>() {

			@Override
			public int compare(Karta o1, Karta o2) {
				return  Double.compare(o1.getCena(), o2.getCena());
			}
		};
		
		sorterDatum = new Comparator<Karta>() {

			@Override
			public int compare(Karta o1, Karta o2) {
				return  o1.getDatumVremeManifestacije().compareTo(o2.getDatumVremeManifestacije());
			}

		};
		
		sorterNaziv = new Comparator<SimpleEntry<Karta, String>>() {

			@Override
			public int compare(SimpleEntry<Karta, String> o1, SimpleEntry<Karta, String> o2) {
				
				return o1.getValue().toLowerCase().compareTo(o2.getValue().toLowerCase());

			}

		
	};
	
	
	}
	
	
	
	
}

	
