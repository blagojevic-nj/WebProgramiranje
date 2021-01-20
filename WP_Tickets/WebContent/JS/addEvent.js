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
		let option = $("<option class='lok-option' value='"+lok.id+"'>"+lok.adresa+"</option>")
		select.append(option);
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
					}
				})
			}
		}
	})
	
	$("select[name='lokacija']").on('change', function(){
		let val = this.value;
		if(!val){
			$("#nova-lok-td").attr("hidden", false);
		}else{
			$("#nova-lok-td").attr("hidden", true);
		}
	})
})

