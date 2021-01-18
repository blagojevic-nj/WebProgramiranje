$(document).ready(function(){
    $("#registration-forma").submit(function(e){
        e.preventDefault();
        var username = $("input[name='username']").val();
        var password = $("input[name='password']").val();
        var ime = $("input[name='ime']").val();
        var prezime = $("input[name='prezime']").val();
        var pol = $("select[name='pol'] option:selected").val();
        var datumRodjenja = $("input[name='datum-rodjenja']").val();
       
        var greska = false;

        if(!username){
            greska = true;
            $("input[name='username']").css("border-bottom", "2px solid red");
        }

        if(!password){
            greska = true;
            $("input[name='password']").css("border-bottom", "2px solid red");
        }

        if(!ime){
            greska = true;
            $("input[name='ime']").css("border-bottom", "2px solid red");
        }

        if(!prezime){
            greska = true;
            $("input[name='prezime']").css("border-bottom", "2px solid red");
        }

        if(!datumRodjenja){
            greska = true;
            $("input[name='datum-rodjenja']").css("border-bottom", "2px solid red");
        }
            
        if(!greska){
            $.post({
			url: "/WP_Tickets/rest/korisnici/registracija",
			contentType: 'application/json',
			data: JSON.stringify({"username": username, "password":password, "ime": ime, "prezime":prezime, "pol":pol, "datumRodjenja":datumRodjenja, "uloga":"KUPAC", "obrisan": false}),
			success: function(korisnik){
				if(korisnik == null){
					$("#error").show();
					$("input[name='username']").css("border-bottom", "2px solid red");
				}else{
					var value = '<%= session.getAttribute("korisnik") %>';
					alert(value);
					window.location.href = "../HTML/home.html";
				}
			}
		})
        }
    });
});