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

import ai.poem.PoemTemplate;

class StatisticWindow extends JFrame{
	
	/**
	 * 最多只會畫出 400 筆資料，超過400筆的話會捨棄較年輕的世代，只把後面的世代資料畫出來
	 * 例如:1000筆資料，只會畫出 601 ~ 1000
	 */
	private static final long serialVersionUID = 1L;
	
	MyPanel panel;
	GenerationData dataSet;
	
	public StatisticWindow(int countPoint,int[] max, int[] min, int[] average, int[][] detailScore){
		this.dataSet = new GenerationData(countPoint, max, min, average,detailScore);
		
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
	
	private class MyPanel extends JPanel{

		/**
		 * 
		 */
		private static final int BORDER_OFFSET = 30;
		private static final int WIDTH = 1300;
		private static final int HEIGHT = 700;
		private double unitX, unitY;
		private int maxValue = dataSet.maxValue;
		private int minValue = dataSet.minValue;
		private int countPoint = dataSet.countPoint;
		
		public MyPanel (){
			super();
			setSize(WIDTH,HEIGHT);
		}
		
		private static final long serialVersionUID = 1L;
		
		public void paintComponent(Graphics g){
			
			maxValue = dataSet.maxValue;
			minValue = dataSet.minValue;
			countPoint = dataSet.countPoint;
			super.paintComponent(g);
			
			unitX = (WIDTH - 2 * BORDER_OFFSET) / (countPoint);
			unitY = (HEIGHT - 2 * BORDER_OFFSET) / (maxValue - minValue+1);
			
			/*畫X,Y軸*/
			((Graphics2D)g).setStroke(new BasicStroke(3));
			g.drawLine(BORDER_OFFSET, HEIGHT - BORDER_OFFSET, WIDTH - BORDER_OFFSET, HEIGHT - BORDER_OFFSET);
			g.drawLine(BORDER_OFFSET, HEIGHT - BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET);
			
			
			/*畫刻度*/
			Stroke bs =   new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{16, 4}, 0);  
			((Graphics2D)g).setStroke(bs);
			g.setFont(new Font(Font.DIALOG,Font.BOLD,12));
			drawScale(g, minValue);
			drawScale(g, (minValue*3+maxValue)/4);
			drawScale(g, (minValue+maxValue)/2);
			drawScale(g, (minValue+maxValue*3)/4);
			drawScale(g, maxValue);
			if ( maxValue > PoemTemplate.MAX_ANTITHESIS_SCORE)
				drawScale(g, PoemTemplate.MAX_ANTITHESIS_SCORE);
			if ( maxValue > PoemTemplate.MAX_DIVERSITY_SCORE)
				drawScale(g, PoemTemplate.MAX_DIVERSITY_SCORE);
			if ( maxValue > PoemTemplate.MAX_RHYTHM_SCORE)
				drawScale(g, PoemTemplate.MAX_RHYTHM_SCORE);
			if ( maxValue > PoemTemplate.MAX_TONE_SCORE)
				drawScale(g, PoemTemplate.MAX_TONE_SCORE);
			
			bs =   new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 6}, 0);  
			((Graphics2D)g).setStroke(bs);
			drawXScale(g, 1);
			for (int i = 10; i < countPoint ; i += 10){
				drawXScale(g, i);
			}
			drawXScale(g, countPoint);
			
			((Graphics2D)g).setStroke(new BasicStroke(3));
			drawLine(g, dataSet.max,Color.red);
			drawLine(g, dataSet.average,new Color(0xAA, 0x00, 0xAA));
			// TODO 不畫下限，因為下限目前都是0
			drawLine(g, dataSet.min, Color.pink);
			Color[] color = {Color.blue,Color.orange,Color.green,Color.gray};
			String[] label = {"  押韻","  平仄","  對仗","  多樣性"};
			for (int i = 0 ; i < PoemTemplate.COUNT_FITNESS_TYPE ; i++){
				drawLine(g, dataSet.detailScore[i] , color[i]);
				g.drawString(label[i], BORDER_OFFSET+(int)(countPoint*unitX),(int)(HEIGHT-BORDER_OFFSET-(dataSet.detailScore[i][countPoint-1]-minValue+1)*unitY));
			}
			
		}
		
		private void drawXScale(Graphics g, int index){
			int x = BORDER_OFFSET+(int)(index*unitX);
			g.drawString(String.valueOf(index+dataSet.ignorePoint),x, HEIGHT - BORDER_OFFSET+15);
			g.drawLine(x, HEIGHT - BORDER_OFFSET + 5, x, BORDER_OFFSET);
		}
		
		private void drawScale(Graphics g, int scale){
			int y = (int)(HEIGHT-BORDER_OFFSET-(scale-minValue+1)*unitY);
			g.drawString(String.valueOf(scale),5,y);
			g.drawLine(BORDER_OFFSET-5, y, WIDTH - BORDER_OFFSET, y);
		}
		
		private void drawLine(Graphics g,int[] data,Color color){
			int preX = 0, preY = 0, x, y;
			int countPoint = data.length;
			
			for (int i = 0 ; i < countPoint ; i++){
				g.setColor(color);
				x = BORDER_OFFSET+(int)((i+1)*unitX);
				y = (int)(HEIGHT-BORDER_OFFSET-(data[i]-minValue+1)*unitY);
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
	
	private class GenerationData {
		
		private static final int MAX_POINT = 400;
		private int[] max, min, average;
		private int[][] detailScore;
		private int maxValue,minValue;
		private int ignorePoint;
		private int countPoint;
		public GenerationData(int countPoint,int[] max, int[] min, int[] average, int[][] detailScore){
			
			if ( countPoint <= MAX_POINT){	
				ignorePoint = 0;
			}
			else{
				ignorePoint = countPoint - MAX_POINT;
				countPoint = MAX_POINT;
			}
			
			this.countPoint = countPoint;
			this.max = new int[countPoint];
			this.min = new int[countPoint];
			this.average = new int[countPoint];
			this.detailScore = new int[PoemTemplate.COUNT_FITNESS_TYPE][countPoint];
			maxValue = 0;
			minValue = 1000000;
			for ( int i = 0 ; i < countPoint ; i++){
				if ( maxValue < max[i + ignorePoint])
					maxValue = max[i + ignorePoint];
				if (minValue > min[i + ignorePoint])
					minValue = min[i + ignorePoint];
			
				this.max[i] = max[i+ ignorePoint];
				this.min[i] = min[i+ ignorePoint];
				this.average[i] = average[i+ ignorePoint];
				for (int j = 0; j < PoemTemplate.COUNT_FITNESS_TYPE ; j++){
					this.detailScore[j][i] = detailScore[j][i + ignorePoint];
				}
			}
			// TODO 因為樣詳細畫出各個fitnessScore，所以把下限拉到0
			minValue = 0;
		}
	}
}


