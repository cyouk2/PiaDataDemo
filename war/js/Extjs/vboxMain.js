Ext.require(['*']);
Ext.onReady(function() {

	Ext.QuickTips.init();
	
	var bd = Ext.getBody();
	
	Ext.define('ImageModel', {
		extend : 'Ext.data.Model',
		fields : [ 'id', 'kind', 'playDate', 'taiNo', 'bonusCount',
				'ballInput', 'ballOutput', 'rate' ]
	});

	var piaDataStore = Ext.create('Ext.data.JsonStore', {
		model : 'ImageModel',
		proxy : {
			type : 'ajax',
			url : 'GetPiaData',
			reader : {
				type : 'json',
				root : 'root'
			}
		}
	});
	
	var dsTaiNo  = Ext.create('Ext.data.Store',{
	    fields : ['taiNo','taiNoName'],
	    data: []
	});

	Ext.Ajax.request({
	    url : 'GetTaiNoList',
	    params : {},
	    success : function(r){
	        var res = Ext.decode(r.responseText, true);
    		var records = [];
            Ext.each(res.root, function(obj){
                records.push({
                	taiNo: obj.taiNo,
                	taiNoName: obj.taiNo
                })
            });
            dsTaiNo.loadData(records);
	    },
	    failure : function(r){

	    }
	});
	
	var btnSreachByTaiNo = Ext.create('Ext.Button', {
		text : '検索',
		handler : function() {
			piaDataStore.load({ url : 'GetPiaData?taiNo=' + combTaiNo.getValue() });
		}
	});

	var combTaiNo = Ext.create('Ext.form.field.ComboBox', {
	 	fieldLabel: 'TAI_NO',
	    store: dsTaiNo,
        queryMode: 'local',
	    displayField: 'taiNo',
	    valueField: 'taiNoName'
	});

	var piaDataGrid = Ext.create('Ext.grid.Panel', {
		store : piaDataStore,
		flex: 0.7,
		title : 'データ',
		columns : [{
			text : 'id',
			hidden : true,
			dataIndex : 'id'
		}, {
			text : 'PLAY_DATE',
			width : 75,
			sortable : true,
			dataIndex : 'playDate'
		}, {
			text : 'TAI_NO',
			width : 75,
			sortable : true,
			dataIndex : 'taiNo'
		}, {
			text : 'BONUS_COUNT',
			width : 100,
			sortable : true,
			dataIndex : 'bonusCount'
		}, {
			text : 'RATE',
			width : 75,
			sortable : true,
			dataIndex : 'rate'
		}, {
			text : 'BALL_OUTPUT',
			width : 100,
			sortable : true,
			dataIndex : 'ballOutput'
		}],
		listeners:{
			select : function(dv, record, item, index, e){
				piaDataFormPanel.getForm().loadRecord(record);
			}
		}
	});
	// bonusCount
	var chartBonusCount = Ext.create('Ext.chart.Chart', {
		animate : false,
		store : piaDataStore,
		axes : [ {
			type : 'Numeric',
			minimum : 0,
			position : 'left',
			fields : [ 'bonusCount' ],
			title : false,
			grid : true,
			label : {
				renderer : Ext.util.Format.numberRenderer('0,0'),
				font : '9px Arial'
			}
		}, {
			type : 'Category',
			position : 'bottom',
			fields : [ 'playDate' ],
			title : false,
			label : {
				font : '9px Arial',
				renderer : function(name) { return name.substring(6, 8); }
			}
		} ],
		series : [ {
			type : 'line',
			axis : 'left',
			xField : 'playDate',
			yField : 'bonusCount',
			tips : {
				trackMouse : true,
				width : 90,
				height : 30,
				renderer : function(storeItem, item) {
					this.setTitle(storeItem.get('bonusCount') + ' 大当たり数 ');
				}
			},
			style : {
				fill : '#38B8BF',
				stroke : '#38B8BF'
			},
			markerConfig : {
				type : 'circle',
				size : 4,
				radius : 4,
				fill : '#38B8BF',
				stroke : '#38B8BF'
			}
		} ]
	});

	 var chartBallOutput_Column = Ext.create('Ext.chart.Chart', {
	        shadow: true,
	        animate: true,
	        store: piaDataStore,
	        axes: [{
	            type: 'Numeric',
	            position: 'left',
	            fields: ['ballOutput'],
	            hidden: true
	        }, {
	            type: 'Category',
	            position: 'bottom',
	            fields: ['playDate'],
	            label: {
	                font: '9px Arial',
		            renderer : function(name) { return name.substring(6, 8); }
	            }
	        }],
	        series: [{
	            type: 'column',
	            axis: 'left',
	            highlight: true,
	            style: {
	                fill: '#456d9f'
	            },
	            highlightCfg: {
	                fill: '#a2b5ca'
	            },
	            label: {
	                contrast: true,
	                display: 'insideEnd',
	                field: 'ballOutput',
	                color: '#333',
	                orientation: 'vertical',
	                'text-anchor': 'middle'
	            },
	            xField: 'playDate',
	            yField: ['ballOutput']
	        }]        
	    });
	 
	function activateHandler(tab){
		piaDataStore.reload();
	}
	var panel1 = Ext.create('Ext.Panel', {
		width : 1000,
		bodyPadding : '20',
		title : 'BALL_OUT_COLUMN',
		layout : 'fit',
		items : [chartBallOutput_Column],
		listeners:{activate:activateHandler}
	});
	
	var panel2 = Ext.create('Ext.Panel', {
		width : 1000,
		title : 'BONUS',
		layout : 'fit',
		items : [chartBonusCount],
		listeners:{activate:activateHandler}
	});
	
	var piaDataFormPanel = Ext.create('Ext.form.Panel', {
		flex: 0.3,
		layout : 'form',
		url : 'SavePiaData',
		standardSubmit:false,
		frame : true,
		title : '入力',
		bodyPadding : '5 5 0',
		fieldDefaults : {
			msgTarget : 'side',
			labelWidth : 60
		},
		defaultType : 'textfield',
		items : [ {
			xtype : 'hiddenfield',
			name : 'id',
			value : ''
		}, {
			fieldLabel : 'playDate',
			xtype : 'datefield',
			name : 'playDate',
			allowBlank : false,
			maxValue: new Date(),
			value : new Date(),
			 format: 'Ymd',
		}, {
			fieldLabel : 'taiNo',
			name : 'taiNo',
			allowBlank : false,
			xtype : 'numberfield',
			value:570
		}, {
			fieldLabel : 'bonusCount',
			name : 'bonusCount',
			xtype : 'numberfield',
			minValue : 0,
			value:0,
			maxValue : 100
		},{
			fieldLabel : 'rate',
			name : 'rate',
			xtype : 'numberfield',
			minValue : 0,
			value:0
		},{
			fieldLabel : 'ballOutput',
			name : 'ballOutput',
			xtype : 'textfield',
			value:"0"
		}],
		buttons : [ {
	        text: 'SAVE',
	        formBind: true, //only enabled once the form is valid
	        disabled: true,
	        handler: function() {
	            var inputform = this.up('form').getForm();
	            inputform.url = 'SavePiaData';
	            if (inputform.isValid()) {
	            	inputform.submit({
	                    success: function(form, action) {
	                    	var strTaiNo = form.getValues().taiNo
	                    	combTaiNo.setValue(strTaiNo);
	                    	piaDataStore.load({ url : 'GetPiaData?taiNo=' + strTaiNo });
	                    },
	                    failure: function(form, action) {
	                        Ext.Msg.alert('Failed', action.result.msg);
	                    }
	                });
	            }
	        }
	    }, {
	        text: 'UPDATE',
	        formBind: true, //only enabled once the form is valid
	        disabled: true,
	        handler: function() {
	            var inputform = this.up('form').getForm();
	            inputform.url = 'UpdatePiaData';
	            var id = inputform.getValues().id;
	            if (id == null || id ==''){
	            	Ext.Msg.alert('Check', '既存データが選択してください。');
	            	return;
	            }
	            if (inputform.isValid()) {
	            	inputform.submit({
	                    success: function(form, action) {
	                    	var strTaiNo = form.getValues().taiNo
	                    	combTaiNo.setValue(strTaiNo);
	                    	piaDataStore.reload();
	            			inputform.reset();
	                        Ext.Msg.alert('Success', action.result.msg);
	                    },
	                    failure: function(form, action) {
	                        Ext.Msg.alert('Failed', action.result.msg);
	                    }
	                });
	            }
	        }
	    },  {
	        text: 'DELETE',
	        formBind: true, //only enabled once the form is valid
	        disabled: true,
	        handler: function() {
	            var inputform = this.up('form').getForm();
	            inputform.url = 'DeletePiaData';
	            var id = inputform.getValues().id;
	            if (id == null || id ==''){
	            	Ext.Msg.alert('Check', '既存データが選択してください。');
	            	return;
	            }
	            if (inputform.isValid()) {
	            	inputform.submit({
	                    success: function(form, action) {
	                    	var strTaiNo = form.getValues().taiNo
	                    	combTaiNo.setValue(strTaiNo);
	                    	piaDataStore.reload();
	            			inputform.reset();
	            			Ext.Ajax.request({
	            			    url : 'GetTaiNoList',
	            			    params : {},
	            			    success : function(r){
	            			        var res = Ext.decode(r.responseText, true);
	            		    		var records = [];
	            		            Ext.each(res.root, function(obj){
	            		                records.push({
	            		                	taiNo: obj.taiNo,
	            		                	taiNoName: obj.taiNo
	            		                })
	            		            });
	            		            dsTaiNo.loadData(records);
	            			    },
	            			    failure : function(r){

	            			    }
	            			});
	                        Ext.Msg.alert('Success', action.result.msg);
	                    },
	                    failure: function(form, action) {
	                        Ext.Msg.alert('Failed', action.result.msg);
	                    }
	                });
	            }
	        }
	    }, {
			text : 'Cancel',
			handler : function() {
				this.up('form').getForm().reset();
			}
		} ]
	});

	
	var piaDataTabPanel = Ext.create('Ext.tab.Panel', {
		activeTab: 0,
		items : [{
			title : 'FORM',
			layout: {type: 'hbox', align: 'stretch'},
            border: false,
            bodyStyle: 'background-color: transparent',
            items: [piaDataFormPanel, piaDataGrid],
    		listeners:{activate:activateHandler}
        }, panel2,panel1],
		tbar : [combTaiNo, btnSreachByTaiNo]
	});
	
	var viewport = Ext.create('Ext.Viewport', {
		layout : {
			type : 'fit',
			padding : 5
		},
		defaults : {
			split : true
		},
		items : [piaDataTabPanel]
	});
});