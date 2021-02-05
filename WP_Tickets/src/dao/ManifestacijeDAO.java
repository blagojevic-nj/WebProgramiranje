package dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Karta;
import beans.Korisnik;
import beans.Lokacija;
import beans.Manifestacija;
import beans.Prodavac;
import beans.TipManifestacije;
import beans.enums.StatusKarte;
import beans.enums.TipKarte;


public class ManifestacijeDAO {

	HashMap<Integer, Manifestacija> manifestacije;
	// Lista sa IsObrisana == false!
	HashMap<Integer, Manifestacija> neobrisaneManifestacije;
	HashMap<Integer, TipManifestacije> TipManifestacijeMapa;
	Comparator<Manifestacija> sorterDatum, sorterCena, sorterLokacija, sorterNaziv;
	ArrayList<Lokacija> sveLokacije = new ArrayList<>(); 
	String putanja;
	
	public HashMap<Integer, Manifestacija> getManifestacije() {
		return manifestacije;
	}
	
	public List<Manifestacija> getHomePageManifestacije() {
		ArrayList<Manifestacija> tmp = new ArrayList<>(filtrirajPoAktivnom(neobrisaneManifestacije, true).values());
		Collections.sort(tmp, sorterDatum);
		LocalDateTime now = LocalDateTime.now();
		List<Manifestacija> nove = tmp.stream().filter(m -> m.getDatumVremeOdrzavanja().isAfter(now)).collect(Collectors.toList());	
		for(Manifestacija man: tmp) {
			if(!nove.contains(man)) nove.add(man);
		}
		return nove;
	}

	public void setManifestacije(HashMap<Integer, Manifestacija> manifestacije) {
		this.manifestacije = manifestacije;
	}

	public ManifestacijeDAO() {
		super();
		this.manifestacije = new HashMap<Integer, Manifestacija>();
		this.neobrisaneManifestacije = new HashMap<Integer, Manifestacija>();
		initSorter();
	}

	public ManifestacijeDAO(String path) {
		super();
		putanja = path;
		load(path);
		setNeobrisaneManifestacije();
		initSorter();
		loadLokacije(path);
	}

	/** Registracija sa proverom da li se moze reg nova manifestacija */
	public boolean RegistracijaNoveManifestacije(Manifestacija m, KorisniciDAO dao, String prodavac) {
		if(!proveraVremena(m.getDatumVremeOdrzavanja(), m.getLokacija().getId()))
			return false;
		
		if(!postojiLokacija(m.getLokacija().getId())) {
			dodajLokaciju(m.getLokacija());
		}
		
		m.setId(manifestacije.values().size()+1);
		if(m.getPoster() == null || m.getPoster().equals("")) {
			m.setPoster("../images/default.png");
		}else {
			String putanja = prebaciSliku(m.getPoster(), m.getId());
			m.setPoster(putanja);
		}
		manifestacije.put(m.getId(), m);
		neobrisaneManifestacije.put(m.getId(), m);
		Prodavac p = (Prodavac) dao.getByUsername(prodavac);
		p.getManifestacije().add(m.getId());
		dao.izmena(p);
		upisiManifestacije();
		
		return true;
	}
	
	public boolean izmenaManifestacije(Manifestacija m, Manifestacija original) {
		Manifestacija man = manifestacije.get(original.getId());
		man.setNaziv(m.getNaziv());
		man.setBrojMesta(m.getBrojMesta());
		man.setBrojPreostalihMesta(m.getBrojPreostalihMesta());
		man.setCenaREGkarte(m.getCenaREGkarte());
		man.setDatumVremeOdrzavanja(m.getDatumVremeOdrzavanja());
		if(!postojiLokacija(m.getLokacija().getId())) {
			dodajLokaciju(m.getLokacija());
		}
		
		man.setTip(m.getTip());
		man.setLokacija(m.getLokacija());
		
		if(m.getPoster() == null || m.getPoster().trim().equals("")) {
			man.setPoster(original.getPoster());
		}else {
			String putanja = prebaciSliku(m.getPoster(), original.getId());
			man.setPoster(putanja);
		}
		
		upisiManifestacije();
		
		return true;
	}
	
	private boolean postojiLokacija(int id) {
		for(Lokacija lok: sveLokacije) {
			if(lok.getId() == id){
				return true;
			}
		}
		
		return false;	
	}
	
	private void dodajLokaciju(Lokacija lok) {
		sveLokacije.add(lok);
		ObjectMapper maper = new ObjectMapper();
		try {
			maper.writeValue(Paths.get(putanja+File.separator + "data" + File.separator + "lokacije.json").toFile(), sveLokacije);
		} catch (IOException e) {
			System.out.println("Greska prilikom dodavanja lokacije!");
		}
	}
	
	private String prebaciSliku(String poster, int id) {
		String images = putanja + File.separator + "images" + File.separator;
		String imageDataBytes = poster.substring(poster.indexOf(",")+1);
		
	   byte[] data = Base64.getDecoder().decode(imageDataBytes);
	   
	   	try(OutputStream stream = new FileOutputStream(images + "/Manifestacije/"+id +".jpg")) {
	   		stream.write(data);
		} catch (Exception e) {
			System.out.println("Greska prilikom upisa slike!");
		}
		
	 
		
		return "../images/Manifestacije/"+id+".jpg";
	}
	
	private void upisiManifestacije() {
		String data = putanja+File.separator + "data" + File.separator;
		ObjectMapper maper = new ObjectMapper();
		try {
			maper.writeValue(Paths.get(data+ "manifestacije.json").toFile(), manifestacije.values());
		} catch (IOException e) {
			System.out.println("Greska prilikom dodavanja manifestacije!");
		}
		
	}
	
	public boolean proveraVremena(LocalDateTime dt, int id) {
		for(Manifestacija man: neobrisaneManifestacije.values()) {
			if(dt.toLocalDate().equals(man.getDatumVremeOdrzavanja().toLocalDate()) && man.getLokacija().getId() == id) {
				
				long duration = Duration.between(dt.toLocalTime(), man.getDatumVremeOdrzavanja().toLocalTime()).toMinutes();
				
				if(Math.abs(duration) < 60) {
					return false;
				}
			}
		}
		return true;
	}

	public Manifestacija getManifestacijaById(int id) {
		return manifestacije.get(id);

	}

	/** Obavezno brisati manifestacije ovom metodom!!! */
	public boolean ObrisiManifestaciju(int id) {
		try {
			manifestacije.get(id).setAktivno(false);
			neobrisaneManifestacije.remove(id);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

////////Funkcije za pretragu i filtriranje po datumu i slicno

	/** Izdvoji Manif. po datumu, true ako hoces da dobijes i obrisane */
	public List<Manifestacija> ManifestacijeZaDatum(LocalDateTime date, boolean UkljuciObrisane) {
		Collection<Manifestacija> kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values()
				: neobrisaneManifestacije.values();

		List<Manifestacija> list = new ArrayList<Manifestacija>();

		for (Manifestacija m : kolekcijaZaIteraciju) {
			if (m.getDatumVremeOdrzavanja().isEqual(date))
				list.add(m);
		}

		return list;
	}

	/** Izdvoji Manif. po datumu Od-Do, true ako hoces da dobijes i obrisane */
	public List<Manifestacija> ManifestacijeOdDo(LocalDateTime Od, LocalDateTime Do, boolean UkljuciObrisane) {
		Collection<Manifestacija> kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values()
				: neobrisaneManifestacije.values();
		List<Manifestacija> list = new ArrayList<Manifestacija>();

		for (Manifestacija m : kolekcijaZaIteraciju) {
			LocalDateTime dt = m.getDatumVremeOdrzavanja();
			if (dt.isAfter(Od) && dt.isBefore(Do))
				list.add(m);
		}

		return list;
	}

	/**
	 * Izdvoji Manif. po datumu pre datog... true ako hoces da dobijes i obrisane
	 */
	public List<Manifestacija> ManifestacijePre(LocalDateTime datum, boolean UkljuciObrisane) {
		Collection<Manifestacija> kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values()
				: neobrisaneManifestacije.values();
		List<Manifestacija> list = new ArrayList<Manifestacija>();

		for (Manifestacija m : kolekcijaZaIteraciju) {
			LocalDateTime dt = m.getDatumVremeOdrzavanja();
			if (dt.isBefore(datum))
				list.add(m);
		}

		return list;
	}

	/** Izdvoji Manif. Pre odredjene... true ako hoces da dobijes i obrisane */
	public List<Manifestacija> ManifestacijePre(int id, boolean UkljuciObrisane) {
		Collection<Manifestacija> kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values()
				: neobrisaneManifestacije.values();
		List<Manifestacija> list = new ArrayList<Manifestacija>();
		LocalDateTime datum = getManifestacijaById(id).getDatumVremeOdrzavanja();

		for (Manifestacija m : kolekcijaZaIteraciju) {
			LocalDateTime dt = m.getDatumVremeOdrzavanja();
			if (dt.isBefore(datum))
				list.add(m);
		}

		return list;
	}

	/**
	 * Izdvoji Manif. po datumu Posle datog... true ako hoces da dobijes i obrisane
	 */
	public List<Manifestacija> ManifestacijePosle(LocalDateTime datum, boolean UkljuciObrisane) {
		Collection<Manifestacija> kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values()
				: neobrisaneManifestacije.values();
		List<Manifestacija> list = new ArrayList<Manifestacija>();

		for (Manifestacija m : kolekcijaZaIteraciju) {
			LocalDateTime dt = m.getDatumVremeOdrzavanja();
			if (dt.isAfter(datum))
				list.add(m);
		}

		return list;
	}

	/** Izdvoji Manif. Posle odredjene... true ako hoces da dobijes i obrisane */
	public List<Manifestacija> ManifestacijePosle(int id, boolean UkljuciObrisane) {
		Collection<Manifestacija> kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values()
				: neobrisaneManifestacije.values();
		List<Manifestacija> list = new ArrayList<Manifestacija>();
		LocalDateTime datum = getManifestacijaById(id).getDatumVremeOdrzavanja();

		for (Manifestacija m : kolekcijaZaIteraciju) {
			LocalDateTime dt = m.getDatumVremeOdrzavanja();
			if (dt.isAfter(datum))
				list.add(m);
		}

		return list;
	}

	public List<Manifestacija> pretragaPoNazivu(String ime, boolean ukljuciObrisane) {
		Collection<Manifestacija> kolekcijaZaIteraciju = ukljuciObrisane ? manifestacije.values()
				: neobrisaneManifestacije.values();

		List<Manifestacija> list = new ArrayList<Manifestacija>();

		for (Manifestacija m : kolekcijaZaIteraciju) {
			if (m.getNaziv().equals(ime))
				list.add(m);
		}

		return list;
	}

	public List<Manifestacija> pretragaPoCeni(double from, double to, boolean ukljuciObrisane) {
		Collection<Manifestacija> kolekcijaZaIteraciju = ukljuciObrisane ? manifestacije.values()
				: neobrisaneManifestacije.values();

		List<Manifestacija> list = new ArrayList<Manifestacija>();

		for (Manifestacija m : kolekcijaZaIteraciju) {
			if (m.getCenaREGkarte() >= from && m.getCenaREGkarte() <= to)
				list.add(m);
		}

		return list;
	}

	public List<Manifestacija> pretragaPoLokaciji(String lokacija, boolean ukljuciObrisane) {
		Collection<Manifestacija> kolekcijaZaIteraciju = ukljuciObrisane ? manifestacije.values()
				: neobrisaneManifestacije.values();

		List<Manifestacija> list = new ArrayList<Manifestacija>();

		for (Manifestacija m : kolekcijaZaIteraciju) {

			String naziv = m.getNaziv();
			// proveri da li je to zasebna rec ili sadrzano u nekoj reci!
			if (m.getNaziv().contains(lokacija)) {
				String[] tokens = naziv.split(" ");
				for (String s : tokens) {
					if (s.equals(lokacija.trim())) {
						list.add(m);
						break;
					}

				}

			}

		}

		return list;
	}

//sortiranje****************************

	/** T/F za najranije->starima / stare-> nove... */
	public List<Manifestacija> sortirajPoDatumu(List<Manifestacija> kolekcija, boolean OdNajranijih) {
		Collections.sort(kolekcija, sorterDatum);
		if (!OdNajranijih) {
			Collections.reverse(kolekcija);
		}
		return kolekcija;

	}

	/**
	 * T/F za najjeftinije->skupo / skupo-> jeftino... 2.True ako hoces i obrisane
	 */
	public List<Manifestacija> sortirajPoCeni(List<Manifestacija> kolekcija, boolean OdNajjeftinijif) {
		Collections.sort(kolekcija, sorterCena);
		if (!OdNajjeftinijif) {
			Collections.reverse(kolekcija);
		}
		return kolekcija;
	}

	/** T/F <=> A->Z / Z-> A... 2.True ako hoces i obrisane */
	public List<Manifestacija> sortirajPoNazivu(List<Manifestacija> kolekcija, boolean a_do_z) {
		Collections.sort(kolekcija, sorterNaziv);
		if (!a_do_z) {
			Collections.reverse(kolekcija);
		}
		return kolekcija;

	}

	/** T/F <=> A->Z / Z-> A... 2.True ako hoces i obrisane */
	public List<Manifestacija> sortirajPoLokaciji(List<Manifestacija> kolekcija, boolean a_do_z) {
		Collections.sort(kolekcija, sorterLokacija);
		if (!a_do_z) {
			Collections.reverse(kolekcija);
		}
		return kolekcija;

	}



	// Filtriranje

	public List<Manifestacija> filtriranjePoTipu(ArrayList<Manifestacija> manifestacije, TipManifestacije tip) {
		ArrayList<Manifestacija> list = new ArrayList<Manifestacija>();
		for (Manifestacija m : manifestacije) {

			if (m.getTip() == tip.getId()) {
				list.add(m);
			}
		}

		return list;
	}

	public List<Manifestacija> filtriranjePoTipu(ArrayList<Manifestacija> manifestacije, int idTipaManifestacije) {
		ArrayList<Manifestacija> list = new ArrayList<Manifestacija>();
		for (Manifestacija m : manifestacije) {

			if (m.getTip() == idTipaManifestacije) {
				list.add(m);
			}
		}

		return list;
	}

	/*manifestacije i lista id-ova svih tipova koje ukljucujes*/
	public List<Manifestacija> filtriranjePoTipu(List<Manifestacija> manifestacije, ArrayList<Integer> idoviTipaManifestacija) {
		if(idoviTipaManifestacija.isEmpty()) return manifestacije;
		ArrayList<Manifestacija> list = new ArrayList<Manifestacija>();
		for(Integer id : idoviTipaManifestacija)
		{
			for (Manifestacija m : manifestacije) {

				if (m.getTip()==id) {
					list.add(m);
				}
			}			
		}
		return list;
	}

	public HashMap<Integer, Manifestacija> filtrirajPoAktivnom(HashMap<Integer, Manifestacija> manifestacije, boolean aktivna)

	{
		HashMap<Integer, Manifestacija> aktivne = new HashMap<Integer, Manifestacija>();
		HashMap<Integer, Manifestacija> neaktivne = new HashMap<Integer, Manifestacija>();

		for (HashMap.Entry<Integer, Manifestacija> m : manifestacije.entrySet()) {
		    
			Manifestacija val = m.getValue();
		    if (val.getAktivno())
				aktivne.put(m.getKey(),m.getValue());
			else
				neaktivne.put(m.getKey(),m.getValue());
		}
		
		if (aktivna)
			return aktivne;
		else
			return neaktivne;
	}
	////////////////////////////////////////////////////////////////////

	// Privatne funkcije....

	/** Izdvaja one koje nisu obrisane za dalji rad... */
	private void setNeobrisaneManifestacije() {
		neobrisaneManifestacije = new HashMap<Integer, Manifestacija>();
		for (Manifestacija m : manifestacije.values()) {
			if (!m.getObrisana()) {
				neobrisaneManifestacije.put(m.getId(), m);
			}
		}
	}

	private void load(String path) {

		manifestacije = new HashMap<Integer, Manifestacija>();
		TipManifestacijeMapa = new HashMap<Integer, TipManifestacije>();
		ObjectMapper mapper = new ObjectMapper();
		String data = path + java.io.File.separator + "data" + java.io.File.separator;
		List<Manifestacija> lista = null;
		List<TipManifestacije> listaTipova = null;

		try {

			lista = Arrays
					.asList(mapper.readValue(Paths.get(data + "manifestacije.json").toFile(), Manifestacija[].class));

			for (Manifestacija m : lista) {
				manifestacije.put(m.getId(), m);
			}

		} catch (IOException e) {
			System.out.println("Neuspelo ucitavanje manifestacija!!!");
		}
		// tipovi Manifestacija...
		try {

			listaTipova = Arrays.asList(
					mapper.readValue(Paths.get(data + "tipoviManifestacije.json").toFile(), TipManifestacije[].class));

			for (TipManifestacije t : listaTipova) {
				TipManifestacijeMapa.put(t.getId(), t);
			}

		} catch (IOException e) {
			System.out.println("Neuspelo ucitavanje Tipova manifestacija!!!");
		}

	}
	
	private void loadLokacije(String path) {
		ObjectMapper mapper = new ObjectMapper();
		String data = path + java.io.File.separator + "data" + java.io.File.separator;
		List<Lokacija> temp;
		try {
			temp = Arrays
					.asList(mapper.readValue(Paths.get(data + "lokacije.json").toFile(), Lokacija[].class));
			for(Lokacija l: temp)
				sveLokacije.add(l);
		} catch (IOException e) {
			System.out.println("Greska prilikom ucitavanja lokacija!");		
		}
	}

	private void initSorter() {

		sorterDatum = new Comparator<Manifestacija>() {

			@Override
			public int compare(Manifestacija o1, Manifestacija o2) {
				return o1.getDatumVremeOdrzavanja().compareTo(o2.getDatumVremeOdrzavanja());

			}
		};

		sorterCena = new Comparator<Manifestacija>() {

			@Override
			public int compare(Manifestacija o1, Manifestacija o2) {
				return Double.compare(o1.getCenaREGkarte(), o2.getCenaREGkarte());
			}
		};

		sorterLokacija = new Comparator<Manifestacija>() {

			@Override
			public int compare(Manifestacija o1, Manifestacija o2) {
				return o1.getLokacija().getAdresa().compareTo(o2.getLokacija().getAdresa());
			}
		};

		sorterNaziv = new Comparator<Manifestacija>() {

			@Override
			public int compare(Manifestacija o1, Manifestacija o2) {
				return o1.getNaziv().toLowerCase().compareTo(o2.getNaziv().toLowerCase());
			}
		};

	}

	public Collection<TipManifestacije> getAllManifestacijeTipovi() {
		return TipManifestacijeMapa.values();
	}

	// ovde ide moj kod

	public Collection<Manifestacija> getMojeManifestacije(Collection<Integer> lista) {
		ArrayList<Manifestacija> retVal = new ArrayList<>();
		for (int i : lista) {
			if (neobrisaneManifestacije.containsKey(i))
				retVal.add(neobrisaneManifestacije.get(i));
		}

		return retVal;
	}

	public Collection<Lokacija> getAllManifestacijeLokacije(){
		return sveLokacije;
	}
	
	
	
	public HashMap<Integer, Manifestacija> getNeobrisaneManifestacije() {
		return neobrisaneManifestacije;
	}

	public void setNeobrisaneManifestacije(HashMap<Integer, Manifestacija> neobrisaneManifestacije) {
		this.neobrisaneManifestacije = neobrisaneManifestacije;
	}
/*-****************Search************/
	
	
	public Collection<Manifestacija> searchNaziv(Collection<Manifestacija> kolekcija, String naziv) {
		List<Manifestacija>nove = new ArrayList<Manifestacija>();
		for(Manifestacija m : kolekcija)
		{
			if(m.getNaziv().toLowerCase().contains(naziv)) nove.add(m);
		}

	return nove;	
	}

	public Collection<Manifestacija> searchCenaOd(Collection<Manifestacija> kolekcija, String CenaOd) {
		List<Manifestacija>nove = new ArrayList<Manifestacija>();
		try {
			int cena=Integer.parseInt(CenaOd);
			for(Manifestacija m : kolekcija)
				{
					if(m.getCenaREGkarte()>cena) nove.add(m);
				}
			return nove;
			}
		catch (Exception e){
			return kolekcija;
		}
	}

	public Collection<Manifestacija> searchCenaDo(Collection<Manifestacija> kolekcija, String CenaDo) {
		List<Manifestacija>nove = new ArrayList<Manifestacija>();
		try {
			int cena=Integer.parseInt(CenaDo);
			for(Manifestacija m : kolekcija)
				{
					if(m.getCenaREGkarte()<cena) nove.add(m);
				}
			return nove;
			}
		catch (Exception e){
			return kolekcija;
		}
	}

	public Collection<Manifestacija> searchDatumOd(Collection<Manifestacija> kolekcija, String DatumOd) {
		List<Manifestacija>nove = new ArrayList<Manifestacija>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt;
		try {
			dt=sdf.parse(DatumOd);
			for(Manifestacija m : kolekcija)
				{				
				//Prevesti u date...
					if(java.sql.Timestamp.valueOf(m.getDatumVremeOdrzavanja()).after(dt)) nove.add(m);
				}
			return nove;
			}
		catch (Exception e){
			return kolekcija;
		}
	}

	public Collection<Manifestacija> searchDatumDo(Collection<Manifestacija> kolekcija, String DatumDo) {
		List<Manifestacija>nove = new ArrayList<Manifestacija>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt;
		try {
			dt=sdf.parse(DatumDo);
			for(Manifestacija m : kolekcija)
				{				
				//Prevesti u date...
					if(java.sql.Timestamp.valueOf(m.getDatumVremeOdrzavanja()).before(dt)) nove.add(m);
				}
			return nove;
			}
		catch (Exception e){
			return kolekcija;
		}
	}

	public Collection<Manifestacija> searchLokacija(Collection<Manifestacija> kolekcija, String lokacija,String tipLokacije) {
		List<Manifestacija>nove = new ArrayList<Manifestacija>();

		if(tipLokacije.equals("koordinate"))
		{
			try {
					double prvaKoordinata = Double.parseDouble(lokacija.split(",")[0]);
					double drugaKoordinata = Double.parseDouble(lokacija.split(",")[1]);
					
					for(Manifestacija m : kolekcija)
					{				
						 
						if(Math.abs(prvaKoordinata - m.getLokacija().getGeoDuzina()) < 0.0005 && Math.abs(drugaKoordinata - m.getLokacija().getGeoSirina()) < 0.0005) {
							nove.add(m);
						}
					}
				return nove;
			}catch (Exception e) {
				return kolekcija;
				}

		}else {
			for(Manifestacija m : kolekcija)
			{		
			if(m.getLokacija().getAdresa().toLowerCase().contains(lokacija))
				{
					nove.add(m);
				}
			}
			
		}
		return nove;
	}

	
	
	  public ArrayList<Karta> kupiKarte(String idManifestacije, int brojKarata,KarteDAO daoKarte,Korisnik kupac,Korisnik prodavac,int tipKarte,double cena)
	  {
		  //dobavi samo aktivne
		  HashMap<Integer,Manifestacija>manifestacije = filtrirajPoAktivnom(neobrisaneManifestacije, true); 
		  Manifestacija m; 
		  //nadji manifestaciju
		  try
		  {
			  m = manifestacije.get(Integer.parseInt(idManifestacije));
			  
		  }catch (Exception e)
		  { 
			  return null;
		  } 
		  //vidi da li ima dovoljno karata, ako ima smanji za taj broj u manifestaciji!
		  if(m!=null && m.getBrojPreostalihMesta()>brojKarata)
		  {
			  m.setBrojPreostalihMesta(m.getBrojPreostalihMesta()-brojKarata); 
			  //vrati sve nove karte
			  upisiManifestacije();
			  return napraviKarte(brojKarata,daoKarte,m,kupac,prodavac,tipKarte,cena);
		  } 
		  return null;
		  
	  }
	 

	/**Nikad ne vraca null, samo praznu listu*/
	  private ArrayList<Karta> napraviKarte(int brojKarata,KarteDAO daoKarte,Manifestacija m, Korisnik kupac, Korisnik prodavac,int tipKarte,double cena)
	  {
		  ArrayList<Karta>noveKarte = new ArrayList<Karta>();
		  TipKarte tip = getTipFromInt(tipKarte);
		  ArrayList<String> newIds =daoKarte.generisiId(brojKarata);
		  for(int i=0;i<brojKarata;i++) 
		  { 
			  Karta k = new Karta(newIds.get(i),prodavac.getUsername(),m.getId(),m.getDatumVremeOdrzavanja(),cena,kupac.getUsername(),StatusKarte.REZERVISANA,tip);		  
			  noveKarte.add(k);
		  }
		  
		  return noveKarte;
	  }
	  
	  
	  public TipKarte getTipFromInt(int tipKarte)
	  {
		  //vip,regular,fanpit
		  TipKarte tip;
		  if(tipKarte == 0)
		  {
			  tip = TipKarte.VIP;
		  }
		  else if(tipKarte == 1)
		  {
			  tip = TipKarte.REGULAR;
		  }
		  else {
			  tip = TipKarte.FAN_PIT;
		  }
		  
		  return tip;
	  }
	 
	  public Double getCenaZaManifestaciju(String idManifestacije)
	  {
		  try {
			  return manifestacije.get(Integer.parseInt(idManifestacije)).getCenaREGkarte();

		  }catch (Exception e) {
			  	return 0.0;
			  	}
	  }

	
	public void proveriKartu(int id) {
		getManifestacije().get(id).setBrojPreostalihMesta(getManifestacije().get(id).getBrojPreostalihMesta()+1);
		upisiManifestacije();
	}
	
	public void aktiviraj(int id) {
		manifestacije.get(id).setAktivno(true);
		upisiManifestacije();
	}
	
	public void obrisi(int id) {
		manifestacije.get(id).setAktivno(false);
		manifestacije.get(id).setObrisana(true);
		neobrisaneManifestacije.remove(id);
		upisiManifestacije();
	}
	
	public List<Manifestacija> nerasprodate(List<Manifestacija>input)
	{
		List<Manifestacija> nove = input.stream().filter(m -> m.getBrojPreostalihMesta()>0).collect(Collectors.toList());
		return new ArrayList<Manifestacija>(nove);
	}
}


