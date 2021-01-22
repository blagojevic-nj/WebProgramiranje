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
import beans.TipKupca;
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
	@Path("/kupi/{id}/{broj}/{tipKarte}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean otkaziKartu(@PathParam("id")String id,@PathParam("broj")int broj,@PathParam("tipKarte")int tipKarte)
	{
		KarteDAO daoKarte = getKarteDAO();
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if (!(trenutni.equals(getKorisniciDAO().getByUsername(trenutni.getUsername()))&&trenutni.getUloga() == Uloga.KUPAC))
		{
			return false;
		}
		ManifestacijeDAO daoManifestacije = getManifestacijeDAO();
		//if(daoManifestacije.kupiKartu(id,broj,daoKarte,trenutni)==null)
		//	{
		//		return false;
		//	}
		//karte kupljene u manifestacije dao!!!
		
		
		
		
		
		KorisniciDAO daoKorisnici = getKorisniciDAO();
		//daoKorisnici.kupiKarte()
		
		
		
		return false;
	}
	
	
	
}
