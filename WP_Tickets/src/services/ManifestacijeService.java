package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
		ManifestacijeDAO manifestacije = getManifestacije();
		return manifestacije.getManifestacijaById(id);
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Manifestacija> getAllManifestacije() {
		ManifestacijeDAO dao = getManifestacije();
		Collection<Manifestacija> sveManifestacije = dao.getManifestacije().values();
		request.getSession().setAttribute("manifestacijeList", sveManifestacije);
		return sveManifestacije;

	}

	@GET
	@Path("/Tipovi")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<TipManifestacije> getAllManifestacijeTipovi() {
		ManifestacijeDAO manifestacije = getManifestacije();
		return manifestacije.getAllManifestacijeTipovi();

	}

	@GET
	@Path("/sort/{idSorta}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Manifestacija> sortManifestacije(@PathParam("idSorta") int idSorta) {
		ManifestacijeDAO dao = getManifestacije();
		@SuppressWarnings("unchecked")
		Collection<Manifestacija> kolekcija = ((Collection<Manifestacija>) request.getSession().getAttribute("manifestacijeList"));
		ArrayList<Manifestacija>manifestacije = new ArrayList<Manifestacija>(kolekcija);
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
	
	@POST
	@Path("/filter")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Collection<Manifestacija> filterManifestacije(Collection<Integer> listaIdFiltera) {
		@SuppressWarnings("unchecked")
		Collection<Manifestacija> kolekcija = ((Collection<Manifestacija>) request.getSession().getAttribute("manifestacijeList"));
		if(listaIdFiltera.isEmpty())
		{
			return kolekcija;
		}
		ArrayList<Integer>uslovi = (ArrayList<Integer>)listaIdFiltera;
		ManifestacijeDAO dao = getManifestacije();
		List<Manifestacija>manifestacije = new ArrayList(kolekcija);
		List<Manifestacija> filtrirano=null;
		if(uslovi.get(0)==-1)
		{
			uslovi.remove(0);
			//filtriraj nerasprodate
			filtrirano = dao.filtriranjePoTipu(filtrirano, uslovi);

			
		}else {
			filtrirano = dao.filtriranjePoTipu(manifestacije, uslovi);
		}
		request.setAttribute("manifestacije", filtrirano);
		
		/**/
		return filtrirano;
		//Ne radi jos al nije ni zavrseno fali mi funkcija za nerasprodate iz karteDAO
	}

	@GET
	@Path("/moje_manifestacije")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
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
			ManifestacijeDAO dao = getManifestacije();
			return dao.filtrirajPoAktivnom(dao.getNeobrisaneManifestacije(), false).values();
		}
		return null;
	}
	
	@GET
	@Path("/Lokacije")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Lokacija> getAllManifestacijeLokacije() {
		return getManifestacije().getAllManifestacijeLokacije();

	}
	
	@POST
	@Path("/registracija")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean registracija(Manifestacija m) {
		Korisnik trenutni = (Korisnik) request.getSession().getAttribute("korisnik");
		if (trenutni == null) {
			return false;
		}

		if (trenutni.equals(getKorisnici().getByUsername(trenutni.getUsername()))
				&& trenutni.getUloga() == Uloga.PRODAVAC) {
			return getManifestacije().RegistracijaNoveManifestacije(m);
		}
		return false;
	}
	
	
	@POST
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Collection<Manifestacija>search(Object upit) {
		LinkedHashMap<String,String> mapa=(LinkedHashMap<String,String>)upit;
		@SuppressWarnings("unchecked")
		Collection<Manifestacija> kolekcija = ((Collection<Manifestacija>) request.getSession().getAttribute("manifestacijeList"));
		ManifestacijeDAO dao = getManifestacije();

	//let upit = {naziv:naziv, lokacija:lokacija, tipLokacije:"adresa",cenaOd:cenaOd, cenaDo:cenaDo,datumOd:datumOd,datumDo:datumDo}

		String naziv = mapa.get("naziv").trim();
		String cenaOd = mapa.get("cenaOd").trim();
		String cenaDo = mapa.get("cenaDo").trim();
		String datumOd = mapa.get("datumOd").trim();
		String datumDo = mapa.get("datumDo").trim();
		String lokacija = mapa.get("lokacija").trim();
		String tipLokacije = mapa.get("tipLokacije").trim();

		
		if(!naziv.equals(""))
		{
			kolekcija = dao.searchNaziv(kolekcija,naziv);
			if(kolekcija.isEmpty())
				{
					request.setAttribute("manifestacije", kolekcija);
					return kolekcija;
				}
		}
		if(!cenaOd.equals(""))
		{
			kolekcija = dao.searchCenaOd(kolekcija,cenaOd);
			if(kolekcija.isEmpty())
			{
				request.setAttribute("manifestacije", kolekcija);
				return kolekcija;
			}
		}
		if(!cenaDo.equals(""))
		{
			kolekcija = dao.searchCenaDo(kolekcija,cenaDo);
			if(kolekcija.isEmpty())
			{
				request.setAttribute("manifestacije", kolekcija);
				return kolekcija;
			}
		}
		if(!datumOd.equals(""))
		{
			kolekcija = dao.searchDatumOd(kolekcija,datumOd);
			if(kolekcija.isEmpty())
			{
				request.setAttribute("manifestacije", kolekcija);
				return kolekcija;
			}
		}
		if(!datumDo.equals(""))
		{
			kolekcija = dao.searchDatumDo(kolekcija,datumDo);
			if(kolekcija.isEmpty())
			{
				request.setAttribute("manifestacije", kolekcija);
				return kolekcija;
			}
		}
		if(!lokacija.equals(""))
		{
			kolekcija = dao.searchLokacija(kolekcija,lokacija,tipLokacije);
			if(kolekcija.isEmpty())
			{
				request.setAttribute("manifestacije", kolekcija);
				return kolekcija;
			}
		}
		
		request.setAttribute("manifestacije", kolekcija);
		return kolekcija;
	
	
	
	
	
	
	
	
	}
	
}
