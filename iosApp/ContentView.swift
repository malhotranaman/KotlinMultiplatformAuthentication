// Location: /iosApp/iosApp/ContentView.swift
import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject private var viewModel = BiometricViewModelWrapper()

    var body: some View {
        VStack(spacing: 16) {
            Text("Secure Authentication")
                .font(.largeTitle)
                .fontWeight(.semibold)
                .padding(.top, 32)

            Text("Use biometrics to securely access your account")
                .font(.body)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal)

            Spacer()

            if viewModel.isBiometricAvailable == nil {
                ProgressView()
                    .scaleEffect(1.5)
                    .padding()
            } else if viewModel.isBiometricAvailable == true {
                Image(systemName: "faceid")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 80, height: 80)
                    .foregroundColor(.blue)
                    .padding(.bottom, 24)

                Button(action: {
                    viewModel.authenticate()
                }) {
                    Text("Authenticate with \(viewModel.getBiometricType())")
                        .fontWeight(.semibold)
                        .frame(minWidth: 200)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .disabled(viewModel.isAuthenticating)
                .padding(.bottom, 24)

                if let authResult = viewModel.authResult {
                    let (text, color) = getAuthResultInfo(authResult)
                    Text(text)
                        .foregroundColor(color)
                        .font(.headline)
                }
            } else {
                Image(systemName: "lock.fill")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 80, height: 80)
                    .foregroundColor(.red)
                    .padding(.bottom, 24)

                Text("Biometric authentication is not available")
                    .font(.headline)
                    .foregroundColor(.red)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)

                Text("Please use alternative authentication methods")
                    .font(.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
            }

            Spacer()
        }
        .padding()
        .onAppear {
            viewModel.checkBiometricAvailability()
        }
    }

    private func getAuthResultInfo(_ authResult: BiometricResult) -> (String, Color) {
        switch authResult {
        case .success:
            return ("Authentication successful!", .green)
        case .error:
            return ("Authentication failed", .red)
        case .notAvailable:
            return ("Biometric auth not available", .red)
        case .userCanceled:
            return ("Authentication canceled", .orange)
        default:
            return ("Unknown result", .gray)
        }
    }
}

// Wrapper for the Kotlin ViewModel to make it observable in SwiftUI
class BiometricViewModelWrapper: ObservableObject {
    private let viewModel = BiometricViewModel()
    private let biometricAuth = BiometricAuth()

    @Published var isBiometricAvailable: Bool? = nil
    @Published var isAuthenticating: Bool = false
    @Published var authResult: BiometricResult? = nil

    init() {
        // Start collecting state updates
        startStateObservation()
    }

    func checkBiometricAvailability() {
        viewModel.checkBiometricAvailability()
    }

    func authenticate() {
        viewModel.authenticate()
    }

    func getBiometricType() -> String {
        return biometricAuth.getBiometricType()
    }

    private func startStateObservation() {
        // This is a simplified example. In a real app, you'd use KMMViewModel or another library
        // to properly collect Kotlin Flow in Swift

        // Simulating state observation with a timer
        // In a real app, you would use proper Flow collection
        Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { [weak self] _ in
            if let state = self?.viewModel.state.value {
                DispatchQueue.main.async {
                    self?.isBiometricAvailable = state.isBiometricAvailable
                    self?.isAuthenticating = state.isAuthenticating
                    self?.authResult = state.authResult
                }
            }
        }
    }
}

// Preview provider for SwiftUI
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}