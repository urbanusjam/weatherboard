var app = angular.module('weather', ['ngRoute', 'ngResource', 'ui.bootstrap', 'ngMaterial', 'ngMessages']);

app.config(function($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'views/login.html',
            controller: 'loginController'
        })
        .when('/login', {
            templateUrl: 'views/login.html',
            controller: 'loginController'
        })
        .when('/boards', {
            templateUrl: 'views/login.html',
            controller: 'loginController'
        })
        .when('/boards/:username', {
            templateUrl: 'views/board.html',
            controller: 'boardController'
        })
        .when('/boards/:username/locations', {
            templateUrl: 'views/board.html',
            controller: 'boardController'
        })
        .otherwise({
            redirectTo: '/login'
        });
});

app.controller('loginController', function($scope, $http, $routeParams, $location, $mdDialog, common) {

    $scope.login = function() {
        $http.get('/login', {
                params: {
                    username: $scope.username
                }
            })
            .then(function(result) {
                $scope.locations = result.data.locations;
                $location.path('/boards/' + $scope.username);

            })
            .catch(function onError(response) {
                console.log(response);
                common.showAlert('El usuario no está registrado');
            });
    };

    $scope.register = function() {
        $http.post('/register', $scope.username)
            .then(function(result) {
                $location.path('/boards/' + $scope.username);
            })
            .catch(function onError(response) {
                if (response.status == 409) {
                    common.showAlert('El usuario ya está registrado');
                } else {
                    common.showAlert('La registración no pudo completarse. Error:', response.status);
                }
            });
    }

});

app.controller('boardController', function($scope, $http, $routeParams, $location, $mdDialog, common) {


    $http.get('/boards/' + $routeParams.username)
        .then(function(result) {
            console.log(result);
            $scope.currentUser = $routeParams.username;
            $scope.locations = result.data;
        })
        .catch(function onError(response) {
            console.log(response);
            $location.path('/login');
        });


    $scope.querySearch = function(searchText) {
        var url = "https://query.yahooapis.com/v1/public/yql?format=json&q=select name, admin1.content, locality1.content, country.content, woeid from geo.places where text='" + searchText + "' and lang='es'";
        return $http.get(url)
            .then(function(response) {
                var places = response.data.query.results;
                if (places != null) {
                    return places.place instanceof Array ? places.place : [places.place]
                } else {
                    return null;
                }
            })
            .catch(function onError(response) {
                console.log(response);
            });
    };

    $scope.searchItemLabel = function(item) {
        return item.name + ", " + item.admin1 + ", " + item.country;
    }

    $scope.searchItemChange = function(item) {
        $scope.selectedItem = item;
    }

    $scope.addLocation = function() {
         console.log($scope.selectedItem);

        $http.post('/boards/' + $scope.currentUser + '/locations', angular.toJson($scope.selectedItem) )
            .then(function(response) {
                $scope.locations.push(response.data);
            })
            .catch(function onError(response) {
                if (response.status == 409) {
                    common.showAlert('La locación ya está registrada');
                } else {
                    common.showAlert('La locación no pudo añadirse.');
                }
            });
    }

    $scope.removeLocation = function(ev, location) {
        var id = location.woeid;
        var confirm = $mdDialog.confirm(ev)
            .targetEvent(ev)
            .textContent('¿Confirma que desea eliminar la locación?')
            .ok('Aceptar')
            .cancel('Cancelar');

        $mdDialog.show(confirm).then(function() {
                $http.delete("/boards/" + $scope.currentUser + "/locations/" + id)
                    .then(function(response) {
                        $scope.locations.splice($scope.locations.indexOf(id), 1);
                    })
                    .catch(function onError(response) {
                        console.log(response);
                        common.showAlert("No se pudo eliminar la locación");
                    });
            }).catch(() => {
                // do nothing on cancel
                // prevent angular 'Possibly unhandled rejection' error message
            });
    }

});

app.directive('wbLocation', function() {
    return {
        restrict: "E",
        template:  "<div class='wb-location-wrapper'>" +
            "<div class='wb-location-left'>" +
            "<span class='wb-title-city'>{{location.name}}</span>" +
            "<span class='wb-title-country'>{{location.country}}</span>" +
            "<span class='wb-date'>{{location.status.date}}</span>" +
            "</div>" +
            "<div class='wb-location-right'>" +
            "<span class='wb-temp'>{{location.status.temperature}} ºC</span>" +
            "<span class='wb-desc'>{{location.status.description}}</span>" +
            "<span class='wb-humidity'>Humedad: {{location.status.humidity}}%</span>" +
            "<span class='wb-pressure'>Presión: {{location.status.pressure}} HP</span> " +
            "</div>"+
            "<span class='wb-update'>Actualizado: {{location.status.lastUpdated}}</span>"+
            "<a class='remove' ng-click='removeLocation($event, location)' style='cursor: pointer'>Quitar</a>"+
            "</div>"

    }

});

app.directive('wbFooter', function() {
    return {
        restrict: "EA",
        template: "<span> The WeatherBoard Test Project - 2017 / by Cora Reyes Calens" +
         "</br><a target='_blank' href='https://github.com/urbanusjam'> github.com/urbanusjam </a> </span>"
    }
})

app.service('common', function($mdDialog) {
    this.showAlert = function(msg) {
        $mdDialog.show(
            $mdDialog.alert()
            .parent(angular.element(document.querySelector('#popupContainer')))
            .clickOutsideToClose(true)
            .textContent(msg)
            .ok('Cerrar')
        );
    };
});