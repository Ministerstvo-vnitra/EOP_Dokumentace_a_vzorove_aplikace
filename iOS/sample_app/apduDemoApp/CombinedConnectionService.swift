//
//  CombinedConnectionService.swift
//
//  Created by David Bielik on 03/06/2018.
//

import Foundation
import CardConnectionWrapper
import ReaderConnectionWrapper

/// The main connection service layer which encapsulates other connection services and their delegates
open class CombinedConnectionService: ReaderConnectionServiceProtocol {
	
	// MARK: Properties
	public var delegate: ReaderConnectionServiceDelegate? {
		didSet {
			resetDelegates()
		}
	}
	public internal(set) var services: [String: ReaderConnectionServiceProtocol] = [:]
	public var knownReaders: [SmartCardReaderProtocol] {
		get { return combinedKnownReaders() }
		set {}
	}
	public var manufacturers: [String] {
		return Array(services.keys)
	}
	
	// MARK: Reader Connection Service
	public init(services: [ReaderConnectionServiceProtocol & Manufactured]) {
		for service in services {
			self.services[service.manufacturer] = service
		}
	}
	
	public func connect(reader: SmartCardReaderProtocol) {
		services[reader.manufacturer]?.connect(reader: reader)
	}
	
	public func disconnect(reader: SmartCardReaderProtocol) {
		services[reader.manufacturer]?.disconnect(reader: reader)
	}
	
	public func pair(reader: SmartCardReaderProtocol) {
		services[reader.manufacturer]?.pair(reader: reader)
		
	}
	
	public func unpair(reader: SmartCardReaderProtocol, onCompletion: (() -> Void)?) {
		services[reader.manufacturer]?.unpair(reader: reader, onCompletion: onCompletion)
	}
	
	public func scanReadersForPairing(untilStopped: Bool = true) {
		services.forEach { $0.value.scanReadersForPairing(untilStopped: untilStopped) }
	}
	
	/**
	Request a pairing scanning operation only on a specific service
	- parameters:
	- manufacturer: the desired service is determined by this String
	- untilStopped: boolean value indicating if the scan should be repeated until explict stopScanning call or just once. Default value is `true`
	*/
	public func scanReadersForPairing(manufacturer: String, untilStopped: Bool = true) {
		services[manufacturer]?.scanReadersForPairing(untilStopped: untilStopped)
	}
	
	public func scanReadersForConnecting(untilStopped: Bool = true) {
		services.forEach { $0.value.scanReadersForConnecting(untilStopped: untilStopped) }
	}
	
	/**
	Request a connection scanning operation only on a specific service
	- parameters:
	- manufacturer: the desired service is determined by this String
	- untilStopped: boolean value indicating if the scan should be repeated until explict stopScanning call or just once. Default value is `false`
	*/
	public func scanReadersForConnecting(manufacturer: String, untilStopped: Bool = false) {
		services[manufacturer]?.scanReadersForConnecting(untilStopped: untilStopped)
	}
	
	public func stopScanning() {
		services.forEach { $0.value.stopScanning() }
	}
	
	// MARK: Private
	private func combinedKnownReaders() -> [SmartCardReaderProtocol] {
		let combinedKnownReaders = services.reduce([], { (res, arg1) -> [SmartCardReaderProtocol] in
			let ((_, service)) = arg1
			var newRes = res
			newRes.append(contentsOf: service.knownReaders)
			return newRes
		})
		// sort by last seen
		return combinedKnownReaders.sorted(by: { $0.lastSeen.compare($1.lastSeen) == .orderedDescending
		})
	}
	
	// MARK: Public
	/// Sets the delegate for each service to self so it can act as a layer in between each service.
	public func resetDelegates() {
		services.forEach { $0.value.delegate = self }
	}
}

// MARK: Reader Connection Service Delegate
extension CombinedConnectionService: ReaderConnectionServiceDelegate {
	
	public func didConnect(reader: SmartCardReaderProtocol) {
		delegate?.didConnect(reader: reader)
	}
	
	public func didDisconnect(reader: SmartCardReaderProtocol) {
		delegate?.didDisconnect(reader: reader)
	}
	
	public func didUpdate(service: ReaderConnectionServiceProtocol, state: ReaderConnectionServiceState) {
		delegate?.didUpdate(service: service, state: state)
	}
	
	public func didPairWith(reader: SmartCardReaderProtocol, connected: Bool) {
		delegate?.didPairWith(reader: reader, connected: connected)
	}
	
	public func didDiscover(newReader: SmartCardReaderProtocol) {
		delegate?.didDiscover(newReader: newReader)
	}
	
	public func didDiscover(knownReader: SmartCardReaderProtocol) {
		delegate?.didDiscover(knownReader: knownReader)
	}
	
	public func didUpdate(knownReaders: [SmartCardReaderProtocol]) {
		delegate?.didUpdate(knownReaders: self.knownReaders)
	}
	
	public func didInsertCard(connection: ProvidingCardConnection, on reader: SmartCardReaderProtocol) {
		delegate?.didInsertCard(connection: connection, on: reader)
	}
	
	public func didRemoveCard(on reader: SmartCardReaderProtocol) {
		delegate?.didRemoveCard(on: reader)
	}
}
