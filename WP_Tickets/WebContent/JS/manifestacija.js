$(document).ready(function(){
	$.get({
		url: "/WP_Tickets/rest/Manifestacije/pregled",
		contentType: "application/json",
		success: function(manifestacija){
			if(manifestacija && !manifestacija.obrisana){
				$.get({
					url : "/WP_Tickets/rest/korisnici/trenutni",
					contentType: "application/json",
					success: function(user){
					if(!user){
						tipKorisnika = "NEREGISTROVANI";
						dodajOpcijeZaKorisnika(manifestacija);
					}else{
						tipKorisnika= user.uloga
						dodajOpcijeZaKorisnika(manifestacija);
					}
					
					$.get({
						url: "/WP_Tickets/rest/Manifestacije/Tipovi",
						contentType: "application/json",
						success: function(tips){
							tipovi = tips
							$("#tip").append(getTip(manifestacija.tip));
						}
					})
				}
			})
	podesiMarker([manifestacija.lokacija.geoDuzina, manifestacija.lokacija.geoSirina])
	$("#img-postera").attr("src", manifestacija.poster)
	$("#naziv").text(manifestacija.naziv);
	$("#brojMesta").text("Broj slobodnih mesta: " + manifestacija.brojMesta);
	$("#datum-vreme").text(getDatumVreme(manifestacija.datumVremeOdrzavanja))
	$("#cena").text(manifestacija.cenaREGkarte + "din");
	$("#lokacija").append(manifestacija.lokacija.adresa);
			}
		}
	})
});

var tipKorisnika;
var tipovi = [];

function podesiMarker(koordinate){
	 var map = new ol.Map({
        view: new ol.View({center: ol.proj.transform(koordinate, 'EPSG:4326', 'EPSG:3857'), zoom:14}),
        layers: [new ol.layer.Tile({
                source: new ol.source.OSM()
        })],
        target:'map'
    });

	var marker = new ol.Feature({
        geometry: new ol.geom.Point(ol.proj.transform(koordinate, 'EPSG:4326', 'EPSG:3857')),
    });
    
    var markers = new ol.source.Vector({
        features: [marker]
    });

    var markerVectorLayer = new ol.layer.Vector({
        source: markers,
    });
    map.addLayer(markerVectorLayer);
}

function getTip(tipM){
	for(tip of tipovi){
		if(tip.id == tipM)
			return tip.nazivTipa;
	}
}

var meseci={"01": "Januar",
            "02": "Februar",
            "03": "Mart",
            "04": "April",
            "05": "Maj",
            "06": "Jun",
            "07": "Jul",
            "08": "Avgust",
            "09": "Septembar",
            "10": "Oktobar",
            "11": "Novembar",
            "12": "Decembar"}


function getDatumVreme(datumvreme){
    var date = datumvreme.split("T")[0];
    var time = datumvreme.split("T")[1];

    return date.split("-")[2] + ". "+meseci[date.split("-")[1]] + " " + date.split("-")[0] + ". u " + time.split(":")[0] +":"+time.split(":")[1];
}
var popust = -1;
function dodeliPopust(pop){
	popust = pop;
}

function dodajOpcijeZaKorisnika(manifestacija){
	if(tipKorisnika == "NEREGISTROVANI"){
		let button = $("<button id='prijava-btn' onclick='prijava()'>Prijavite se za dodatne opcije</button>")
		$("#opcije").append(button);
	}else if(tipKorisnika == "ADMIN"){
		if(!manifestacija.aktivno){
			let button = $("<button id='prijava-btn' onclick='aktiviraj("+manifestacija.id+")'>Aktivirajte ovu manifestaciju</button>")
			$("#opcije").append(button);	
		}
	} else if(tipKorisnika == "PRODAVAC"){
		/*Pravicemo se da jeste njegova manifestacija, traba da ima dugme obrisi i  izmeni*/
		alert("ovde mora provjera da li je njegova manifestacija!")
		if(!manifestacija.aktivno){
			let button = $("<button id='izmena-btn' onclick='izmeni()'>Izmena</button>")
			$("#opcije").append(button);
			let button2 = $("<button id='brisanje-btn' onclick='obrisi()'>Obrisi</button>")
			$("#opcije").append(button2);
		}
	} else if(tipKorisnika == "KUPAC"){
		$.get({
				url: "/WP_Tickets/rest/korisnici/tip",
				contentType: "application/json",
				success: function(type){
					let cena = $("<span id='cena'>Cena: "+manifestacija.cenaREGkarte * (100-type.popust)/100+"  dinara</span>");
					let select = $("<select name='tipKarte' onchange='promeniCenu("+manifestacija.cenaREGkarte+")'><option value='1'>Regular</option><option value='FAN_PIT'>2</option><option value='0'>Vip</option></select>")
					let button = $("<button id='kupi-btn' onclick='kupi("+manifestacija.brojMesta+","+manifestacija.id+")'>Kupi</button>")
					let kolicina = $("<input type='number' name='kolicina' min='0' value='0'>")
					let popust = $("<span id='popust'>Popust: "+type.popust+" %</span>");
					$("#opcije").append(cena).append(popust).append(button).append(select).append(kolicina);
				}
			})
		
	}
}

function prijava(){
	window.location.href ="../HTML/login.html";
}

function aktiviraj(manId){
	$.post({
		url: "/WP_Tickets/rest/Manifestacije/aktivacija/"+manId,
		success: function(){
			window.location.href == "../HTML/manifestacija.html";
		}
	})
}

function izmeni(){
	alert("Sad saljemo za izmenu zahtev");
}

function obrisi(){
	alert("Sad saljemo za brisanje zahtev");
}

function kupi(brojMesta, idMan){
	if(!$("input[name='kolicina']").val())
		{
			$("input[name='kolicina']").css("border", "2px solid red");
			return;
		}
	if(parseInt($("input[name='kolicina']").val()) <=0 || parseInt($("input[name='kolicina']").val()) > brojMesta){
		$("input[name='kolicina']").css("border", "2px solid red");
			return;
	}
	
	var kol = parseInt($("input[name='kolicina']").val());
	var tip = $("select[name='tipKarte'] option:selected").val();
	
	$.get({
		url: "/WP_Tickets/rest/Karte/kupi/"+idMan +"/"+kol+"/"+tip,
		contentType: "application/json",
		success: function(ok){
			if(ok){
				alert("Potvrdjeno!")
			}else{
				alert("Neka greska!")
			}
		}
	})
}	

function promeniCenu(cena){
	var popust = $("#popust").text();
	var broj = popust.split(" ")[1];
	if($("select[name='tipKarte'] option:selected").val() == "REGULAR"){
		
		$("#cena").text("Cena: " + cena*(100-broj)/100 + "  dinara");
	}else if($("select[name='tipKarte'] option:selected").val() == "FAN_PIT")
	{
	
		$("#cena").text("Cena: " + cena*2*(100-broj)/100 + " dinara");
	}else{
		
		$("#cena").text("Cena: " + cena*4*(100-broj)/100 + " dinara");
	}
}
