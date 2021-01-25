package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


import beans.Karta;
import beans.Korisnik;
import beans.Kupac;
import beans.Manifestacija;
import beans.enums.Uloga;
import dao.KarteDAO;
import dao.KorisniciDAO;
import dao.ManifestacijeDAO;


@Path("/Karte")
public class KarteService {

	@Context
	ServletContext ctx;

	@Context
	HttpServletRequest request;
	
	private KarteDAO getKarteDAO() {
		KarteDAO dao = (KarteDAO) ctx.getAttribute("karte");
		if (dao == null) {
			dao = new KarteDAO(ctx.getRealPath("."));
			ctx.setAttribute("karte", dao);
		}
		return dao;
	}
	
	private ManifestacijeDAO getManifestacijeDAO() {
		ManifestacijeDAO manifestacije = (ManifestacijeDAO) ctx.getAttribute("manifestacije");
		if (manifestacije == null) {
			manifestacije = new ManifestacijeDAO(ctx.getRealPath("."));
			ctx.setAttribute("manifestacije", manifestacije);
		}
		return manifestacije;
	}
	
	private KorisniciDAO getKorisniciDAO() {
		KorisniciDAO korisnici = (KorisniciDAO) ctx.getAttribute("KorisniciDAO");
		if (korisnici == null) {
			korisnici = new KorisniciDAO(ctx.getRealPath("."));
			ctx.setAttribute("KorisniciDAO", korisnici);
		}
		return korisnici;
	}
	
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Karta> getAllManifestacije() {
		KarteDAO dao = getKarteDAO();
		Collection<Karta>karte;
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if (trenutni == null) {
			return null;
		}

		if (trenutni.equals(getKorisniciDAO().getByUsername(trenutni.getUsername()))&& trenutni.getUloga() == Uloga.KUPAC)
		{
			karte = dao.getKarte(2, trenutni.getUsername());
		} 
		else if (trenutni.equals(getKorisniciDAO().getByUsername(trenutni.getUsername()))&& trenutni.getUloga() == Uloga.PRODAVAC)
		{
			karte = dao.getKarte(1, trenutni.getUsername());		
		}
		else if(trenutni.getUloga() == Uloga.ADMIN)
		{
			karte = dao.getKarte(1, "ADMIN");		

		}else {
			return null;
		}
	
		return karte;

	}
	@GET
	@Path("/kupi/{idManifestacije}/{broj}/{tipKarte}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean otkaziKartu(@PathParam("idManifestacije")String idManifestacije,@PathParam("broj")int broj,@PathParam("tipKarte")int tipKarte)
	{
		KarteDAO daoKarte = getKarteDAO();
		ManifestacijeDAO daoManifestacije = getManifestacijeDAO();
		
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		//provera trenutnog
		if (!(trenutni.equals(getKorisniciDAO().getByUsername(trenutni.getUsername()))&&trenutni.getUloga() == Uloga.KUPAC))
		{
			return false;
		}
		//ako je blokiran nema kupovine
		if(((Kupac)trenutni).getBlokiran())
			return false;
		
		//Ako nije blokiran, dobavi cenu reg karte
		double cenaForKupac = getKorisniciDAO().getCenaForKupac(trenutni,daoManifestacije.getCenaZaManifestaciju(idManifestacije),tipKarte);
		//dobavi ko prodaje
		Korisnik prodavac = getKorisniciDAO().getProdavacZaManifestaciju(idManifestacije);
		//obavi kupovinu u manifestacije dao i napravi sve karte
		ArrayList<Karta>noveKarte = daoManifestacije.kupiKarte(idManifestacije,broj,daoKarte,trenutni,prodavac,tipKarte,cenaForKupac);
		if(noveKarte==null)
			{
			//doslo je do greske, prekini kupovinu
				return false;
			}
		//dodaj nove karte u mapu karata
		daoKarte.DodajNoveKarte(noveKarte);
		//dodaj nove karte kupcu
		getKorisniciDAO().dodajKarteKupcu(trenutni,noveKarte);
		getKorisniciDAO().dodajKarteProdavcu(noveKarte);
		//dodaj kupcu bodove i proveri status
		//getKorisniciDAO().dodajBodove(broj,cenaForKupac,trenutni);

		return true;
	}
	
	
	@POST
	@Path("/otkazi/{id}")
	public boolean otkazi(@PathParam("id") String id) {
		return getKarteDAO().otkazi(id,  getManifestacijeDAO());
	}
	
	@POST
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Collection<Karta> search(Object upit) {
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> mapa = (LinkedHashMap<String, String>) upit;
		KarteDAO daoKarte = getKarteDAO();
		ManifestacijeDAO daoManifestacije = getManifestacijeDAO();
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if(trenutni==null) return null;
		if (!(trenutni.equals(getKorisniciDAO().getByUsername(trenutni.getUsername()))))
		{
			return null;
		}
		Collection<Karta> kolekcija;
		int tip;
		switch (trenutni.getUloga()) {
		case ADMIN:
			kolekcija = new ArrayList<Karta>(daoKarte.getKarte(0,trenutni.getUsername()));
			break;
		case PRODAVAC:
			kolekcija = new ArrayList<Karta>(daoKarte.getKarte(1,trenutni.getUsername()));
			break;
		case KUPAC:
			kolekcija = new ArrayList<Karta>(daoKarte.getKarte(2,trenutni.getUsername()));
			break;
		default:
			return null;

		} 
		//karte datog korisnika
		String naziv = mapa.get("naziv").trim().toLowerCase();
		String cenaOd = mapa.get("cenaod").trim().toLowerCase();
		String cenaDo = mapa.get("cenado").trim().toLowerCase();
		String datumOd = mapa.get("datumod").trim().toLowerCase();
		String datumDo = mapa.get("datumdo").trim().toLowerCase();

		if (!naziv.equals("")) {
			kolekcija = daoKarte.searchNaziv(kolekcija, naziv,daoManifestacije);
			if (kolekcija.isEmpty()) {
				request.getSession().setAttribute("trenutneKarte", kolekcija);
				return kolekcija;
			}
		}
		if (!cenaOd.equals("")) {
			kolekcija = daoKarte.searchCenaOd(kolekcija, cenaOd);
			if (kolekcija.isEmpty()) {
				request.getSession().setAttribute("trenutneKarte", kolekcija);
				return kolekcija;
			}
		}
		if (!cenaDo.equals("")) {
			kolekcija = daoKarte.searchCenaDo(kolekcija, cenaDo);
			if (kolekcija.isEmpty()) {
				request.getSession().setAttribute("trenutneKarte", kolekcija);
				return kolekcija;
			}
		}
		if (!datumOd.equals("")) {
			kolekcija = daoKarte.searchDatumOd(kolekcija, datumOd);
			if (kolekcija.isEmpty()) {
				request.getSession().setAttribute("trenutneKarte", kolekcija);
				return kolekcija;
			}
		}
		if (!datumDo.equals("")) {
			kolekcija = daoKarte.searchDatumDo(kolekcija, datumDo);
			if (kolekcija.isEmpty()) {
				request.getSession().setAttribute("trenutneKarte", kolekcija);
				return kolekcija;
			}
		}

		request.getSession().setAttribute("trenutneKarte", kolekcija);
		return kolekcija;

	}

	
	@POST
	@Path("/filter")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Collection<Karta> filterKarti(Collection<String> listaUslova) {
		KarteDAO daoKarte = getKarteDAO();
		//uzmi karte iz sesije
		@SuppressWarnings("unchecked")
		Collection<Karta> kolekcija = ((Collection<Karta>) request.getSession().getAttribute("trenutneKarte"));
		Collection<Karta> result;
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if (!(trenutni.equals(getKorisniciDAO().getByUsername(trenutni.getUsername()))))
		{
			return null;
		}
		//ako ih nema znaci nije radjen search radi sa svima
		if(kolekcija == null) {
			int tip;
			switch (trenutni.getUloga()) {
			case ADMIN:
				kolekcija = new ArrayList<Karta>(daoKarte.getKarte(0,trenutni.getUsername()));
				break;
			case PRODAVAC:
				kolekcija = new ArrayList<Karta>(daoKarte.getKarte(1,trenutni.getUsername()));
				break;
			case KUPAC:
				kolekcija = new ArrayList<Karta>(daoKarte.getKarte(2,trenutni.getUsername()));
				break;
			default:
				return null;
			}
		}
		if (listaUslova.isEmpty()) {
			return kolekcija;
		}
		ArrayList<String> uslovi = (ArrayList<String>) listaUslova;
		result =daoKarte.filtriraj(kolekcija,listaUslova);
		return result;
	}
	
	
	@GET
	@Path("/sort/{idSorta}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Karta> sortKarte(@PathParam("idSorta") int idSorta) {
		KarteDAO daoKarte = getKarteDAO();
		ManifestacijeDAO daoManifestacije = getManifestacijeDAO();
		@SuppressWarnings("unchecked")
		Collection<Karta> kolekcija = ((Collection<Karta>) request.getSession().getAttribute("trenutneKarte"));
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if (!(trenutni.equals(getKorisniciDAO().getByUsername(trenutni.getUsername()))))
		{
			return null;
		}
		//ako ih nema znaci nije radjen search radi sa svima
		if(kolekcija == null) {
			int tip;
			switch (trenutni.getUloga()) {
			case ADMIN:
				kolekcija = new ArrayList<Karta>(daoKarte.getKarte(0,trenutni.getUsername()));
				break;
			case PRODAVAC:
				kolekcija = new ArrayList<Karta>(daoKarte.getKarte(1,trenutni.getUsername()));
				break;
			case KUPAC:
				kolekcija = new ArrayList<Karta>(daoKarte.getKarte(2,trenutni.getUsername()));
				break;
			default:
				return null;
			}
		}
		List<Karta> result = new ArrayList<Karta>(kolekcija);
		switch (idSorta) {
		case 1:		
			result = daoKarte.sortirajPoCeniKarte(result, false);
			break;
		case 2:
			result = daoKarte.sortirajPoCeniKarte(result, true);
			break;
		case 3:
			result = daoKarte.sortirajPoDatumu(result, false);
			break;
		case 4:
			result = daoKarte.sortirajPoDatumu(result, true);
			break;
		case 5:
			result = daoKarte.sortirajPoImenuManifestacije(result, daoManifestacije, false);

			break;
		case 6:
			result = daoKarte.sortirajPoImenuManifestacije(result, daoManifestacije, true);
			break;

		default:
			result = new ArrayList<Karta>();
			break;
		}
		return result;
	}
	
	
}