//
//  AccountsViewModel.swift
//  SpendCraftiOS
//
//  Created by AI Assistant on 2024.
//

import Foundation
import CoreData
import SwiftUI

class AccountsViewModel: ObservableObject {
    @Published var accounts: [AccountEntity] = []
    
    private let context = CoreDataStack.shared.container.viewContext
    
    func loadAccounts() {
        let fetchRequest: NSFetchRequest<AccountEntity> = AccountEntity.fetchRequest() as! NSFetchRequest<AccountEntity>
        fetchRequest.sortDescriptors = [NSSortDescriptor(keyPath: \AccountEntity.name, ascending: true)]
        
        do {
            accounts = try context.fetch(fetchRequest)
        } catch {
            print("Error fetching accounts: \(error)")
        }
    }
    
    func addAccount(name: String, type: String, currency: String) {
        let account = AccountEntity(context: context)
        account.id = Int64.random(in: 1...1000000)
        account.name = name
        account.type = type
        account.currency = currency
        account.isDefault = accounts.isEmpty
        account.archived = false
        
        CoreDataStack.shared.saveContext()
        loadAccounts()
    }
    
    func updateAccount(_ account: AccountEntity, name: String, type: String, currency: String) {
        account.name = name
        account.type = type
        account.currency = currency
        
        CoreDataStack.shared.saveContext()
        loadAccounts()
    }
    
    func deleteAccount(_ account: AccountEntity) {
        context.delete(account)
        CoreDataStack.shared.saveContext()
        loadAccounts()
    }
    
    func setDefaultAccount(_ account: AccountEntity) {
        // Remove default from all accounts
        for acc in accounts {
            acc.isDefault = false
        }
        // Set this account as default
        account.isDefault = true
        
        CoreDataStack.shared.saveContext()
        loadAccounts()
    }
    
    func archiveAccount(_ account: AccountEntity) {
        account.archived.toggle()
        CoreDataStack.shared.saveContext()
        loadAccounts()
    }
}