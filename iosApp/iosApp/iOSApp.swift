import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        KoinStarterKt.initializeKoin()
        getStations()
    }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
    
    func getStations()  {
        Task {
            for await it in UseCasesProvider().stationsUseCase().startRepeatingRequest(initialLocation: UserLocation(latitude: 48.1395388, longitude: 11.5567907)) {
                debugPrint(it?.first)
            }
        }
    }
}
