package asset.customer.manage

import application.StockApplication
import application.executor.UI
import application.usecase.UseCaseException
import asset.customer.usecase.AddCustomerUseCase
import asset.customer.usecase.GetCustomerUseCase
import asset.customer.usecase.UpdateCustomerUseCase
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.Stage
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory
import util.DialogUtil
import util.NumberOnlyTextFormatter
import util.TextToIntFormatter
import javax.inject.Inject

class ManageCustomerController {

    companion object {

        private const val UNSUPPORTED_CUSTOMER_ID = -1

        fun createForUpdate(customerId: Int): Scene {
            val view = createView(customerId)
            return Scene(view)
        }

        fun createForAdd(): Scene {
            val view = createView()
            return Scene(view)
        }

        private fun createView(customerId: Int = UNSUPPORTED_CUSTOMER_ID): Pane {
            val loader = FXMLLoader(ManageCustomerController::class.java.getResource("../../../../resources/asset/customer/stock-manage-customer.fxml"))
            val view = loader.load<Pane>()
            val controller = loader.getController<ManageCustomerController>()
            controller.initialize(customerId)
            return view
        }
    }

    init {
        StockApplication.stockComponent.inject(this)
    }

    private val logger = LoggerFactory.getLogger(ManageCustomerController::class.java)

    @Inject
    lateinit var addCustomerUseCase: AddCustomerUseCase
    @Inject
    lateinit var updateCustomerUseCase: UpdateCustomerUseCase
    @Inject
    lateinit var getCustomerUseCase: GetCustomerUseCase

    @FXML
    private lateinit var idTextField: TextField
    @FXML
    private lateinit var nameTextField: TextField
    @FXML
    private lateinit var addressTextField: TextField
    @FXML
    private lateinit var idNumberTextField: TextField
    @FXML
    private lateinit var pdvNumberTextField: TextField
    @FXML
    private lateinit var cancelButton: Button
    @FXML
    private lateinit var saveButton: Button

    private fun initialize(customerId: Int) {
        logger.info("Initializing view for customer id '$customerId'")

        initializeCommon()

        if (customerId == UNSUPPORTED_CUSTOMER_ID) {
            initializeForAdd()
        } else {
            initializeForUpdate(customerId)
        }
    }

    private fun initializeCommon() {
        idTextField.textFormatter = TextToIntFormatter()
        idNumberTextField.textFormatter = NumberOnlyTextFormatter()
        pdvNumberTextField.textFormatter = NumberOnlyTextFormatter()
        cancelButton.setOnAction { (cancelButton.scene.window as Stage).close() }
    }

    private fun initializeForAdd() {
        saveButton.setOnAction {
            launch(UI) {
                try {
                    addCustomerUseCase.addNewCustomer(
                            id = idTextField.text.toInt(),
                            name = nameTextField.text,
                            idNumber = idNumberTextField.text,
                            pdvNumber = pdvNumberTextField.text,
                            address = addressTextField.text)
                    close()
                } catch (e: UseCaseException) {
                    DialogUtil.showErrorDialog(header = "Failed to insert customer", content = e.message)
                }
            }
        }
    }

    private fun initializeForUpdate(id: Int) {
        idTextField.isEditable = false

        launch(UI) {
            try {
                val customer = getCustomerUseCase.getCustomer(id)
                fillInFields(
                        id = customer.id,
                        name = customer.name,
                        idNumber = customer.idNumber,
                        pdvNumber = customer.pdvNumber,
                        address = customer.address)
            } catch (e: UseCaseException) {
                DialogUtil.showErrorDialog(header = "Failed to get customer", content = e.message)
            }
        }

        saveButton.setOnAction {
            launch(UI) {
                try {
                    updateCustomerUseCase.updateCustomer(
                            id = idTextField.text.toInt(),
                            name = nameTextField.text,
                            idNumber = idNumberTextField.text,
                            pdvNumber = pdvNumberTextField.text,
                            address = addressTextField.text)
                    close()
                } catch (e: UseCaseException) {
                    DialogUtil.showErrorDialog(header = "Failed to insert customer", content = e.message)
                }
            }
        }
    }

    private fun fillInFields(id: Int, name: String, idNumber: String, pdvNumber: String, address: String) {
        idTextField.text = id.toString()
        nameTextField.text = name
        idNumberTextField.text = idNumber
        pdvNumberTextField.text = pdvNumber
        addressTextField.text = address
    }

    private fun close() {
        (cancelButton.scene.window as Stage).close()
    }
}