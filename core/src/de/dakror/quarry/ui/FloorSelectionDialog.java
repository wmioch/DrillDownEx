/*******************************************************************************
 * Copyright 2018 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.dakror.quarry.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.dakror.common.Callback;
import de.dakror.quarry.Const;
import de.dakror.quarry.Quarry;
import de.dakror.quarry.game.Layer;
import de.dakror.quarry.scenes.Game;
import de.dakror.quarry.structure.logistics.ItemElevator;
import de.dakror.quarry.ui.Ui;

/**
 * Dialog for selecting target floor for ItemElevator
 * 
 * @author Maximilian Stark | Dakror
 */
public class FloorSelectionDialog extends Window {
    Label titleLabel;
    Label currentFloorLabel;
    ImageButton upButton;
    ImageButton downButton;
    ImageButton up10Button;
    ImageButton down10Button;
    ImageButton confirmButton;
    ImageButton cancelButton;
    
    int sourceFloor;
    int selectedFloor;
    int x, y;
    
    Callback<Integer> confirmCallback;
    Callback<Boolean> cancelCallback;
    Ui ui;
    
    public FloorSelectionDialog(Skin skin) {
        super("", skin, "default"); // Empty window title to avoid overlap
        setModal(true);
        
        // Add title as part of content instead of window title
        Label windowTitle = new Label(Quarry.Q.i18n.get("elevator.select_floor"), skin, "small");
        windowTitle.setAlignment(Align.center);
        
        titleLabel = new Label(Quarry.Q.i18n.get("elevator.select_target"), skin, "small");
        titleLabel.setAlignment(Align.center);
        currentFloorLabel = new Label("0", skin, "small");
        currentFloorLabel.setAlignment(Align.center);
        
        upButton = new ImageButton(skin, "arrow");
        downButton = new ImageButton(skin, "arrow");
        cancelButton = new ImageButton(skin, "x");
        
        // Create +10 and -10 buttons with "arrow_fast" style for double arrows
        up10Button = new ImageButton(skin, "arrow_fast");
        down10Button = new ImageButton(skin, "arrow_fast");
        
        // Create confirm button with custom style (don't modify shared style)
        ImageButton.ImageButtonStyle confirmStyle = new ImageButton.ImageButtonStyle(skin.get("round", ImageButton.ImageButtonStyle.class));
        confirmStyle.imageUp = skin.getDrawable("symb_rightarrow");
        confirmButton = new ImageButton(confirmStyle);
        confirmButton.getImageCell().size(32);
        
        // Rotate up button
        upButton.getImage().setOrigin(24, 24);
        upButton.getImage().setRotation(90);
        upButton.getImageCell().size(48);
        
        // Rotate down button
        downButton.getImage().setOrigin(24, 24);
        downButton.getImage().setRotation(-90);
        downButton.getImageCell().size(48);
        
        // Rotate up10 button (double arrow) - same size for proper centering
        up10Button.getImage().setOrigin(24, 24);
        up10Button.getImage().setRotation(90);
        up10Button.getImageCell().size(48);
        
        // Rotate down10 button (double arrow) - same size for proper centering
        down10Button.getImage().setOrigin(24, 24);
        down10Button.getImage().setRotation(-90);
        down10Button.getImageCell().size(48);
        
        upButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Quarry.Q.sound.play(Quarry.Q.clickSfx);
                changeFloor(-1);
            }
        });
        
        up10Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Quarry.Q.sound.play(Quarry.Q.clickSfx);
                changeFloor(-10);
            }
        });
        
        downButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Quarry.Q.sound.play(Quarry.Q.clickSfx);
                changeFloor(1);
            }
        });
        
        down10Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Quarry.Q.sound.play(Quarry.Q.clickSfx);
                changeFloor(10);
            }
        });
        
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Quarry.Q.sound.play(Quarry.Q.clickSfx);
                if (isFloorValid()) {
                    if (confirmCallback != null) {
                        confirmCallback.call(selectedFloor);
                    }
                    hide();
                }
            }
        });
        
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Quarry.Q.sound.play(Quarry.Q.clickSfx);
                if (cancelCallback != null) {
                    cancelCallback.call(false);
                }
                hide();
            }
        });
        
        // Layout with buttons arranged: up10, up1, floor, down1, down10
        Table content = new Table();
        content.pad(10);
        content.add(windowTitle).colspan(1).padTop(5).padBottom(10).row();
        content.add(up10Button).size(48, 48).padBottom(15).row();
        content.add(upButton).size(48, 48).padBottom(5).row();
        content.add(currentFloorLabel).padBottom(5).row();
        content.add(downButton).size(48, 48).padBottom(15).row();
        content.add(down10Button).size(48, 48).padBottom(15).row();
        Table buttonRow = new Table();
        buttonRow.add(confirmButton).size(48, 48).pad(8);
        buttonRow.add(cancelButton).size(48, 48).pad(8);
        content.add(buttonRow).colspan(1).row();
        
        add(content);
        pack();
    }
    
    public void show(Ui ui, int sourceFloor, int x, int y, Callback<Integer> confirm, Callback<Boolean> cancel) {
        // only one at a time
        if (getStage() != null)
            return;
            
        this.ui = ui;
        this.sourceFloor = sourceFloor;
        this.x = x;
        this.y = y;
        this.selectedFloor = sourceFloor;
        this.confirmCallback = confirm;
        this.cancelCallback = cancel;
        
        updateFloorDisplay();
        pack();
        setPosition((Const.UI_W - getWidth()) / 2, (Const.UI_H - getHeight()) / 2);
        ui.show(this);
    }
    
    public void hide() {
        if (ui != null) {
            ui.hide(this);
        }
        confirmCallback = null;
        cancelCallback = null;
    }
    
    private void changeFloor(int delta) {
        selectedFloor += delta;
        selectedFloor = Math.max(0, Math.min(Game.G.getLayerCount() - 1, selectedFloor));
        updateFloorDisplay();
    }
    
    private void updateFloorDisplay() {
        currentFloorLabel.setText(String.format("%d", -selectedFloor));
        
        // Disable buttons at boundaries
        upButton.setDisabled(selectedFloor == 0);
        up10Button.setDisabled(selectedFloor - 10 < 0);
        downButton.setDisabled(selectedFloor == Game.G.getLayerCount() - 1);
        down10Button.setDisabled(selectedFloor + 10 > Game.G.getLayerCount() - 1);
        
        // Check if floor is valid
        boolean valid = isFloorValid();
        confirmButton.setDisabled(!valid);
        
        if (valid) {
            int distance = Math.abs(selectedFloor - sourceFloor);
            currentFloorLabel.setColor(1, 1, 1, 1); // White
            String distText = String.format("%s (%s: %d)", 
                -selectedFloor,
                Quarry.Q.i18n.get("elevator.distance"),
                distance);
            currentFloorLabel.setText(distText);
        } else {
            currentFloorLabel.setColor(1, 0.3f, 0.3f, 1); // Red
        }
    }
    
    private boolean isFloorValid() {
        if (selectedFloor == sourceFloor) {
            return false; // Can't target same floor
        }
        
        Layer targetLayer = Game.G.getLayer(selectedFloor);
        if (targetLayer == null) {
            return false;
        }
        
        // Check if position is free
        if (targetLayer.getStructure(x, y) != null) {
            return false;
        }
        
        // Check fog of war (create temp structure for checking)
        ItemElevator temp = new ItemElevator(x, y);
        if (!targetLayer.isNotInFogOfWar(temp, false)) {
            return false;
        }
        
        return true;
    }
}

