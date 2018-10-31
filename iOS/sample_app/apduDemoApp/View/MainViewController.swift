//ViewController.swift
//  apduDemoApp
//
//  Created by Marek Hrasna on 28/02/2018. <marek.hrasna@ahead-itec.com>
//  Copyright © 2018 AHEAD iTec, s.r.o. All rights reserved.
//

import UIKit
import Cartography
import GemaltoReader
import FeitianReader
import ACSReader
import ReaderConnectionWrapper

class MainViewController: UIViewController {

	let titleLabel = UILabel()
	let pairButton = UIButton()
	
	public var indicator = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.gray)
	
	var manufacturersView: ManufacturerTableView?
	var discoveredReaders: [SmartCardReaderProtocol] = []
	
	let testAppSignature = ["64", "1c", "55", "88", "58", "53", "a4", "00", "b2", "d1", "64",
							"0a", "bb", "86", "73", "01"].compactMap { UInt8($0, radix: 16)}
	
	var connectionService: CombinedConnectionService?
	var menuViewController: MenuViewController?
	
	override func viewDidLoad() {
		super.viewDidLoad()
		// Do any additional setup after loading the view, typically from a nib.
		connectionService = CombinedConnectionService(services: [
			GemaltoConnectionService(gemaltoApplicationSignature: testAppSignature),
																 FeitianConnectionService(),
																 ACSConnectionService()])
		connectionService?.delegate = self
	}
	
	override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
		self.manufacturersView = ManufacturerTableView(frame: CGRect(), connectionService: connectionService!, indicator: indicator)
		initUI()
	}
	
	func initUI() {
		self.view.backgroundColor = UIColor.white
		//Title Label
		titleLabel.text = "APDU SDK Test"
		titleLabel.textColor = UIColor.black
		self.view.addSubview(titleLabel)
		
		//ManufactorTableView
		self.view.addSubview(manufacturersView!)
		
		//Indicator
		indicator.frame = CGRect(x: 0, y: 0, width: 40, height: 40)
		self.view.addSubview(self.indicator)
		self.indicator.bringSubviewToFront(self.view)
		UIApplication.shared.isNetworkActivityIndicatorVisible = true


		constrain(titleLabel, indicator, manufacturersView!) {titleLabel, indicator, manufaturersView in
			
			titleLabel.top == titleLabel.superview!.top + 32
			titleLabel.centerX == titleLabel.superview!.centerX
			
			indicator.top == titleLabel.bottom + 16
			indicator.centerX == indicator.superview!.centerX
			
			manufaturersView.top == indicator.bottom + 16
			manufaturersView.left == manufaturersView.superview!.left + 16
			manufaturersView.right == manufaturersView.superview!.right - 16
			manufaturersView.bottom == manufaturersView.superview!.bottom - 100
			
		}
	}
}

extension MainViewController: ReaderConnectionServiceDelegate {
	func didConnect(reader: SmartCardReaderProtocol) {
		guard let connection = reader.cardConnectionProvider else { return }
		
		menuViewController = MenuViewController()
		menuViewController!.providingCardConnection = connection
		self.present(menuViewController!, animated: true, completion: nil)
	}
	func didDisconnect(reader: SmartCardReaderProtocol) {}
	func didUpdate(service: ReaderConnectionServiceProtocol, state: ReaderConnectionServiceState) {}
	func didPairWith(reader: SmartCardReaderProtocol, connected: Bool) {
		discoveredReaders.append(reader)
		indicator.stopAnimating()
		
		let alertController = UIAlertController(title: NSLocalizedString("ParingSuccess", comment: ""), message: NSLocalizedString("ParingSuccess", comment: ""), preferredStyle: .alert)
		alertController.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: ""), style: .default, handler: nil))
		
		guard let connection = reader.cardConnectionProvider else { return }
		
		menuViewController = MenuViewController()
		menuViewController!.providingCardConnection = connection
		self.present(menuViewController!, animated: true, completion: nil)
	}
	func didDiscover(newReader: SmartCardReaderProtocol) {
		connectionService?.pair(reader: newReader)
	}
	func didDiscover(knownReader: SmartCardReaderProtocol) {
		connectionService?.connect(reader: knownReader)
	}
	func didUpdate(knownReaders: [SmartCardReaderProtocol]) {}
	func didInsertCard(connection: ProvidingCardConnection, on reader: SmartCardReaderProtocol) {
		menuViewController = MenuViewController()
		menuViewController!.providingCardConnection = connection
		self.present(menuViewController!, animated: true, completion: nil)
	}
	func didRemoveCard(on reader: SmartCardReaderProtocol) {}
}
