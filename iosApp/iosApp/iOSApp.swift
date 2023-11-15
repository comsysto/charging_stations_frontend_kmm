import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        KoinStarter.initializeKoin()
    }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
