$(document).ready(function () {
	// var rootUtl = "http://earthserver-devel.vhosts.cite.gr/harvester-application/harvester/";
	var rootUtl = "http://localhost:8082/femme-harvester-application-devel/harvester/";
	//var rootUtl = "http://localhost:8080/harvester-application/harvester/";
	var harvestsUrl = rootUtl + 'harvests';
	var retrieveHarvestsUrl = rootUtl + 'getHarvestsUI';

	function StartItemHarvest(rowID, data1, data2, data3) {
		var rowData = gridControl.CiteLiveGrid('getRowData', rowID);
		var rowKeys = gridControl.CiteLiveGrid('decodeRowId', rowID);

		//var requestData = {};
		//requestData.id = rowKeys.ID;
		var requestData = "PENDING";

		Earthserver.Client.Utilities.callWS(harvestsUrl + '/' + rowKeys.ID, 'POST', {
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			requestData: requestData,
			onSuccess: function () {
				console.log('Harvest started');
				gridControl.CiteLiveGrid('refresh');
			},
			onError: function () {
				console.log('Harvest start error');
			}
		});
	}

	function StopItemHarvest(rowID, data1, data2, data3) {
		var rowData = gridControl.CiteLiveGrid('getRowData', rowID);
		var rowKeys = gridControl.CiteLiveGrid('decodeRowId', rowID);

		//var requestData = {};
		//requestData.id = rowKeys.ID;
		var requestData = "STOPPED";

		Earthserver.Client.Utilities.callWS(harvestsUrl + '/' + rowKeys.ID, 'POST', {
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			requestData: requestData,
			onSuccess: function () {
				console.log('Harvest stopped');
				gridControl.CiteLiveGrid('refresh');
			},
			onError: function () {
				console.log('Harvest stop error');
			}
		});
	}

	var gridControl = $(".myGrid");

	var inlineEditor = $('<div id="' + $.ui.CiteBaseControl.generateControlId() + '" class="inline-editor" style="display: none;">');
	inlineEditor.HarvesterInlineEditor({
		autoInitialize: true
	});

	gridControl.parent().append(inlineEditor);

	var getDataCallback = Earthserver.Client.Utilities.getRetrievalFunction(Earthserver.Client.Utilities.getDataFunction(retrieveHarvestsUrl));

	gridControl.CiteLiveGrid($.extend(Earthserver.Client.Utilities.toGridPP(), {

		autoInitialize: true,
		autoLoadInitialDataset: false,
		currentDisplayMode: $.ui.CiteBaseControl.DisplayMode.Edit,

		pagingType: $.ui.CiteLiveGrid.PagingType.SimpleNumbers,
		pagingPrevLabel: 'Previous',
		pagingNextLabel: 'Next',
		hasTitle: true,
		processingLabel: '<div class="sk-spinner sk-spinner-double-bounce"><div class="sk-double-bounce1"></div><div class="sk-double-bounce2"></div></div>',

		editIcon: $.ui.CiteLiveGrid.EditMode.Inline,
		addIcon: $.ui.CiteLiveGrid.AddIconMode.Callback,
		showEditIconOnEachRow: true,
		showDeleteIconOnEachRow: true,

		retrieveDataCallback: getDataCallback,
		customAddRowCallback: function (callback) {
			callback({});
		},
		customEditIconCallback: function () {
			throw "Inline Edit Not Implemented";
		},
		customDeleteRowCallback: function (rowid, keys, data, rowContext) {
			var rowData = gridControl.CiteLiveGrid('getRowData', rowid);
			var rowKeys = gridControl.CiteLiveGrid('decodeRowId', rowid);

			var requestData = {};
			requestData.id = rowKeys.ID;

			Earthserver.Client.Utilities.callWS(harvestsUrl, 'DELETE', {
				contentType: "application/json; charset=utf-8",
				dataType: "text",
				requestData: requestData,
				onSuccess: function (data) {
					console.log('Delete success');
					gridControl.CiteLiveGrid('refresh');
				},
				onError: function (data) {
					console.log('Delete error');
				}
			});
		},

		customRowButtons: [
			$.ui.CiteLiveGrid.createCustomButton('start', 'Start', null, function (rowID, data1, data2, data3) {
				StartItemHarvest(rowID, data1, data2, data3);
			}, 'startButton btn btn-success small', $.ui.CiteLiveGrid.ButtonType.Button, 120, []),
			$.ui.CiteLiveGrid.createCustomButton('stop', 'Stop', null, function (rowID, data1, data2, data3) {
				StopItemHarvest(rowID, data1, data2, data3);
			}, 'stopButton btn btn-danger small', $.ui.CiteLiveGrid.ButtonType.Button, 120, []),
		],

		inlineEditor: inlineEditor,

		inlineEditorGetItemCallback: function (keys, callback) {
		    if (callback) {

				var requestData = {};
				requestData.id = keys.ID;

				var item = {};

				Earthserver.Client.Utilities.callWS(harvestsUrl + '/' + requestData.id , 'GET' , {
					contentType : "application/json; charset=utf-8",
					dataType : "json",
					requestData : item,
					onSuccess : function (data) {
						console.log(data);
						item.endpoint = data.endpoint;
						item.endpointAlias = data.endpointAlias;
						item.period = data.schedule.period;
						item.periodType = data.schedule.periodType;
						item.totalElements = data.currentHarvestCycle.totalElements;
						item.newElements = 	data.currentHarvestCycle.newElements;
						item.updatedElements = data.currentHarvestCycle.updatedElements;
						item.failedElements = data.currentHarvestCycle.failedElements;
						if (data.currentHarvestCycle.errorMessage = "null") {
							item.errorMessage = 'No error';
						}
						else {
							item.errorMessage = data.currentHarvestCycle.errorMessage;
						}
						item.previousHarvestCycles = [];
						if (data.previousHarvestCycles != undefined ){
							for (var i=0; i < data.previousHarvestCycles.length; i++){
								item.previousHarvestCycles.push(data.previousHarvestCycles[i]);
							}
						}
						if (data.previousHarvestCycles == undefined){
							item.previousHarvests = 0;	
						}
						else {
							item.previousHarvests = data.previousHarvestCycles.length;
						}
						callback(item);
					},
					onError : function () {
						console.log("Create error");
					}
				});
				
			}
		},

		inlineEditorSaveItemCallback: function (keys, data, callback) {
			var requestData = {};
			requestData.endpoint = data.endpoint;
			requestData.endpointAlias = data.endpointAlias;
			requestData.schedule = {};
			requestData.schedule.period = data.period;
			requestData.schedule.periodType = data.periodType;

			console.log(requestData);

			Earthserver.Client.Utilities.callWS(harvestsUrl, 'POST', {
				contentType: "application/json; charset=utf-8",
				dataType: "text",
				requestData: requestData,
				onSuccess: function (data) {
					console.log("Create success");
					if (callback) callback();
				},
				onError: function (data) {
					console.log("Create error");
				}
			});
		}
	}));

	gridControl.on('init.dt', function () {
		gridControl.find('.dataTables_info').addClass('label');
	});

	gridControl.CiteLiveGrid('refresh');

	bindEvents();
	
});

function bindEvents(){
	var input = $( ":button" ).addClass( "marked" );
	var isNew = new Boolean();
	initializeMode(isNew);
	setModeAsNew(isNew);
	// cancelNewMode()
}

function initializeMode(isNew){
	isNew = Boolean(false);
	console.log(isNew);
	return isNew;
}

function setModeAsNew(isNew){
	$(".marked").on('click', function (e) {
		isNew = Boolean("true");
		console.log(isNew);
    });
	return isNew;
}

function cancelNewMode(){
	// $("#cancel").on('click', function (e) {
	// 	isNew = Boolean("false");
	// 	console.log(isNew);
    // });

	// $("button #cancel .btn btn-default").on('click', function (e) {
	// 	alert("fadsfasdfa");
    // });

	// $( "[name='cancel ']" ).click(function() {
  	// 	alert("fadsfasdfa");
	// });

	// $("#label9").find("#cancel").click(function () {
    //     alert("hi there");
    //     return false;
	// });
	// return isNew;
}