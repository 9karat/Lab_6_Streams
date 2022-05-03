package com.example; 

import javafx.application.Application; 
import javafx.scene.Scene; 
import javafx.stage.Stage; 
import java.io.IOException; 
import java.io.DataInputStream; 
import java.io.DataOutputStream; 
import java.net.Socket; 
import java.util.logging.Level; 
import java.util.logging.Logger; 
import javafx.application.Platform; 
import javafx.event.ActionEvent; 
import javafx.geometry.Insets; 
import javafx.scene.Group; 
import javafx.scene.control.*; 
import javafx.scene.paint.Color; 
import javafx.scene.text.Font; 
import javafx.scene.text.Text; 
import javafx.scene.control.Button; 
import javafx.scene.effect.DropShadow; 
import javafx.scene.layout.GridPane; 
import javafx.scene.text.FontWeight;
import javafx.stage.WindowEvent; 

public class Client extends Application { 
    static Stage clientStage = new Stage(); 
    
    Group root = new Group(); 
    Group group = new Group(); 
    GridPane gp = new GridPane();
    
    ToggleGroup tg1 = new ToggleGroup(); 
    ToggleGroup tg2 = new ToggleGroup(); 
    ToggleGroup tg3 = new ToggleGroup(); 
    ToggleGroup tg4 = new ToggleGroup(); 
    
    Button connect; 
    Button disconnect;
    
    String port; 
    String host; 

    Text portText; 
    Text hostText; 
    Text thread1Area; 
    Text thread2Area; 
    Text thread3Area; 
    Text thread4Area;

    TextArea display;
    TextField portField;
    TextField hostField; 
    
    Text totalAreaRes;
    
    DropShadow dropShadow; 
    
    RadioButton radioButton1; 
    RadioButton radioButton2; 
    RadioButton radioButton3; 
    RadioButton radioButton4; 
    RadioButton radioButton5; 
    RadioButton radioButton6; 
    RadioButton radioButton7; 
    RadioButton radioButton8; 
    
    DataOutputStream toServer; 
    DataInputStream fromServer;
    
    Socket socket; 
    
    Boolean closeSocket = false; 
    
    @Override public void start(Stage clientStage) { 
        clientStage.setTitle("Лабораторна робота №6 АІ-195 Бондар - Клієнт");
        clientStage.setResizable(false); 
        
        CreateGraphNodes();      
        CreateControlNodes();
        
        onAction();
        
        Scene scene = new Scene(root, 750, 300, Color.WHITESMOKE);
        
        clientStage.setScene(scene);
        clientStage.show();
        clientStage.setOnCloseRequest((WindowEvent we) -> { 
            System.exit(0); 
        }); 
    }
    
    private void Shadow() { 
        dropShadow = new DropShadow(); 
        dropShadow.setRadius(5.0); 
        dropShadow.setOffsetX(5.0); 
        dropShadow.setOffsetY(5.0); 
        dropShadow.setColor(Color.GRAY); 
    }
    
    private void CreateGraphNodes() { 
        Shadow(); 
        
        group.setEffect(dropShadow); 
        root.getChildren().add(createText(40, 20, "Клієнт", Color.GREEN)); 
    } 
    
    private void CreateControlNodes() {
        display = new TextArea(); 
        display.setLayoutX(30); 
        display.setLayoutY(30);
        display.setPrefSize(180, 80); 
        display.setWrapText(true); 
        
        portText = new Text("Port"); 
        portText.setLayoutX(230); 
        portText.setLayoutY(50); 
        portField = new TextField(); 
        portField.setText("8000"); 
        portField.setLayoutX(270); 
        portField.setLayoutY(30); 
        portField.setPrefSize(165, 0); 

        hostText = new Text("Host"); 
        hostText.setLayoutX(230); 
        hostText.setLayoutY(90); 
        hostField = new TextField(); 
        hostField.setText("127.0.0.1"); 
        hostField.setLayoutX(270); 
        hostField.setLayoutY(70); 
        hostField.setPrefSize(165, 0);
        
        connect = new Button(); 
        connect.setText("Приєднатися"); 
        connect.setLayoutX(465); 
        connect.setLayoutY(30); 
        connect.setPrefSize(100, 0); 
        connect.setTextFill(Color.BROWN); 
        connect.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        disconnect = new Button(); 
        disconnect.setText("Від'єднатися"); 
        disconnect.setLayoutX(465); 
        disconnect.setLayoutY(60); 
        disconnect.setPrefSize(100, 0); 
        disconnect.setTextFill(Color.BROWN); 
        disconnect.setFont(Font.font("Arial", FontWeight.BOLD, 12)); 
        disconnect.setDisable(true); 
        
        radioButton1 = new RadioButton("Розпочати"); 
        radioButton1.setToggleGroup(tg1); 
        radioButton2 = new RadioButton("Завершити"); 
        radioButton2.setToggleGroup(tg1); 
        radioButton3 = new RadioButton("Розпочати"); 
        radioButton3.setToggleGroup(tg2); 
        radioButton4 = new RadioButton("Завершити"); 
        radioButton4.setToggleGroup(tg2); 
        radioButton5 = new RadioButton("Розпочати"); 
        radioButton5.setToggleGroup(tg3); 
        radioButton6 = new RadioButton("Завершити"); 
        radioButton6.setToggleGroup(tg3); 
        radioButton7 = new RadioButton("Розпочати");
        radioButton7.setToggleGroup(tg4);
        radioButton8 = new RadioButton("Завершити"); 
        radioButton8.setToggleGroup(tg4);
        
        gp.setHgap(25); 
        gp.setVgap(10); 
        gp.setPadding(new Insets(130, 0, 0, 260)); 
        
        gp.add(new Text("Потік 1"), 0, 0); 
        gp.add(new Text("Потік 2"), 1, 0); 
        gp.add(new Text("Потік 3"), 2, 0); 
        gp.add(new Text("Потік 4"), 3, 0); 

        gp.add(radioButton1, 0, 1); 
        gp.add(radioButton2, 0, 2); 
        gp.add(radioButton3, 1, 1); 
        gp.add(radioButton4, 1, 2); 
        gp.add(radioButton5, 2, 1); 
        gp.add(radioButton6, 2, 2); 
        gp.add(radioButton7, 3, 1); 
        gp.add(radioButton8, 3, 2); 
        gp.setDisable(true); 

        Text textSumArea = createText(30, 140, "Загальна площа, кв.см: ", Color.BLACK); 
        Text textAreas = new Text(30, 235, "Площі прямокутників, кв. см:"); 
        
        thread1Area = new Text(260, 235, "0"); 
        thread1Area.setFill(Color.BLUE); 
        thread1Area.setFont(Font.font(16)); 
        
        thread2Area = new Text(345, 235, "0"); 
        thread2Area.setFill(Color.BLUE); 
        thread2Area.setFont(Font.font(16)); 
        thread3Area = new Text(430, 235, "0"); 
        thread3Area.setFill(Color.BLUE); 
        thread3Area.setFont(Font.font(16)); 
        thread4Area = new Text(515, 235, "0"); 
        thread4Area.setFill(Color.BLUE); 
        thread4Area.setFont(Font.font(16)); 
        
        totalAreaRes = new Text(100, 190, "0"); 
        totalAreaRes.setFill(Color.BLACK); 
        totalAreaRes.setFont(Font.font(18)); 
        
        group.getChildren().addAll(gp, display, portField, portText, hostField, hostText, connect, disconnect, textSumArea, textAreas, totalAreaRes, thread1Area, thread2Area, thread3Area, thread4Area); 
        
        root.getChildren().addAll(group); 
    } 
    
    private void onAction() { 
        connect.setOnAction((ActionEvent event) -> { 
            host = hostField.getText(); 
            port = portField.getText(); 
            if (host.isEmpty() || port.isEmpty()) 
                display.appendText("<> Хост та порт повинні бути обрані\n"); 
            else new Thread(() -> { 
                try {
                    socket = new Socket(host, Integer.parseInt(port)); 
                    if (socket.isConnected()) { 
                        closeSocket = false;
                        fromServer = new DataInputStream(socket.getInputStream()); 
                        toServer = new DataOutputStream(socket.getOutputStream()); 
                        
                        toServer.writeUTF(">>> З'єднано: " + socket.getInetAddress().getHostAddress() + "\n"); 
                        toServer.flush(); 
                        connect.setDisable(true); 
                        
                        disconnect.setDisable(false); 
                        gp.setDisable(false); 
                        
                        while (true) { 
                            String serverMessage = fromServer.readUTF(); 
                            String[] values = serverMessage.split(" "); 
                            
                            switch (values[0]) { 
                                case ">>>": 
                                    Platform.runLater(() -> { 
                                        display.appendText(serverMessage); 
                                    }); 
                                    break; 
                                case "<<<": 
                                    Platform.runLater(() -> { 
                                        display.appendText(serverMessage); 
                                    }); 
                                    break; 
                                case "Thread1": 
                                    Platform.runLater(() -> { 
                                        thread1Area.setText(values[1]); 
                                    }); 
                                    break; 
                                case "Thread2": 
                                    Platform.runLater(() -> { 
                                        thread2Area.setText(values[1]); 
                                    });
                                    break;
                                case "Thread3": 
                                    Platform.runLater(() -> { 
                                        thread3Area.setText(values[1]); 
                                    }); 
                                    break; 
                                case "Thread4": 
                                    Platform.runLater(() -> { 
                                        thread4Area.setText(values[1]); 
                                    }); 
                                    break; 
                                case "Thread5": 
                                    Platform.runLater(() -> {
                                        totalAreaRes.setText(values[1]); 
                                    }); 
                                    break; 
                                default: break; 
                            } 
                            
                            radioButton1.setOnAction((ActionEvent t) -> { 
                                try { 
                                    toServer.writeUTF("Thread1 start"); 
                                    toServer.flush(); 
                                } catch (IOException ex) { 
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
                                } 
                            }); 
                            
                            radioButton2.setOnAction((ActionEvent t) -> { 
                                try { 
                                    toServer.writeUTF("Thread1 cancel"); 
                                    toServer.flush(); 
                                } catch (IOException ex) { 
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
                                } 
                            }); 
                            
                            radioButton3.setOnAction((ActionEvent t) -> { 
                                try { 
                                    toServer.writeUTF("Thread2 start"); 
                                    toServer.flush(); 
                                } catch (IOException ex) { 
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
                                } 
                            }); 
                            
                            radioButton4.setOnAction((ActionEvent t) -> { 
                                try { 
                                    toServer.writeUTF("Thread2 cancel"); 
                                    toServer.flush(); 
                                } catch (IOException ex) {
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
                                } 
                            }); 
                            
                            radioButton5.setOnAction((ActionEvent t) -> { 
                                try { 
                                    toServer.writeUTF("Thread3 start"); 
                                    toServer.flush(); 
                                } catch (IOException ex) { 
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
                                } 
                            }); 
                            
                            radioButton6.setOnAction((ActionEvent t) -> { 
                                try { 
                                    toServer.writeUTF("Thread3 cancel"); 
                                    toServer.flush(); 
                                } catch (IOException ex) { 
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                } 
                            }); 
                            
                            radioButton7.setOnAction((ActionEvent t) -> { 
                                try { 
                                    toServer.writeUTF("Thread4 start"); 
                                    toServer.flush(); 
                                } catch (IOException ex) { 
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
                                } 
                            }); 
                            
                            radioButton8.setOnAction((ActionEvent t) -> { 
                                try { 
                                    toServer.writeUTF("Thread4 cancel"); 
                                    toServer.flush(); 
                                } catch (IOException ex) { 
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
                                } 
                            }); 
                            
                            if (closeSocket) { 
                                fromServer.close(); 
                                socket.close(); 
                            } 
                        } 
                    } 
                } catch (IOException e) { } 
            }).start(); 
        }); 
        
        disconnect.setOnAction((ActionEvent event) -> {
            try { 
                toServer.writeUTF("<<< Роз'єдано: " + socket.getInetAddress().getHostAddress() + "\n"); 
                toServer.flush(); 
                
                connect.setDisable(false); 
                disconnect.setDisable(true); 
                
                gp.setDisable(true); 
                closeSocket = true; 
            } catch (IOException ex) { 
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex); 
            } 
        }); 
    } 
    
    Text createText(double beg_x, double beg_y, String text, Color color) { 
        Text t = new Text(beg_x, beg_y, text); 
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14)); 
        t.setFill(color); 
        
        return t; 
    } 
}
