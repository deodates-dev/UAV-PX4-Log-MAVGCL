/****************************************************************************
 *
 *   Copyright (c) 2017 Eike Mansfeld ecm@gmx.de. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 ****************************************************************************/

package com.comino.jfx.extensions;

import com.comino.flight.prefs.MAVPreferences;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Badge extends Label {

	private String DEFAULT_CSS = "-fx-border-radius: 3;-fx-background-radius: 3;-fx-padding: 2;";

	public final static int MODE_OFF 		=  0;
	public final static int MODE_ON 		=  1;
	public final static int MODE_OK 		=  2;
	public final static int MODE_BLINK  	=  3;
	public final static int MODE_ERROR  	=  4;
	public final static int MODE_SPECIAL  	=  5;

	private int     mode   = MODE_OFF;
	private String  color  = null;
	private String  textColor   = "#F0F0F0";
	private boolean toggle = false;
	private Timeline timeline = null;

	private String oldcolor;

	public Badge( @NamedArg("description") String description ) {
		super();
		this.setDisable(false);
		this.setPrefWidth(999);
		this.setAlignment(Pos.CENTER);

		if(MAVPreferences.isLightTheme()) 
			this.color   = "#C0C0C0";
		 else
			this.color   = "#"+Integer.toHexString(Color.DARKGRAY.hashCode());
			

		if(MAVPreferences.isLightTheme())
			setStyle(DEFAULT_CSS+"-fx-background-color: #C0C0C0;-fx-text-fill:#808080;");
		else
			setStyle(DEFAULT_CSS+"-fx-background-color: #404040;-fx-text-fill:#808080;");

		if(description!=null) {
			this.setTooltip(new Tooltip(description));
		}

		this.disabledProperty().addListener((v,o,n) -> {
			if(n.booleanValue()) {
				if(MAVPreferences.isLightTheme()) 
					setStyle(DEFAULT_CSS+"-fx-background-color: #C0C0C0;-fx-text-fill:#808080;");
				else
					setStyle(DEFAULT_CSS+"-fx-background-color: #404040;-fx-text-fill:#808080;");
			}
			else
				setMode(mode);


		});
	}

	public void setMode(int mode, Color color) {
		if(this.mode == mode)
			return;
		this.mode = mode;
		if(!MAVPreferences.isLightTheme()) 
			setBackgroundColor(color);
	}

	public void setMode(int mode) {

		if(timeline!=null)
			timeline.stop();

		toggle=false;

		if(this.mode == mode && color == oldcolor)
			return;

		this.oldcolor = color;
		this.mode = mode;

		Platform.runLater(() -> {

			setDisable(toggle);

			switch(mode) {
			case MODE_OFF:
				if(MAVPreferences.isLightTheme()) 
					setStyle(DEFAULT_CSS+"-fx-background-color: #C0C0C0;-fx-text-fill:#808080;");
				else
					setStyle(DEFAULT_CSS+"-fx-background-color: #404040;-fx-text-fill:#808080;");
				break;
			case MODE_ON:
				if(MAVPreferences.isLightTheme()) 
					setStyle(DEFAULT_CSS+"-fx-background-color: #C0C0C0;-fx-text-fill:#101010;");
				else	 
					setStyle(DEFAULT_CSS+"-fx-background-color:"+color+";-fx-text-fill:"+textColor+";");
				break;
			case MODE_BLINK:
				if(MAVPreferences.isLightTheme()) 
					setStyle(DEFAULT_CSS+"-fx-background-color: #C0C0C0;-fx-text-fill:"+color+";");
				else	
					setStyle(DEFAULT_CSS+"-fx-background-color:"+color+";-fx-text-fill:#F0F0F0;");
				if(timeline!=null) timeline.play();
				break;
			case MODE_OK:		
				setStyle(DEFAULT_CSS+"-fx-background-color: #32Bd32;-fx-text-fill:#F0F0F0;");
				break;
			case MODE_ERROR:		
				setStyle(DEFAULT_CSS+"-fx-background-color: #C02020;-fx-text-fill:#F0F0F0;");
				break;
			case MODE_SPECIAL:		
				setStyle(DEFAULT_CSS+"-fx-background-color: #B8860B;-fx-text-fill:#F0F0F0;");
				break;
			default:
				if(MAVPreferences.isLightTheme()) 
					setStyle(DEFAULT_CSS+"-fx-background-color: #C0C0C0;-fx-text-fill:"+color+";");
				else	
					setStyle(DEFAULT_CSS+"-fx-background-color: #404040;-fx-text-fill:#808080;");
				break;
			}
		});

	}


	public void setBackgroundColor(Color color) {
		
		this.color = "#"+Integer.toHexString(color.darker().desaturate().hashCode());		
		if(color.getBrightness()<0.7)
			this.textColor ="#F0F0F0";
		else
			this.textColor ="#"+Integer.toHexString(color.darker().darker().darker().darker().hashCode());
		
	}

	public void setBackgroundColorWhiteText(Color color) {
	//	if(MAVPreferences.isLightTheme()) {
		this.color = "#"+Integer.toHexString(color.darker().desaturate().hashCode());
		this.textColor ="#F0F0F0";
	//	}
	}

	public void setRate(String rate) {
		timeline = new Timeline(new KeyFrame(
				Duration.millis(Integer.parseInt(rate)),
				ae -> {
					toggle = !toggle; setDisable(toggle);
				}));
		timeline.setCycleCount(Animation.INDEFINITE);
	}

	public void clear() {
		this.setText("");
	}

	public String getRate() {
		return null;
	}

	public String getColor() {
		return color.toString();
	}

	public void setColor(String value) {
		setBackgroundColor(Color.valueOf(value));
	}


}
