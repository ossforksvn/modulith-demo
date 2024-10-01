/*
 * Copyright (c) 2012-2024 Savoir Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.savoir.modulith;

import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.application.Preloader;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Splash Screen For Modulith Game Demo
 */
public class IntroScreen extends Preloader {

    private final StackPane parent = new StackPane();

    private Stage preloaderStage;

    private ProgressIndicator progressIndicator;

    @Override
    public void init() throws Exception {

        // Load and set the background image for the splash screen
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image("SavoirModulithSplash.png"), // Replace with your background image path
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        );
        this.parent.setBackground(new Background(backgroundImage));
        this.progressIndicator = new ProgressBar();
        this.parent.getChildren().add(0, this.progressIndicator);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.preloaderStage = stage;
        Scene scene = new Scene(parent, 640, 480);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            this.preloaderStage.close();
        }
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            double progress = ((ProgressNotification) info).getProgress();
            this.progressIndicator.setProgress(progress);
        }
    }
}
