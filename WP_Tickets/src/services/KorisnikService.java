package services;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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
}
