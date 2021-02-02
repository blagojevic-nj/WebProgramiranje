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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Korisnik;
import beans.Manifestacija;
import beans.TipKupca;
import beans.enums.Uloga;
import dao.KarteDAO;
import dao.KorisniciDAO;
import dao.ManifestacijeDAO;

@Path("/korisnici")
public class KorisnikService {

	@Context
	ServletContext ctx;
	
	@Context
	HttpServletRequest request;

	private KorisniciDAO getKorisnici() {
		KorisniciDAO korisnici = (KorisniciDAO) ctx.getAttribute("KorisniciDAO");
		if (korisnici == null) {
			korisnici = new KorisniciDAO(ctx.getRealPath("."));
			ctx.setAttribute("KorisniciDAO", korisnici);
		}
		return korisnici;
	}
	
	private KarteDAO getKarteDAO() {
		KarteDAO dao = (KarteDAO) ctx.getAttribute("karte");
		if (dao == null) {
			dao = new KarteDAO(ctx.getRealPath("."));
			ctx.setAttribute("karte", dao);
		}
		return dao;
	}
	
	@GET
	@Path("/prijava")
	@Produces(MediaType.APPLICATION_JSON)
	public Korisnik prijava(@QueryParam("username") String username, @QueryParam("password") String password) {
		Korisnik user = getKorisnici().prijava(username, password);
		
		if(user != null)
			request.getSession().setAttribute("korisnik", user);
		
		return user;
	}
	
	@GET
	@Path("/odjava")
	public void odjava() {
		Korisnik trenutni = (Korisnik)request.getSession().getAttribute("korisnik");
		if(trenutni != null)
			request.getSession().setAttribute("korisnik", null);
	}
	
	@POST
	@Path("/registracija")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Korisnik registracija(Korisnik k) {
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if(trenutni == null) {
			Korisnik user = getKorisnici().registracija(k);
			
			if(user != null) {
				request.getSession().setAttribute("korisnik", user);
			}
			return user;
		}
		
		if(trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername())) && trenutni.getUloga() == Uloga.ADMIN) {
			k.setUloga(Uloga.PRODAVAC);
			Korisnik user = getKorisnici().registracija(k);
			return user;
		}
		
		return null;
	}
	
	@GET
	@Path("/pregled/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Korisnik mojProfil(@PathParam("username") String username) {
		Korisnik trenutni = (Korisnik)request.getSession().getAttribute("korisnik");
		if(trenutni.equals(getKorisnici().getByUsername(username))) {
			// sam registrovani korisnik zeli da vidi podatke o sebi
			return trenutni;
		}else {
			if(trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername())) && trenutni.getUloga().equals(Uloga.ADMIN)) {
				// sad admin hoce da pregleda nekog korisnika
				return getKorisnici().getByUsername(username);
			}
			
			else if(trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername())) && trenutni.getUloga().equals(Uloga.PRODAVAC)) {
				// sad prodavac hoce da pregleda nekog korisnika
				return getKorisnici().getByUsername(username);
			}
		}
		
		return null;
	}
	
	@GET
	@Path("/trenutni")
	@Produces(MediaType.APPLICATION_JSON)
	public Korisnik trenutni() {
		try {
			Korisnik trenutni = (Korisnik)request.getSession().getAttribute("korisnik");
			return trenutni;
		} catch(Exception e){
			return null;
		}
	}
	
	@GET
	@Path("/izmena")
	@Produces(MediaType.APPLICATION_JSON)
	public Korisnik izmena(@QueryParam("password") String password, @QueryParam("ime") String ime, @QueryParam("prezime") String prezime ) {
		Korisnik trenutni = (Korisnik)request.getSession().getAttribute("korisnik");
		if(trenutni != null && trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername()))) {
			trenutni.setIme(ime);
			trenutni.setPrezime(prezime);
			trenutni.setPassword(password);
			request.getSession().setAttribute("korisnik", trenutni);
			getKorisnici().izmena(trenutni);
			return trenutni;
		}
		return null;
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Korisnik> getAll(){
		Korisnik trenutni = (Korisnik)request.getSession().getAttribute("korisnik");
		if(trenutni != null && trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername()))) {
			if(trenutni.getUloga() == Uloga.ADMIN) {
				request.getSession().setAttribute("KorisniciIspis", getKorisnici().getAllUsers());
				return getKorisnici().getAllUsers();
			}else if(trenutni.getUloga() == Uloga.PRODAVAC) {
				return getKorisnici().zaProdavca(trenutni.getUsername());
			}
		}
		
		return null;
	}
	
	@GET
	@Path("/tip")
	@Produces(MediaType.APPLICATION_JSON)
	public TipKupca getTip(){
		Korisnik trenutni = (Korisnik)request.getSession().getAttribute("korisnik");
		if(trenutni != null && trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername())) && trenutni.getUloga() == Uloga.KUPAC) {
			return getKorisnici().getTip(trenutni.getUsername());
		}
		
		return null;
	}
	
	@GET
	@Path("/brisanje")
	public void brisanje(@QueryParam("username") String username ) {
		Korisnik trenutni = (Korisnik)request.getSession().getAttribute("korisnik");
		if(trenutni != null && trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername())) && trenutni.getUloga() == Uloga.ADMIN) {
			getKorisnici().brisanjeKorisnika(username);
		}
	}
	
	@GET
	@Path("/blokiranje")
	public void blokiranje(@QueryParam("username") String username ) {
		Korisnik trenutni = (Korisnik)request.getSession().getAttribute("korisnik");
		if(trenutni != null && trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername())) && trenutni.getUloga() == Uloga.ADMIN) {
			getKorisnici().blokiranjeKorisnika(username);
		}
	}


	@POST
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Collection<Korisnik> search(Object upit) {
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> mapa = (LinkedHashMap<String, String>) upit;
		KorisniciDAO daokorisnici = getKorisnici();
		String ime = mapa.get("ime").trim().toLowerCase();
		String prezime = mapa.get("prz").trim().toLowerCase();
		String username = mapa.get("usr").trim();
		Collection<Korisnik> result = new ArrayList<Korisnik>(daokorisnici.getAllUsers());
		if (!username.equals("")) {
			//pretraga svih sa datim username
			Korisnik k = daokorisnici.getByUsername(username);
			result.clear();
			if(k != null)			result.add(k);

		}
		if (!ime.equals("")) {
			result = daokorisnici.getByName(result,ime);
		}
		if (!prezime.equals("")) {
			result = daokorisnici.getBySurname(result,prezime);
		}
		request.getSession().setAttribute("KorisniciIspis", result);
		return result;
	}
	
	
	@POST
	@Path("/filter")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Collection<Korisnik> filterKorisnikaAdmin(Collection<String> listaUslova) {
		//uslovi{admin prodavac kupac zlatni srebrni bronzani}
		KorisniciDAO daokorisnici = getKorisnici();
		//uzmi korisnike iz sesije
		@SuppressWarnings("unchecked")
		Collection<Korisnik> kolekcija = ((Collection<Korisnik>) request.getSession().getAttribute("KorisniciIspis"));
		Collection<Korisnik> result;
		//ako ih nema znaci nije radjen search radi sa svima
		if(kolekcija == null)	result= new ArrayList<Korisnik>(daokorisnici.getAllUsers());
		else result= new ArrayList<Korisnik>(kolekcija);
		if (listaUslova.isEmpty()) {
			return result;
		}
		result = daokorisnici.filtriraj(result, listaUslova, getKarteDAO());
		//nije provereno kasno sam radio ne diraj nista!
		return result;
	}

	@GET
	@Path("/sort/{idSorta}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Korisnik> sortAdmin(@PathParam("idSorta") int idSorta) {
		KorisniciDAO dao = getKorisnici();
		@SuppressWarnings("unchecked")
		Collection<Korisnik> kolekcija = ((Collection<Korisnik>) request.getSession().getAttribute("KorisniciIspis"));
		List<Korisnik> result;
		//ako ih nema znaci nije radjen search radi sa svima
		if(kolekcija == null)	result= new ArrayList<Korisnik>(dao.getAllUsers());
		else result= new ArrayList<Korisnik>(kolekcija);
		
		
		switch (idSorta) {
		case 1:
			result = dao.sortirajPoImenu(result, false);
			break;
		case 2:
			result = dao.sortirajPoImenu(result, true);
			break;
		case 3:
			result = dao.sortirajPoPrezimenu(result, false);
			break;
		case 4:
			result = dao.sortirajPoPrezimenu(result, true);
			break;
		case 5:
			result = dao.sortirajPoUsername(result, false);
			break;
		case 6:
			result = dao.sortirajPoUsername(result, true);
			break;
		case 7:
			result = dao.sortirajPoBodovima(result, false);
			break;
		case 8:
			result = dao.sortirajPoBodovima(result, true);
			break;
		default:
			result = new ArrayList<Korisnik>();
			break;
		}
		return result;
	}
	
	
	
	
}
