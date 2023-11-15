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
            do {try await debugPrint(UseCasesProvider().stationsUseCase().getStationsLocal())}
            catch {debugPrint(error)}
        }
                
    }
}
