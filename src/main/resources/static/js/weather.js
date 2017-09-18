$(document).ready(function() {

    var endpoint = "https://query.yahooapis.com/v1/public/yql?format=json";
    var query = "select name, admin1.content, locality1.content, country.content, woeid from geo.places where text='%s' and lang='es'";
    var selectedLocation = {};

    $(".wb-search").autocomplete({
        source: function(request, response) {
            $.ajax({
                url: endpoint,
                type: "GET",
                dataType: "json",
                data: {
                    q: query.replace("%s", request.term),
                    field: "name"
                },
                success: function(data) {
                    event.preventDefault();
                    var result = data.query.results;
                    if(result != null) {
                        console.log(result.place);
                        response($.map(result.place instanceof Array ? result.place : [result.place], function (item, key) {
                            return {
                                label: item.name + ", " + item.admin1 + ", " + item.country,
                                value: {
                                    woeid : item.woeid,
                                    city: item.admin1,
                                    locality: item.locality1,
                                    country: item.country
                                }
                            }
                        }));
                    }
                }
            });
        },
        minLength: 5,
        select: function(event, ui) {
            event.preventDefault();
            console.log("selected item: " + ui.item.label);
            $(".wb-search").val(ui.item.label);
            selectedLocation = JSON.stringify(ui.item.value);
            console.log(selectedLocation);
        },
        focus: function(event, ui) {
            event.preventDefault();
            $(".wb-search").val(ui.item.label);
        }
    });

    $(".login").click(function() {
        $.ajax({
            url: "/boards",
            type: "GET",
            contentType: "application/json",
            data: $(".wb-login").val(),
            success: function(data) {
                window.location.replace("/boards/" + $(".wb-login").val());
            },
            error: function(xhr, status, text) {
                alert("La locación no pudo ser eliminada");
            }
        });
    });

    $(".signup").click(function() {
        $.ajax({
            url: "/boards",
            type: "POST",
            contentType: "application/json",
            data: $(".wb-login").val(),
            success: function(data) {
                window.location.replace("/boards/" + $(".wb-login").val());
            },
            error: function(xhr, status, text) {
                if(xhr.status == 409) {
                    alert("El usuario ya existe");
                }
            }
        });
    });


    $(".add").click(function() {
        $.ajax({
            url: "/boards/panda/locations",
            type: "POST",
            contentType: "application/json",
            data: selectedLocation,
            success: function(data) {
                alert("Locación agregada");
                location.reload();
            },
            error: function(xhr, status, text) {
                if(xhr.status == 409) {
                    alert("La locación ya fue agregada");
                }
            }
        });
    });

    $(".remove").click(function() {
        $.ajax({
            url: "/boards/panda/locations/"+$(this).attr("id"),
            type: "DELETE",
            contentType: "application/json",
            success: function(data) {
                alert("Locación eliminada");
                location.reload();
            },
            error: function(xhr, status, text) {
                alert("La locación no pudo ser eliminada");
            }
        });
    });

});