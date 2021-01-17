package dao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Korisnik;
import beans.Kupac;
import beans.Prodavac;

public class KorisniciDAO {
	private HashMap<String, Korisnik> mapaKorisnika;

	public KorisniciDAO() {
		mapaKorisnika = new HashMap<>();
	}
	
	public KorisniciDAO(String path) {
		mapaKorisnika = new HashMap<>();
		loadUsers(path);
	}

	public Collection<Korisnik> getAllUsers() {
		return mapaKorisnika.values();
	}

	private void loadUsers(String path) {
		ObjectMapper mapper = new ObjectMapper();
		String data = path + "data" + File.separator;
		try {
			List<Korisnik> admini = Arrays
					.asList(mapper.readValue(Paths.get(data + "admini.json").toFile(), Korisnik[].class));
			
			for (Korisnik k : admini)
				mapaKorisnika.put(k.getUsername(), k);

			List<Prodavac> prodavci = Arrays
					.asList(mapper.readValue(Paths.get(data + "prodavci.json").toFile(), Prodavac[].class));
			for (Prodavac k : prodavci)
				mapaKorisnika.put(k.getUsername(), k);

			List<Kupac> kupci = Arrays.asList(mapper.readValue(Paths.get(data + "kupci.json").toFile(), Kupac[].class));
			for (Kupac k : kupci)
				mapaKorisnika.put(k.getUsername(), k);

		} catch (IOException e) {
			System.out.println("Nesto se dogodilo");
		}
	}

	public Korisnik getByUsername(String username) {
		return mapaKorisnika.get(username);
	}

	public Korisnik prijava(String username, String password) {
		if (mapaKorisnika.containsKey(username)) {
			if (mapaKorisnika.get(username).getPassword().equals(password))
				return mapaKorisnika.get(username);
		}
		return null;
	}
}
