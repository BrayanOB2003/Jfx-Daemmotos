package ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import model.Product;
import model.Shop;

public class MainController {
	
	//Main components
	
	@FXML
    private Button buttonMakeSale;

    @FXML
    private Button buttonAddNewProduct;

    @FXML
    private Button buttonInventory;

	@FXML
    private AnchorPane newScreen;

    @FXML
    private Font x3;

    @FXML
    private Color x4;
    
    //AddNewProduct components
    
    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPricePurchase;

    @FXML
    private TextField txtPriceSale;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtReferences;
    
    private InventoryController inventoryController;
    
    private SaleController saleController;
    
    private  boolean imported;
    
    private Shop shop;
    
    private Product product;
    
    private List<String> references;
    
    public MainController() {
    	shop = new Shop();
    	references = new ArrayList<>();
    	inventoryController = new InventoryController(shop);
    	saleController = new SaleController(shop);
    	imported = false;
    }
    
    public void initialize() {
    	if(!imported) {
        	importData("data/data.csv");  
        	imported = true;
    	}
    }
    
    //Add numeric text formatting to text fields
    private void initNumberFormatTextField() {   
    	UnaryOperator<Change> filter = change -> {
    	    String text = change.getText();

    	    if (text.matches("[0-9]*")) {
    	        return change;
    	    }
    	    
    	    return null;
    	};

    	
    	TextFormatter<String> formatter1 = new TextFormatter<>(filter);
    	TextFormatter<String> formatter2 = new TextFormatter<>(filter);
    	TextFormatter<String> formatter3 = new TextFormatter<>(filter);
    	
    	txtPricePurchase.setTextFormatter(formatter1);
    	txtPriceSale.setTextFormatter(formatter2);
    	txtQuantity.setTextFormatter(formatter3);
    }
    
    @FXML
    public void clearInvetory(ActionEvent event) {
    	try {
    		File file = new File("src/data/data.csv");
    		file.delete();
    		shop.getProducts().clear();
    	} catch(Exception e) {
    		
    	}
    }
    
    @FXML
    public void makeSale(ActionEvent event) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MakeSaleScreen.fxml"));
		
		fxmlLoader.setController(saleController);
		
		Parent root = fxmlLoader.load();
		
		newScreen.getChildren().clear();
		newScreen.getChildren().add(root);
		
		buttonInventory.setStyle("");
		buttonAddNewProduct.setStyle("");
		buttonMakeSale.setStyle("-fx-background-color: #00BFFF");
		
    }

    @FXML
    public void openAddNewProduct(ActionEvent event) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddProductScreen.fxml"));
		
		fxmlLoader.setController(this);
		
		Parent root = fxmlLoader.load();
		
		newScreen.getChildren().clear();
		newScreen.getChildren().add(root);
		
		buttonInventory.setStyle("");
		buttonMakeSale.setStyle("");
		buttonAddNewProduct.setStyle("-fx-background-color: #00BFFF");
		
		initNumberFormatTextField();
    }
    
    private boolean addNewProduct() {
    	
    		try {
    			
    			if(!txtName.getText().isEmpty() && !txtPricePurchase.getText().isEmpty() && !txtPriceSale.getText().isEmpty()
    					&& !txtQuantity.getText().isEmpty() && !txtQuantity.getText().isEmpty() && !references.isEmpty()) {
    				
    				int incrementalCode = shop.getProducts().size() + 1;

    				shop.addProduct(txtName.getText(), new ArrayList<>(references),Double.parseDouble(txtPricePurchase.getText()), 
    						Double.parseDouble(txtPriceSale.getText()),incrementalCode,Integer.parseInt(txtQuantity.getText()));
    				references.clear();
    				return true;
    			} else {
    				return false;
    			}
    			
    			
    		} catch (Exception e) {
    			
    			return false;
    		}
    }
    
    @FXML
    public void addNewProduct(ActionEvent event) {
    	
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Info");
    	
    	if(addNewProduct()) {
    		
    		txtName.clear();
    		txtPricePurchase.clear();
    		txtPriceSale.clear();
    		txtQuantity.clear();
    		txtReferences.clear();
    		
	        alert.setContentText("Se agrego el producto.");
	        alert.showAndWait();
    	} else {
    		
	        alert.setContentText("Falta informacion.");
	        alert.showAndWait();
    	}
    }
    
    @FXML
    public void importData(ActionEvent event) {
    	
    	boolean imported = false;
    	
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Info");
    	
    	try {
    		FileChooser fileChooser = new FileChooser();
        	fileChooser.setTitle("Selecciona un archivo");
        	
        	FileChooser.ExtensionFilter extFilter = 
                    new FileChooser.ExtensionFilter("CSV files","*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
        	
        	File file = fileChooser.showOpenDialog(null);
        	imported = importData(file.getAbsolutePath());
        	
        	
    	} catch (Exception e){
    		imported = false;
    	}
    	
    	if(imported) {
    		alert.setContentText("Se importaron los datos.");
	        alert.showAndWait();
    	} else {
    		alert.setContentText("No se pudieron importar los datos.");
	        alert.showAndWait();
    	}
    }
    
    @FXML
    public void exportData(ActionEvent event) {
    	boolean exported = false;
    	
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Info");
    	
    	try {
    		FileChooser fileChooser = new FileChooser();
        	fileChooser.setTitle("Selecciona un directorio");
        	
        	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files","*.csv");
            
        	fileChooser.getExtensionFilters().add(extFilter);
        	
        	File file = fileChooser.showSaveDialog(null);
        	exported = exportData(file.getAbsolutePath());
        	
    	} catch (Exception e){
    		exported = false;
    	}
    	
    	if(exported) {
    		alert.setContentText("Se exportaron los datos.");
	        alert.showAndWait();
    	} else {
    		alert.setContentText("No se pudieron exportar los datos.");
	        alert.showAndWait();
    	}
    }
    
    public boolean exportData(String path) {
    	return shop.exportData(path);
    }
    
    public boolean importData(String path) {
    	return shop.importData(path);
    }
    
    @FXML
    public void priceChangePurchase(KeyEvent event) {
    	product = new Product();
    	
    	if(!txtPricePurchase.getText().isEmpty() || event.getCode() == KeyCode.DELETE) {
    		
    		txtPriceSale.setText(String.valueOf((int)product.generatePriceSale(Double.parseDouble(txtPricePurchase.getText()))));
    	
    	}
    }

    @FXML
    public void putValuePurchase(KeyEvent event) {
    	
    }

    @FXML
    public void openInventory(ActionEvent event) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InvetoryScreen.fxml"));
		
		fxmlLoader.setController(inventoryController);
		
		Parent root = fxmlLoader.load();
		
		newScreen.getChildren().clear();
		newScreen.getChildren().add(root);
		
		buttonMakeSale.setStyle("");
		buttonAddNewProduct.setStyle("");
		buttonInventory.setStyle("-fx-background-color: #00BFFF");
    }
    
    @FXML
    public void addReference(ActionEvent event) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
    	if(!txtReferences.getText().isEmpty()) {
    		references.add(txtReferences.getText());
    		txtReferences.clear();
    		alert.setTitle("Info");
	        alert.setContentText("Referencia agregada.");
	        alert.showAndWait();
    	} else {
    		alert.setTitle("Info");
	        alert.setContentText("Digita una referencia.");
	        alert.showAndWait();
    	}
    }

}
