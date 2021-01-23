package beans;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import dao.LocalDateTimeDeserializer;
import dao.LocalDateTimeSerializer;

public class Manifestacija {

	private int id;
	private String naziv;
	private int tip;
	private int brojMesta;
	private int brojPreostalihMesta;


	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime datumVremeOdrzavanja;
	private double cenaREGkarte;
	private Boolean aktivno;
	private Lokacija lokacija;
	private String poster;
	private Boolean obrisana;

	public Manifestacija() {

	}

	public Manifestacija(int id, String naziv, int tip, int brojMesta, LocalDateTime datumVremeOdrzavanja,
			double cenaREGkarte, Boolean aktivno, Lokacija lokacija, String poster, Boolean obrisana) {
		super();
		this.id = id;
		this.naziv = naziv;
		this.tip = tip;
		this.brojMesta = brojMesta;
		this.brojPreostalihMesta = brojMesta;
		this.datumVremeOdrzavanja = datumVremeOdrzavanja;
		this.cenaREGkarte = cenaREGkarte;
		this.aktivno = aktivno;
		this.lokacija = lokacija;
		this.poster = poster;
		this.obrisana = obrisana;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public int getTip() {
		return tip;
	}

	public void setTip(int tip) {
		this.tip = tip;
	}

	public int getBrojMesta() {
		return brojMesta;
	}

	public void setBrojMesta(int brojMesta) {
		this.brojMesta = brojMesta;
	}

	public LocalDateTime getDatumVremeOdrzavanja() {
		return datumVremeOdrzavanja;
	}

	public void setDatumVremeOdrzavanja(LocalDateTime datumVremeOdrzavanja) {
		this.datumVremeOdrzavanja = datumVremeOdrzavanja;
	}

	public double getCenaREGkarte() {
		return cenaREGkarte;
	}

	public void setCenaREGkarte(double cenaREGkarte) {
		this.cenaREGkarte = cenaREGkarte;
	}

	public Boolean getAktivno() {
		return aktivno;
	}

	public void setAktivno(Boolean aktivno) {
		this.aktivno = aktivno;
	}

	public Lokacija getLokacija() {
		return lokacija;
	}

	public void setLokacija(Lokacija lokacija) {
		this.lokacija = lokacija;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public Boolean getObrisana() {
		return obrisana;
	}

	public void setObrisana(Boolean obrisana) {
		this.obrisana = obrisana;
	}
	
	public int getBrojPreostalihMesta() {
		return brojPreostalihMesta;
	}

	public void setBrojPreostalihMesta(int brojPreostalihMesta) {
		this.brojPreostalihMesta = brojPreostalihMesta;
	}

}
