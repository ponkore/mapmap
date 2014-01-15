angular.module('myapp', ['ui.bootstrap', 'ngGrid']);
var GridDemoCtrl = function ($scope) {
  $scope.myData = [{name: "aaa", age: 50},
                   {name: "bbb", age: 43},
                   {name: "ccc", age: 27},
                   {name: "ddd", age: 29},
                   {name: "eee", age: 34}];
  $scope.myOptions = { data: 'myData' };
};
