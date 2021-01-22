package beans;

import java.time.LocalDate;
import java.util.List;

import beans.enums.Pol;
import beans.enums.Uloga;

public class Kupac extends Korisnik {
	private List<String> karte;
	private int brojBodova;
	private int tip;

	public Kupac() {
		super();
	}

	public Kupac(String username, String password, String ime, String prezime, Pol pol, LocalDate datumRodjenja, Uloga uloga,
			Boolean obrisan, Boolean blokiran, List<String> karte, int brojBodova, int tip) {
		super(username, password, ime, prezime, pol, datumRodjenja, uloga, obrisan, blokiran);
		this.karte = karte;
		this.brojBodova = brojBodova;
		this.tip = tip;
	}

	public List<String> getKarte() {
		return karte;
	}

	public void setKarte(List<String> karte) {
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

}
