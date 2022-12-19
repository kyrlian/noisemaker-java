package com.k.noiseMaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

enum graphTypes {
	VALUE, FREQ
}

class TracerPanel extends JPanel {
	double tMin = 0.0;
	double tMax = 1.0;
	double tStep;
	double vMin = -1.0;
	double vMax = 1.0;
	IDrawable track;
	private static final long serialVersionUID = 1L;
	SharedArea sArea;
	graphTypes graphType = graphTypes.VALUE;// VALUE of FREQ

	TracerPanel(IDrawable track, SharedArea sArea) {
		this(track, sArea,graphTypes.VALUE);
	}

	TracerPanel(IDrawable track, SharedArea sArea, graphTypes graphType) {
		this.track = track;
		this.sArea = sArea;
		switch (track.getType()) {
		case "Track":
			this.graphType = graphType;
			break;
		default:
			this.graphType = graphTypes.VALUE;// VALUE of FREQ
			break;
		}
		updateGraphInfo( track,  sArea);
	}
	
	void updateGraphInfo(IDrawable track, SharedArea sArea){
		double currentTime = (double) sArea.get("currentTime");
		double margin = .01;//seconds ahead-after current time
		this.tMin = 0.0;
		this.tMax = 1.0;
		this.vMax = track.estimateMaxAmplitude() * 1.05;// 5%
		this.vMin = -0.05 * vMax;
		switch (track.getType()) {
		case "EffectEnveloppe":
			this.tMax = ((EffectEnveloppe) track).getLength();// 2 periods
			break;
		case "Track":			
			this.tMin = Math.max(currentTime-margin , ((Track) track).getStartTime());
			double tlen = (((Track) track).getLazyLength());
			if (tlen > 0.0) {
				this.tMax = tMin + tlen * 1.05;
			} else {
				double minFreq = Collections.min(((Track) track).getFreqs(currentTime));
				this.tMax = tMin + 2.0 / minFreq;// 2 periods
			}
			this.tMax = Math.max(currentTime+margin , tMax);
			//this.graphType = graphTypes.FREQ;
			switch (graphType) {
			case VALUE:
				this.vMin = -1 * vMax;
				break;
			case FREQ:
				int samplesPerSecond = (int) sArea.get("samplesPerSecond");
				this.vMax = (samplesPerSecond / FConstants.MinSamplesPerPeriod) * 1.05;
				break;
			default:
				break;
			}
			break;
		case "SignalComplexOscillator":
		case "SignalSimpleOscillator":
		case "Wave":
			this.tMin = Math.max(currentTime-margin , 0);			
			//this.tMax = 2.0 / ((Wave) track).getFreq();// 2 periods
			double minFreq = Collections.min(((Wave) track).getFreqs(currentTime));
			this.tMax = this.tMin + (2.0 / minFreq);// 2 periods
			
			//Logger.log("tmin:"+this.tMin+", tmax:"+this.tMax);
			
			//Logger.log("MinFreq:"+minFreq);
			//Logger.log("tMax:"+tMax);
			this.vMin = -1 * vMax;
			break;
		default:
			break;
		}
	}
/*
 //unused
	TracerPanel(IDrawable track, double tMin, double tMax, double vMin, double vMax, SharedArea sArea, graphTypes graphType) {
		this.track = track;
		this.tMin = tMin;
		this.tMax = tMax;
		this.vMin = vMin;
		this.vMax = vMax;
		this.sArea = sArea;
		this.graphType = graphType;// VALUE of FREQ
	}*/

	private void doValueDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.blue);
		Dimension size = getSize();
		Insets insets = getInsets();
		int w = size.width - insets.left - insets.right;
		int h = size.height - insets.top - insets.bottom;
		double tScale = (double) (tMax - tMin) / w;
		double vScale = h / (vMax - vMin);
		int oldy = 0;// h - (int)(((track.getValue(tMin)-vMin) * vScale));
		for (int x = 0; x < w; x++) {
			double t = tMin + (double) x * tScale;
			int y = h - (int) (((track.getValue(t) - vMin) * vScale));
			if (x > 0) {
				g2d.drawLine(x - 1, oldy, x, y);// continuous
			}/*
			 * else { g2d.drawLine(x, y, x, y);// first dot }
			 */
			oldy = y;
		}
		drawCurrentTime(g, tScale, h);
	}

	private void doFreqDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.blue);
		Dimension size = getSize();
		Insets insets = getInsets();
		int w = size.width - insets.left - insets.right;
		int h = size.height - insets.top - insets.bottom;
		double tScale = (double) (tMax - tMin) / w;
		double fScale = h / (vMax - vMin);
		// for (double t = tMin; t < tMax; t += tStep) {
		// Logger.log("Repainting...");
		for (int x = 0; x < w; x++) {
			double t = tMin + (double) x * tScale;
			List<Double> freqs = ((ISignal)track).getFreqs(t);			
			for (double f : freqs) {
				int y = h - (int) ((f * fScale) + vMin);
				g2d.drawLine(x, y, x, y);
			}
		}
		// draw current t
		drawCurrentTime(g, tScale, h);
		// could print xAxis and yAxis basic scales
	}

	private void drawCurrentTime(Graphics g, double tScale, int h) {
		// draw current t
		double currentTime = (double) sArea.get("currentTime");
		// Logger.log("currentTime:"+currentTime);
		int x = (int) ((currentTime - tMin) / tScale);
		// Logger.log("x:"+x);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.red);
		g2d.drawLine(x, 0, x, h);
		// could print xAxis and yAxis basic scales
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		updateGraphInfo( track,  sArea);
		switch (graphType) {
		case VALUE:
			doValueDrawing(g);
			break;
		case FREQ:
			doFreqDrawing(g);
			break;
		default:
			break;
		}

		// Logger.log("Repainting Graph ");
	}
}
