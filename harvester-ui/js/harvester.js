$(document).ready(function () {
	// var rootUrl = "http://earthserver-devel.vhosts.cite.gr/harvester-application/harvester/";
	var rootUrl = "http://localhost:8082/femme-harvester/harvester/";
	var harvestsUrl = rootUrl + 'harvests';
	var retrieveHarvestsUrl = rootUrl + 'getHarvestsUI';
	var editHarvestsUrl = rootUrl + 'editHarvests';

	function StartItemHarvest(rowID, data1, data2, data3) {
		var rowData = gridControl.CiteLiveGrid('getRowData', rowID);
		var rowKeys = gridControl.CiteLiveGrid('decodeRowId', rowID);

		//var requestData = {};
		//requestData.id = rowKeys.ID;
		var requestData = "PENDING";

		Earthserver.Client.Utilities.callWS(harvestsUrl + '/' + rowKeys.ID + '/status', 'POST', {
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

		Earthserver.Client.Utilities.callWS(harvestsUrl + '/' + rowKeys.ID + '/status', 'POST', {
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
		autoInitialize: true,
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
						isNew = modeManager.setModeAsEdit();
						item.status = data.status;
						item.endpoint = data.endpoint;
						item.endpointAlias = data.endpointAlias;
						item.endpointType = data.type;
						item.period = data.schedule.period;
						item.periodType = data.schedule.periodType;
						item.totalElements = data.currentHarvestCycle.totalElements;
						item.newElements = 	data.currentHarvestCycle.newElements;
						item.updatedElements = data.currentHarvestCycle.updatedElements;
						item.failedElements = data.currentHarvestCycle.failedElements;
						if (item.status == "ERROR"){
							item.errorMessage = data.currentHarvestCycle.errorMessage;
						}
						else {
							item.errorMessage = 'No error';
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
			requestData.type = data.endpointType;
			requestData.schedule = {};
			requestData.schedule.period = data.period;
			requestData.schedule.periodType = data.periodType;

			console.log(requestData);

			var id = keys.ID ? '/' + keys.ID : '';

			console.log("AAAAAAAa");
			console.log(id);

			Earthserver.Client.Utilities.callWS(harvestsUrl + id, 'POST', {
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

	modeManager.modeInit();
	
});

var modeManager = {

    modeInit: function() {
		var isNew = false;
		isNew = this.setModeAsNew(isNew);
    },

    setModeAsNew: function(isNew) {
		$(".btn.btn-sm.btn-default").on('click', function (e) {
			isNew = true;
			console.log(isNew);
			modeManager.newModeMessage();
    	});
		return isNew;
    },

	setModeAsEdit: function(){
		isNew = false;
		console.log(isNew);
		this.editModeMessage();
		return isNew;
	},

	editModeMessage: function(){
		console.log("=========");
		console.log("EDIT MODE");
		console.log("=========");
	},

	newModeMessage: function(){
		console.log("=========");
		console.log("NEW MODE");
		console.log("=========");
	}
};