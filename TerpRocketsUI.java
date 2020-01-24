
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javafx.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.text.SimpleDateFormat;
import java.io.*;

/* The webpage I based the graphs on:
* https://levelup.gitconnected.com/realtime-charts-with-javafx-ed33c46b9c8d
*/
public class TerpRocketsUI extends Application{
	
	Series<String, Number> altSeries = new XYChart.Series<>();
	Series<String, Number> velSeries = new XYChart.Series<>();
	final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
	ScheduledExecutorService dataFeederThread = Executors.newSingleThreadScheduledExecutor();
	int altitude = 0;
	Double masterClock = 0.0;
	int step = 500; //in milliseconds 
	Label masterTime = new Label();
	Label currentAlt = new Label();
	
	@Override
	public void start(Stage primarystage){
		
		boolean zeroOut = false;
		//zero out the fake data to be put in
		if(zeroOut == false) {
			velSeries.getData().add(new XYChart.Data<>("0", 0));
			altSeries.getData().add(new XYChart.Data<>("0", 0));
			currentAlt.setText("Current Alt: 0");
			zeroOut = true;
		}
		
		primarystage.setTitle("Command and Control");
		
		
        BorderPane border = new BorderPane();
        
        VBox chartsVBox = new VBox();
        HBox timeHBox = new HBox();
        
        //create the charts
        LineChart<String, Number> altChart = makeAltGraph();
        LineChart<String, Number> velChart = makeVelGraph();
        chartsVBox.setSpacing(60);
        
        //move, set color and add chart
        chartsVBox.setTranslateX(-30.0);
        chartsVBox.setTranslateY(0.0);
        chartsVBox.setStyle("-fx-background-color: #42f563");
        
        chartsVBox.getChildren().add(altChart);
        chartsVBox.getChildren().add(velChart);
        
        
        
        
        
        //set border regions and color
        masterTime.setFont(Font.font(50));
        masterTime.setStyle("-fx-background-color: #e80000");
        currentAlt.setFont(Font.font(50));
        currentAlt.setStyle("-fx-background-color: #e80000");
        
        timeHBox.getChildren().add(masterTime);
        timeHBox.getChildren().add(currentAlt);
        
        timeHBox.setSpacing(50);
        
        border.setTop(timeHBox);
       
        border.setRight(chartsVBox);
        border.setStyle("-fx-background-color: #221aba");
       
     
       	
       Scene scene = new Scene(border, 1900, 1000);
       border.getTop().setTranslateX((scene.getWidth()/2) - 100);
       primarystage.setScene(scene);
     // primarystage.setFullScreen(true);
       
       //terminates the program on hitting the 'close' button
       primarystage.setOnCloseRequest(new EventHandler<WindowEvent>() {
    	   @Override
    	   public void handle(WindowEvent t) {
    		   Platform.exit();
    		   System.exit(0);
    	   }
       });
        
		primarystage.show();
		feedData();
	}
	
	
	//build the altitude graph
	public LineChart<String, Number> makeAltGraph() {
		final CategoryAxis  xAxis = new CategoryAxis (); 
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        xAxis.setAnimated(true); 
        yAxis.setLabel("Altitude (m)");
        yAxis.setAnimated(true); 
        
        
        LineChart<String, Number> altChart = new LineChart(xAxis, yAxis);
        altChart.setTitle("Altitude");
        altChart.setAnimated(false);
        
        
       
        altSeries.setName("Rocket Altitude Data");
        altChart.getData().add(altSeries);
        return altChart;
	}
	
	//build the velocity graph
	public LineChart<String, Number> makeVelGraph() {
		final CategoryAxis  xAxis = new CategoryAxis (); 
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        xAxis.setAnimated(true); 
        yAxis.setLabel("Velocity (m/s)");
        yAxis.setAnimated(true); 
        
        
        LineChart<String, Number> velChart = new LineChart(xAxis, yAxis);
        velChart.setTitle("Velocity");
        velChart.setAnimated(false);
        
        
       
        velSeries.setName("Rocket Velocity Data");
        velChart.getData().add(velSeries);
        return velChart;
	}
	
	//feeds fake data the charts every 500 milliseconds
	public void feedData() {
		
		dataFeederThread.scheduleAtFixedRate(() -> {
           
            Integer val = fakeAltData();
            String time = incrementTime();
            
            // Update the chart
            Platform.runLater(() -> {
            	//add next step of data
                altSeries.getData().add(new XYChart.Data<>(time, val));
                velSeries.getData().add(new XYChart.Data<>(time, val));
                currentAlt.setText("Current Alt: " + val.toString());
                Date now = new Date();
                masterTime.setText(simpleDateFormat.format(now));
            });
        }, 0, step, TimeUnit.MILLISECONDS);
		
		
	}
	
	//random number generator for fake data
	public int fakeAltData() {
		Integer random = ThreadLocalRandom.current().nextInt(20) + 3;
		return altitude+= random;
	}
	
	//updates the clock
	public String incrementTime() {
		masterClock = masterClock + (double) step/1000;
		return masterClock.toString();
	}
	/*
	 * force
	 * 3 pressures
	 * time
	 * 
	 * 
	 */
	
	
	
	
	
}
