package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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
	
	
}