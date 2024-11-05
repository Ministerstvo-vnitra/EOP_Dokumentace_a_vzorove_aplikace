//UITableViewController.swift
//  apduDemoApp
//
//  Created by Marek Hrasna on 13/03/2018. <marek.hrasna@ahead-itec.com>
//  Copyright © 2018 AHEAD iTec, s.r.o. All rights reserved.
//

import UIKit
import CoreBluetooth

//
// MARK :- BLETableViewController
//
class BLETableViewController: UITableViewController {

	var centralManager: CBCentralManager?
	var peripherals = Array<CBPeripheral>()
	
	private let headerId = NSLocalizedString("headerId", comment: "")
	private let cellId = NSLocalizedString("cellId", comment: "")

	//
	// MARK :- HEADER
	//
	override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
		return 50
	}
	
	override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
		if let header = tableView.dequeueReusableHeaderFooterView(withIdentifier: headerId) as? ManufacturerViewHeader {
			return header
		} else {
			return tableView.dequeueReusableCell(withIdentifier: headerId)
		}
	}
	
	//
	// MARK :- CELL
	//
	override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
		return peripherals.count
	}
	
	override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
		return 65
	}
	
	override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
		let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath)
		let peripheral = peripherals[indexPath.row]
		
		cell.textLabel?.text = peripheral.name
		return cell
	}
	
	override func viewDidLoad() {
		super.viewDidLoad()
		
		//Initialise CoreBluetooth Central Manager
		centralManager = CBCentralManager(delegate: self, queue: DispatchQueue.main)
		
		title = NSLocalizedString("BLETableViewTitle", comment: "")
		view.backgroundColor = .white
		tableView.backgroundColor = .lightGray
		tableView.register(ManufacturerViewHeader.self, forHeaderFooterViewReuseIdentifier: headerId)
		tableView.register(UITableViewCell.self, forCellReuseIdentifier: cellId)
	}
}

extension BLETableViewController: CBCentralManagerDelegate {
	func centralManagerDidUpdateState(_ central: CBCentralManager) {
		if (central.state == .poweredOn){
			self.centralManager?.scanForPeripherals(withServices: nil, options: nil)
		}
		else {
			let alertController: UIAlertController = UIAlertController(
                title: NSLocalizedString("NoBluetooth", comment: ""),
                message: NSLocalizedString("NoBluetooth", comment: ""),
                preferredStyle: UIAlertController.Style.alert
            )
			alertController.view.backgroundColor = UIColor.white
			alertController.view.layer.cornerRadius = 8.0
			
			let actionCancel = UIAlertAction(
                title: NSLocalizedString("Cancel", comment: ""),
                style: .cancel,
                handler: {(action:UIAlertAction) in
                }
            )
			alertController.addAction(actionCancel)
			self.present(alertController, animated: true, completion: nil)
		}
	}

	func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
		peripherals.append(peripheral)
		tableView.reloadData()
	}
}

extension BLETableViewController: CBPeripheralDelegate {
	func peripheralDidUpdateName(_ peripheral: CBPeripheral) {
		tableView.reloadData()
	}
}
