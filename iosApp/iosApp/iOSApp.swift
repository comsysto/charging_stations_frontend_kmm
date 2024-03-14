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
    let userLng: Double = 11.5567907;
    let userLat: Double = 48.1395388;
    
    func getStations()  {
        Task {
            for await it in UseCasesProvider().stationsUseCase()
                .startRepeatingRequest(initialLocation: UserLocation(
                    latitude:userLat, longitude: userLng)
                ) {
                debugPrint(it?.first)
            }
        }
    }
}
