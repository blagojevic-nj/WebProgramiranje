package services;

import java.time.LocalDateTime;
import java.util.Collection;

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
import beans.Komentar;
import beans.Korisnik;
import beans.Kupac;
import beans.Manifestacija;
import beans.Prodavac;
import beans.enums.StatusKarte;
import beans.enums.Uloga;
import dao.KarteDAO;
import dao.KomentariDAO;
import dao.KorisniciDAO;

@Path("/komentari")
public class KomentariService {
	
	@Context
	ServletContext ctx;
	@Context
	HttpServletRequest request;
	
	private KomentariDAO getKomentari() {
		KomentariDAO komentari = (KomentariDAO) ctx.getAttribute("KomentariDAO");
		if (komentari == null) {
			komentari = new KomentariDAO(ctx.getRealPath("."));
			ctx.setAttribute("KomentariDAO", komentari);
		}
		return komentari;
	}
	
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
	public Collection<Komentar> zaKorisnika(){
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		Manifestacija zaPrikaz = (Manifestacija) request.getSession().getAttribute("manifestacija");
		if(trenutni == null) {
			return getKomentari().zaKupca(zaPrikaz.getId());
		}
		if(trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername()))) {
			if(trenutni.getUloga() == Uloga.ADMIN) {
				return getKomentari().zaAdmina(zaPrikaz.getId());
			}else if(trenutni.getUloga() == Uloga.KUPAC) {
				return getKomentari().zaKupca(zaPrikaz.getId());
			}else if(trenutni.getUloga() == Uloga.PRODAVAC) {
				if(((Prodavac) trenutni).getManifestacije().contains(zaPrikaz.getId())) {
					return getKomentari().zaProdavca(zaPrikaz.getId());
				}else {
					return getKomentari().zaKupca(zaPrikaz.getId());
				}
			}
		}
		
		return null;
	}
	
	@POST
	@Path("/dodaj")
	@Consumes(MediaType.APPLICATION_JSON)
	public Boolean dodaj(Komentar kom) {
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if(trenutni == null)
			return false;
		
		if(trenutni.getUloga() == Uloga.KUPAC) {
			Kupac k = (Kupac) trenutni;
			kom.setUsernameKupca(k.getUsername());
			getKomentari().dodajKomentar(kom);
			return true;
		}
		
		return false;
	}
	
	@GET 
	@Path("/provera")
	public int provera() {
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		Manifestacija zaPrikaz = (Manifestacija) request.getSession().getAttribute("manifestacija");
		if(zaPrikaz.getDatumVremeOdrzavanja().isAfter(LocalDateTime.now()))
			return -2;	// -2 vracamo ako se jos nije zavrsila
		if(trenutni == null)
			return -1; // -1 vracamo ako je neregistrovani
		
		if(trenutni.getUloga() == Uloga.KUPAC) {
			Kupac k = (Kupac) trenutni;
			
			for(String karta: k.getKarte()) {
				if(getKarteDAO().getMapaKarata().containsKey(karta)) {
					Karta temp = getKarteDAO().getMapaKarata().get(karta);
					if(temp.getManifestacija() == zaPrikaz.getId() && temp.getStatus() == StatusKarte.REZERVISANA) {
						return 0; // 0, kupac moze da dodaje komentare
					}
				}
			}
			return -1;
		}else if(trenutni.getUloga() == Uloga.PRODAVAC){
			Prodavac p = (Prodavac) trenutni;
			if(p.getManifestacije().contains(zaPrikaz.getId()))
				return 1; //1 ima odobri i odbij
			return -1;
		}
		return 2; // admin ima sve al mena odobri i odbij
	}
	
	@POST
	@Path("/odobri/{id}")
	public void odobri(@PathParam("id") int id) {
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if(trenutni.getUloga() == Uloga.PRODAVAC)
			getKomentari().odobriKomentar(id);
	}
	
	@POST
	@Path("/odbij/{id}")
	public void odbij(@PathParam("id") int id) {
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if(trenutni.getUloga() == Uloga.PRODAVAC)
			getKomentari().odbijKomentar(id);
	}
	
}
