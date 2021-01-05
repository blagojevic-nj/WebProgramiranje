package beans;

public class Komentar {
	private int id;
	private String usernameKupca;
	private int manifestacija;
	private String tekstKomentara;
	private int ocena;
	private Boolean odobren;
	private Boolean obrisan;

	public Komentar() {

	}

	public Komentar(int id, String usernameKupca, int manifestacija, String tekstKomentara, int ocena, Boolean odobren,
			Boolean obrisan) {
		super();
		this.id = id;
		this.usernameKupca = usernameKupca;
		this.manifestacija = manifestacija;
		this.tekstKomentara = tekstKomentara;
		this.ocena = ocena;
		this.odobren = odobren;
		this.obrisan = obrisan;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsernameKupca() {
		return usernameKupca;
	}

	public void setUsernameKupca(String usernameKupca) {
		this.usernameKupca = usernameKupca;
	}

	public int getManifestacija() {
		return manifestacija;
	}

	public void setManifestacija(int manifestacija) {
		this.manifestacija = manifestacija;
	}

	public String getTekstKomentara() {
		return tekstKomentara;
	}

	public void setTekstKomentara(String tekstKomentara) {
		this.tekstKomentara = tekstKomentara;
	}

	public int getOcena() {
		return ocena;
	}

	public void setOcena(int ocena) {
		this.ocena = ocena;
	}

	public Boolean getOdobren() {
		return odobren;
	}

	public void setOdobren(Boolean odobren) {
		this.odobren = odobren;
	}

	public Boolean getObrisan() {
		return obrisan;
	}

	public void setObrisan(Boolean obrisan) {
		this.obrisan = obrisan;
	}

}
