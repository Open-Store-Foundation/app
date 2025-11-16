import UIKit
import UniformTypeIdentifiers
import OsSignerLib

@objc(ActionViewController)
class ActionViewController: UIViewController {
    
    private lazy var handler = GcipHandlerFactoryPlatform.shared.extensionHandler()
    
    private var gcipViewController: UIViewController?
    private var isInitialized = false

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .systemBackground

        if let sheet = self.presentationController as? UISheetPresentationController {
            sheet.detents = [.medium(), .large()]
            sheet.selectedDetentIdentifier = .medium
            sheet.prefersGrabberVisible = true
        }

        initializeKmpIfNeeded()
        processExtensionItems()
    }
    
    private func initializeKmpIfNeeded() {
        guard !isInitialized else { return }
        IosApp.shared.initialize()
        isInitialized = true
    }
    
    private func processExtensionItems() {
        guard let context = extensionContext else {
            completeWithError(status: GcipStatus.invalidformat)
            return
        }

        handler.handleExtensionContext(
            context: context,
            onSuccess: { [weak self] result in
                DispatchQueue.main.async {
                    guard let self = self else { return }
                    self.handleBinaryData(data: result)
                }
            },
            onError: { [weak self] result in
                DispatchQueue.main.async {
                    guard let self = self else { return }
                    self.completeWithError(status: result.error)
                }
            }
        )
    }
    
    private func handleBinaryData(data: Data) {
        let vc = GcipBridgeKt.createGcipViewController(
            requestData: data,
            caller: nil,
            onError: { [weak self] data in
                guard let data = data else {
                    self?.completeWithError(status: GcipStatus.unknownerror)
                    return
                }
                
                self?.completeWithData(data: data)
            },
            onSuccess: { [weak self] data in
                self?.completeWithData(data: data)
            }
        )
        
        presentGcipViewController(vc)
    }
    
    private func presentGcipViewController(_ vc: UIViewController) {
        gcipViewController = vc
        
        addChild(vc)
        vc.view.frame = view.bounds
        vc.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.addSubview(vc.view)
        vc.didMove(toParent: self)
    }
    
    private func completeWithError(status: GcipStatus) {
        guard let context = extensionContext else {
            return
        }

        let error = NSError(domain: IosApp.shared.bundleId, code: Int(status.value_), userInfo: nil)
        context.cancelRequest(withError: error)
    }
    
    private func completeWithData(data: Data) {
        guard let context = extensionContext else {
            return
        }
        
        let returnItem = handler.createExtensionItem(data: data)
        context.completeRequest(returningItems: [returnItem], completionHandler: nil)
    }
}
