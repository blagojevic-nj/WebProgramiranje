package services;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Manifestacija;
import dao.ManifestacijeDAO;

@Path("/Manifestacije")
public class ManifestacijeService {
	
	
	@Context
	ServletContext ctx;

	
	private ManifestacijeDAO getManifestacije()
	{
		ManifestacijeDAO manifestacije = (ManifestacijeDAO)ctx.getAttribute("manifestacije");
		if(manifestacije==null)
		{
			manifestacije = new ManifestacijeDAO(ctx.getRealPath("."));
			ctx.setAttribute("manifestacije", manifestacije);
		}
		return manifestacije;
	}
	
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Manifestacija getManifestacija(@PathParam("id") int id)
	{
		ManifestacijeDAO manifestacije = new ManifestacijeDAO(ctx.getRealPath("."));
		return manifestacije.getManifestacijaById(id);
	}
	
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Manifestacija> getAllManifestacije()
	{
		ManifestacijeDAO manifestacije = new ManifestacijeDAO(ctx.getRealPath("."));
		return manifestacije.sortirajPoDatumu(true, false);
		
	}
	
	
	
	
	
	
	
	
	
}
