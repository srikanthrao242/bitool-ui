package application;
	
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.pridict.RInterface.RAddRFunctions;
import com.pridictit.Core.GetJData;
import com.pridictit.Core.StaticVariables.StageFactory;
import com.pridictit.hive.Parquet;
import com.pridictit.hive.StaticDataFrames;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	
	public static final String TEST = "test";
	
	public static Stage mainStage;
	public BorderPane outerRoot;
	private static TabPane tabPane;
	private static VBox vbox;
	private static MenuButton dsNames ;
	public static ArrayList<String> datasets;
	//private static final String testAppCssUrl = AddCategories.class.getResource("/application/application.css").toExternalForm();
	//private static int TAB_INSTANCEID = 0;
	
	private static Main instance;

    public static Main getInstance() {
        return instance;
    }
    public Optional<Pair<String, String>> loginDailog()
    {
    	// Create the custom dialog.
    	Dialog<Pair<String, String>> dialog = new Dialog<>();
    	dialog.setTitle("Login Dialog");
    	dialog.setHeaderText("Look, a Custom Login Dialog");

    	// Set the icon (must be included in the project).
    	//dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

    	// Set the button types.
    	ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

    	// Create the username and password labels and fields.
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(20, 150, 10, 10));

    	TextField username = new TextField();
    	username.setPromptText("Username");
    	PasswordField password = new PasswordField();
    	password.setPromptText("Password");

    	grid.add(new Label("Username:"), 0, 0);
    	grid.add(username, 1, 0);
    	grid.add(new Label("Password:"), 0, 1);
    	grid.add(password, 1, 1);

    	// Enable/Disable login button depending on whether a username was entered.
    	Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
    	loginButton.setDisable(true);

    	// Do some validation (using the Java 8 lambda syntax).
    	username.textProperty().addListener((observable, oldValue, newValue) -> {
    	    loginButton.setDisable(newValue.trim().isEmpty());
    	});

    	dialog.getDialogPane().setContent(grid);

    	// Request focus on the username field by default.
    	Platform.runLater(() -> username.requestFocus());

    	// Convert the result to a username-password-pair when the login button is clicked.
    	dialog.setResultConverter(dialogButton -> {
    	    if (dialogButton == loginButtonType) {
    	        return new Pair<>(username.getText(), password.getText());
    	    }
    	    return null;
    	});

    	Optional<Pair<String, String>> result = dialog.showAndWait();
    	
    	return result;
    }
    public void newTab(String analyticName) throws IOException 
    {
    	Tab tab = new Tab(analyticName);    	
    	tab.setId(analyticName);
    	ScrollPane sp = new ScrollPane();
    	sp.setId("sp"+analyticName);
    	VBox vbox = new VBox();
    	vbox.setId("vb"+analyticName);
    	sp.setContent(vbox);
    	tab.setContent(sp);
    	/*FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/addCategories.fxml"));
    	tab.setContent(loader.load());*/
        tabPane.getTabs().add(tab);        
    }
    public void newTabCreate(ActionEvent event) throws IOException 
    {    	
    	int numTabs = tabPane.getTabs().size();
    	TextInputDialog dialog = new TextInputDialog("Analytic"+(numTabs+1));
    	dialog.setTitle("Pridict_IT");
    	dialog.setHeaderText("Enter Analytic Name");
    	dialog.setContentText("Analytic Name:");
    	
    	Optional<String> result = dialog.showAndWait();
    	
    	if (result.isPresent()){
    		this.newTab(result.get());
    	}
    }
    public void addNodetoTab(Node e){
    	System.out.println(tabPane.toString());
    	Tab activeTab = tabPane.getSelectionModel().getSelectedItem(); 
    	ScrollPane sp = (ScrollPane)activeTab.getContent();
    	VBox vbox = (VBox)sp.getContent();
    	vbox.getChildren().add(e);
    	System.out.println(activeTab.toString());
    	//activeTab.setContent(e);
    }
    public void addTableMenuItem(String tableName)
    {
    	MenuItem mI = new MenuItem(tableName);	
    	/*mI.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				System.out.println("table name clicked successfully");
				AddCategories ac= new AddCategories();
				ac.addCategories(tableName);
			}
    		
    	});*/
    	mI.addEventHandler(KeyEvent.KEY_PRESSED,  new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
            	if(ke.getCode().equals( KeyCode.DELETE ) ){
            		System.out.println("from delete "+tableName);
            	}
            };
        });
		mI.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				System.out.println("table name clicked successfully");
				AddCategories ac= new AddCategories(tableName);
				ac.addCategories();
				RAddRFunctions rs = new RAddRFunctions();
				Map<String,Object>msg = new HashMap<String,Object>();
				msg.put("dataset", tableName);
				msg.put("mtype", "getDataFrame");
				rs.getDataFrame(msg);
				
			}
			
		});
    	dsNames.getItems().add(mI);
    }
    public void loadFXML(String fxmlpath) throws IOException
    {
    	Parent parent1 = FXMLLoader.load(getClass().getResource(fxmlpath));
        Stage stage = new Stage();        
        stage.setTitle("PRIDICT_IT");        
        stage.setScene(new Scene(parent1));       
        stage.initStyle(StageStyle.UTILITY);        
		stage.setResizable(false);		
		stage.setMaximized(false);		 
        stage.show();
    }
    private MenuBar buildMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        menuBar.prefWidthProperty().bind(mainStage.widthProperty());
        
        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newMenuItem.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					newTabCreate(event);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        });
        MenuItem saveMenuItem = new MenuItem("Save");
        saveMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+E"));
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());
        
        fileMenu.getItems().addAll(newMenuItem, saveMenuItem,
                new SeparatorMenuItem(), exitMenuItem);
        
        Menu uploadMenu = new Menu("Upload");
        MenuItem csvMenuItem = new MenuItem("CSV");
        
        csvMenuItem.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					loadFXML("/application/filechoser.fxml");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        });
        uploadMenu.getItems().add(csvMenuItem);        
        menuBar.getMenus().addAll(fileMenu, uploadMenu);
        outerRoot.setTop(menuBar);        
        return menuBar;
    }
	@Override
	public void start(Stage stage) {
		try {
			mainStage = stage;
			StageFactory stageFactory = StageFactory.INSTANCE;
			stageFactory.setCurrentStage(stage);
			outerRoot = new BorderPane();
			datasets = new ArrayList<String>();
			StackPane left = new StackPane();
			left.setPrefWidth(47);
			outerRoot.setLeft(left);
			dsNames = new MenuButton();
			dsNames.setText("DS");
			dsNames.setPopupSide(Side.RIGHT);
			tabPane = new TabPane();
			vbox = new VBox();
	        outerRoot.setTop(buildMenuBar());
	        vbox.prefWidth(47);
	        vbox.getChildren().add(dsNames);
	        outerRoot.setLeft(vbox);	        
	        outerRoot.setCenter(tabPane);
	        Scene scene = new Scene(outerRoot, 1024, 768);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        stage.setScene(scene);
	        stage.setTitle("PRIDICT_IT");
	        tabPane.getSelectionModel().selectedItemProperty().addListener((obs,ov,nv)->{
	        	if(tabPane.getSelectionModel().getSelectedIndex() == 0){
	        		stage.setTitle("PRIDICT_IT");
	        	}else{
	        		stage.setTitle(nv.getText());
	        	}
	        });
	        Optional<Pair<String, String>> result = loginDailog();
	        result.ifPresent(usernamePassword -> {
	        	
	        	if(usernamePassword.getKey().equals("srikanth")&&usernamePassword.getValue().equals("srikanth"))
	        	{
	        		Parquet parquet = new Parquet();
	        		HashMap<String, Object> response = parquet.createDatabases();
	        		if(response.get("msg").equals("Success"))
	        		{
	        			HashMap<String, Object> response1 = parquet.createDataSourceInfo();
	        			if(response1.get("msg").equals("Success")){
	        				HashMap<String, Object> response2 = parquet.createPredicatesTables();
	        				if(response2.get("msg").equals("Success")){
	        					StaticDataFrames sd = new StaticDataFrames();
	        					HashMap<String, Object> response3 = sd.fillMainDataFrames();
	        					if(response3.get("msg").equals("Success")){
	        						GetJData gD = new GetJData();
			        				gD.getDatasources();
			        				gD.getCategories();
			        				stage.show();
	        					}else{
	        						System.out.println("error");
	        					}
	        				}	        				
	        			}
	        		}
	        	}
	        	else
	        	{
	        		System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
	        	}
	    	    
	    	});
	        instance = this;
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
