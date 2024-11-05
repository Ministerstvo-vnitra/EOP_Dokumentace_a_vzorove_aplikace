//File.swift
//  apduDemoApp
//
//  Created by Marek Hrasna on 22/03/2018. <marek.hrasna@ahead-itec.com>
//  Copyright © 2018 AHEAD iTec, s.r.o. All rights reserved.
//

import Foundation
import UIKit


//
// MARK :- HEADER
//
class ManufacturerViewHeader: UITableViewHeaderFooterView {
	
	override init(reuseIdentifier: String?) {
		super.init(reuseIdentifier: reuseIdentifier)
		
		contentView.backgroundColor = UIColor.white
		self.textLabel?.text = NSLocalizedString("SelectManufacturer", comment: "")
	}
	
	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}
}
