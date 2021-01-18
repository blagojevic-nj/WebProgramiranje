package beans;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import beans.enums.Pol;
import beans.enums.Uloga;
import dao.LocalDateDeserializer;
import dao.LocalDateSerializer;

public class Korisnik {

	private String username;
	private String password;
	private String ime;
	private String prezime;
	private Pol pol;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate datumRodjenja;
	private Uloga uloga;
	private Boolean obrisan;

	public Korisnik(String username, String password, String ime, String prezime, Pol pol, LocalDate datumRodjenja,
			Uloga uloga, Boolean obrisan) {
		super();
		this.username = username;
		this.password = password;
		this.ime = ime;
		this.prezime = prezime;
		this.pol = pol;
		this.datumRodjenja = datumRodjenja;
		this.uloga = uloga;
		this.obrisan = obrisan;
	}

	public Korisnik() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public Pol getPol() {
		return pol;
	}

	public void setPol(Pol pol) {
		this.pol = pol;
	}

	public LocalDate getDatumRodjenja() {
		return datumRodjenja;
	}

	public void setDatumRodjenja(LocalDate datumRodjenja) {
		this.datumRodjenja = datumRodjenja;
	}

	public Uloga getUloga() {
		return uloga;
	}

	public void setUloga(Uloga uloga) {
		this.uloga = uloga;
	}

	public Boolean getObrisan() {
		return obrisan;
	}

	public void setObrisan(Boolean obrisan) {
		this.obrisan = obrisan;
	}

	@Override
	public String toString() {
		return username + "," + password + "," + ime + "," + prezime + "," + pol + "," + datumRodjenja + "," + uloga;
	}

	@Override
	public boolean equals(Object obj) {
		Korisnik k;
		try {
			k = (Korisnik) obj;
		} catch (Exception e) {
			return false;
		}

		return this.username.equals(k.username) && this.password.equals(k.password) && this.ime.equals(k.ime)
				&& this.prezime.equals(k.prezime) && this.uloga.equals(k.uloga);
	}

}