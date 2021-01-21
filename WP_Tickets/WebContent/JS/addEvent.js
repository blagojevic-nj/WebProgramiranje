var globalLokacije = [];

var map = new ol.Map({
        view: new ol.View({center: ol.proj.transform([19.84, 45.25], 'EPSG:4326', 'EPSG:3857'), zoom:14}),
        layers: [new ol.layer.Tile({
                source: new ol.source.OSM()
        })],
        target:'map'
    });

	var marker = new ol.Feature({
        geometry: new ol.geom.Point(ol.proj.transform([19.84, 45.25], 'EPSG:4326', 'EPSG:3857')),
    });
    
    var markers = new ol.source.Vector({
        features: []
    });

    var markerVectorLayer = new ol.layer.Vector({
        source: markers,
    });
    map.addLayer(markerVectorLayer);
      
    map.on('singleclick', function(evt){
   		let lokacija = $("select[name='lokacija'] option:selected").val();
   		
   		if(!lokacija){
   			map.removeLayer(markerVectorLayer);
	    	var coor = ol.proj.transform(evt.coordinate, 'EPSG:3857', 'EPSG:4326');
	    	marker = new ol.Feature({
	            geometry: new ol.geom.Point(ol.proj.transform(coor, 'EPSG:4326', 'EPSG:3857')),
	        });
	        
	        markers = new ol.source.Vector({
	            features: [marker]
	        });
	
	        markerVectorLayer = new ol.layer.Vector({
	            source: markers,
	        });
	        map.addLayer(markerVectorLayer);
   		}
  	});

function dodajTipove(tipovi){
	var select = $("select[name='tip']");
	for(let tip of tipovi){
		let option = $("<option class='tip-option' value='"+tip.id+"'>"+tip.nazivTipa+"</option>")
		select.append(option);
	}
}

function dodajLokacije(lokacije){
	var select = $("select[name='lokacija']");
	
	for(let lok of lokacije){
		if(lok.obrisana == false){
			let option = $("<option class='lok-option' value='"+lok.id+"'>"+lok.adresa+"</option>")
			select.append(option);
		}
		
	}
}
  	
$(document).ready(function() {
	$.get({
		url : "/WP_Tickets/rest/korisnici/trenutni",
		contentType: "application/json",
		success: function(user){
			if(!user || user.uloga != "PRODAVAC"){
				window.location.href = "../HTML/home.html";
			}else{
				$.get({
					url: "/WP_Tickets/rest/Manifestacije/Tipovi",
					contentType: "application/json",
					success: function(tipovi){
						dodajTipove(tipovi)
					}
				})
				
				$.get({
					url: "/WP_Tickets/rest/Manifestacije/Lokacije",
					contentType: "application/json",
					success: function(lokacije){
						dodajLokacije(lokacije)
						globalLokacije = lokacije;
					}
				})
			}
		}
	})
	
	$("select[name='lokacija']").on('change', function(){
		let val = this.value;
		if(!val){
			$("#nova-lok-td").attr("hidden", false);
			map.removeLayer(markerVectorLayer);
			markers = new ol.source.Vector({
			            features: []
			        });
			
			        markerVectorLayer = new ol.layer.Vector({
			            source: markers,
			        });
			        map.addLayer(markerVectorLayer);
		}else{
			$("#nova-lok-td").attr("hidden", true);
			for(let lokacija of globalLokacije){
				if(lokacija.id == val){
					map.removeLayer(markerVectorLayer);
		
			    	marker = new ol.Feature({
			            geometry: new ol.geom.Point(ol.proj.transform([lokacija.geoDuzina, lokacija.geoSirina], 'EPSG:4326', 'EPSG:3857')),
			        });
			        
			        markers = new ol.source.Vector({
			            features: [marker]
			        });
			
			        markerVectorLayer = new ol.layer.Vector({
			            source: markers,
			        });
			        map.addLayer(markerVectorLayer);
				}
			}
		}
	})
	
	$("#event-forma").submit(function(e){
		e.preventDefault();
		
		let naziv = $("input[name='naziv']").val();
		let brojMesta = $("input[name='brojMesta']").val();
		let datumVreme = $("input[name='datum-vreme']").val();
		let cena = $("input[name='cena']").val();
		let lokacija = $("select[name='lokacija'] option:selected").val();
		let poster = $("input[name='poster']").val();
		let tip= $("select[name='tip'] option:selected").val();
		let novaLokacija = $("input[name='nova-lokacija']").val();
		
		$("input[name='naziv']").css("border-bottom", "2px solid white");
		$("input[name='brojMesta']").css("border-bottom", "2px solid white");
		$("input[name='datum-vreme']").css("border-bottom", "2px solid white");
		$("input[name='cena']").css("border-bottom", "2px solid white");
		$("input[name='poster']").css("border-bottom", "2px solid white");
		$("input[name='nova-lokacija']").css("border-bottom", "2px solid white");
		$("#error").css("color", "white");
		
		let greska = false;
		
		if(!naziv){
			$("input[name='naziv']").css("border-bottom", "2px solid red");
			greska = true;
		}
		
		if(!brojMesta){
			$("input[name='brojMesta']").css("border-bottom", "2px solid red");
			greska = true;
		}
		
		if(!datumVreme){
			$("input[name='datum-vreme']").css("border-bottom", "2px solid red");
			greska = true;
		}
		
		if(!cena){
			$("input[name='cena']").css("border-bottom", "2px solid red");
			greska = true;
		}
		if(!poster){
			$("input[name='poster']").css("border-bottom", "2px solid red");
			greska = true;
		}
		
		if(!lokacija){
		
			if(!novaLokacija){
				$("input[name='nova-lokacija']").css("border-bottom", "2px solid red");
				greska = true;
			}else{
				if(markerVectorLayer.getSource().getFeatures().length == 0){
					$("#error").css("color", "red");
					greska = true;
				}
			}
			
		}
		
		if(!greska){
			var coor = ol.proj.transform(marker.getGeometry().getCoordinates(), 'EPSG:3857', 'EPSG:4326');
			var lok = null;
			if(lokacija){
				for(loc of globalLokacije){
					if(loc.id == lokacija){
						lok = loc;
						break;
					}
				}
			}else{
				lok = {"id": globalLokacije.length+1, "geoDuzina": coor[0], "geoSirina": coor[1], "adresa": novaLokacija, "obrisana": false };
			}
			
			
			$.post({
				url: "/WP_Tickets/rest/Manifestacije/registracija",
				data: JSON.stringify({"id": -1, "naziv": naziv, "tip": tip, "brojMesta": brojMesta, "datumVremeOdrzavanja": datumVreme, "cenaREGkarte":cena, "aktivno":false, "lokacija":lok, "poster": poster, "obrisana":false}),
				contentType: "application/json",
				success: function(bul){
					if(bul == "false")
						$("input[name='datum-vreme']").css("border-bottom", "2px solid red");
					else{
						window.location.href ="../HTML/home.html";
					}
				}
			})
		}
	})
})

