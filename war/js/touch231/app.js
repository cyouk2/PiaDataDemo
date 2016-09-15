Ext.define('taiNoModel', {
	extend : 'Ext.data.Model',
	config : {
		fields : [{
				name : 'taiNo',
				type : 'string'
			}
		]
	}
});

Ext.define('piaDataModel', {
	extend : 'Ext.data.Model',
	config : {
		fields : [{
				name : 'id',
				type : 'string'
			}, {
				name : 'playDate',
				type : 'string'
			}, {
				name : 'taiNo',
				type : 'integer'
			}, {
				name : 'bonusCount',
				type : 'integer'
			}, {
				name : 'ballInput',
				type : 'integer'
			}, {
				name : 'ballOutput',
				type : 'integer'
			}, {
				name : 'rate',
				type : 'integer'
			}, {
				name : 'playDateN',
				type : 'string'
			}, {
				name : 'bonusCountN',
				type : 'integer'
			}, {
				name : 'ballInputN',
				type : 'integer'
			}, {
				name : 'ballOutputN',
				type : 'integer'
			}, {
				name : 'rateN',
				type : 'integer'
			}, {
				name : 'totalOut',
				type : 'integer'
			}, {
				name : 'outMax',
				type : 'integer'
			}
		]
	}
});

Ext.application({
	// name
	name : 'Sencha',
	
	getPlayDate : function () {
		var dataOfPlayDate = [];
		for (var i = 0; i >= -62; i--) {
			var dateTmp = Ext.Date.add(new Date(), Ext.Date.DAY, i);
			var strPattern = i == 0 ? 'm/d （本日）' : 'm/d （' + (0 - i) + '日前）';
			var el = {};
			el.text = Ext.Date.format(dateTmp, strPattern);
			el.value = Ext.Date.format(dateTmp, 'Ymd');
			dataOfPlayDate.push(el);
		}
		return dataOfPlayDate;
	},
	
	getItemTpl : function(){
		return ['<div><span style="color:#000099";font-size:15px;>{playDate}</span><br/>',
		 '<span style="color:#006600;font-size:x-small;width: 70px;display: inline-block;">当たり ：{bonusCount}</span>',
		 '<span style="color:#ff0066;font-size:x-small;width: 70px;display: inline-block;">確率 ：{rate}</span>',
		 '<span style="color: #660066;font-size:x-small;width: 70px;display: inline-block;">出玉 ：{ballOutput}</span></div>'].join("");
	}, 
	// launch
	launch : function () {

		// #################################  form  start ##############################
		// 既存の台番情報のstore
		var taiNoStore = Ext.create("Ext.data.Store", {
				model : "taiNoModel",
				proxy : {
					type : "ajax",
					url : "GetTaiNoList",
					reader : {
						type : "json",
						rootProperty : "root"
					}
				},
				autoLoad : true
			});
		
		// 検索ボタン
		var searchButtonForm = Ext.create('Ext.Button', {
				text : '検索',
				ui : 'confirm',
				handler : function () {
					var strtaiNo = formPanel.getValues().taiNo;
					var strplayDate = formPanel.getValues().playDate;

					if (strtaiNo == null || strtaiNo == '') {
						Ext.Msg.alert('更新', '台番を入力してください。', Ext.emptyFn);
						return;
					}
					if (strplayDate == null || strplayDate == '') {
						Ext.Msg.alert('更新', '日付を選択してください。', Ext.emptyFn);
						return;
					}

					formPanel.setMasked({
						xtype : 'loadmask',
						message : '検索中...'
					});
					var strUrl = 'GetPiaData?taiNo=' + strtaiNo + '&playDate=' + strplayDate;
					Ext.Ajax.request({
						url : strUrl,
						success : function (response) {
							formPanel.setMasked(false);
							var esponseText = Ext.JSON.decode(response.responseText, true);
							if (esponseText.root.length > 0) {
								var record = esponseText.root[0];
								var piadataInfo = Ext.create('piaDataModel', record);
								formPanel.setRecord(piadataInfo);
							} else {
								formPanel.reset();
								Ext.Msg.alert('検索', '情報が存在しません。', Ext.emptyFn);
							}
						}
					});
				}
			});
		// 保存更新ボタン
		var saveUpdateButtonForm = Ext.create('Ext.Button', {
				text : '保存/更新',
				ui : 'confirm',
				handler : function () {
					var id = formPanel.getValues().id;
					var strtaiNo = formPanel.getValues().taiNo;
					var strplayDate = formPanel.getValues().playDate;
					if (strtaiNo == null || strtaiNo == '') {
						Ext.Msg.alert('保存 / 更新', '台番を入力してください。', Ext.emptyFn);
						return;
					}
					if (strplayDate == null || strplayDate == '') {
						Ext.Msg.alert('保存 / 更新', '日付を選択してください。', Ext.emptyFn);
						return;
					}
					if (id) {
						formPanel.submit({
							url : 'UpdatePiaData',
							waitMsg : 'データ更新中...',
							success : function (result, data) {
								Ext.Msg.alert('更新', data.msg, Ext.emptyFn);
							}
						});
					} else {
						formPanel.submit({
							url : 'SavePiaData',
							waitMsg : 'データ保存中...',
							success : function (result, data) {
								Ext.Msg.alert('保存 ', data.msg, Ext.emptyFn);
							}
						});
					}
				}
			});
		// 削除ボタン
		var deleteButtonForm = Ext.create('Ext.Button', {
				text : '削除',
				ui : 'confirm',
				handler : function () {
					var id = formPanel.getValues().id;
					if (id) {
						Ext.Msg.confirm("確認", "データを削除しますか？",
							function (buttonId, value, opt) {
							if (buttonId == 'yes') {
								formPanel.submit({
									url : 'DeletePiaData',
									waitMsg : 'データ削除中...',
									success : function (result, data) {
										Ext.Msg.alert('削除', data.msg, Ext.emptyFn);
									}
								});
							}
						});
					} else {
						Ext.Msg.alert('削除', 'データを入力してください。', Ext.emptyFn);
					}
				}
			});
		// 削除ボタン
		var clearButtonForm = Ext.create('Ext.Button', {
				text : 'クリア',
				ui : 'confirm',
				handler : function () {
					formPanel.reset();
				}
			});
		var formPanel = Ext.create('Ext.form.Panel', {
				title : 'Contact',
				iconCls : 'user',
				items : [{
						xtype : 'fieldset',
						title : 'PIA DATA',
						instructions : 'Please enter the information above.',
						defaults : {
							required : true
						},
						items : [{
								xtype : 'hiddenfield',
								name : 'id',
								value : ''
							}, {
								xtype : 'selectfield',
								name : 'playDate',
								label : '日付',
								valueField : 'value',
								displayField : 'text',
								store : {
									data : this.getPlayDate()
								}
							},{
								xtype : 'selectfield',
								name : 'taiNo',
								label : '台番',
								valueField : 'taiNo',
								displayField : 'taiNo',
								store : taiNoStore
							}, {
								xtype : 'numberfield',
								name : 'bonusCount',
								label : '当たり',
								minValue : 0,
								value : 12,
							}, {
								xtype : 'numberfield',
								name : 'rate',
								label : '確率',
								minValue : 0,
								value : 100,
							}, {
								xtype : 'numberfield',
								name : 'ballOutput',
								label : '出玉数',
								value : -1,
							}
						]
					}, {
						xtype : 'toolbar',
						docked : 'bottom',
						scrollable : {
							direction : 'horizontal',
							directionLock : true
						},
						items : [searchButtonForm, {
								xtype : 'spacer'
							},
							saveUpdateButtonForm, {
								xtype : 'spacer'
							},
							deleteButtonForm, {
								xtype : 'spacer'
							},
							clearButtonForm]
					}
				]

			});
		// #################################  chart  start ##############################


		//台番
		var taiNoSelectField = Ext.create('Ext.field.Select', {
				label : '台番',
				valueField : 'taiNo',
				displayField : 'taiNo',
				store : taiNoStore
			});
		// 検索ボタン
		var searchButton = Ext.create('Ext.Button', {
				text : '検索',
				ui : 'action',
				handler : function () {
					storeChart.load({
						params : {
							taiNo : taiNoSelectField.getValue()
						}
					});
				}
			});
		// chartpanelのstore
		var storeChart = Ext.create("Ext.data.Store", {
				model : "piaDataModel",
				proxy : {
					type : "ajax",
					url : "GetPiaDataForChart",
					reader : {
						type : "json",
						rootProperty : "root"
					}
				},
				autoLoad : true
			});
		// chartpanel
		var chartpanel = Ext.create("Ext.Panel", {
				title : 'Chart',
				iconCls : 'star',
				layout : 'fit',
				items : [{
						xtype : 'chart',
						background : "none",
						store : storeChart,
						animate : true,
						interactions : ['panzoom', 'itemhighlight'],
						legend : {
							position : "bottom"
						},
						series : [{
								type : 'line',
								xField : 'playDateN',
								yField : 'rate',
								title : '確率',
								style : {
									stroke : '#e600e6',
									lineWidth : 2
								},
								highlightCfg : {
									scale : 2
								},
								marker : {
									type : 'circle',
									stroke : '#0d1f96',
									fill : '#115fa6',
									lineWidth : 1,
									radius : 2,
									fx : {
										duration : 300
									}
								}
							}, {
								type : 'line',
								xField : 'playDateN',
								yField : 'rateN',
								title : '10000/確率',
								style : {
									stroke : '#993399',
									lineWidth : 2
								},
								highlightCfg : {
									scale : 2
								},
								marker : {
									type : 'circle',
									stroke : '#0d1f96',
									fill : '#115fa6',
									lineWidth : 1,
									radius : 2,
									fx : {
										duration : 300
									}
								}
							}, {
								type : 'line',
								xField : 'playDateN',
								yField : 'bonusCountN',
								title : '当たり',
								style : {
									stroke : '#1a1aff',
									lineWidth : 2
								},
								highlightCfg : {
									scale : 2
								},
								marker : {
									type : 'circle',
									stroke : '#black',
									fill : '#a61120',
									lineWidth : 1,
									radius : 2,
									fx : {
										duration : 300
									}
								}
							}, {
								type : 'bar',
								xField : 'playDateN',
								yField : ['ballOutputN'],
								title : ['出玉'],
								style : {
									maxBarWidth : 3,
									lineWidth : 1,
									fill : "#00001a",
									stroke : '#00001a'
								}
							}, {
								type : 'line',
								xField : 'playDateN',
								yField : 'totalOut',
								title : '差玉',
								style : {
									stroke : '#660033',
									lineWidth : 2
								},
								highlightCfg : {
									scale : 2
								},
								marker : {
									type : 'circle',
									stroke : '#black',
									fill : '#e6e600',
									lineWidth : 1,
									radius : 2,
									fx : {
										duration : 300
									}
								}
							}, {
								type : 'line',
								xField : 'playDateN',
								yField : 'outMax',
								title : '差玉MAX',
								style : {
									stroke : '#006600',
									lineWidth : 2
								},
								highlightCfg : {
									scale : 2
								},
								marker : {
									type : 'circle',
									stroke : '#black',
									fill : '#006600',
									lineWidth : 1,
									radius : 2,
									fx : {
										duration : 300
									}
								}
							}
						],
						axes : [{
								type : 'numeric',
								position : 'left',
								grid : {
									odd : {
										fill : '#fafafa'
									}
								},
								style : {
									axisLine : true,
									estStepSize : 25,
									stroke : '#ddd'
								}
							}, {
								type : 'category',
								position : 'bottom',
								style : {
									estStepSize : 4,
									stroke : '#999'
								}
							}
						]
					}

				]
			});
		
		// ################################   List     Start   ########################
		var list = Ext.create('Ext.List', {
				itemTpl : this.getItemTpl(),
				store : storeChart,
				listeners : {
					selectionchange : function (selection, records, eOpts) {
						tabpanels.setActiveItem(2);
						var piadataInfo = Ext.create('piaDataModel', records[0].data);
						formPanel.setRecord(piadataInfo);
					}
				}
			});

		var listpanel = Ext.create("Ext.Panel", {
				title : 'home',
				iconCls : 'home',
				layout : 'fit',
				items : [list]
			});
		// ################################   TabPanel     Start   ########################
		var tabpanels = Ext.create('Ext.TabPanel', {
				xtype : 'tabpanel',
				tabBarPosition : 'bottom',
				items : [ {
					xtype : 'toolbar',
					docked : 'top',
					scrollable : {
						direction : 'horizontal',
						directionLock : true
					},
					items : [taiNoSelectField, searchButton]
				},listpanel, chartpanel, formPanel]
			});

		Ext.Viewport.add(tabpanels);
	}
});