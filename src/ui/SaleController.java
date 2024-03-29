package ui;

import java.awt.Event;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import model.Product;
import model.Shop;

public class SaleController {
	
	@FXML
    private TableColumn<Product, String> columnCode;

    @FXML
    private TableColumn<Product, String> columnName;
    
    @FXML
    private TableColumn<Product, String> columnPrice;

    @FXML
    private TableView<Product> tableProducts;
    
    @FXML
    private TextField txtCode;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtQuantity;
    
    @FXML
    private TextField txtSaleValue;

    @FXML
    private TextField txtSearchProduct;

    private Shop shop;
    private Product selectedProduct;
    private int productIndex;
    
    
    public SaleController(Shop shop) {
    	this.shop = shop;
    }
    
    public void initialize() {
    	loadInformation();
    	initNumberFormatTextField();
    }
    
    private void initNumberFormatTextField() {   
    	UnaryOperator<Change> filter = change -> {
    	    String text = change.getText();

    	    if (text.matches("[0-9]*")) {
    	        return change;
    	    }
    	    
    	    return null;
    	};
    	
    	TextFormatter<String> formatter1 = new TextFormatter<>(filter);
    	
    	txtQuantity.setTextFormatter(formatter1);
    }
    
    public void loadInformation() {
    	
    	ObservableList<Product> data = FXCollections.observableArrayList(shop.getProducts());
    	 
    	columnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
    	columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    	columnPrice.setCellValueFactory(new PropertyValueFactory<>("priceSale"));
    	
        tableProducts.setItems(data);
        tableProducts.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
        	if(tableProducts.getSelectionModel().getSelectedItem() != null) {
        		selectedProduct = tableProducts.getSelectionModel().getSelectedItem();
        		productIndex = shop.getProducts().indexOf(selectedProduct);
        		
        		txtCode.setText(selectedProduct.getCode());
        		txtName.setText(selectedProduct.getName());
        	}
        });
    }
    
    private void loadInventory(List<Product> list) {
    	ObservableList<Product> data = FXCollections.observableArrayList(list);
   	 	
    	columnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
    	columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    	columnPrice.setCellValueFactory(new PropertyValueFactory<>("priceSale"));
    	
        tableProducts.setItems(data);
        tableProducts.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
        	if(tableProducts.getSelectionModel().getSelectedItem() != null) {
        		selectedProduct = tableProducts.getSelectionModel().getSelectedItem();
        		productIndex = shop.getProducts().indexOf(selectedProduct);
        		
        		txtCode.setText(selectedProduct.getCode());
        		txtName.setText(selectedProduct.getName());
        	}
        });
    } 
    
    @FXML
    public void typingProduct(KeyEvent event) {
    	if(!txtSearchProduct.getText().isEmpty() || (!txtSearchProduct.getText().isEmpty() && event.getCode() == KeyCode.DELETE)) {
    		String characters = txtSearchProduct.getText();
    		int size = characters.length();
    		
        	List<Product> aux = shop.getProducts().stream()
        			.filter(product -> characters.length() <= product.getName().length())
        			.collect(Collectors.toList());
        			
        	List<Product> searches = aux.stream()
        			.filter(product -> characters.equalsIgnoreCase(product.getName().substring(0, size)))
        			.collect(Collectors.toList());
        	
        	loadInventory(searches);
    	} else {
    		loadInformation();
    	}
    }
    
    @FXML
    void typingQuantity(KeyEvent event) {
    	if(tableProducts.getSelectionModel().getSelectedItem() != null && !txtQuantity.getText().isEmpty() || 
    			(!txtSaleValue.getText().isEmpty() && event.getCode() == KeyCode.DELETE)) {
    		
    		long quantity = Long.parseLong(txtQuantity.getText());
    		double price = tableProducts.getSelectionModel().getSelectedItem().getPriceSale();
    		
    		long saleValue =(long) price * quantity;   
    		
    		txtSaleValue.setText(String.valueOf(saleValue));
    		
    	} else {
    		txtSaleValue.setText("0");
    	}
    }
    
    
    @FXML
    public void makeSale(ActionEvent event) {
    	
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Info");
    	
    	if(!txtName.getText().isEmpty() && !txtCode.getText().isEmpty() && !txtQuantity.getText().isEmpty()) {
    		if(selectedProduct.makeSales(Integer.parseInt(txtQuantity.getText()))) {
	    		shop.getProducts().set(productIndex, selectedProduct);
	    		alert.setContentText("Venta efectuada.");
	    		alert.showAndWait();
	    		txtCode.clear();
	    		txtName.clear();
	    		txtQuantity.clear();
    		} else {
    			alert.setContentText("Cantidad de producto insuficiente.");
	    		alert.showAndWait();
    		}
    	} else {
    		alert.setContentText("Falta información.");
    		alert.showAndWait();
    	}
    }
    
    @FXML
    public void makePurchase(ActionEvent event) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Info");
    	
    	if(!txtName.getText().isEmpty() && !txtCode.getText().isEmpty() && !txtQuantity.getText().isEmpty()) {
    		selectedProduct.makePurchase(Integer.parseInt(txtQuantity.getText()));
    		shop.getProducts().set(productIndex, selectedProduct);
    		alert.setContentText("Compra efectuada.");
    		alert.showAndWait();
    		txtCode.clear();
    		txtName.clear();
    		txtQuantity.clear();
    		txtSaleValue.clear();
    	} else {
    		alert.setContentText("Falta información.");
    		alert.showAndWait();
    	}
    }
}
