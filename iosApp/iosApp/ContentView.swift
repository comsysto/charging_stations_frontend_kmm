import SwiftUI
import shared

struct ContentView: View {
    let stations = UseCasesProvider.stationsUseCase.getStationsLocal()
	var body: some View {
        Text(String(stations))
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
