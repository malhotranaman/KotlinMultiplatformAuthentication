// Location: /iosApp/iosApp/BiometricAuthIOS.swift
import Foundation
import LocalAuthentication

@objc
class BiometricAuthIOS: NSObject {

    @objc func isBiometricAvailable() -> Bool {
        let context = LAContext()
        var error: NSError?

        return context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error)
    }

    @objc func authenticate(
        title: String,
        subtitle: String,
        cancelText: String,
        completion: @escaping (String) -> Void
    ) {
        let context = LAContext()
        var error: NSError?

        // Check if biometric authentication is available
        guard context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) else {
            completion("NOT_AVAILABLE")
            return
        }

        // Set cancel button title
        context.localizedCancelTitle = cancelText

        // iOS combines the title and subtitle in the reason parameter
        // We'll format it to show both pieces of information
        let reason = subtitle.isEmpty ? title : "\(title)\n\(subtitle)"

        // Perform biometric authentication
        context.evaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, localizedReason: reason) { success, error in
            DispatchQueue.main.async {
                if success {
                    completion("SUCCESS")
                } else if let error = error {
                    if let laError = error as? LAError {
                        switch laError.code {
                        case .userCancel:
                            completion("USER_CANCELED")
                        case .biometryNotAvailable, .biometryNotEnrolled:
                            completion("NOT_AVAILABLE")
                        default:
                            completion("ERROR")
                        }
                    } else {
                        completion("ERROR")
                    }
                } else {
                    completion("ERROR")
                }
            }
        }
    }

    // Helper method to get the authentication type (Face ID or Touch ID)
    @objc func getBiometricType() -> String {
        let context = LAContext()
        _ = context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)

        switch context.biometryType {
        case .faceID:
            return "Face ID"
        case .touchID:
            return "Touch ID"
        default:
            return "Biometric Authentication"
        }
    }
}