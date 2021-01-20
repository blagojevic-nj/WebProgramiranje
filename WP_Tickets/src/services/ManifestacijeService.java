package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Korisnik;
import beans.Kupac;
import beans.Lokacija;
import beans.Manifestacija;
import beans.Prodavac;
import beans.TipManifestacije;
import beans.enums.Uloga;
import dao.KarteDAO;
import dao.KorisniciDAO;
import dao.ManifestacijeDAO;

@Path("/Manifestacije")
public class ManifestacijeService {

	@Context
	ServletContext ctx;

	@Context
	HttpServletRequest request;

	private ManifestacijeDAO getManifestacije() {
		ManifestacijeDAO manifestacije = (ManifestacijeDAO) ctx.getAttribute("manifestacije");
		if (manifestacije == null) {
			manifestacije = new ManifestacijeDAO(ctx.getRealPath("."));
			ctx.setAttribute("manifestacije", manifestacije);
		}
		return manifestacije;
	}

	private KorisniciDAO getKorisnici() {
		KorisniciDAO korisnici = (KorisniciDAO) ctx.getAttribute("KorisniciDAO");
		if (korisnici == null) {
			korisnici = new KorisniciDAO(ctx.getRealPath("."));
			ctx.setAttribute("KorisniciDAO", korisnici);
		}
		return korisnici;
	}

	private KarteDAO getKarte() {
		KarteDAO karte = (KarteDAO) ctx.getAttribute("KarteDAO");
		if (karte == null) {
			karte = new KarteDAO(ctx.getRealPath("."));
			ctx.setAttribute("KarteDAO", karte);
		}
		return karte;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Manifestacija getManifestacija(@PathParam("id") int id) {
		ManifestacijeDAO manifestacije = new ManifestacijeDAO(ctx.getRealPath("."));
		return manifestacije.getManifestacijaById(id);
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Manifestacija> getAllManifestacije() {
		ManifestacijeDAO dao = new ManifestacijeDAO(ctx.getRealPath("."));
		Collection<Manifestacija> sveManifestacije = dao.getManifestacije().values();
		request.getSession().setAttribute("manifestacije", sveManifestacije);
		return sveManifestacije;

	}

	@GET
	@Path("/Tipovi")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<TipManifestacije> getAllManifestacijeTipovi() {
		ManifestacijeDAO manifestacije = new ManifestacijeDAO(ctx.getRealPath("."));
		return manifestacije.getAllManifestacijeTipovi();

	}

	@GET
	@Path("/sort/{idSorta}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Manifestacija> sortManifestacije(@PathParam("idSorta") int idSorta) {
		ManifestacijeDAO dao = new ManifestacijeDAO(ctx.getRealPath("."));
		@SuppressWarnings("unchecked")
		List<Manifestacija> manifestacije = (List<Manifestacija>) request.getSession().getAttribute("manifestacije");
		List<Manifestacija> sortirano = null;
		switch (idSorta) {
		case 1:
			sortirano = dao.sortirajPoCeni(manifestacije, true);
			break;
		case 2:
			sortirano = dao.sortirajPoCeni(manifestacije, false);
			break;
		case 3:
			sortirano = dao.sortirajPoDatumu(manifestacije, true);
			break;
		case 4:
			sortirano = dao.sortirajPoDatumu(manifestacije, false);
			break;
		case 5:
			sortirano = dao.sortirajPoNazivu(manifestacije, true);
			break;
		case 6:
			sortirano = dao.sortirajPoNazivu(manifestacije, false);
			break;
		case 7:
			sortirano = dao.sortirajPoLokaciji(manifestacije, true);
			break;
		case 8:
			sortirano = dao.sortirajPoLokaciji(manifestacije, false);
			break;

		default:
			sortirano = new ArrayList<Manifestacija>();
			break;
		}
		request.setAttribute("manifestacije", sortirano);
		return sortirano;
	}

	@GET
	@Path("/moje_manifestacije")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Manifestacija> getMojeManifestacije() {
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if (trenutni == null) {
			return null;
		}

		if (trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername()))
				&& trenutni.getUloga() == Uloga.KUPAC) {
			Kupac kupac = (Kupac) getKorisnici().getByUsername(trenutni.getUsername());
			return getManifestacije().getMojeManifestacije(getKarte().getManifestacijeZaKarte(kupac.getKarte()));
		} else if (trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername()))
				&& trenutni.getUloga() == Uloga.PRODAVAC) {
			Prodavac prodavac = (Prodavac) getKorisnici().getByUsername(trenutni.getUsername());
			return getManifestacije().getMojeManifestacije(prodavac.getManifestacije());
		}
		return null;
	}

	@GET
	@Path("/nove")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Manifestacija> getNoveManifestacije() {
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if (trenutni == null) {
			return null;
		}

		if (trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername()))
				&& trenutni.getUloga() == Uloga.ADMIN) {
			return getManifestacije()
					.filtrirajPoAktivnom(getManifestacije().getManifestacije().values(), false);
		}
		return null;
	}
	
	@GET
	@Path("/Lokacije")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Lokacija> getAllManifestacijeLokacije() {
		return getManifestacije().getAllManifestacijeLokacije();

	}
}
