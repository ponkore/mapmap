angular.module('myapp', ['ui.bootstrap', 'ngGrid']);
var GridDemoCtrl = function ($scope) {
  $scope.myData = [{name: "吹雪", age: 50},
                   {name: "赤城", age: 43},
                   {name: "金剛", age: 27},
                   {name: "島風", age: 29},
                   {name: "雷", age: 34}];
  $scope.myOptions = {
      data: 'myData',
      columnDefs: [
        {field:'name', displayName:'名前'},
        {field:'age', displayName:'年齢', cellTemplate: 'sample-06-cellTemplate.html'}
      ]
  };
};
