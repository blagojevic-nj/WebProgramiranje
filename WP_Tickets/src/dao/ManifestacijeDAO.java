package dao;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Manifestacija;
import beans.TipManifestacije;

public class ManifestacijeDAO {
	
	HashMap<Integer, Manifestacija>manifestacije;
	//Lista sa IsObrisana == false!
	HashMap<Integer, Manifestacija>neobrisaneManifestacije;
	HashMap<Integer, TipManifestacije>TipManifestacijeMapa;
	Comparator<Manifestacija>sorterDatum,sorterCena,sorterLokacija,sorterNaziv;
	
	
	public HashMap<Integer, Manifestacija> getManifestacije() {
		return manifestacije;
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
		load(path);
		setNeobrisaneManifestacije();
		initSorter();
	}

	/**Registracija sa proverom da li se moze reg nova manifestacija*/
	public boolean RegistracijaNoveManifestacije(Manifestacija m)
	{
		
		return true;
	}
	
	public Manifestacija getManifestacijaById(int id)
	{
		return manifestacije.get(id);
	
	}

	/**Obavezno brisati manifestacije ovom metodom!!!*/
	public boolean ObrisiManifestaciju(int id)
	{
		try {
			manifestacije.get(id).setAktivno(false);
			neobrisaneManifestacije.remove(id);
		}catch(Exception e)
		{
			return false;
		}

		
		return true;
	}
	
	
////////Funkcije za pretragu i filtriranje po datumu i slicno

	/**Izdvoji Manif. po datumu, true ako hoces da dobijes i obrisane*/
	public List<Manifestacija> ManifestacijeZaDatum(LocalDateTime date, boolean UkljuciObrisane)
	{
		Collection<Manifestacija>kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		
		List<Manifestacija>list = new ArrayList<Manifestacija>();
		
		for(Manifestacija m : kolekcijaZaIteraciju)
		{
			if(m.getDatumVremeOdrzavanja().isEqual(date))
				list.add(m);
		}
		
		return list;
	}
	
	/**Izdvoji Manif. po datumu Od-Do, true ako hoces da dobijes i obrisane*/
	public List<Manifestacija> ManifestacijeOdDo(LocalDateTime Od,LocalDateTime Do, boolean UkljuciObrisane)
	{
		Collection<Manifestacija>kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		List<Manifestacija>list = new ArrayList<Manifestacija>();
		
		for(Manifestacija m : kolekcijaZaIteraciju)
		{
			LocalDateTime dt = m.getDatumVremeOdrzavanja();
			if(dt.isAfter(Od) && dt.isBefore(Do) )
				list.add(m);
		}
		
		return list;
	}
	
	/**Izdvoji Manif. po datumu pre datog... true ako hoces da dobijes i obrisane*/
	public List<Manifestacija> ManifestacijePre(LocalDateTime datum, boolean UkljuciObrisane)
	{
		Collection<Manifestacija>kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		List<Manifestacija>list = new ArrayList<Manifestacija>();
		
		for(Manifestacija m : kolekcijaZaIteraciju)
		{
			LocalDateTime dt = m.getDatumVremeOdrzavanja();
			if(dt.isBefore(datum) )
				list.add(m);
		}
		
		return list;
	}
	
	/**Izdvoji Manif. Pre odredjene... true ako hoces da dobijes i obrisane*/
	public List<Manifestacija> ManifestacijePre(int id, boolean UkljuciObrisane)
	{
		Collection<Manifestacija>kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		List<Manifestacija>list = new ArrayList<Manifestacija>();
		LocalDateTime datum = getManifestacijaById(id).getDatumVremeOdrzavanja();
		
		for(Manifestacija m : kolekcijaZaIteraciju)
		{
			LocalDateTime dt = m.getDatumVremeOdrzavanja();
			if(dt.isBefore(datum) )
				list.add(m);
		}
		
		return list;
	}
	
	
	/**Izdvoji Manif. po datumu Posle datog... true ako hoces da dobijes i obrisane*/
	public List<Manifestacija> ManifestacijePosle(LocalDateTime datum, boolean UkljuciObrisane)
	{
		Collection<Manifestacija>kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		List<Manifestacija>list = new ArrayList<Manifestacija>();
		
		for(Manifestacija m : kolekcijaZaIteraciju)
		{
			LocalDateTime dt = m.getDatumVremeOdrzavanja();
			if(dt.isAfter(datum) )
				list.add(m);
		}
		
		return list;
	}
	
	
	/**Izdvoji Manif. Posle odredjene... true ako hoces da dobijes i obrisane*/
	public List<Manifestacija> ManifestacijePosle(int id, boolean UkljuciObrisane)
	{
		Collection<Manifestacija>kolekcijaZaIteraciju = UkljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		List<Manifestacija>list = new ArrayList<Manifestacija>();
		LocalDateTime datum = getManifestacijaById(id).getDatumVremeOdrzavanja();
		
		for(Manifestacija m : kolekcijaZaIteraciju)
		{
			LocalDateTime dt = m.getDatumVremeOdrzavanja();
			if(dt.isAfter(datum) )
				list.add(m);
		}
		
		return list;
	}
	
	
	public List<Manifestacija> pretragaPoNazivu(String ime, boolean ukljuciObrisane)
	{
		Collection<Manifestacija>kolekcijaZaIteraciju = ukljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		
		List<Manifestacija>list = new ArrayList<Manifestacija>();
		
		for(Manifestacija m : kolekcijaZaIteraciju)
		{
			if(m.getNaziv().equals(ime))
				list.add(m);
		}
		
		return list;
	}

	
	public List<Manifestacija> pretragaPoCeni(double from, double to,boolean ukljuciObrisane )
	{
		Collection<Manifestacija>kolekcijaZaIteraciju = ukljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		
		List<Manifestacija>list = new ArrayList<Manifestacija>();
		
		for(Manifestacija m : kolekcijaZaIteraciju)
		{
			if(m.getCenaREGkarte()>=from && m.getCenaREGkarte()<=to)
				list.add(m);
		}
		
		return list;
	}

	
	public List<Manifestacija> pretragaPoLokaciji(String lokacija, boolean ukljuciObrisane)
	{
		Collection<Manifestacija>kolekcijaZaIteraciju = ukljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		
		List<Manifestacija>list = new ArrayList<Manifestacija>();
		
		for(Manifestacija m : kolekcijaZaIteraciju)
		{
			
			String naziv = m.getNaziv();
			//proveri da li je to zasebna rec ili sadrzano u nekoj reci!
			if(m.getNaziv().contains(lokacija))
			{
				String[] tokens = naziv.split(" ");
				for(String s : tokens)
				{
					if(s.equals(lokacija.trim()))
					{
						list.add(m);
						break;
					}

				}

			}
				
				
				
		}
		
		return list;
	}

	
	
//sortiranje****************************
	
	
		/**T/F za najranije->starima / stare-> nove... 2.True ako hoces i obrisane*/
	public List<Manifestacija> sortirajPoDatumu(boolean OdNajranijih,boolean ukljuciObrisane)
	{
		Collection<Manifestacija>kolekcija = ukljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		List<Manifestacija> list = new ArrayList<Manifestacija>(kolekcija);
		Collections.sort(list,sorterDatum);
		if(!OdNajranijih)
		{
			Collections.reverse(list);
		}
		return list;
		
	}
	
	
	/**T/F za najjeftinije->skupo / skupo-> jeftino... 2.True ako hoces i obrisane*/
	public List<Manifestacija> sortirajPoCeni(boolean OdNajjeftinijif,boolean ukljuciObrisane)
	{
		Collection<Manifestacija>kolekcija = ukljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		List<Manifestacija> list = new ArrayList<Manifestacija>(kolekcija);
		Collections.sort(list,sorterCena);
		if(!OdNajjeftinijif)
		{
			Collections.reverse(list);
		}
		return list;
		
	}
	
	
	/**T/F <=> A->Z / Z-> A... 2.True ako hoces i obrisane*/
	public List<Manifestacija> sortirajPoNazivu(boolean a_do_z,boolean ukljuciObrisane)
	{
		Collection<Manifestacija>kolekcija = ukljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		List<Manifestacija> list = new ArrayList<Manifestacija>(kolekcija);
		Collections.sort(list,sorterNaziv);
		if(!a_do_z)
		{
			Collections.reverse(list);
		}
		return list;
		
	}
	
	/**T/F <=> A->Z / Z-> A... 2.True ako hoces i obrisane*/
	public List<Manifestacija> sortirajPoLokaciji(boolean a_do_z,boolean ukljuciObrisane)
	{
		Collection<Manifestacija>kolekcija = ukljuciObrisane ? manifestacije.values() : neobrisaneManifestacije.values() ;
		List<Manifestacija> list = new ArrayList<Manifestacija>(kolekcija);
		Collections.sort(list,sorterLokacija);
		if(!a_do_z)
		{
			Collections.reverse(list);
		}
		return list;
		
	}
	
	
//*********************************************	
	
	//Filtriranje
	
	public List<Manifestacija> filtriranjePoTipu(ArrayList<Manifestacija>manifestacije,TipManifestacije tip)
	{
		ArrayList<Manifestacija>list = new ArrayList<Manifestacija>();
		for(Manifestacija m : manifestacije)
		{
			
			if(m.getTip()==tip.getId())
			{
				list.add(m);
			}
		}
		
		return list;
	}
	
	public List<Manifestacija> filtriranjePoTipu(ArrayList<Manifestacija>manifestacije,int idTipaManifestacije)
	{
		ArrayList<Manifestacija>list = new ArrayList<Manifestacija>();
		for(Manifestacija m : manifestacije)
		{
			
			if(m.getTip()==idTipaManifestacije)
			{
				list.add(m);
			}
		}
		
		return list;
	}
	
	public List<Manifestacija> filtriranjePoTipu(ArrayList<Manifestacija>manifestacije,String nazivManifestacije)
	{
		ArrayList<Manifestacija>list = new ArrayList<Manifestacija>();
		ArrayList<Integer>listaTipovaManifestacija = new ArrayList<Integer>();
		for(TipManifestacije tm : TipManifestacijeMapa.values())
		{
			if(tm.getNazivTipa().equals(nazivManifestacije))
				listaTipovaManifestacija.add(tm.getId());
		}
		for(Manifestacija m : manifestacije)
		{
			
			if(listaTipovaManifestacija.contains(m.getTip()))
			{
				list.add(m);
			}
		}
		
		return list;
	}
	
	/**Prikaz slobodnih karata uraditi u karte dao!!!!*/
	/*
	 * public List<Manifestacija> nerasprodateManifestacije(boolean ukljuciObrisane)
	 * { Collection<Manifestacija>kolekcija = ukljuciObrisane ?
	 * manifestacije.values() : neobrisaneManifestacije.values() ;
	 * List<Manifestacija> list = new ArrayList<Manifestacija>(kolekcija);
	 * 
	 * for(Manifestacija m : kolekcija) { if(m.getBrojMesta()==0) list.add(m); }
	 * 
	 * return list;
	 * 
	 * }
	 */

	/**Iz liste svih uzmi samo one koje su Aktivne(T) ili neaktivne (F)!*/
	public List<Manifestacija> filtrirajPoAktivnom(List<Manifestacija> manifestacije,boolean aktivna)

	{
		List<Manifestacija> aktivne = new ArrayList<Manifestacija>();
		List<Manifestacija> neaktivne = new ArrayList<Manifestacija>();
		

			for(Manifestacija m : manifestacije)
			{
				if(m.getAktivno())aktivne.add(m);
				else neaktivne.add(m);
			}
		
		
		if(aktivna)
			return aktivne;
		else
			return neaktivne;
	}
	////////////////////////////////////////////////////////////////////
	
	//Privatne funkcije....
	
	/**Izdvaja one koje nisu obrisane za dalji rad...*/
	private void setNeobrisaneManifestacije()
	{
		neobrisaneManifestacije = new HashMap<Integer, Manifestacija>();
		for(Manifestacija m : manifestacije.values())
		{
			if(!m.getObrisana())
			{
				neobrisaneManifestacije.put(m.getId(), m);
			}
		}
	}

	private void load(String path) {
		
		manifestacije = new HashMap<Integer, Manifestacija>();
		TipManifestacijeMapa = new HashMap<Integer, TipManifestacije>();
		ObjectMapper mapper = new ObjectMapper();
		String data = path + java.io.File.separator+"data" + java.io.File.separator;
		List<Manifestacija>lista = null;
		List<TipManifestacije>listaTipova = null;
		
		try
		{
		
			lista=Arrays.asList(mapper.readValue(Paths.get(data + "manifestacije.json").toFile(), Manifestacija[].class));
			
			for(Manifestacija m : lista)				
				{
					manifestacije.put(m.getId(),m);
				}
			
			
			
		}catch(IOException e)
				{
					System.out.println("Neuspelo ucitavanje manifestacija!!!");
				}
		//tipovi Manifestacija...
		try
		{
		
			listaTipova=Arrays.asList(mapper.readValue(Paths.get(data + "tipoviManifestacije.json").toFile(), TipManifestacije[].class));
			
			for(TipManifestacije t : listaTipova)				
				{
					TipManifestacijeMapa.put(t.getId(),t);
				}
			
			
			
		}catch(IOException e)
				{
					System.out.println("Neuspelo ucitavanje Tipova manifestacija!!!");
				}
		

	}

	private void initSorter() 
	{
		
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
				return o1.getLokacija().getAdresa().compareTo(o2.getLokacija().getAdresa());
			}
		};
		
		
		
		
		
	}
}


















