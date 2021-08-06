function Ribbon(params) {
	
	/**
	 * Class to define a menu in the Ribbon
	 */
	function RibbonMenu(text, translatable, select) {
		
		var _text = text;
		var _translatable = !!translatable;
		var _sections = [];
		
		this.onSelect = select;
		
		this.getText = function() {
			if (_translatable) {
				return Utils.translate(_text);
			}
			
			return _text;
		};
		
		this.getSections = function() {
			return _sections;
		};
		
		this.addSection = function(text, buttons) {
			_sections.push({
				text : text,
				buttons : buttons
			});
		};
		
	}

	/**
	 * ID of the Ribbon
	 * @private
	 */
	var _uid = params.id;
	var _translatable = !!params.translatable;
	
	/**
	 * Menus to be displayed
	 * @private
	 */
	var _menus = [];
	
	/**
	 * Main container for the Ribbon
	 * @private
	 */
	var _container = document.createElement("div");
	_container.classList.add("dauth-ribbon");
	_container.setAttribute("id", _uid);
	
	/**
	 * Generate all Menus
	 * @private
	 */
	var _generateMenu = function() {
		var ul = document.createElement("ul");
		
		for (var i = 0; i < _menus.length; i++) {
			var menu = _menus[i].menu;
			var li = document.createElement("li");
			li.setAttribute("id", _menus[i].id);
			li.dataset["id"] = _menus[i].id;
			li.appendChild(document.createTextNode(menu.getText()));
			li.addEventListener("click", function(e) {
				e.stopPropagation();
				e.preventDefault();
	
				_setSelected(this.getAttribute("id"));
				
				var m = _getMenu(this.dataset["id"]);
				if (m.onSelect != null) {
					m.onSelect(e);
				}
			});
			
			ul.appendChild(li);
		}
		
		return ul;
	};
	
	/**
	 * Generate the section for a specific Menu
	 * @private
	 */
	var _generateSection = function(menuObj) {
		var id = menuObj.id;
		var menu = menuObj.menu;
		
		var menuPanel = document.createElement("div"); 
		menuPanel.setAttribute("id", "menu-" + id);
		menuPanel.classList.add("dauth-ribbon-menu");
		
		for (var i = 0; i < menu.getSections().length; i++) {
			var sectionPanel = document.createElement("div");
			sectionPanel.setAttribute("id", "menu-section-" + id);
			sectionPanel.classList.add("dauth-ribbon-menu-section");
			menuPanel.appendChild(sectionPanel);
			
			var sectionText = menu.getSections()[i].text;
			if (_translatable) {
				sectionText = Utils.translate(sectionText);
			}
			
			var sectionButtons = menu.getSections()[i].buttons;
			
			var elementsPanel = document.createElement("div");
			sectionPanel.setAttribute("id", "menu-elements-" + id);
			elementsPanel.classList.add("dauth-ribbon-menu-section-elements");
			sectionPanel.appendChild(elementsPanel);
			
			for (var j = 0; j < sectionButtons.length; j++) {
				sectionButtons[j].set(elementsPanel);
				
				sectionButtons[j].get().classList.remove('dauth-button');
				sectionButtons[j].get().classList.add('dauth-button-ribbon');
			}
			
			var elementsLabel = document.createElement("span");
			elementsLabel.appendChild(document.createTextNode(sectionText));
			sectionPanel.appendChild(elementsLabel);
		}
		
		return menuPanel;
	};
	
	/**
	 * Set a specific Menu selected / and other unselected
	 * @private
	 */
	var _setSelected = function(id) {
		var ribbon = document.getElementById(_uid);
		
		var menus = ribbon.getElementsByTagName("li");
		for (var i = 0; i < _menus.length; i++) {
			menus[i].setAttribute("class", "");
		}
		
		document.getElementById(id).setAttribute("class", "selected");
		
		var sections = ribbon.getElementsByClassName("dauth-ribbon-menu");
		for (var i = 0; i < sections.length; i++) {
			sections[i].setAttribute("class", "dauth-ribbon-menu");
		}
		
		document.getElementById("menu-" + id).setAttribute("class", "dauth-ribbon-menu selected");
	};
	
	/**
	 * Retrieve the Menu by its id
	 * @private
	 */
	var _getMenu = function(id) {
		for (var i = 0; i < _menus.length; i++) {
			if (_menus[i].id == id) {
				return _menus[i].menu;
			}
		}
		
		return null;
	};
	
	/**
	 * Retrieve the Menu by its id
	 * @public
	 */
	this.getMenu = function(id) {
		return _getMenu(id);
	};
	
	/**
	 * Add a Menu to the Ribbon
	 * @public
	 */
	this.addMenu = function(id, text, select) {
		_menus.push({
			id : id,
			menu : new RibbonMenu(text, _translatable, select)
		});
	};
	
	/**
	 * Externally select a Menu
	 * @public
	 */
	this.setSelected = function(id) {
		_setSelected(id);
	};
	
	/**
	 * Insert the HTML Element
	 * @public
	 */
	this.set = function(element) {
		_container.appendChild(_generateMenu());
		
		for (var i = 0; i < _menus.length; i++) {
			_container.appendChild(_generateSection(_menus[i]));
		}
		
		element.appendChild(_container);
	};
	
}