//MenuViewController.swift
//  apduDemoApp
//
//  Created by Marek Hrasna on 11/04/2018. <marek.hrasna@ahead-itec.com>
//  Copyright © 2018 AHEAD iTec, s.r.o. All rights reserved.
//

import Foundation

import UIKit
import Cartography
import APDUSDK
import ReaderConnectionWrapper
import APDUSender

class MenuViewController: UIViewController {
	var apduProvider: APDUProvider?
	private var containers: [Container]?
	private var continueSigning: (() -> Void)?
	private var sessionPin: [UInt8]?
	
	var providingCardConnection: ProvidingCardConnection?
	
	let date = Date()
	var isoformatter = ISO8601DateFormatter.init()
	
	let titleLabel = UILabel()
	let signButton = UIButton()
	
	let inputDataView = UITextView()
	let outputDataView = UITextView()
	
	var indicator = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.gray)
	
	override func viewDidLoad() {
		super.viewDidLoad()
		
		initUI()
	}
	
	override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)
	}
	
	override func viewWillDisappear(_ animated: Bool) {
		apduProvider?.reset(delegate: self)
	}
	
	func initUI() {
		self.view.backgroundColor = UIColor.white
		//Title Label
		titleLabel.text = "APDU SDK Test"
		titleLabel.textColor = UIColor.black
		self.view.addSubview(titleLabel)
		
		//Input Data View
		inputDataView.textColor = UIColor.black
		inputDataView.backgroundColor = UIColor(red: 246/255, green: 248/255, blue: 250/255, alpha: 1.0)
		let timeStr = isoformatter.string(from: date)
		inputDataView.insertText(timeStr)
		//inputDataView.insertText(String(date.description))
		self.view.addSubview(inputDataView)
		
		//Sign Button
		signButton.setTitleColor(UIColor.blue, for: UIControl.State.normal)
		signButton.setTitle("Sign", for: UIControl.State.normal)
		signButton.addTarget(self, action: #selector(signButtonTapped), for: UIControl.Event.touchUpInside)
		self.view.addSubview(signButton)

		//Output Data View
		outputDataView.textColor = UIColor.black
		outputDataView.backgroundColor = UIColor(red: 246/255, green: 248/255, blue: 250/255, alpha: 1.0)
		self.view.addSubview(outputDataView)
		
		//Indicator
		indicator.frame = CGRect(x: 0, y: 0, width: 40, height: 40)
		self.view.addSubview(self.indicator)
		self.indicator.bringSubviewToFront(self.view)
		UIApplication.shared.isNetworkActivityIndicatorVisible = true
		
		
		constrain(titleLabel,
				  inputDataView,
				  signButton,
				  indicator,
				  outputDataView) {titleLabel, inputDataView, signButton, indicator, outputDataView in
			
			titleLabel.top == titleLabel.superview!.top + 32
			titleLabel.centerX == titleLabel.superview!.centerX
			
			inputDataView.top == titleLabel.bottom + 32
			inputDataView.centerX == titleLabel.centerX
			inputDataView.height == 100
			inputDataView.width == 300
					
			signButton.top == inputDataView.bottom + 16
			signButton.centerX == inputDataView.centerX
					
			indicator.top == signButton.bottom + 8
			indicator.centerX == indicator.superview!.centerX
					
			outputDataView.top == indicator.bottom + 24
			outputDataView.centerX == signButton.centerX
			outputDataView.height == 300
			outputDataView.width == 300
					
		}
	}
	
	@objc func signButtonTapped() {
		DispatchQueue.main.async {
			self.indicator.startAnimating()
			self.outputDataView.text = ""
			
			self.signButton.isUserInteractionEnabled = false
			self.signButton.alpha = 0.2
		}
		
		initAPDUProvider(delegate: self)
	}
	
	private func insertOutputText(text: String) {
        outputDataView.insertText(text)
	}
	
	private func endSigningAndUpdateUI(text: String) {
        outputDataView.insertText(text)
        signButton.isUserInteractionEnabled = true
        signButton.alpha = 1.0
			
        indicator.stopAnimating()
	}
	
	private func initAPDUProvider(delegate: APDUProviderInstanceDelegate) {
		guard let providingCardConnection = providingCardConnection else {
			print("Providing card connection not initialized")
			return
		}
		APDUProvider.initialize(cardConnection: providingCardConnection, delegate: delegate)
	}
}

extension MenuViewController: GeneralDelegate {
	func onFailed(stringConvertible: CustomStringConvertible) {
        DispatchQueue.main.async {
            print("Error: \(stringConvertible)")

            let alertController = UIAlertController(title: R.string.localizable.signingError(), message: String(describing: stringConvertible), preferredStyle: .alert)
            alertController.addAction(UIAlertAction(title: R.string.localizable.oK(), style: .default, handler: {(alert: UIAlertAction!) in
                self.dismiss(animated: true, completion: nil)
            }))
            self.present(alertController, animated: true, completion: nil)
            self.endSigningAndUpdateUI(text: "Error: \(stringConvertible)")
        }

	}
	
	func onFailed(statusWord: StatusWord) {
        DispatchQueue.main.async {
            print("Wrong status word: \(statusWord)")

            let alertController = UIAlertController(title: R.string.localizable.signingError(), message: R.string.localizable.wrongStatusWord() + String(describing: statusWord), preferredStyle: .alert)
            alertController.addAction(UIAlertAction(title: R.string.localizable.oK(), style: .default, handler: {(alert: UIAlertAction!) in
                self.dismiss(animated: true, completion: nil)
            }))
            self.present(alertController, animated: true, completion: nil)
            self.endSigningAndUpdateUI(text: "Wrong status word: \(statusWord)")
        }
	}
	
	func onFailed(error: Errors) {
        DispatchQueue.main.async {
            print("Error: \(error)")
            self.endSigningAndUpdateUI(text: "Error: \(error)")
        }
	}
}

extension MenuViewController: APDUProviderInstanceDelegate {
	func onSuccess(result: APDUProvider) {
		apduProvider = result
		apduProvider?.getContainers(delegate: self)
	}
}

extension MenuViewController: ContainerArrayDelegate {
	func onSuccess(result: [Container]) {
        DispatchQueue.main.async {
            guard result.count > 0 else {
                print("No container found.")
                self.endSigningAndUpdateUI(text: "Error: No container found.")
                return
            }

            self.containers = result

            guard let container = self.containers?[0] else {
                print("Couldn't select the first container.")
                return
            }
            self.insertOutputText(text: "\(result.count) containers found\n")
            self.apduProvider?.signData(container: container, data: Array(self.isoformatter.string(from: self.date).utf8), algID: AlgorithmID.rsaSha256pkcs1padding, delegate: self)
        }
	}
}

extension MenuViewController: VerifyDelegate {
	func onSuccessSessionPin(result: [UInt8]) {
		print("Session pin generated successfully")
		sessionPin = result
	}
	
	func onSuccessVerify() {
        DispatchQueue.main.async {
            if let continueSigningNotNil = self.continueSigning {
                self.continueSigning = nil
                continueSigningNotNil()
            } else {
                print("Verification successful")
                self.insertOutputText(text: "Verification successful\n")
            }
        }
	}
}

extension MenuViewController: VerifyUsingSessionPinDelegate {
	func onSuccessVerifyUsingSessionPin() {
        DispatchQueue.main.async {
            print("Verification using session pin successful")
            self.insertOutputText(text: "Verification using session pin successful\n")
        }
	}
}

extension MenuViewController: SignDelegate {
	
	func onVerifyNeeded(pinReference: VerifyCodes, selfProvider: APDUProvider, continueSigning: @escaping () -> Void) {
		self.continueSigning = continueSigning
		selfProvider.verify(pinReferece: pinReference, pin: [0x31, 0x31, 0x31, 0x31, 0x31], generateSessionPin: false, delegate: self)
	}
	
	func onSuccessSign(data: [UInt8]) {
        DispatchQueue.main.async {
            print("Sign successful")
            print(data)
            self.endSigningAndUpdateUI(text: "Signing successful\n" + String(describing: data))
        }
	}
}

extension MenuViewController: ResetDelegate {
	func onSuccess() {
		print("Reset successful")
	}
}



