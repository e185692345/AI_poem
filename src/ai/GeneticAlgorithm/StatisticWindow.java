package ai.GeneticAlgorithm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.*;

class StatisticWindow extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	MyPanel panel;
	GenerationData dataSet;
	
	public StatisticWindow(GenerationData dataSet){
		this.dataSet = dataSet;
		
		DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
		Date date = new Date();
		this.setTitle(sdf.format(date)+" ("+date.getTime()+")");
		this.setLayout(null);
		this.setBounds(0,0,1320,730);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new MyPanel();
		panel.setBackground(Color.WHITE);
		this.add(panel);
		this.setVisible(true);
	}
	
	class MyPanel extends JPanel{

		/**
		 * 
		 */
		private static final int offset = 30;
		private static final int width = 1300;
		private static final int height = 700;
		private double unitX, unitY;
		private int maxValue = dataSet.getMaxValue();
		private int minValue = dataSet.getMinValue();
		private int countPoint = dataSet.getMax().length;
	
		public MyPanel (){
			super();
			setSize(width,height);
		}
		
		private static final long serialVersionUID = 1L;
		
		public void paintComponent(Graphics g){
			
			maxValue = dataSet.getMaxValue();
			minValue = dataSet.getMinValue();
			countPoint = dataSet.getMax().length;
			super.paintComponent(g);
			
			unitX = (width - 2 * offset) / (countPoint);
			unitY = (height - 2 * offset) / (maxValue - minValue+1);
			
			/*畫X,Y軸*/
			((Graphics2D)g).setStroke(new BasicStroke(3));
			g.drawLine(offset, height - offset, width - offset, height - offset);
			g.drawLine(offset, height - offset, offset, offset);
			
			
			/*畫刻度*/
			Stroke bs =   new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{16, 4}, 0);  
			((Graphics2D)g).setStroke(bs);
			g.setFont(new Font(Font.DIALOG,Font.BOLD,12));
			DrawScale(g, minValue);
			DrawScale(g, (minValue*3+maxValue)/4);
			DrawScale(g, (minValue+maxValue)/2);
			DrawScale(g, (minValue+maxValue*3)/4);
			DrawScale(g, maxValue);
			
			bs =   new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 6}, 0);  
			((Graphics2D)g).setStroke(bs);
			DrawXScale(g, 1);
			for (int i = 10; i < countPoint ; i += 10){
				DrawXScale(g, i);
			}
			DrawXScale(g, countPoint);
			
			((Graphics2D)g).setStroke(new BasicStroke(3));
			DrawLine(g, dataSet.getMax(),Color.red);
			DrawLine(g, dataSet.getAverage(),new Color(0xAA, 0x00, 0xAA));
			DrawLine(g, dataSet.getMin(), Color.blue);
			
		}
		
		private void DrawXScale(Graphics g, int index){
			int x = offset+(int)(index*unitX);
			g.drawString(String.valueOf(index),x, height - offset+15);
			g.drawLine(x, height - offset + 5, x, offset);
		}
		
		private void DrawScale(Graphics g, int scale){
			int y = (int)(height-offset-(scale-minValue+1)*unitY);
			g.drawString(String.valueOf(scale),5,y);
			g.drawLine(offset-5, y, width - offset, y);
		}
		
		private void DrawLine(Graphics g,int[] data,Color color){
			int preX = 0, preY = 0, x, y;
			int countPoint = data.length;
			
			for (int i = 0 ; i < countPoint ; i++){
				g.setColor(color);
				x = offset+(int)((i+1)*unitX);
				y = (int)(height-offset-(data[i]-minValue+1)*unitY);
				if ( i > 0){
					/*((Graphics2D)g).setStroke(new BasicStroke(3.0f));*/
					g.drawLine(preX, preY, x, y);
				}
				g.drawOval(x-2, y-2, 4, 4);
				/*g.setColor(Color.red);
				g.setFont(new Font(Font.SERIF,Font.BOLD,20));
				if ( i < countPoint-1 && data[i] < data[i+1] )
					g.drawString(String.valueOf(data[i]),(int)x-2,(int)y+18);
				else
					g.drawString(String.valueOf(data[i]),(int)x-2,(int)y-6);*/
				preX = x;
				preY = y;
			}
		}
		
	}
}


