var korisnici = [{"username":"prodavacica2","password":"pass","ime":"Andja","prezime":"Andjic","pol":"ZENSKO","datumRodjenja":"1999-12-12","uloga":"PRODAVAC","obrisan":false,"manifestacije":[],"karte":[]},{"username":"prodavacica","password":"pass","ime":"Anka","prezime":"Ankic","pol":"ZENSKO","datumRodjenja":"1999-12-12","uloga":"PRODAVAC","obrisan":false,"manifestacije":[],"karte":[]},{"username":"prodavac1","password":"pass","ime":"Mare","prezime":"Prodavac","pol":"MUSKO","datumRodjenja":"1999-02-19","uloga":"PRODAVAC","obrisan":false,"manifestacije":[],"karte":[]},{"username":"user1","password":"pass","ime":"Marko","prezime":"Bjelica","pol":"MUSKO","datumRodjenja":"1998-12-12","uloga":"KUPAC","obrisan":false,"karte":[],"brojBodova":0,"tip":3,"blokiran":false},{"username":"fdsfsdf","password":"dasdasd","ime":"dasdas","prezime":"dasdas","pol":"MUSKO","datumRodjenja":"1999-12-12","uloga":"KUPAC","obrisan":false,"karte":[],"brojBodova":0,"tip":4,"blokiran":false},{"username":"user2","password":"pass","ime":"Mare","prezime":"Maruza","pol":"MUSKO","datumRodjenja":"1999-12-12","uloga":"KUPAC","obrisan":false,"karte":[],"brojBodova":0,"tip":5,"blokiran":false},{"username":"fdsfsd","password":"dasdas","ime":"dasdas","prezime":"dasdas","pol":"MUSKO","datumRodjenja":"1999-12-12","uloga":"KUPAC","obrisan":false,"karte":[],"brojBodova":0,"tip":7,"blokiran":false}]

function blok(username){
   $.get({
        url: "/WP_Tickets/rest/korisnici/blokiranje?username=" + username,
        contentType: "application/json",
        success: function(){

            alert("uspesno smo blokirali korisnika")
        }
   })
}

function brisi(username){
    $.get({
        url: "/WP_Tickets/rest/korisnici/brisanje?username=" + username,
        contentType: "application/json",
        success: function(){

            alert("uspesno smo obrisali korisnika")
        }
   })
}
function dodajRedKorisnika(korisnik){
    let tr;
    if(korisnik.uloga == "PRODAVAC")
        tr = $("<tr class='td-korisnik prodavac' id='"+korisnik.username+"'></tr>");
    else
        tr = $("<tr class='td-korisnik kupac' id='"+korisnik.username+"'></tr>");
    let slika = $("<td><img class='slika-user' src='../images/"+korisnik.pol+".png' height='50px'></td>")
    let username = $("<td><p class='username'>"+korisnik.username+"</p></td>")
    let imePrezime =  $("<td><p class='imepre'>"+korisnik.ime + " " + korisnik.prezime+"</p></td>")
    let rodjen = $("<td><p class='rodjen'>Rodjen:"+ korisnik.datumRodjenja+"</p></td>")
    let uloga =  $("<td><p class='uloga'>"+ korisnik.uloga+"</p></td>")
    let blok;
    if(korisnik.blokiran){

        blok =  $("<td><button class='odblok' onclick='blok(\""+korisnik.username+"\")'>Odblokiraj</button></td>")
    } else{
        blok =  $("<td><button class='blok' onclick='blok(\""+korisnik.username+"\")'>Blokiraj</button></td>")
    }

    let brisi =  $("<td><button class='brisi' onclick='brisi(\""+korisnik.username+"\")'>Obrisi</button></td>")

    
    tr.append(slika);
    tr.append(username);
    tr.append(imePrezime)
    tr.append(rodjen);
    tr.append(uloga);
    tr.append(blok);
    tr.append(brisi);
    $("#tabela-korisnika").append(tr);
}

$(document).ready(function(){
    for(let kor of korisnici)
        dodajRedKorisnika(kor)
})