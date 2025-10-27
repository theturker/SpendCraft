//
//  LanguageHelper.swift
//  SpendCraftiOS
//
//  Dil Yönetimi
//

import Foundation
import ObjectiveC

class LanguageHelper {
    static let shared = LanguageHelper()
    
    private init() {}
    
    // Uygulama dilini değiştir
    func setLanguage(_ languageCode: String) {
        UserDefaults.standard.set([languageCode], forKey: "AppleLanguages")
        UserDefaults.standard.synchronize()
        
        // Bundle'ı güncelle
        Bundle.setLanguage(languageCode)
    }
    
    // Mevcut dili al
    func getCurrentLanguage() -> String {
        return UserDefaults.standard.stringArray(forKey: "AppleLanguages")?.first ?? "tr"
    }
}

// Bundle extension for language switching
extension Bundle {
    private static var bundleKey: UInt8 = 0
    
    class func setLanguage(_ language: String) {
        defer {
            object_setClass(Bundle.main, AnyLanguageBundle.self)
        }
        
        objc_setAssociatedObject(Bundle.main, &bundleKey, Bundle.main.path(forResource: language, ofType: "lproj"), .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
    }
    
    var localizedPath: String? {
        return objc_getAssociatedObject(self, &Bundle.bundleKey) as? String
    }
}

private class AnyLanguageBundle: Bundle {
    override func localizedString(forKey key: String, value: String?, table tableName: String?) -> String {
        guard let path = self.localizedPath,
              let bundle = Bundle(path: path) else {
            return super.localizedString(forKey: key, value: value, table: tableName)
        }
        return bundle.localizedString(forKey: key, value: value, table: tableName)
    }
}
