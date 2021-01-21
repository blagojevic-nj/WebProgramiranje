package beans;

import java.time.LocalDate;
import java.util.List;

import beans.enums.Pol;
import beans.enums.Uloga;

public class Prodavac extends Korisnik {
	private List<Integer> manifestacije;
	private List<String> karte;
	private Boolean blokiran;
	public Prodavac() {
		super();
	}

	public Prodavac(String username, String password, String ime, String prezime, Pol pol, LocalDate datumRodjenja,
			Uloga uloga, Boolean obrisan, List<Integer> manifestacije, List<String> karte, Boolean blokiran) {
		super(username, password, ime, prezime, pol, datumRodjenja, uloga, obrisan);
		this.manifestacije = manifestacije;
		this.karte = karte;
		this.blokiran = blokiran;
	}

	public List<Integer> getManifestacije() {
		return manifestacije;
	}

	public void setManifestacije(List<Integer> manifestacije) {
		this.manifestacije = manifestacije;
	}

	public List<String> getKarte() {
		return karte;
	}

	public void setKarte(List<String> karte) {
		this.karte = karte;
	}

	public Boolean getBlokiran() {
		return blokiran;
	}

	public void setBlokiran(Boolean blokiran) {
		this.blokiran = blokiran;
	}
}
