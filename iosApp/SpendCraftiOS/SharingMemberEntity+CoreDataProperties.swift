//
//  SharingMemberEntity+CoreDataProperties.swift
//  SpendCraftiOS
//
//  Aile/Ortak bütçe üyeleri özellikleri
//

import Foundation
import CoreData

extension SharingMemberEntity {
    
    @nonobjc public class func fetchRequest() -> NSFetchRequest<SharingMemberEntity> {
        return NSFetchRequest<SharingMemberEntity>(entityName: "SharingMemberEntity")
    }
    
    @NSManaged public var id: Int64
    @NSManaged public var userId: String
    @NSManaged public var householdId: String
    @NSManaged public var role: String // OWNER, EDITOR, VIEWER
    @NSManaged public var invitedAt: Int64
    @NSManaged public var joinedAt: Int64
    @NSManaged public var isActive: Bool
    
}

extension SharingMemberEntity : Identifiable {
    
}
