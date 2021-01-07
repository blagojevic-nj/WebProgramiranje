package beans;

import java.time.LocalDate;
import java.util.List;

import beans.enums.Pol;
import beans.enums.Uloga;

public class Kupac extends Korisnik {
	private List<Integer> karte;
	private int brojBodova;
	private int tip;
	private Boolean blokiran;

	public Kupac() {
		super();
	}

	public Kupac(String username, String password, String ime, String prezime, Pol pol, LocalDate datumRodjenja, Uloga uloga,
			Boolean obrisan, List<Integer> karte, int brojBodova, int tip) {
		super(username, password, ime, prezime, pol, datumRodjenja, uloga, obrisan);
		this.karte = karte;
		this.brojBodova = brojBodova;
		this.tip = tip;
	}

	public List<Integer> getKarte() {
		return karte;
	}

	public void setKarte(List<Integer> karte) {
		this.karte = karte;
	}

	public int getBrojBodova() {
		return brojBodova;
	}

	public void setBrojBodova(int brojBodova) {
		this.brojBodova = brojBodova;
	}

	public int getTip() {
		return tip;
	}

	public void setTip(int tip) {
		this.tip = tip;
	}

	public Boolean getBlokiran() {
		return blokiran;
	}

	public void setBlokiran(Boolean blokiran) {
		this.blokiran = blokiran;
	}

}
