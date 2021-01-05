package beans;

public class Lokacija {

	private int id;
	private double geoDuzina;
	private double geoSirina;
	private String adresa;
	private Boolean obrisana;

	public Lokacija() {

	}

	public Lokacija(int id, double geoDuzina, double geoSirina, String adresa, Boolean obrisana) {
		super();
		this.id = id;
		this.geoDuzina = geoDuzina;
		this.geoSirina = geoSirina;
		this.adresa = adresa;
		this.obrisana = obrisana;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getGeoDuzina() {
		return geoDuzina;
	}

	public void setGeoDuzina(double geoDuzina) {
		this.geoDuzina = geoDuzina;
	}

	public double getGeoSirina() {
		return geoSirina;
	}

	public void setGeoSirina(double geoSirina) {
		this.geoSirina = geoSirina;
	}

	public String getAdresa() {
		return adresa;
	}

	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}

	public Boolean getObrisana() {
		return obrisana;
	}

	public void setObrisana(Boolean obrisana) {
		this.obrisana = obrisana;
	}

	@Override
	public String toString() {
		return id +"," + geoDuzina + "," + geoSirina + "," + adresa + "," + obrisana;
	}

}
