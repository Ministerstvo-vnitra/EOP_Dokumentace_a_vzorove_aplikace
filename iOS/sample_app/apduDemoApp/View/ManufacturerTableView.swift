//manufactorTableView.swift
//  apduDemoApp
//
//  Created by Marek Hrasna on 21/03/2018. <marek.hrasna@ahead-itec.com>
//  Copyright © 2018 AHEAD iTec, s.r.o. All rights reserved.
//

import Foundation
import UIKit
import Cartography
import ReaderConnectionWrapper

class ManufacturerTableView: UIView, UITableViewDelegate, UITableViewDataSource {
	
	let tableView = UITableView()
	
	var providingConnectionService: CombinedConnectionService
	var manufacturers: [String]
	
	private let headerId = R.string.localizable.headerId()
	let cellId = R.string.localizable.cellId()
	
	var indicator: UIActivityIndicatorView
	
	init(frame: CGRect, connectionService: CombinedConnectionService, indicator: UIActivityIndicatorView) {
		self.indicator = indicator
		self.providingConnectionService = connectionService
		self.manufacturers = connectionService.manufacturers
		super.init(frame: frame)
		tableView.backgroundColor = UIColor(red: 246/255, green: 248/255, blue: 250/255, alpha: 1.0)
		tableView.register(ManufacturerViewHeader.self, forHeaderFooterViewReuseIdentifier: headerId)
		tableView.register(UITableViewCell.self, forCellReuseIdentifier: cellId)
		tableView.dataSource = self
		tableView.delegate = self
		tableView.allowsSelection = true
		tableView.separatorStyle = .none
		addSubview(tableView)
		initItems()


	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
	
	
	func initItems() {
		constrain(tableView) {tableView in
			tableView.bottom == tableView.superview!.bottom
			tableView.left == tableView.superview!.left
			tableView.right == tableView.superview!.right
			tableView.top == tableView.superview!.top
		}
	}
	
	//
	// MARK :- HEADER
	//
	func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
		return 50
	}

	func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
		let header = tableView.dequeueReusableHeaderFooterView(withIdentifier: headerId) as! ManufacturerViewHeader
		return header
	}
	
	//
	// MARK :- CELL
	//
	func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		return manufacturers.count
	}
	
	func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
		return 65
	}
	
	func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
		let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath)
		
		let manufacturer = manufacturers[indexPath.row]
		cell.textLabel?.text = manufacturer
		cell.backgroundColor = UIColor.lightGray
		return cell
	}
	
	func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
		let manufacturer = manufacturers[indexPath.row]
		indicator.startAnimating()
		let knownReaders = providingConnectionService.knownReaders
		if knownReaders.contains(where: { reader in
			return reader.manufacturer == manufacturer
		}) {
			providingConnectionService.scanReadersForConnecting(manufacturer: manufacturer)
		} else {
			providingConnectionService.scanReadersForPairing(manufacturer: manufacturer)
		}
	}
}
