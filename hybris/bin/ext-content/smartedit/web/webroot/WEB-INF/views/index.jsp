<!DOCTYPE html>
<html>

<head>
	<title data-translate="application.name"></title>

    <!--3rd prty libs-->    
	<script src="static-resources/dist/smartedit/js/thirdparties.js"></script>
	<script src="static-resources/thirdparties/ckeditor/ckeditor.js"></script>
    
    <!-- 3rd party css -->
    <link rel="stylesheet" href="static-resources/thirdparties/eonasdan-bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="static-resources/dist/smartedit/css/outer-styling.css">

    <!--libs-->
    <script src="static-resources/smarteditcontainer/js/smarteditcontainer.js"></script>
	<script src="static-resources/smarteditloader/js/smarteditloader.js"></script>
    <meta name="viewport" content="initial-scale=1, maximum-scale=1">

</head>

<body data-ng-app="smarteditloader">
	<div ng-class="{'alert-overlay': true, 'ySEEmptyMessage': (!alerts || alerts.length == 0 ) }">
	    <alerts-box alerts="alerts" />
	</div>
    <div data-ng-view></div>
</body>

</html>
