package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Application {

    public static final int DEFAULT_PORT = 8000;

    Group root = new Group();
    Group group = new Group();

    Text portText;
    Text connectionsText;
    TextArea connectDisplay;

    DropShadow dropShadow;

    Thread thread1;
    Thread thread2;
    Thread thread3;
    Thread thread4;
    Thread thread5;

    ServerSocket serverSocket;
    Socket socket;

    DataInputStream input;
    DataOutputStream output;

    Boolean connecting = true;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(DEFAULT_PORT);
    }

    @Override
    public void start(Stage serverStage) {
        serverStage.setTitle("Лабораторна робота №6 АІ-195 Бондар - Сервер");
        serverStage.setResizable(false);

        Rectangle rect1 = createRectangle(80, 70, 40, 130, Color.GREY);
        Rectangle rect2 = createRectangle(160, 70, 50, 110, Color.GREEN);
        Rectangle rect3 = createRectangle(240, 70, 50, 120, Color.BLUE);
        Rectangle rect4 = createRectangle(320, 70, 50, 130, Color.BURLYWOOD);

        Task<Void> task_1 = getTask(rect1, 250);
        Task<Void> task_2 = getTask(rect2, 200);
        Task<Void> task_3 = getTask(rect3, 240);
        Task<Void> task_4 = getTask(rect4, 220);

        Text textThread = createText(100, 370, "", Color.BLACK);
        Text text = createText(180, 370, "", Color.BLACK);

        Task<Void> task_5 = new Task<>() {

            @Override
            protected Void call() throws Exception {
                textThread.setText(Thread.currentThread().getName());

                while (true) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            double sumArea = calculationArea(rect1) + calculationArea(rect2) + calculationArea(rect3) + calculationArea(rect4);
                            text.setText("Загальна площа: " + sumArea + ", кв.см");

                            if (!connecting) {
                                try {
                                    output.writeUTF("Потік 5: " + sumArea);
                                    output.flush();
                                } catch (IOException ex) {
                                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    });

                    Thread.sleep(50);
                }
            }
        };

        root.getChildren().add(text);
        root.getChildren().add(textThread);

        thread1 = new Thread(task_1, "Потік 1");
        thread2 = new Thread(task_2, "Потік 2");
        thread3 = new Thread(task_3, "Потік 3");
        thread4 = new Thread(task_4, "Потік 4");
        thread5 = new Thread(task_5, "Потік 5");
        thread5.start();

        CreateGraphNodes();
        CreateControlNodes();

        Scene scene = new Scene(root, 750, 400, Color.WHITESMOKE);
        serverStage.setScene(scene);
        serverStage.show();

        new Thread(() -> {
            try {
                while (true) {
                    if (connecting) {
                        socket = serverSocket.accept();
                        connecting = false;
                        input = new DataInputStream(socket.getInputStream());
                        output = new DataOutputStream(socket.getOutputStream());
                        output.writeUTF(">>> З'єднано\nPort: " + socket.getLocalPort() + "\nHost: "
                                + socket.getLocalAddress().getHostAddress() + "\n");
                        output.flush();
                    } else {
                        String message = input.readUTF();
                        if (message.startsWith("<<<")) {
                            connecting = true;
                            thread1.suspend();
                            thread2.suspend();
                            thread3.suspend();
                            thread4.suspend();
                            thread5.suspend();

                            Platform.runLater(() -> {
                                connectionsText.setText("Підключень: 0");
                                connectDisplay.appendText(message);
                            });

                            output.writeUTF("<<< Роз'єданно\nPort: " + socket.getLocalPort() + "\nHost: "
                                    + socket.getLocalAddress().getHostAddress() + "\n");
                            output.flush();
                            output.close();
                        } else if (message.startsWith(">>>")) {
                            Platform.runLater(() -> {
                                connectionsText.setText("Підключень: 1");
                                connectDisplay.appendText(message);
                            });
                        } else {
                            String[] operands = message.split(" ");

                            switch (operands[0]) {
                                case "Thread1":
                                    if (operands[1].equals("start")) {
                                        if (thread1.getState().toString().equals("TIMED_WAITING"))
                                            thread1.resume();
                                        else if (!thread1.isAlive()) {
                                            thread1.start();
                                        }
                                    } else {
                                        thread1.suspend();
                                    }
                                    break;

                                case "Thread2":
                                    if (operands[1].equals("start")) {
                                        if (thread2.getState().toString().equals("TIMED_WAITING"))
                                            thread2.resume();
                                        else if (!thread2.isAlive()) {
                                            thread2.start();
                                        }
                                    } else {
                                        thread2.suspend();
                                    }
                                    break;

                                case "Thread3":
                                    if (operands[1].equals("start")) {
                                        if (thread3.getState().toString().equals("TIMED_WAITING"))
                                            thread3.resume();
                                        else if (!thread3.isAlive()) {
                                            thread3.start();
                                        }
                                    } else {
                                        thread3.suspend();
                                    }
                                    break;

                                case "Thread4":
                                    if (operands[1].equals("start")) {
                                        if (thread4.getState().toString().equals("TIMED_WAITING"))
                                            thread4.resume();
                                        else if (!thread4.isAlive()) {
                                            thread4.start();
                                        }
                                    } else {
                                        thread4.suspend();
                                    }
                                    break;

                                default:
                                    break;
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();

        Client client = new Client();

        client.start(Client.clientStage);
        serverStage.setOnCloseRequest((WindowEvent we) -> {
            System.exit(0);
        });

        serverStage.setOnCloseRequest(event -> {
            task_1.cancel();
            task_2.cancel();
            task_3.cancel();
            task_4.cancel();
            task_5.cancel();

            thread1.stop();
            thread2.stop();
            thread3.stop();
            thread4.stop();
            thread5.stop();
        });
    }

    public Task<Void> getTask(Rectangle rect_l, Integer maxHeightRect) {
        Text textThread = createText(rect_l.getX() - 5, rect_l.getY() - 5, "", Color.GRAY);
        Text text = createText(rect_l.getX(), rect_l.getY() - 20, "", Color.BLACK);

        Task<Void> task = new Task<>() {
            int maxHeight = maxHeightRect;

            @Override
            protected Void call() throws Exception {
                textThread.setText(Thread.currentThread().getName() + " ");

                while (true) {
                    Platform.runLater(() -> {
                        if (rect_l.getHeight() == 1) {
                            pulseRectangle(rect_l, true);
                            maxHeight = maxHeightRect;
                        } else if (rect_l.getHeight() == maxHeightRect) {
                            pulseRectangle(rect_l, false);
                            maxHeight -= 1;
                        } else if (rect_l.getHeight() == maxHeight) {
                            pulseRectangle(rect_l, false);
                            maxHeight -= 1;
                        } else {
                            pulseRectangle(rect_l, true);
                        }

                        Double area = calculationArea(rect_l);
                        text.setText(Double.toString(area));

                        if (!connecting) {
                            try {
                                output.writeUTF(textThread.getText() + text.getText());
                                output.flush();
                            } catch (IOException ex) {
                                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });

                    Thread.sleep(50);
                }
            }
        };

        root.getChildren().add(rect_l);
        root.getChildren().add(text);
        root.getChildren().add(textThread);
        return task;
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
        root.getChildren().add(createText(170, 20, "Площі прямокутників, кв.см", Color.BLACK));
    }

    private void CreateControlNodes() {
        connectDisplay = new TextArea();
        connectDisplay.setLayoutX(500);
        connectDisplay.setLayoutY(70);
        connectDisplay.setPrefSize(200, 100);
        connectDisplay.setEditable(false);

        portText = new Text("Port: " + DEFAULT_PORT);
        portText.setLayoutX(500);
        portText.setLayoutY(60);

        connectionsText = new Text("Connections: 0");
        connectionsText.setLayoutX(580);
        connectionsText.setLayoutY(60);
        group.getChildren().addAll(connectDisplay, portText, connectionsText);

        root.getChildren().addAll(group);
    }

    Text createText(double beg_x, double beg_y, String text, Color color) {
        Text t = new Text(beg_x, beg_y, text);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setFill(color);
        return t;
    }

    Rectangle createRectangle(double beg_x, double beg_y, double w, double h, Color color) {
        Rectangle r = new Rectangle(beg_x, beg_y, w, h);
        r.setFill(color);
        return r;
    }

    double calculationArea(Rectangle rect_l) {
        return rect_l.getWidth() * rect_l.getHeight();
    }

    public void pulseRectangle(Rectangle rect_l, boolean flag_direction) {
        if (flag_direction == true) {
            rect_l.setHeight(rect_l.getHeight() + 1);
        } else {
            rect_l.setHeight(rect_l.getHeight() - 1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}