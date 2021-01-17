package services;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Korisnik;
import dao.KorisniciDAO;

@Path("/korisnici")
public class KorisnikService {

	@Context
	ServletContext ctx;

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
		return getKorisnici().prijava(username, password);
	}
	
	@POST
	@Path("/registracija")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Korisnik registracija(Korisnik k) {
		return getKorisnici().registracija(k);
	}
}
