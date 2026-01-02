import SwiftUI
import SharedWallet

class AlwaysVisibleHomeIndicatorController: UIViewController {
    
    private let contentViewController: UIViewController

    init(contentViewController: UIViewController) {
        self.contentViewController = contentViewController
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        // Add the Compose controller as a child
        addChild(contentViewController)
        view.addSubview(contentViewController.view)

        // Set up constraints or frame
        contentViewController.view.frame = view.bounds
        contentViewController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]

        contentViewController.didMove(toParent: self)
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.setNeedsUpdateOfHomeIndicatorAutoHidden()
    }

    // This is the key property to override
    override var prefersHomeIndicatorAutoHidden: Bool {
        return false // Return false to KEEP the indicator visible
    }

    override var childForHomeIndicatorAutoHidden: UIViewController? {
        return nil
    }
}

struct ComposeView: UIViewControllerRepresentable {
    
    func makeUIViewController(context: Context) -> UIViewController {
        // Create the standard KMP Compose controller
        let composeController = MainViewControllerKt.MainViewController()

        // Wrap it in your custom controller that forces visibility
        return AlwaysVisibleHomeIndicatorController(contentViewController: composeController)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {

    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
            .persistentSystemOverlays(.visible)
    }
}
