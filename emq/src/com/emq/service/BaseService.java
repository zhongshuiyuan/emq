package com.emq.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.usermodel.CharacterProperties;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import Algorithm.Coords.Converter;
import Algorithm.Coords.Point;
import com.emq.dao.BaseDao;
import com.emq.exception.ErrorMsgConstants;
import com.emq.logger.Logger;
import com.emq.model.CigStoreGISMap;
import com.emq.model.ConditionObject;
import com.emq.model.GISMapFactory;
import com.emq.ui.dwr.CigStoreFinderImpl;
import com.emq.util.TimeUtil;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.mapinfo.util.DoublePoint;
/**
 * 基础业务逻辑服务
 * @author lyt
 * @created 2009-9-22
 */
public class BaseService {
	
	private CigStoreGISMap target = null;
	
	private GISMapFactory gisMapFactory;
	
	public GISMapFactory getGisMapFactory() {
		return gisMapFactory;
	}
	public void setGisMapFactory(GISMapFactory gisMapFactory) {
		this.gisMapFactory = gisMapFactory;
	}
	
	private static Logger log = Logger.getLogger(BaseService.class);
	
	private BaseDao baseDao;


	public BaseDao getBaseDao() {
		return baseDao;
	}
	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	
	/**
	 * 获取R1登录人员编码
	 * @return String
	 */
	public String getUserId()  {
//		Context ctx = null;
//		if (ctx == null) {
//			try {
//				ctx = Context.getInstance();
//			} catch (Exception e) {
//				//e.printStackTrace(); //开发中不在控制台输出
//			}
//		}
//		if (ctx != null) {
//			try {
//				UserInfo userInfo = ctx.getCurrentLoginInfo();
//				log.debug("当前用户ID:" + userInfo.getUserID());
//				return userInfo.getUserID();
//			} catch (Exception e) {
//				 //e.printStackTrace();
//				 //log.error(e.getMessage());
//			}
//		} else {
//			log.error("您好，登录信息过期，请退出重新登录！");
//		}
		return "dev_icss";
	}
	
	/**
	 * 获取R1登录人员组织编码
	 * @return String
	 */
	public List getUserOrgList()  {
//		Context ctx = null;
//		if (ctx == null) {
//			try {
//				ctx = Context.getInstance();
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//				e.printStackTrace(); //开发中不在控制台输出
//			}
//		}
//		if (ctx != null) {
//			try {
//				UserInfo userInfo = ctx.getCurrentLoginInfo();
//				log.debug("当前用户ID:" + userInfo.getUserID());
//				List orgList = ctx.getCurrentOrganization();
//				return orgList;
//			} catch (Exception e) {
//				e.printStackTrace();
//				log.error(e.getMessage());
//			}
//		} else {
//			log.error("您好，登录信息过期，请退出重新登录！");
//		}
		return new ArrayList();
	}

	/**
	 * 获取R1登录人员名称
	 * @return String
	 */
	public String getUserName() {
//		Context ctx = null;
//		if (ctx == null) {
//			log.debug("开始初始化RONE信息");
//			try {
//				ctx = Context.getInstance();
//			} catch (Exception e) {
//				e.printStackTrace(); //开发中不在控制台输出
//			}
//		}
//
//		if (ctx != null) {
//			try {
//				UserInfo userInfo = ctx.getCurrentLoginInfo();
//				//log.debug("当前用户:" + userInfo.getName());
//				return userInfo.getName();
//			} catch (Exception e) {
//				//e.printStackTrace();
//				//log.error(e.getMessage());
//			}
//		} else {
//			log.error("您好，登录信息过期，请退出重新登录！");
//		}
		return "测试用户";
	}
	
	/**
	 * 根据查询条件进行地图查询
	 * @param mapId
	 * @param startx
	 * @param starty
	 * @param newx
	 * @param newy
	 * @param session
	 * @return
	 */
	public void findByCondition(String mapId,ConditionObject co,HttpSession session){
		CigStoreFinderImpl cf = new CigStoreFinderImpl();
		try{
			cf.highlightByCondition(mapId,co, session);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 自动调度偏移计算
	 */
	public void runConverter(){
		List sqlList = new ArrayList();
		List dataList = baseDao.getRunConverterList();
		System.out.println("一共有"+dataList.size()+"需要计算偏移坐标!");
		try {
			target = (CigStoreGISMap) gisMapFactory.create("CigStoreMap");
			target.loadMap();
			Converter converter = new Converter();
			for(int i=0;i<dataList.size();i++){
				Map map = (Map)dataList.get(i);
				Double x = (Double)map.get("X1");
				Double y = (Double)map.get("Y1");
				Point p = null;
				DoublePoint dp = null;
				if(x<y){
					p = converter.getEncryPoint(y,x); 
					dp = new DoublePoint(y,x);
					sqlList.add("update GIS_MS_LICENSE set X="+p.getY()+",Y="+p.getX()+" where XKZHM='"+map.get("XKZHM")+"'");
				}else{
					p = converter.getEncryPoint(x,y); 
					dp = new DoublePoint(x,y);
					sqlList.add("update GIS_MS_LICENSE set X="+p.getX()+",Y="+p.getY()+" where XKZHM='"+map.get("XKZHM")+"'");
				}
//				超出范围列如采点数据
				boolean within = target.within(dp,(String)map.get("GSDM"));
				if(!within){
					String sql = "update gis_ms_license_check set sfxs=1,lrcdsj=getdate() where xkzhm='"+map.get("XKZHM")+"'";
					sqlList.add(sql);
				}
			}
			baseDao.exeUpdateSqlByBach(sqlList);
			System.out.println("计算偏移坐标结束!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改xy坐标
	 * @param po 
	 */
	public boolean updateCheckPoint(Double x,Double y,String xkzhm){
		boolean mark = true;
		try{
			Map pointMap = this.getBaseDao().getDataForMap("select x,y,x1,y1 from GIS_MS_LICENSE_CHECK where XKZHM='"+xkzhm+"'");
			Double x_temp = (Double)pointMap.get("X")-(Double)pointMap.get("X1");
			Double y_temp = (Double)pointMap.get("Y")-(Double)pointMap.get("Y1");
			Double x1 = x-x_temp;
		    Double y1 = y-y_temp;;
			this.getBaseDao().exeUpdateSql("update GIS_MS_LICENSE_CHECK set x="+x+",y="+y+",x1="+x1+",y1="+y1+" where XKZHM='"+xkzhm+"'");
		} catch (Exception e) {
			mark = false;
			e.printStackTrace();
		}
		return mark;
	}
	
	/**
	 * 批量执行SQL
	 * @param sqlList
	 * @return
	 */
	public boolean exeUpdateSqlByBach(List sqlList){
		boolean mark = true;
		try{
			this.baseDao.exeUpdateSqlByBach(sqlList);
		}catch(Exception e){
			return false;
		}
		return mark;
	}
	
	/**
	 * 执行UpdateSQL
	 * @param sqlList
	 * @return
	 */
	public boolean exeUpdateSql(String sql){
		boolean mark = true;
		try{
			this.baseDao.execute(sql);
		}catch(Exception e){
			return false;
		}
		return mark;
	}
	
	/**
	 * Excel导入记录表最大ID
	 * @param sqlList
	 * @return
	 */
	public int getImportMaxId(){
		int maxIdStr = 0;
		try{
			Map maxId = this.baseDao.getMapData("select max(ID) as ID from EMQ_BOOK_IMPORT");
			if(maxId!=null||maxId.size()==1){
				maxIdStr = new Integer(maxId.get("ID").toString()).intValue();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return maxIdStr;
	}
	
	/**
	 * 生成台帐Excel
	 * @param dataList
	 */
	public String createPassBookExcel(List dataList){
		String fileName = TimeUtil.dateToString(new Date(),"yyyyMMddHHmmss")+".xls";
		try {
			WritableWorkbook book=Workbook.createWorkbook(new File(fileName));
			WritableSheet sheet=book.createSheet("sheet1",0);
			for(int i=0;i<71;i++){
				sheet.setColumnView(i,20);
			}
			WritableCellFormat contentFromart = new WritableCellFormat(NumberFormats.TEXT); 
			WritableFont font1 = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD);
			WritableFont datafont = new WritableFont(WritableFont.ARIAL,10,WritableFont.NO_BOLD); 
			WritableCellFormat wf_head = new WritableCellFormat(font1);
			wf_head.setBorder(Border.ALL, BorderLineStyle.THIN);
			WritableCellFormat wf_data = new WritableCellFormat(datafont);
			wf_data.setBorder(Border.ALL, BorderLineStyle.THIN);
			wf_data.setAlignment(jxl.format.Alignment.CENTRE);
			wf_data.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			wf_head.setAlignment(jxl.format.Alignment.CENTRE);
			wf_head.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			sheet.addCell(new Label(0,0,"厂站名称",wf_head));
			sheet.addCell(new Label(1,0,"关口计量点",wf_head));
			sheet.addCell(new Label(9,0,"电能表信息",wf_head));
			sheet.addCell(new Label(27,0,"电压互感器信息",wf_head));
			sheet.addCell(new Label(41,0,"电流互感器信息",wf_head));
			sheet.addCell(new Label(55,0,"失压计时仪信息",wf_head));
			sheet.addCell(new Label(59,0,"GPS时钟信息",wf_head));
			sheet.addCell(new Label(63,0,"二次回路信息",wf_head));
			sheet.addCell(new Label(1,1,"分属单位",wf_head));
			sheet.addCell(new Label(2,1,"计量点名称",wf_head));
			sheet.addCell(new Label(3,1,"对侧计量点名称",wf_head));
			sheet.addCell(new Label(4,1,"对侧计量点安装地点",wf_head));
			sheet.addCell(new Label(5,1,"计量点类别",wf_head));
			sheet.addCell(new Label(6,1,"计量点性质",wf_head));
			sheet.addCell(new Label(7,1,"关口类型",wf_head));
			sheet.addCell(new Label(8,1,"电能表配置",wf_head));
			sheet.addCell(new Label(9,1,"型号",wf_head));
			sheet.addCell(new Label(10,1,"类型",wf_head));
			sheet.addCell(new Label(11,1,"厂家",wf_head));
			sheet.addCell(new Label(12,1,"出厂编号",wf_head));
			sheet.addCell(new Label(13,1,"接线方式",wf_head));
			sheet.addCell(new Label(14,1,"额定电压(V)",wf_head));
			sheet.addCell(new Label(15,1,"额定电流(A)",wf_head));
			sheet.addCell(new Label(16,1,"准确度等级",wf_head));
			sheet.addCell(new Label(17,1,"通讯接口",wf_head));
			sheet.addCell(new Label(18,1,"通讯方式",wf_head));
			sheet.addCell(new Label(19,1,"采集器型号",wf_head));
			sheet.addCell(new Label(20,1,"采集器厂家",wf_head));
			sheet.addCell(new Label(21,1,"TV变比",wf_head));
			sheet.addCell(new Label(22,1,"TA变比",wf_head));
			sheet.addCell(new Label(23,1,"倍率",wf_head));
			sheet.addCell(new Label(24,1,"最近检测时间 ",wf_head));
			sheet.addCell(new Label(25,1,"最近检测结论 ",wf_head));
			sheet.addCell(new Label(26,1,"型号",wf_head));
			sheet.addCell(new Label(27,1,"类型",wf_head));
			sheet.addCell(new Label(28,1,"厂家",wf_head));
			sheet.addCell(new Label(29,1,"相别",wf_head));
			sheet.addCell(new Label(30,1,"编号",wf_head));
			sheet.addCell(new Label(31,1,"一次额定电压(kV)",wf_head));
			sheet.addCell(new Label(32,1,"二次额定电压(V)",wf_head));
			sheet.addCell(new Label(33,1,"计量绕组额定二次负荷(VA)",wf_head));
			sheet.addCell(new Label(34,1,"准确度等级",wf_head));
			sheet.addCell(new Label(35,1,"所用TV性质(母线/线路)",wf_head));
			sheet.addCell(new Label(36,1,"是否专用绕组",wf_head));
			sheet.addCell(new Label(37,1,"是否专用TV",wf_head));
			sheet.addCell(new Label(38,1,"最近检测时间",wf_head));
			sheet.addCell(new Label(39,1,"最近检测结论",wf_head));
			sheet.addCell(new Label(40,1,"型号",wf_head));
			sheet.addCell(new Label(41,1,"类型",wf_head));
			sheet.addCell(new Label(42,1,"厂家",wf_head));
			sheet.addCell(new Label(43,1,"相别",wf_head));
			sheet.addCell(new Label(44,1,"编号",wf_head));
			sheet.addCell(new Label(45,1,"一次额定电流(A)",wf_head));
			sheet.addCell(new Label(46,1,"二次额定电流(A)",wf_head));
			sheet.addCell(new Label(47,1,"计量绕组额定二次负荷(VA)",wf_head));
			sheet.addCell(new Label(48,1,"准确度等级",wf_head));
			sheet.addCell(new Label(49,1,"所用TA性质（独立/套管）",wf_head));
			sheet.addCell(new Label(50,1,"是否专用绕组",wf_head));
			sheet.addCell(new Label(51,1,"是否专用TA",wf_head));
			sheet.addCell(new Label(52,1,"最近检测时间",wf_head));
			sheet.addCell(new Label(53,1,"最近检测结论",wf_head));
			sheet.addCell(new Label(54,1,"型号",wf_head));
			sheet.addCell(new Label(55,1,"类型",wf_head));
			sheet.addCell(new Label(56,1,"厂家",wf_head));
			sheet.addCell(new Label(57,1,"编号",wf_head));
			sheet.addCell(new Label(58,1,"型号",wf_head));
			sheet.addCell(new Label(59,1,"类型",wf_head));
			sheet.addCell(new Label(60,1,"厂家",wf_head));
			sheet.addCell(new Label(61,1,"编号",wf_head));
			sheet.addCell(new Label(62,1,"TV二次回路导线截面积(mm2)",wf_head));
			sheet.addCell(new Label(63,1,"TV二次回路长度（m）",wf_head));
			sheet.addCell(new Label(64,1,"TA二次回路导线截面积(mm2)",wf_head));
			sheet.addCell(new Label(65,1,"TV二次压降最近检测时间",wf_head));
			sheet.addCell(new Label(66,1,"TV二次压降最近检测结论",wf_head));
			sheet.addCell(new Label(67,1,"TV二次负荷最近检测时间",wf_head));
			sheet.addCell(new Label(68,1,"TV二次负荷最近检测结论",wf_head));
			sheet.addCell(new Label(69,1,"TA二次负荷最近检测时间",wf_head));
			sheet.addCell(new Label(70,1,"TA二次负荷最近检测结论",wf_head));
			sheet.mergeCells(0, 0, 0, 1);
			sheet.mergeCells(1, 0, 7, 0);
			sheet.mergeCells(8, 0, 25, 0);
			sheet.mergeCells(26, 0, 39, 0);
			sheet.mergeCells(40, 0, 53, 0);
			sheet.mergeCells(54, 0, 57, 0);
			sheet.mergeCells(58, 0, 61, 0);
			sheet.mergeCells(62, 0, 70, 0);
			for(int i=0;i<dataList.size();i++){
				int dataRow = i+2;
				Map map = (Map)dataList.get(i);
				sheet.addCell(new Label(0,dataRow,map.get("STATION_NAME").toString(),wf_data));
				sheet.addCell(new Label(1,dataRow,map.get("BELONG_UNIT_NAME").toString(),wf_data));
				sheet.addCell(new Label(2,dataRow,map.get("MEASURE_POINT_NAME").toString(),wf_data));
				sheet.addCell(new Label(3,dataRow,map.get("SUB_POINT_NAME").toString(),wf_data));
				sheet.addCell(new Label(4,dataRow,map.get("SUB_POINT_ADDRESS").toString(),wf_data));
				sheet.addCell(new Label(5,dataRow,map.get("MEASURE_POINT_TYPE").toString(),wf_data));
				sheet.addCell(new Label(6,dataRow,map.get("MEASURE_POINT_KIND").toString(),wf_data));
				sheet.addCell(new Label(7,dataRow,map.get("PASS_TYPE").toString(),wf_data));
				sheet.addCell(new Label(8,dataRow,map.get("E_CONFIGURE").toString(),wf_data));
				sheet.addCell(new Label(9,dataRow,map.get("E_MODEL").toString(),wf_data));
				sheet.addCell(new Label(10,dataRow,map.get("E_TYPE").toString(),wf_data));
				sheet.addCell(new Label(11,dataRow,map.get("E_MANUFACTURER").toString(),wf_data));
				
//				sheet.addCell(new Label(12,dataRow,map.get("E_FACTORY_NUMBER").toString(),contentFromart));
				if(map.get("E_FACTORY_NUMBER")!=null&&!map.get("E_FACTORY_NUMBER").equals("")){
					sheet.addCell(new jxl.write.Number(12,dataRow,new Double(map.get("E_FACTORY_NUMBER").toString()),wf_data));
				}else{
					sheet.addCell(new Label(12,dataRow,map.get("E_FACTORY_NUMBER").toString(),wf_data));
				}
				
				sheet.addCell(new Label(13,dataRow,map.get("E_CONNECTION").toString(),wf_data));
				sheet.addCell(new Label(14,dataRow,map.get("E_TENSION").toString(),wf_data));
				sheet.addCell(new Label(15,dataRow,map.get("E_ELECTRICITY").toString(),wf_data));
				sheet.addCell(new Label(16,dataRow,map.get("E_RANK").toString(),wf_data));
				sheet.addCell(new Label(17,dataRow,map.get("E_MESSAGE_INTERFACE").toString(),wf_data));
				sheet.addCell(new Label(18,dataRow,map.get("E_MESSAGE_TYPE").toString(),wf_data));
				sheet.addCell(new Label(19,dataRow,map.get("E_COL_MODEL").toString(),wf_data));
				sheet.addCell(new Label(20,dataRow,map.get("E_COL_FACTORY").toString(),wf_data));
				sheet.addCell(new Label(21,dataRow,map.get("E_TV_RATE").toString(),wf_data));
				sheet.addCell(new Label(22,dataRow,map.get("E_TA_RATE").toString(),wf_data));
				sheet.addCell(new Label(23,dataRow,map.get("E_MULTIPLE").toString(),wf_data));
				sheet.addCell(new Label(24,dataRow,map.get("E_CHECK_TIME").toString(),wf_data));
				sheet.addCell(new Label(25,dataRow,map.get("E_CHECK_RESULT").toString(),wf_data));
				sheet.addCell(new Label(26,dataRow,map.get("T_MODEL").toString(),wf_data));
				sheet.addCell(new Label(27,dataRow,map.get("T_TYPE").toString(),wf_data));
				sheet.addCell(new Label(28,dataRow,map.get("T_FACTORY").toString(),wf_data));
				sheet.addCell(new Label(29,dataRow,map.get("T_DISCREPANCY").toString(),wf_data));
				
				sheet.addCell(new Label(30,dataRow,map.get("T_UNMBER").toString(),contentFromart));
//				if(map.get("T_UNMBER")!=null&&!map.get("T_UNMBER").equals("")){
//					sheet.addCell(new jxl.write.Number(30,dataRow,new Double(map.get("T_UNMBER").toString()),wf_data));
//				}else{
//					sheet.addCell(new Label(30,dataRow,map.get("T_UNMBER").toString(),wf_data));
//				}
				
				sheet.addCell(new Label(31,dataRow,map.get("T_TENSION_FIRST").toString(),wf_data));
				sheet.addCell(new Label(32,dataRow,map.get("T_TENSION_SECOND").toString(),wf_data));
				sheet.addCell(new Label(33,dataRow,map.get("T_TENSION_SECOND_CHARGE").toString(),wf_data));
				sheet.addCell(new Label(34,dataRow,map.get("T_RANK").toString(),wf_data));
				sheet.addCell(new Label(35,dataRow,map.get("T_TV_KIND").toString(),wf_data));
				sheet.addCell(new Label(36,dataRow,map.get("T_IS_SPECIAL").toString(),wf_data));
				sheet.addCell(new Label(37,dataRow,map.get("T_IS_SPECIAL_TV").toString(),wf_data));
				sheet.addCell(new Label(38,dataRow,map.get("T_CHECK_TIME").toString(),wf_data));
				sheet.addCell(new Label(39,dataRow,map.get("T_CHECK_RESULT").toString(),wf_data));
				sheet.addCell(new Label(40,dataRow,map.get("EI_MODEL").toString(),wf_data));
				sheet.addCell(new Label(41,dataRow,map.get("EI_TYPE").toString(),wf_data));
				sheet.addCell(new Label(42,dataRow,map.get("EI_MANUFACTURER").toString(),wf_data));
				sheet.addCell(new Label(43,dataRow,map.get("EI_DISCREPANCY").toString(),wf_data));
				
				sheet.addCell(new Label(44,dataRow,map.get("EI_UNMBER").toString(),contentFromart));
//				if(map.get("EI_UNMBER")!=null&&!map.get("EI_UNMBER").equals("")){
//					sheet.addCell(new jxl.write.Number(44,dataRow,new Double(map.get("EI_UNMBER").toString()),wf_data));
//				}else{
//					sheet.addCell(new Label(44,dataRow,map.get("EI_UNMBER").toString(),wf_data));
//				}
				
				sheet.addCell(new Label(45,dataRow,map.get("EI_RATED_TENSION_FIRST").toString(),wf_data));
				sheet.addCell(new Label(46,dataRow,map.get("EI_RATED_TENSION_SECOND").toString(),wf_data));
				sheet.addCell(new Label(47,dataRow,map.get("EI_RATED_TENSION_SECOND_CHARGE").toString(),wf_data));
				sheet.addCell(new Label(48,dataRow,map.get("EI_VERACITY_RANK").toString(),wf_data));
				sheet.addCell(new Label(49,dataRow,map.get("EI_TV_KIND").toString(),wf_data));
				sheet.addCell(new Label(50,dataRow,map.get("EI_IS_SPECIAL").toString(),wf_data));
				sheet.addCell(new Label(51,dataRow,map.get("EI_IS_SPECIAL_TV").toString(),wf_data));
				sheet.addCell(new Label(52,dataRow,map.get("EI_CHECK_TIME").toString(),wf_data));
				sheet.addCell(new Label(53,dataRow,map.get("EI_CHECK_RESULT").toString(),wf_data));
				sheet.addCell(new Label(54,dataRow,map.get("LT_MODEL").toString(),wf_data));
				sheet.addCell(new Label(55,dataRow,map.get("LT_TYPE").toString(),wf_data));
				sheet.addCell(new Label(56,dataRow,map.get("LT_MANUFACTURER").toString(),wf_data));
				
//				sheet.addCell(new Label(57,dataRow,map.get("LT_UNMBER").toString(),contentFromart));
				if(map.get("LT_UNMBER")!=null&&!map.get("LT_UNMBER").equals("")){
					sheet.addCell(new jxl.write.Number(57,dataRow,new Double(map.get("LT_UNMBER").toString()),wf_data));
				}else{
					sheet.addCell(new Label(57,dataRow,map.get("LT_UNMBER").toString(),wf_data));
				}
				
				sheet.addCell(new Label(58,dataRow,map.get("GPS_MODEL").toString(),wf_data));
				sheet.addCell(new Label(59,dataRow,map.get("GPS_TYPE").toString(),wf_data));
				sheet.addCell(new Label(60,dataRow,map.get("GPS_MANUFACTURER").toString(),wf_data));
				
//				sheet.addCell(new Label(61,dataRow,map.get("GPS_UNMBER").toString(),contentFromart));
				if(map.get("GPS_UNMBER")!=null&&!map.get("GPS_UNMBER").equals("")){
					sheet.addCell(new jxl.write.Number(61,dataRow,new Double(map.get("GPS_UNMBER").toString()),wf_data));
				}else{
					sheet.addCell(new Label(61,dataRow,map.get("GPS_UNMBER").toString(),wf_data));
				}
				
				sheet.addCell(new Label(62,dataRow,map.get("TV_AREA").toString(),wf_data));
				sheet.addCell(new Label(63,dataRow,map.get("TV_LENGTH").toString(),wf_data));
				sheet.addCell(new Label(64,dataRow,map.get("TA_LENGTH").toString(),wf_data));
				sheet.addCell(new Label(65,dataRow,map.get("TV_TIME").toString(),wf_data));
				sheet.addCell(new Label(66,dataRow,map.get("TV_RESULT").toString(),wf_data));
				sheet.addCell(new Label(67,dataRow,map.get("TV_CHARGE_TIME").toString(),wf_data));
				sheet.addCell(new Label(68,dataRow,map.get("TV_CHARGE_RESULT").toString(),wf_data));
				sheet.addCell(new Label(69,dataRow,map.get("TA_CHARGE_TIME").toString(),wf_data));
				sheet.addCell(new Label(70,dataRow,map.get("TA_CHARGE_RESULT").toString(),wf_data));
			}
			for(int i=0;i<=70;i++){
				int startRow = 2;
				int endRow = 2;
				boolean flag = false;
				for(int j=0;j<dataList.size();j++){
					if(sheet.getCell(i, j+2).getContents().equals(sheet.getCell(i, j+3).getContents())){
						endRow ++;
					}else{
						flag = true;
					}
					if(flag){
						if(startRow!=(j+2)){
							sheet.mergeCells(i, startRow, i, j+2);
						}
						startRow = j+3;
						flag = false;
					}
				}
			}
			book.write(); 
			book.close(); 
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return fileName;
	}
	
	/**
	 * 根据上传文件ID获取文件保存路径
	 * @param id
	 * @return
	 */
	public String getDownLoadFile(Integer id){
		Map map = baseDao.getDataForMap("select FILE_PATH from EMQ_BOOK_IMPORT where id="+id);
		return map.get("FILE_PATH").toString();
	}
	
	/**
	 * 生成word
	 * @param fileName
	 * @return
	 */
	public Object[] createWordFile(String fileName){
		ActiveXComponent msWordApp = new ActiveXComponent("Word.Application");
		Dispatch documents = Dispatch.get(msWordApp,"Documents").toDispatch();
		Dispatch document = Dispatch.call(documents,"Add").toDispatch(); 
		Dispatch selection = Dispatch.get(msWordApp,"Selection").toDispatch();
//		Dispatch.put(selection,"Text","new word!");
		Dispatch.call(selection, "MoveDown");
		Dispatch.call(document,"SaveAs","D:/"+fileName);
		Object[] object = new Object[3];
		object[0] = msWordApp;
		object[1] = document;
		object[2] = selection;
		return object;
	}
	
	/**
	 * 插入表格
	 * @param document
	 * @param selection
	 * @param dataList
	 * @param headStr
	 */
	private void setTable(Dispatch document,Dispatch selection,List dataList,String headStr){
		String[] headList = headStr.split(",");
//		 建立表格
        Dispatch tables = Dispatch.get(document, "Tables").toDispatch();
        Dispatch range = Dispatch.get(selection, "Range").toDispatch();
//      设置列数,栏数,表格外框宽度
        Dispatch newTable = Dispatch.call(tables, "Add", range,new Variant(dataList.size()+1), new Variant(headList.length)).toDispatch(); 
        Dispatch.call(selection, "MoveRight"); // 光标移到最右边
        putTxtToCell(document,selection,dataList,headList); // 表格内写入内容(从第1列第1栏开始)
        autoFitTable(document); //自动调整表格
        // mergeCell(1,1,1,all_count,1); //表格合并(从第1列第1栏开始,第X列第1栏结束)
//		 取消选择(因为最后insert进去的文字会显示反白,所以要取消)
        Dispatch.call(selection, "MoveRight", new Variant(1), new Variant(1));
	}
	
	/**
	 * 插入图片
	 * @param document
	 * @param selection
	 * @param dataList
	 * @param headStr
	 */
	private void setImage(Dispatch selection,String imgPath){
		Dispatch.call(Dispatch.get(selection, "InLineShapes").toDispatch(),"AddPicture",imgPath);
		Dispatch.call(selection, "MoveDown");
	}
	
	/**
	 * 关闭word
	 * @param MsWordApp
	 * @param document
	 */
	private void closeWord(ActiveXComponent MsWordApp,Dispatch document){
		Dispatch.call(document,"Close");
		Dispatch.call(MsWordApp,"Quit");
		MsWordApp = null; 
		document = null;
	}
	
	/**
     * 自动调整表格
     */
    private void autoFitTable(Dispatch document){
        Dispatch tables = Dispatch.get(document, "Tables").toDispatch();
        int count = Dispatch.get(tables, "Count").toInt(); // word中的表格数量
        for (int i = 0; i < count; i++){
            Dispatch table = Dispatch.call(tables, "Item", new Variant(i + 1)).toDispatch();
            Dispatch cols = Dispatch.get(table, "Columns").toDispatch();
            Dispatch.call(cols, "AutoFit");
        }
    }
    
    /**
     * 在指定的表格里填入内容
     * @param tableIndex 表格起始点
     * @param cellRowIdx 第几列
     * @param cellColIdx 第几栏
     * @param txt 内容字符串数组
     */
    private void putTxtToCell(Dispatch document,Dispatch selection,List dataList,String[] headList){
        // 所有表格
        Dispatch tables = Dispatch.get(document, "Tables").toDispatch();
        Dispatch font = Dispatch.get(selection, "Font").toDispatch(); // 字型格式化需要的对象
		Dispatch alignment = Dispatch.get(selection, "ParagraphFormat").toDispatch(); // 行列格式化需要的对象
		Dispatch table = Dispatch.call(tables, "Item",new Variant(1)).toDispatch();
		Dispatch borders = Dispatch.get(table, "Borders").toDispatch();    
		Dispatch border = null;
		for(int i=1;i<7;i++){
			border = Dispatch.call(borders, "Item", new Variant(i)).toDispatch();  
//			Dispatch.put(border, "LineWidth", new Variant(0.1));    
			Dispatch.put(border, "Visible", new Variant(true));   
		}
		//写表头
		for(int i=0;i<headList.length;i++){
	        Dispatch cell = Dispatch.call(table, "Cell",new Variant(1), new Variant(i+1)).toDispatch();
	        // Dispatch.put(cell, "Height",new Variant(1)); //设置列高
	        Dispatch.call(cell, "Select");
	        // 主要内容
	        // Dispatch.call(selection, "TypeParagraph"); //空一行段落
	        // Dispatch.put(alignment, "Alignment", "3"); //(1:置中 2:靠右 3:靠左)
	        Dispatch.put(selection, "Text", headList[i]); // 写入word的内容
	        Dispatch.put(font, "Bold", "1"); // 字型租体(1:粗体 0:取消租体)
	        Dispatch.put(font, "Color", "1,1,1,1"); // 字型颜色
	        // Dispatch.put(font, "Italic", "1"); //字型斜体(1:斜体 0:取消斜体)
	        // Dispatch.put(font, "Underline", "1"); //文字加底线
	        //Dispatch.call(selection, "TypeParagraph"); // 空一行段落
	        Dispatch.put(alignment, "Alignment", "1"); // (1:置中 2:靠右 3:靠左)
		}
		Dispatch.call(selection, "MoveDown"); // 光标往下一行(才不会输入盖过上一输入位置)
        // 主要内容(即参数数组中的值)
        for (int i = 0; i < dataList.size(); i++){
            Map dataMap = (Map)dataList.get(i);
//            border = Dispatch.call(borders, "Item", new Variant(i)).toDispatch();  
//			Dispatch.put(border, "LineWidth", new Variant(1));    
//            Dispatch.put(border, "Visible", new Variant(true));
            for (int j = 0; j < headList.length; j++){
                // 要填入的表格(对表格列依序填入内容),cellRowIdx++代表从第一列开始
                Dispatch cell = Dispatch.call(table, "Cell",new Variant(i+2), new Variant(j+1)).toDispatch();
                // Dispatch.put(cell, "Height",new Variant(1)); //设置列高
                Dispatch.call(cell, "Select");
                // 主要内容
                // Dispatch.call(selection, "TypeParagraph"); //空一行段落
                // Dispatch.put(alignment, "Alignment", "3"); //(1:置中 2:靠右 3:靠左)
                Dispatch.put(selection, "Text", dataMap.get(headList[j])); // 写入word的内容
                Dispatch.put(font, "Bold", "0"); // 字型租体(1:粗体 0:取消租体)
                Dispatch.put(font, "Color", "1,1,1,1"); // 字型颜色
                // Dispatch.put(font, "Italic", "1"); //字型斜体(1:斜体 0:取消斜体)
                // Dispatch.put(font, "Underline", "1"); //文字加底线
                //Dispatch.call(selection, "TypeParagraph"); // 空一行段落
                Dispatch.put(alignment, "Alignment", "1"); // (1:置中 2:靠右 3:靠左)
                Dispatch.call(selection, "MoveDown"); // 光标往下一行(才不会输入盖过上一输入位置)
            }
        }
    }

    /**
     * 获取供电局
     * @param parentNodeId
     * @param treeLevel
     * @param ifleaf
     * @return
     */
    public List getPowerSuppLy(String parentNodeId,int treeLevel,boolean ifleaf){
 	   List mapList = new ArrayList();
 		//以下虚拟数据,实际从库中取
 		Map map = new HashMap();
 		map.put("id", "1");
 		map.put("text", "大理供电局");
 		//map.put("nextLevel", nextLevel);
 		map.put("leaf", ifleaf);
 		map.put("treeLevel", treeLevel);
 		Map map1 = new HashMap();
 		map1.put("id", "2");
 		map1.put("text", "丽江供电局");
 		//map1.put("nextLevel", nextLevel);
 		map1.put("leaf", ifleaf);
 		map1.put("treeLevel", treeLevel);
 		mapList.add(map);
 		mapList.add(map1);
 		return mapList;
    }
    
    /**
     * 获取县区市
     * @param parentNodeId
     * @param treeLevel
     * @param ifleaf
     * @return
     */
    public List getArea(String parentNodeId,int treeLevel,boolean ifleaf){
 	   List mapList = new ArrayList();
 		//以下虚拟数据,实际从库中取
 	   if(parentNodeId.equals("1")){
 	         //以下虚拟数据,实际从库中取
 				Map map = new HashMap();
 				map.put("id", "1_1");
 				map.put("text", "祥云");
 				//map.put("nextLevel", nextLevel);
 				map.put("leaf", ifleaf);
 				map.put("treeLevel", treeLevel);
 				Map map1 = new HashMap();
 				map1.put("id", "1_2");
 				map1.put("text", "下关");
 				//map1.put("nextLevel",nextLevel);
 				map1.put("leaf", ifleaf);
 				map1.put("treeLevel", treeLevel);
 				mapList.add(map);
 				mapList.add(map1);
 			}else if(parentNodeId.equals("2")){
 				//以下虚拟数据,实际从库中取
 				Map map = new HashMap();
 				map.put("id", "2_1");
 				map.put("text", "古城");
 				//map.put("nextLevel", nextLevel);
 				map.put("leaf", ifleaf);
 				map.put("treeLevel", treeLevel);
 				Map map1 = new HashMap();
 				map1.put("id", "2_2");
 				map1.put("text", "云山");
 				//map1.put("nextLevel", nextLevel);
 				map1.put("leaf", ifleaf);
 				map1.put("treeLevel", treeLevel);
 				mapList.add(map);
 				mapList.add(map1);
 			}
 		return mapList;
    }
    
    /**
     * 获取变电站
     * @param parentNodeId
     * @param treeLevel
     * @param ifleaf
     * @return
     */
    public List getTransformerSubstation(String parentNodeId,int treeLevel,boolean ifleaf){
 	   List mapList = new ArrayList();
 	   if(parentNodeId.equals("1_1")){
 	         //以下虚拟数据,实际从库中取
 				Map map = new HashMap();
 				map.put("id", "1_1_1");
 				map.put("text", "变电站祥云1");
 			//	map.put("nextLevel", nextLevel);
 				map.put("leaf", ifleaf);
 				map.put("treeLevel", treeLevel);
 				Map map1 = new HashMap();
 				map1.put("id", "1_1_2");
 				map1.put("text", "变电站祥云2");
 			//	map1.put("nextLevel", nextLevel);
 				map1.put("leaf", ifleaf);
 				map1.put("treeLevel", treeLevel);
 				mapList.add(map);
 				mapList.add(map1);
 			}else if(parentNodeId.equals("1_2")){
 				Map map = new HashMap();
 				map.put("id", "1_2_1");
 				map.put("text", "变电站下关1");
 				//map.put("nextLevel", nextLevel);
 				map.put("leaf", ifleaf);
 				map.put("treeLevel", treeLevel);
 				Map map1 = new HashMap();
 				map1.put("id", "1_2_2");
 				map1.put("text", "变电站下关2");
 				//map1.put("nextLevel", nextLevel);
 				map1.put("leaf", ifleaf);
 				map1.put("treeLevel", treeLevel);
 				mapList.add(map);
 				mapList.add(map1);
 			}else if(parentNodeId.equals("2_1")){
 				Map map = new HashMap();
 				map.put("id", "2_1_1");
 				map.put("text", "变电站古城1");
 				//map.put("nextLevel",nextLevel);
 				map.put("leaf", ifleaf);
 				map.put("treeLevel", treeLevel);
 				Map map1 = new HashMap();
 				map1.put("id", "2_1_2");
 				map1.put("text", "变电站古城2");
 				//map1.put("nextLevel", nextLevel);
 				map1.put("leaf", ifleaf);
 				map1.put("treeLevel", treeLevel);
 				mapList.add(map);
 				mapList.add(map1);
 			}else if(parentNodeId.equals("2_2")){
 				Map map = new HashMap();
 				map.put("id", "2_2_1");
 				map.put("text", "变电站云山1");
 				//map.put("nextLevel", nextLevel);
 				map.put("leaf", ifleaf);
 				map.put("treeLevel", treeLevel);
 				Map map1 = new HashMap();
 				map1.put("id", "2_2_2");
 				map1.put("text", "变电站云山2");
 				//map1.put("nextLevel", nextLevel);
 				map1.put("leaf", ifleaf);
 				map1.put("treeLevel", treeLevel);
 				mapList.add(map);
 				mapList.add(map1);
 			}
 	   return mapList;
    }
	
	public static void main(String[] args){
		String fileName = TimeUtil.dateToString(new Date(),"yyyyMMddHHmmss")+".doc";
		BaseService baseService = new BaseService();
		List dataList = new ArrayList();
		Map map = new HashMap();
		map.put("姓名", "/cmpCtrol/10.png");
		map.put("性别", "男");
		map.put("年龄", "7");
		map.put("学历", "4");
		map.put("婚姻状况", "4");
		map.put("住址", "3");
		map.put("公司", "2");
		map.put("职业", "1");
		map.put("爱好", "6");
		map.put("工作性质", "sdf");
		map.put("个人方向", "tt");
		dataList.add(map);
		String headStr = "姓名,性别,年龄,学历,婚姻状况,住址,公司,职业,爱好,工作性质,个人方向";
		Object[] object = baseService.createWordFile(fileName);
		ActiveXComponent msWordApp = (ActiveXComponent)object[0]; 
		Dispatch document = (Dispatch)object[1];
		Dispatch selection = (Dispatch)object[2];
		baseService.setTable(document, selection, dataList, headStr);
		baseService.setImage(selection, "D:/tool/Tomcat 5.0/webapps/EMQ/imgs/cmpCtrol/10.png");
		baseService.closeWord(msWordApp, document);
	}
	
}
