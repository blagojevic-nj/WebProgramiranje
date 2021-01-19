package services;

import java.util.Collection;

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
import beans.enums.Uloga;
import dao.KorisniciDAO;

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
		Korisnik user = getKorisnici().registracija(k);
		
		if(user != null) {
			request.getSession().setAttribute("korisnik", user);
		}
		return user;
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
				return getKorisnici().getAllUsers();
			}else if(trenutni.getUloga() == Uloga.PRODAVAC) {
				return getKorisnici().zaProdavca(trenutni.getUsername());
			}
		}
		
		return null;
	}
}
