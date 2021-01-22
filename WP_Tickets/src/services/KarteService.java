package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.model.ParamQualifier;

import beans.Karta;
import beans.Korisnik;
import beans.Kupac;
import beans.Prodavac;
import beans.enums.Uloga;
import dao.KarteDAO;
import dao.KorisniciDAO;


@Path("/KarteService")
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
	
	
	private KorisniciDAO getKorisnici() {
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

		if (trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername()))&& trenutni.getUloga() == Uloga.KUPAC)
		{
			karte = dao.getKarte(2, trenutni.getUsername());
		} 
		else if (trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername()))&& trenutni.getUloga() == Uloga.PRODAVAC)
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
	@Path("/otkazi/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean otkaziKartu(@PathParam("id")String idKarte)
	{
		return false;
	}
	
	
	
}
