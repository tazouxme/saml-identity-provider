function Table(params) {

	var _uid = params.id;
	var _columns = params.columns;
	var _sizes = params.sizes;
	var _mimeTypes = params.mimeTypes;
	var _columnPk = params.columnPk;
	var _translatable = !!params.translatable;
	
	/**
	 * Data set in the table
	 */
	var _data = [];
	
	/**
	 * Select / Unselect listeners / { onSelect : function(pk) {...}, onUnselect : function(pk) {...} }
	 */
	var _selectionListener;
	
	/**
	 * Cell Renderers [{ column: 0, renderer: function(data) {} }, ...]
	 */
	var _renderers = [];
	
	/**
	 * Sort function
	 */
	var _sort;
	
	/**
	 * Header container for the Table
	 */
	var _tableHead = document.createElement("thead");
	_tableHead.setAttribute("class", "dauth-table-head");
	
	/**
	 * Header container for the Table
	 */
	var _tableBody = document.createElement("tbody");
	_tableBody.setAttribute("class", "dauth-table-body");
	
	/**
	 * Main container for the Table
	 */
	var _table = document.createElement("table");
	_table.classList.add("dauth-table");
	_table.setAttribute("id", _uid);
	_table.appendChild(_tableHead);
	_table.appendChild(_tableBody);
	
	/**
	 * Get the selectionListener
	 * @Private
	 */
	var _getSelectionListener = function() {
		return _selectionListener;
	};
	
	/**
	 * Set the selectionListener
	 * @Private
	 */
	var _setSelectionListener = function(selectionListener) {
		_selectionListener = selectionListener;
	};
	
	/**
	 * Table builder function
	 * @Private
	 */
	var _buildTable = function() {
		_tableHead.appendChild(_buildHeaders());
		
		var rows = _buildRows(_data);
		for (var i = 0; i < rows.length; i++) {
			_tableBody.appendChild(rows[i]);
		}
	};
	
	/**
	 * Header builder function
	 * @Private
	 */
	var _buildHeaders = function() {
		var header = document.createElement("tr");
		
		for (var i = 0; i < _columns.length; i++) {
			var title = _columns[i];
			if (_translatable) {
				title = DAuth.lang.text.getText(title);
			}
			
			var cell = document.createElement("th");
			cell.appendChild(document.createTextNode(title));
			
			header.appendChild(cell);
		}
	
		return header;
	};
	
	/**
	 * Rows builder function
	 * @Private
	 */
	var _buildRows = function() {
		var rows = [];
		
		for (var i = 0; i < _data.length; i++) {
			rows.push(_buildRow(_data[i], i));
		}
		
		return rows;
	};
	
	/**
	 * Rows builder and filter function
	 * @Private
	 */
	var _filterRows = function(filter) {
		var rows = [];
		
		for (var i = 0; i < _data.length; i++) {
			if (filter(_data[i])) {
				rows.push(_buildRow(_data[i], i));
			}
		}
		
		return rows;
	};
	
	/**
	 * Row builder function
	 * @Private
	 */
	var _buildRow = function(data, rowIndex) {
		var row = document.createElement("tr");
		row.setAttribute("class", "dauth-table-row");
		
		for (var i = 0; i < _columns.length; i++) {
			row.appendChild(_buildCell(_mimeTypes[i], data, row, rowIndex, i));
			
			if (i == _columnPk) {
				row.dataset["pk"] = data[i].value;
			}
		}
		
		if (row.dataset["pk"] == null) {
			for (var j = _columns.length; j < data.length; j++) {
				if (j == _columnPk) {
					row.dataset["pk"] = data[j].value;
				}
			}
		}
		
		row.addEventListener("click", function(e) {
			e.preventDefault();
			e.stopPropagation();
			
			if (e.ctrlKey) {
				this.classList.remove("table-row-selected");
				if (_selectionListener != null && typeof(_selectionListener.onUnselect) === 'function') {
					_selectionListener.onUnselect(e, this.dataset["pk"]);
				}
		    } else {
		    	if (this.classList.contains("table-row-selected")) {
		    		return;
		    	}
		    	
		    	var nodes = _tableBody.getElementsByClassName("table-row-selected");
		    	for (var i = 0; i < nodes.length; i++) {
		    		nodes[i].classList.remove("table-row-selected");
		    	};
		    	
		    	this.classList.add("table-row-selected");
				if (_selectionListener != null && typeof(_selectionListener.onSelect) === 'function') {
					_selectionListener.onSelect(e, this.dataset["pk"]);
				}
		    }
		});
		
		return row;
	};
	
	/**
	 * Cell builder function
	 * @Private
	 */
	var _buildCell = function(mimeType, data, row, rowIndex, cellIndex) {
		var cell = document.createElement("td");
		var cellId = "dauth-table-cell-" + rowIndex + "" + cellIndex;
		
		cell.setAttribute("class", "dauth-table-cell");
		cell.setAttribute("id", cellId);
		cell.dataset["column"] = data[cellIndex].column;
		cell.dataset["mimeType"] = mimeType.code;
		
		if (_getRenderer(mimeType.code, cellIndex) != null) {
			var renderer = _getRenderer(mimeType.code, cellIndex);
			cell.appendChild(renderer(data[cellIndex]));
		} else {
			cell.appendChild(document.createTextNode(data[cellIndex].value));
		}
		
		return cell;
	};
	
	/**
	 * Get a column Renderer
	 * @private
	 * @param column
	 */
	var _getRenderer = function(mimeType, column) {
		for (var i = 0; i < _renderers.length; i++) {
			if (_renderers[i].column == column) {
				return _renderers[i].renderer;
			}
		}
		
		if (mimeType == "BOOLEAN") {
			return function(data) {
				var cellPicture = document.createElement("img");
			
				if (data.value) {
					cellPicture.src = "./lib/img/true.png";
				} else {
					cellPicture.src = "./lib/img/false.png";
				}
	
				var cell = document.createElement("div");
				cell.classList.add("boolean");
				cell.appendChild(cellPicture);
				
				return cell;
			}
		} else if (mimeType == "NUMBER") {
			return function(data) {
				var cell = document.createElement("div");
				cell.classList.add("number");
				
				cell.innerHTML = data.value;
				return cell;
			}
		} else if (mimeType == "DATE") {
			return function(data) {
				var cell = document.createElement("div");
				cell.classList.add("date");
				
				var d = new Date(data.value);
				var formatDay = (d.getDate()) > 9 ? (d.getDate()) : '0' + (d.getDate());
				var formatMonth = (d.getMonth() + 1) > 9 ? (d.getMonth() + 1) : '0' + (d.getMonth() + 1);
				
				cell.innerHTML = formatDay + '/' + formatMonth + '/' + d.getFullYear();
				return cell;
			}
		}
		
		return null;
	};
	
	/**
	 * Set a column Renderer
	 * @private
	 * @param column
	 * @param renderer
	 */
	var _setRenderer = function(column, renderer) {
		for (var i = 0; i < _renderers.length; i++) {
			if (_renderers[i].column == column) {
				_renderers.splice(i, 1, { column: column, renderer: renderer });
				return;
			}
		}
		
		_renderers.push({ column: column, renderer: renderer });
	};
	
	/**
	 * Set sort function
	 * @private
	 * @param sort
	 */
	var _setSort = function(sort) {
		_sort = sort;
	}
	
	/**
	 * Get the row associated to the given pk
	 * @Private
	 */
	var _getRow = function(pk) {
		if (pk == null || !_contains(pk)) {
			return null;
		}
		
		var rows = _tableBody.getElementsByTagName("tr");
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			
			if (row.dataset["pk"] == pk) {
				return row;
			}
		}
		
		return null;
	};
	
	/**
	 * Get all rows of the table
	 * @Private
	 */
	var _getRows = function() {
		return _tableBody.getElementsByTagName("tr");
	};
	
	/**
	 * Get the current selected row 
	 * @Private
	 */
	var _getSelectedRow = function() {
		var selectedElements = _tableBody.getElementsByClassName("table-row-selected");
		if (selectedElements.length == 1) {
			return selectedElements[0];
		}
		
		return null;
	};
	
	/**
	 * Get the current selected line in the table
	 * @Private
	 */
	var _getSelectedData = function() {
		var row = _getSelectedRow();
		var data = {};
		
		if (row != null) {
			var foundData = _findData(row.dataset["pk"]);
			for (var i = 0; i < foundData.length; i++) {
				data[foundData[i].column] = foundData[i].value;
			}
		}
		
		return data;
	};
	
	/**
	 * Set the selected row 
	 * @Private
	 */
	var _setSelectedRow = function(pk) {
		var rows = _tableBody.getElementsByClassName("dauth-table-row");
		for (var i = 0; i < rows.length; i++) {
			if (pk == rows[i].dataset["pk"]) {
				rows[i].dispatchEvent(new Event("click"));
				return;
			}
		}
	};
	
	/**
	 * Clear the selection 
	 * @Private
	 */
	var _clearSelection = function() {
		var row = _getSelectedRow();
		if (row != null) {
			row.classList.remove("table-row-selected");
		}
	};
	
	/**
	 * Update the row with new values
	 * @Private
	 */
	var _updateRow = function(data, pk) {
		var row = _getRow(pk);
		
		if (pk == null && row == null) {
			row = _getSelectedRow();
			pk = row.dataset["pk"];
		}
		
		_updateData(pk, data);
		
		// update view
		var cells = row.getElementsByTagName("td");
		for (var i = 0; i < cells.length; i++) {
			var cell = cells[i];
			cell.textContent = data[i].value;
		}
	};
	
	/**
	 * Remove the row with new values
	 * @Private
	 */
	var _removeRow = function(pk) {
		var row = _getRow(pk);

		if (pk == null && row == null) {
			row = _getSelectedRow();
			pk = row.dataset["pk"];
		}
		
		_removeData(pk);
		row.parentNode.removeChild(row);
	};
	
	/**
	 * Update data from the table
	 * @Private
	 */
	var _updateData = function(pk, data) {
		for (var i = 0; i < _data.length; i++) {
			if (_data[i][_columnPk].value == pk) {
				_data.splice(i, 1, data);
				return;
			}
		}
	};
	
	/**
	 * Remove data from the table
	 * @Private
	 */
	var _removeData = function(pk) {
		for (var i = 0; i < _data.length; i++) {
			if (_data[i][_columnPk].value == pk) {
				_data.splice(i, 1);
				return;
			}
		}
	};
	
	/**
	 * Check if entered pk is present in the Table
	 * @Private
	 */
	var _contains = function(pk) {
		var rows = _tableBody.getElementsByTagName("tr");
		
		for (var i = 0; i < rows.length; i++) {
			if (rows[i].dataset["pk"] === pk) {
				return true;
			} 
		}
		
		return false;
	};
	
	/**
	 * Check if entered value is present in the Table
	 * @Private
	 */
	var _containsAt = function(column, value) {
		for (var i = 0; i < _data.length; i++) {
			if (value == _data[i][column].value) {
				return true;
			}
		}
		
		return false;
	};
	
	/**
	 * Get the size of the Table
	 * @private
	 */
	var _size = function() {
		return _data.length;
	};
	
	/**
	 * Find a data row in the data array for a given pk
	 * @private
	 */
	var _findData = function(pk) {
		for (var i = 0; i < _data.length; i++) {
			var d = _data[i];
			
			if (d[_columnPk].value == pk) {
				return d;
			}
		}
		
		return null;
	};
	
	/**
	 * Sort the table
	 * @private
	 */
	var _sortTable = function() {
		_data.sort(_sort);
		_clearTable();
		
		var rows = _buildRows(_data);
		for (var i = 0; i < rows.length; i++) {
			_tableBody.appendChild(rows[i]);
		}
	}
	
	/**
	 * Sort the table
	 * @private
	 */
	var _filter = function(filter) {
		_clearTable();
		
		var rows = _filterRows(filter);
		for (var i = 0; i < rows.length; i++) {
			_tableBody.appendChild(rows[i]);
		}
	}
	
	/**
	 * Clear the table and the data
	 * @private
	 */
	var _clear = function() {
		_clearTable();
		_data = [];
	};
	
	/**
	 * Clear the table
	 * @private
	 */
	var _clearTable = function() {
		var _children = _tableBody.getElementsByTagName("tr");
		while (_children.length > 0) {
			_tableBody.removeChild(_children[0]);
		}
	};
	
	/**
	 * Retrieve the data
	 * @Public
	 */
	this.getData = function() {
		return _data;
	};
	
	/**
	 * Retrieve the displayed data
	 * @Public
	 */
	this.getDisplayedData = function() {
		var rows = _getRows();
		var data = [];
		
		for (var i = 0; i < rows.length; i++) {
			var foundData = _findData(rows[i].dataset["pk"]);
			var d = {};
			
			for (var j = 0; j < foundData.length; j++) {
				d[foundData[j].column] = foundData[j].value
			}
			
			data.push(d);
		}
		
		return data;
	};
	
	/**
	 * Get the current selected line in the table
	 * @Public
	 */
	this.getSelectedData = function() {
		return _getSelectedData();
	};
	
	/**
	 * Set the data
	 * @Public
	 */
	this.setData = function(data) {
		_clear();
		_data = data;
		
		if (_sort != null) {
			_sortTable();
			return;
		}
		
		var rows = _buildRows(_data);
		for (var i = 0; i < rows.length; i++) {
			_tableBody.appendChild(rows[i]);
		}
	};
	
	/**
	 * Get the selectionListener
	 * @Public
	 */
	this.getSelectionListener = function() {
		return _getSelectionListener();
	};
	
	/**
	 * Set the selectionListener
	 * @Public
	 */
	this.setSelectionListener = function(selectionListener) {
		_setSelectionListener(selectionListener);
	};
	
	/**
	 * Get the line in the table associated to the given pk
	 * @Public
	 */
	this.getLine = function(pk) {
		var row = _getRow(pk);
		
		if (row != null) {
			return _findData(row.dataset["pk"]);
		}
		
		return null;
	};
	
	/**
	 * Get the current selected line in the table
	 * @Public
	 */
	this.getSelectedLine = function() {
		var row = _getSelectedRow();
		
		if (row != null) {
			return _findData(row.dataset["pk"]);
		}
		
		return null;
	};
	
	/**
	 * Set the selected line in the table
	 * @Public
	 */
	this.setSelectedLine = function(pk) {
		_setSelectedRow(pk);
	};
	
	/**
	 * Clear the selection 
	 * @public
	 */
	this.clearSelection = function() {
		_clearSelection();
	};
	
	/**
	 * Add a new Line in the Table
	 * @Public
	 */
	this.addLine = function(data) {
		_data.push(data);
		
		if (_sort != null) {
			_sortTable();
		}
	};
	
	/**
	 * Update a Line in the Table with new data
	 * @Public
	 */
	this.updateLine = function(data, pk) {
		_updateRow(data, pk);
		
		if (_sort != null) {
			_sortTable();
			_setSelectedRow(pk);
		}
	};
	
	/**
	 * Remove a Line in the Table 
	 * @Public
	 */
	this.removeLine = function(pk) {
		_removeRow(pk);
	};
	
	/**
	 * Get row count from the current table
	 * @public
	 */
	this.getRowCount = function() {
		var rows = _tableBody.getElementsByTagName("tr");
		return rows.length - 1;
	};
	
	/**
	 * Checks if the current table contains the pk
	 * @public
	 */
	this.contains = function(pk) {
		return _contains(pk);
	};
	
	/**
	 * Checks if the current table contains the value
	 * @public
	 */
	this.containsAt = function(column, value) {
		return _containsAt(column, value);
	};
	
	/**
	 * Filters the table without destroying stored data 
	 * @public
	 */
	this.filter = function(filter) {
		return _filter(filter);
	};
	
	/**
	 * Clear the table
	 * @public
	 */
	this.clear = function() {
		_clear();
	};
	
	/**
	 * Set a column Renderer
	 * @public
	 * @param column
	 * @param renderer
	 */
	this.addRenderer = function(column, renderer) {
		_setRenderer(column, renderer);
	};
	
	/**
	 * Set sort function
	 * @public
	 * @param sort
	 */
	this.setSort = function(sort) {
		_setSort(sort);
	};
	
	/**
	 * Return the HTML Table
	 * @public
	 */
	this.set = function(element) {
		_buildTable();
		
		var c = document.createElement("div");
		c.classList.add("dauth-table-container");
		c.setAttribute("id", _uid + "-container");
		c.appendChild(_table);

		element.appendChild(c);
	};
	
}